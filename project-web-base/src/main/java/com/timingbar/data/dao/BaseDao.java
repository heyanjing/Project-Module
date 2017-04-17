package com.timingbar.data.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.he.module.Constants;
import com.he.module.util.Beans;
import com.he.module.util.Strings;
import com.timingbar.data.jdbc.JdbcTemplateSupport;
import com.timingbar.data.util.Querys;

// 可不要@NoRepositoryBean
@NoRepositoryBean
@Transactional(readOnly = true)
public class BaseDao<T, ID extends Serializable> extends BaseSimpleJpaDao<T, ID> {

    // Entity Info
    protected String entityName;
    protected String entityIdName;

    // Data Info
    protected DataSource dataSource;
    protected JdbcTemplateSupport jdbcTemplateSupport;

    /**
     * 必须将注解@PersistenceContext放在set方法上，若注解在field上会报错说有多个entityManager
     * entityManager默认名称为em
     * ?子类若是其他数据源,,,则可以覆盖该方法
     */
    @PersistenceContext(unitName = "em")
    public void setEntityManager(EntityManager entityManager) {
        this.em = entityManager;
    }

    @PostConstruct
    public void init() {
        super.init();
        this.entityName = this.entityInformation.getEntityName();
        this.entityIdName = this.entityInformation.getIdAttributeNames().iterator().next();
        // Data Info
        this.dataSource = getEntityManagerFactoryInfo().getDataSource();
        this.jdbcTemplateSupport = new JdbcTemplateSupport(this.dataSource);
    }

    /**
     * 获取实体工厂管理对象
     */
    protected EntityManager getEntityManager() {
        return this.em;
    }

