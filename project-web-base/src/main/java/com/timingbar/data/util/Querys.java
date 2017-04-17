package com.timingbar.data.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.he.module.Constants;
import com.he.module.util.Casts;
import com.he.module.util.Reflections;
import com.he.module.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.Map.Entry;

public final class Querys {

    public enum Type {
        SQL, HQL, JPQL
    }

    public enum Operator {
        EQ, LIKE, GT, LT, GTE, LTE
    }

    private Querys() {
    }

    public static class QueryFilter {
        public String name;
        public Object value;
        public Operator operator;

        public QueryFilter(String name, Operator operator, Object value) {
            this.name = name;
            this.value = value;
            this.operator = operator;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("operator", this.operator).append("value", this.value).append("name", this.name).toString();
        }
    }

    public static Query createQuery(Session session, String sql, Object obj, Type queryType) {
        Query q = null;
        if (queryType.equals(Type.SQL)) {
            q = session.createSQLQuery(sql);
        } else {
            q = session.createQuery(sql);
        }
        if (obj != null) {
            if (obj instanceof Map) {
                Map<String, Object> params = (Map<String, Object>) obj;
                for (Entry<String, Object> param : params.entrySet()) {
                    q.setParameter(param.getKey(), param.getValue());
                }
            } else {
                Object[] args = (Object[]) obj;
                for (int i = 0; i < args.length; i++) {
                    q.setParameter(i, args[i]);
                }
            }
        }
        return q;
    }

    public static Query createSqlQuery(Session session, String sql, Object obj) {
        return createQuery(session, sql, obj, Type.SQL);
    }

    public static Query createHqlQuery(Session session, String sql, Object obj) {
        return createQuery(session, sql, obj, Type.HQL);
    }

    public static QueryFilter createQueryFilter(String name, Operator operator, Object value) {
        return new QueryFilter(name, operator, value);
    }

    /**
     * searchParams中key的格式为OPERATOR_FIELDNAME
     */
    public static Map<String, QueryFilter> parse(Map<String, Object> searchParams) {
        Map<String, QueryFilter> filters = Maps.newHashMap();
        for (Entry<String, Object> entry : searchParams.entrySet()) {
            // 过滤掉空值
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isBlank(String.valueOf(value))) {
                continue;
            }
            // 拆分operator与fieldAttribute
            String[] names = StringUtils.split(key, "_");
            if (names.length != 2) {
                throw new IllegalArgumentException(key + " is not a valid search filter name");
            }
            String name = names[1];
            Operator operator = Operator.valueOf(names[0]);
            // 创建QueryFilter
            QueryFilter filter = Querys.createQueryFilter(name, operator, value);
            filters.put(key, filter);
        }
        return filters;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <E> Specification<E> createMapFilter(final Class<E> entityClass, final Map filter, final Map like) {
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = Lists.newArrayList();
                if (filter != null && !filter.isEmpty()) {
                    convertStringToFieldType(entityClass, filter);
                    Iterator its = filter.entrySet().iterator();
                    while (its.hasNext()) {
                        Entry entry = (Entry) its.next();
                        String name = (String) entry.getKey();
                        Object value = entry.getValue();
                        boolean valueIsNull = value == null;
                        Path expression;
                        if (Strings.indexOfAny(name, ">=", "<=", ">", "<") > 0) {
                            // 过滤掉空值
                            if (valueIsNull) {
                                continue;
                            }
                            if (name.indexOf(">=") > 0) {
                                expression = root.get(name.replace(">=", ""));
                                predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) value));
                                continue;
                            }
                            if (name.indexOf("<=") > 0) {
                                expression = root.get(name.replace("<=", ""));
                                predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) value));
                                continue;
                            }
                            if (name.indexOf(">") > 0) {
                                expression = root.get(name.replace(">", ""));
                                predicates.add(builder.greaterThan(expression, (Comparable) value));
                                continue;
                            }
                            if (name.indexOf("<") > 0) {
                                expression = root.get(name.replace("<", ""));
                                predicates.add(builder.lessThan(expression, (Comparable) value));
                                continue;
                            }
                        } else {
                            if (name.indexOf("!") > 0) {
                                expression = root.get(Strings.remove(name.trim(), "!", "="));
                                if (valueIsNull) {
                                    predicates.add(builder.isNotNull(expression));
                                } else {
                                    predicates.add(builder.notEqual(expression, value));
                                }
                                continue;
                            } else {
                                expression = root.get(name);
                                if (valueIsNull) {
                                    predicates.add(builder.isNull(expression));
                                } else {
                                    predicates.add(builder.equal(expression, value));
                                }
                                continue;
                            }
                        }
                    }
                }
                if (like != null && !like.isEmpty()) {
                    Iterator its = like.entrySet().iterator();
                    while (its.hasNext()) {
                        Entry entry = (Entry) its.next();
                        String name = (String) entry.getKey();
                        String value = (String) entry.getValue();
                        boolean valueIsNull = value == null;
                        if (valueIsNull) {
                            continue;
                        } else {
                            if (Strings.isNotBlank(value)) {
                                Path expression = root.get(name);
                                if (Strings.isContains(value, "%")) {
                                    predicates.add(builder.like(expression, value));
                                } else {
                                    predicates.add(builder.like(expression, "%" + value + "%"));
                                }
                            }
                        }
                    }
                }
                // 将所有条件用 and 联合起来
                if (predicates.size() > 0) {
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
                return builder.conjunction();
            }
        };
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <E> Specification<E> createSearchFilter(final Collection<QueryFilter> filters) {
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                if (filters != null && !(filters.isEmpty())) {
                    List<Predicate> predicates = Lists.newArrayList();
                    for (QueryFilter filter : filters) {
                        // nested path translate, 如Task的名为"user.name"的filedName, 转换为Task.user.name属性
                        String[] names = StringUtils.split(filter.name, ".");
                        Path expression = root.get(names[0]);
                        for (int i = 1; i < names.length; i++) {
                            expression = expression.get(names[i]);
                        }
                        // logic operator
                        switch (filter.operator) {
                            case EQ:
                                predicates.add(builder.equal(expression, filter.value));
                                break;
                            case LIKE:
                                predicates.add(builder.like(expression, "%" + filter.value + "%"));
                                break;
                            case GT:
                                predicates.add(builder.greaterThan(expression, (Comparable) filter.value));
                                break;
                            case LT:
                                predicates.add(builder.lessThan(expression, (Comparable) filter.value));
                                break;
                            case GTE:
                                predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
                                break;
                            case LTE:
                                predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
                                break;
                            default:
                                break;
                        }
                    }
                    // 将所有条件用 and 联合起来
                    if (predicates.size() > 0) {
                        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                    }
                }
                return builder.conjunction();
            }
        };
    }

    public static Sort createSortFilter(Map<String, String> sort) {
        Sort sorter = null;
        List<Sort> sorters = Lists.newArrayList();
        if (sort != null && !sort.isEmpty()) {
            for (Entry<String, String> entry : sort.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if (Strings.isNullOrEmpty(value)) {
                    sorters.add(new Sort(Direction.ASC, name));
                } else {
                    if (value.trim().toLowerCase().equals("asc")) {
                        sorters.add(new Sort(Direction.ASC, name));
                    } else {
                        sorters.add(new Sort(Direction.DESC, name));
                    }
                }
            }
            sorter = sorters.get(0);
            for (int i = 1; i < sorters.size(); i++) {
                sorter = sorter.and(sorters.get(i));
            }
        }
        return sorter;
    }

    public static Sort createSortFilter_(Map<String, Object> sort) {
        Sort sorter = null;
        List<Sort> sorters = Lists.newArrayList();
        if (sort != null && !sort.isEmpty()) {
            Set<String> keys = sort.keySet();
            for (String key : keys) {
                String value = String.valueOf(sort.get(key));
                if (Strings.isNullOrEmpty(value)) {
                    sorters.add(new Sort(Direction.ASC, key));
                } else {
                    if (value.trim().toLowerCase().equals("asc")) {
                        sorters.add(new Sort(Direction.ASC, key));
                    } else {
                        sorters.add(new Sort(Direction.DESC, key));
                    }
                }
            }
            sorter = sorters.get(0);
            for (int i = 1; i < sorters.size(); i++) {
                sorter = sorter.and(sorters.get(i));
            }
        }
        return sorter;
    }

    private static Map<String, Object> convertStringToFieldType(Class<?> clazz, Map<String, Object> map) {
        Set<String> keys = map.keySet();
        for (String map_key : keys) {
            Object map_value = map.get(map_key);
            String name = map_key.replaceAll("\\>|\\<|\\=|\\!|\\ |\\t", ""); // 清除>,<,!,空格,\t
            if (map_value instanceof String) {
                String str_value = String.valueOf(map_value).trim();
                Class<?> field_type = Reflections.getField(clazz, name).getType();
                Object value = Casts.to(str_value, field_type);
                map.put(map_key, value);
            }
        }
        return map;
    }

    public static Sort newSort(String name, Direction direction) {
        return new Sort(direction, name);
    }

    public static Sort newSort(Direction direction, String name) {
        return new Sort(direction, name);
    }

    public static Pageable newPageRequest() {
        return new PageRequest(Constants.PAGE_NUMBER, Constants.PAGE_SIZE);
    }

    public static Pageable newPageRequest(int page, int size) {
        return new PageRequest(page, size);
    }

    public static Pageable newPageRequest(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    public static Pageable newPageRequest(int page, int size, Direction direction) {
        return new PageRequest(page, size, direction);
    }

    public static Pageable newPageRequest(int page, int size, Direction direction, String... properties) {
        return new PageRequest(page, size, direction, properties);
    }

}