    protected EntityManagerFactoryInfo getEntityManagerFactoryInfo() {
        return (EntityManagerFactoryInfo) this.em.getEntityManagerFactory();
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected JdbcTemplateSupport getJdbcTemplateSupport() {
        return this.jdbcTemplateSupport;
    }

    /**
     * 获取 Session
     */
    public Session getSession() {
        return (Session) getEntityManager().getDelegate();
    }

    /**
     * 获取实体类型
     */
    protected Class<T> getEntityClass() {
        return this.entityClass;
    }

    /**
     * 获取实体名称
     */
    protected String getEntityName() {
        return this.entityName;
    }

    @SuppressWarnings("unused")
    protected void getHibernamteProperties() {
        SessionFactory sessionFactory = ((HibernateEntityManagerFactory) getEntityManagerFactoryInfo()).getSessionFactory();
        Properties properties = ((SessionFactoryImpl) sessionFactory).getProperties();

        String url = (String) properties.get("hibernate.connection.url");
        String username = (String) properties.get("hibernate.connection.username");
        String password = (String) properties.get("hibernate.connection.password");
    }

    //
    // save
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void saveInBatch(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            em.persist(list.get(i));
            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    //
    // create
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public <S extends T> S create(S entity) {
        return this.save(entity);
    }

    @Transactional
    public T createAndFlush(T entity) {
        return this.saveAndFlush(entity);
    }

    @Transactional
    public <S extends T> List<S> create(Iterable<S> entities) {
        return this.save(entities);
    }

    @Transactional
    public <S extends T> List<S> createAndFlush(Iterable<S> entities) {
        return saveAndFlush(entities);
    }

    @Transactional
    public <S extends T> List<S> saveAndFlush(Iterable<S> entities) {
        List<S> result = this.save(entities);
        this.flush();
        return result;
    }

    //
    // update
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public <S extends T> S update(S entity) {
        return this.save(entity);
    }

    @Transactional
    public <S extends T> List<S> update(Iterable<S> entities) {
        return this.save(entities);
    }

    @Transactional
    public void updateInBatch(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            em.merge(list.get(i));
            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    //
    // delete
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void delete(ID[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            return;
        }
        List<T> models = Lists.newArrayList();
        for (ID id : ids) {
            T model = null;
            try {
                model = this.entityClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + this.entityClass + " error", e);
            }
            try {
                Beans.setProperty(model, this.entityIdName, id);
            } catch (Exception e) {
                throw new RuntimeException("batch delete " + this.entityClass + " error, can not set id", e);
            }
            models.add(model);
        }
        deleteInBatch(models);
    }

    //
    // remove
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void remove(ID[] ids) {
        this.delete(ids);
    }

    @Transactional
    public void remove(T entity) {
        this.delete(entity);
    }

    @Transactional
    public void remove(ID id) {
        super.delete(id);
    }

    @Transactional
    public void remove(Iterable<T> entities) {
        this.delete(entities);
    }

    //
    // get
    // --------------------------------------------------------------------------------------------------------------------------------
    public T get(ID id) {
        return null != id ? this.findOne(id) : null;
    }

    public T get(String key, Object value) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.get(filter);
    }

    public T get(Map<String, ?> filter) {
        List<T> objs = this.find(filter);
        return objs.size() > 0 ? objs.get(0) : null;
    }

    public T getFirst() {
        List<T> entities = this.find(1);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    /**
     * 获得最后一个
     */
    public T getLast() {
        return null;
    }

    //
    // find
    // --------------------------------------------------------------------------------------------------------------------------------
    public List<T> find() {
        return this.findAll();
    }

    public List<T> find(String key, Object value) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.find(filter);
    }

    public List<T> find(int size) {
        return this.page(1, size).getContent();
    }

    public List<T> find(Map<String, ?> filter) {
        return this.find(filter, null);
    }

    public List<T> find(Map<String, ?> filter, int size) {
        return this.find(filter, null, size);
    }

    public List<T> find(Map<String, ?> filter, Map<String, String> like) {
        return this.find(filter, like, null);
    }

    public List<T> find(Map<String, ?> filter, Map<String, String> like, int size) {
        return this.find(filter, like, null, size);
    }

    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort) {
        return super.findAll(Querys.createMapFilter(this.entityClass, filter, like), Querys.createSortFilter(sort));
    }

    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int size) {
        return this.page(filter, like, sort, 1, size).getContent();
    }

    //
    // page
    // --------------------------------------------------------------------------------------------------------------------------------
    public Page<T> page(int pageNumber, int pageSize) {
        return this.page(null, pageNumber, pageSize);
    }

    public Page<T> page(String key, Object value, int pageNumber, int pageSize) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.page(filter, pageNumber, pageSize);
    }

    public Page<T> page(Map<String, ?> filter, int pageNumber, int pageSize) {
        return this.page(filter, null, null, pageNumber, pageSize);
    }

    public Page<T> page(Map<String, ?> filter, Map<String, String> like, int pageNumber, int pageSize) {
        return this.page(filter, like, null, pageNumber, pageSize);
    }

    public Page<T> page(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int pageNumber, int pageSize) {
        return super.findAll(Querys.createMapFilter(this.entityClass, filter, like), new PageRequest(pageNumber - 1, pageSize, Querys.createSortFilter(sort)));
    }

    //
    // other
    // --------------------------------------------------------------------------------------------------------------------------------
    public Long getCount() {
        return super.count();
    }

    public Long getCount(String key, Object value) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.getCount(filter);
    }

    public Long getCount(Map<String, ?> filter) {
        return this.getCount(filter, null);
    }

    public Long getCount(Map<String, ?> filter, Map<String, String> like) {
        return this.count(Querys.createMapFilter(this.entityClass, filter, like));
    }

    public boolean isExists(ID id) {
        return this.exists(id);
    }

    public boolean isExists(String key, Object value) {
        return (this.getCount(key, value) > 0);
    }

    public boolean isExists(Map<String, ?> filter) {
        return (this.getCount(filter) > 0);
    }

    public boolean isExists(Map<String, ?> filter, Map<String, String> like) {
        return (this.getCount(filter, like) > 0);
    }

    //
    // find by hql
    // --------------------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public List<T> findByHql(String hql) {
        return this.createHqlQuery(hql, null).list();
    }

    @SuppressWarnings("unchecked")
    public List<T> findByHql(String hql, Object... arg) {
        return this.createHqlQuery(hql, arg).list();
    }

    @SuppressWarnings("unchecked")
    public List<T> findByHql(String hql, Map<String, ?> params) {
        return this.createHqlQuery(hql, params).list();
    }

    //
    // page by hql
    // --------------------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private Page<T> pageWithHql(String hql, int pageNumber, int pageSize, Map<String, String> sort, Object params) {
        String count_hql = "SELECT COUNT(*) " + Strings.substringAfterAndContainsIgnoreCases(hql, "from");
        Query count_query = this.createHqlQuery(count_hql, params);
        Long count = ((Long) count_query.uniqueResult()).longValue();
        Sort sorts = Querys.createSortFilter(sort);
        if (sorts != null) {
            StringBuffer order_hql = new StringBuffer(" ORDER BY ");
            order_hql.append(Strings.remove(sorts.toString(), ":"));
            hql += order_hql.toString();
        }
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1) {
            pageSize = Constants.PAGE_SIZE;
        }
        int offset = (pageNumber - 1) * pageSize;
        List<T> entities = this.createHqlQuery(hql, params).setFirstResult(offset).setMaxResults(pageSize).list();
        return new PageImpl<T>(entities, new PageRequest(pageNumber - 1, pageSize), count);
    }

    public Page<T> pageByHql(String hql, int pageNumber, int pageSize, Map<String, ?> params) {
        return this.pageWithHql(hql, pageNumber, pageSize, null, params);
    }

    public Page<T> pageByHql(String hql, int pageNumber, int pageSize, Object... params) {
        return this.pageWithHql(hql, pageNumber, pageSize, null, params);
    }

    public Page<T> pageByHql(String hql, int pageNumber, int pageSize, Map<String, String> sort, Map<String, ?> params) {
        return this.pageWithHql(hql, pageNumber, pageSize, sort, params);
    }

    public Page<T> pageByHql(String hql, int pageNumber, int pageSize, Map<String, String> sort, Object... params) {
        return this.pageWithHql(hql, pageNumber, pageSize, sort, params);
    }

    public Page<T> pageByHql(String hql, Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int pageNumber, int pageSize) {
        Map<String, Object> params = Maps.newHashMap(filter);
        for (String key : like.keySet()) {
            String value = Strings.remove(like.get(key), "%");
            params.put(key, "%" + value + "%");
        }
        return this.pageWithHql(hql, pageNumber, pageSize, sort, params);
    }

    //
    // find by sql
    // --------------------------------------------------------------------------------------------------------------------------------
    public List<T> findBySql(String sql) {
        return this.jdbcTemplateSupport.find(sql, entityClass);
    }

    public List<T> findBySql(String sql, Map<String, ?> params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    public List<T> findBySql(String sql, Object... params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    //
    // page by sql
    // --------------------------------------------------------------------------------------------------------------------------------
    public Page<T> pageBySql(String sql, int pageNumber, int pageSize) {
        return this.jdbcTemplateSupport.page(sql, this.entityClass, pageNumber, pageSize);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Map<String, ?> params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Object... params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    //
    // find by sql and entityClass for DTO
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> List<E> findBySql(String sql, Class<E> entityClass) {
        return this.jdbcTemplateSupport.find(sql, entityClass);
    }

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Map<String, ?> params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Object... params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    //
    // page by sql and entityClass for DTO
    // --------------------------------------------------------------------------------------------------------------------------------

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    //
    // find by sql and RowMapper
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> List<E> findBySql(String sql, RowMapper<E> mapper) {
        return getJdbcTemplateSupport().find(sql, mapper);
    }

    public <E> List<E> findBySql(String sql, RowMapper<E> mapper, Map<String, ?> params) {
        return getJdbcTemplateSupport().find(sql, mapper, params);
    }

    public <E> List<E> findBySql(String sql, RowMapper<E> mapper, Object... params) {
        return getJdbcTemplateSupport().find(sql, mapper, params);
    }

    //
    // page by sql and RowMapper
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> Page<E> pageBySql(String sql, RowMapper<E> mapper, int pageNumber, int pageSize) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize);
    }

    public <E> Page<E> pageBySql(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Map<String, ?> params) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize, params);
    }

    public <E> Page<E> pageBySql(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Object... params) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize, params);
    }

    //
    // execute hql
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public int executeHql(String hql) {
        return this.createHqlQuery(hql, null).executeUpdate();
    }

    @Transactional
    public int executeHql(String hql, Object... params) {
        return this.createHqlQuery(hql, params).executeUpdate();
    }

    @Transactional
    public int executeHql(String hql, Map<String, ?> params) {
        return this.createHqlQuery(hql, params).executeUpdate();
    }

    //
    // execute sql
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public int executeSql(String sql) {
        return this.createSqlQuery(sql, null).executeUpdate();
    }

    @Transactional
    public int executeSql(String sql, Object... params) {
        return this.createSqlQuery(sql, params).executeUpdate();
    }

    @Transactional
    public int executeSql(String sql, Map<String, ?> params) {
        return this.createSqlQuery(sql, params).executeUpdate();
    }

    //
    // create hql
    // --------------------------------------------------------------------------------------------------------------------------------
    private Query createHqlQuery(String hql, Object params) {
        return Querys.createHqlQuery(getSession(), hql, params);
    }

    //
    // create sql
    // --------------------------------------------------------------------------------------------------------------------------------
    private Query createSqlQuery(String sql, Object params) {
        return Querys.createSqlQuery(getSession(), sql, params);
    }

}