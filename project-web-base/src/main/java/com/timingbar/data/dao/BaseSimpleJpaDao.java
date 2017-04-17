package com.timingbar.data.dao;

import static org.springframework.data.jpa.repository.query.QueryUtils.COUNT_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.DELETE_ALL_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.applyAndBind;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;
import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.he.module.util.Reflections;

// 可不要@NoRepositoryBean
@NoRepositoryBean
@Transactional(readOnly = true)
public class BaseSimpleJpaDao<T, ID extends Serializable> {

    protected Class<T> entityClass;

    public void init() {
        this.entityClass = Reflections.getClassGenricType(getClass());
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(this.entityClass, em);
        this.provider = PersistenceProvider.fromEntityManager(em);
    }

    //
    // SimpleJpaRepository
    // --------------------------------------------------------------------------------------------------------------------------------
    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

    protected JpaEntityInformation<T, ?> entityInformation;
    protected EntityManager em;
    protected PersistenceProvider provider;

    protected CrudMethodMetadata metadata;

    protected Class<T> getDomainClass() {
        return entityInformation.getJavaType();
    }

    private String getDeleteAllQueryString() {
        return getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName());
    }

    private String getCountQueryString() {
        String countQuery = String.format(COUNT_QUERY_STRING, provider.getCountQueryPlaceholder(), "%s");
        return getQueryString(countQuery, entityInformation.getEntityName());
    }

    /**
     * Configures a custom {@link CrudMethodMetadata} to be used to detect {@link LockModeType}s and query hints to be
     * applied to queries.
     * 
     * @param crudMethodMetadata
     */
    public void setRepositoryMethodMetadata(CrudMethodMetadata crudMethodMetadata) {
        this.metadata = crudMethodMetadata;
    }

    protected CrudMethodMetadata getRepositoryMethodMetadata() {
        return metadata;
    }

    //
    // SimpleJpaRepository Save
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public <S extends T> S save(S entity) {
        if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        S result = save(entity);
        flush();
        return result;
    }

    @Transactional
    public <S extends T> List<S> save(Iterable<S> entities) {

        List<S> result = new ArrayList<S>();

        if (entities == null) {
            return result;
        }

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @Transactional
    public void flush() {
        em.flush();
    }

    //
    // SimpleJpaRepository Delete
    // --------------------------------------------------------------------------------------------------------------------------------
    @Transactional
    public void delete(ID id) {

        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        T entity = findOne(id);

        if (entity == null) {
            throw new EmptyResultDataAccessException(String.format("No %s entity with id %s exists!", entityInformation.getJavaType(), id), 1);
        }

        delete(entity);
    }

    @Transactional
    public void delete(T entity) {

        Assert.notNull(entity, "The entity must not be null!");
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Transactional
    public void delete(Iterable<? extends T> entities) {

        Assert.notNull(entities, "The given Iterable of entities not be null!");

        for (T entity : entities) {
            delete(entity);
        }
    }

    @Transactional
    public void deleteInBatch(Iterable<T> entities) {

        Assert.notNull(entities, "The given Iterable of entities not be null!");

        if (!entities.iterator().hasNext()) {
            return;
        }

        applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), entities, em).executeUpdate();
    }

    @Transactional
    public void deleteAll() {
        for (T element : findAll()) {
            delete(element);
        }
    }

    @Transactional
    public void deleteAllInBatch() {
        em.createQuery(getDeleteAllQueryString()).executeUpdate();
    }

    //
    // SimpleJpaRepository Find
    // --------------------------------------------------------------------------------------------------------------------------------
    public T getOne(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        return em.getReference(getDomainClass(), id);
    }

    public T findOne(ID id) {

        Assert.notNull(id, ID_MUST_NOT_BE_NULL);

        Class<T> domainType = getDomainClass();

        if (metadata == null) {
            return em.find(domainType, id);
        }

        LockModeType type = metadata.getLockModeType();

        Map<String, Object> hints = getQueryHints();

        return type == null ? em.find(domainType, id, hints) : em.find(domainType, id, type, hints);
    }

    public T findOne(Specification<T> spec) {
        try {
            return getQuery(spec, (Sort) null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findAll() {
        return getQuery(null, (Sort) null).getResultList();
    }

    public List<T> findAll(Iterable<ID> ids) {

        if (ids == null || !ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        if (entityInformation.hasCompositeId()) {

            List<T> results = new ArrayList<T>();

            for (ID id : ids) {
                results.add(findOne(id));
            }

            return results;
        }

        ByIdsSpecification<T> specification = new ByIdsSpecification<T>(entityInformation);
        TypedQuery<T> query = getQuery(specification, (Sort) null);

        return query.setParameter(specification.parameter, ids).getResultList();
    }

    public List<T> findAll(Sort sort) {
        return getQuery(null, sort).getResultList();
    }

    public Page<T> findAll(Pageable pageable) {

        if (null == pageable) {
            return new PageImpl<T>(findAll());
        }

        return findAll(null, pageable);
    }

    public List<T> findAll(Specification<T> spec) {
        return getQuery(spec, (Sort) null).getResultList();
    }

    public Page<T> findAll(Specification<T> spec, Pageable pageable) {

        TypedQuery<T> query = getQuery(spec, pageable);
        return pageable == null ? new PageImpl<T>(query.getResultList()) : readPage(query, pageable, spec);
    }

    public List<T> findAll(Specification<T> spec, Sort sort) {
        return getQuery(spec, sort).getResultList();
    }

    public long count() {
        return em.createQuery(getCountQueryString(), Long.class).getSingleResult();
    }

    public long count(Specification<T> spec) {
        return executeCountQuery(getCountQuery(spec));
    }

    public boolean exists(ID id) {
        Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        if (entityInformation.getIdAttribute() == null) {
            return findOne(id) != null;
        }

        String placeholder = provider.getCountQueryPlaceholder();
        String entityName = entityInformation.getEntityName();
        Iterable<String> idAttributeNames = entityInformation.getIdAttributeNames();
        String existsQuery = QueryUtils.getExistsQueryString(entityName, placeholder, idAttributeNames);

        TypedQuery<Long> query = em.createQuery(existsQuery, Long.class);

        if (!entityInformation.hasCompositeId()) {
            query.setParameter(idAttributeNames.iterator().next(), id);
            return query.getSingleResult() == 1L;
        }

        for (String idAttributeName : idAttributeNames) {

            Object idAttributeValue = entityInformation.getCompositeIdAttributeValue(id, idAttributeName);

            boolean complexIdParameterValueDiscovered = idAttributeValue != null && !query.getParameter(idAttributeName).getParameterType().isAssignableFrom(idAttributeValue.getClass());

            if (complexIdParameterValueDiscovered) {

                // fall-back to findOne(id) which does the proper mapping for the parameter.
                return findOne(id) != null;
            }

            query.setParameter(idAttributeName, idAttributeValue);
        }

        return query.getSingleResult() == 1L;
    }

    /**
     * Reads the given {@link TypedQuery} into a {@link Page} applying the given {@link Pageable} and {@link Specification}.
     * 
     * @param query must not be {@literal null}.
     * @param spec can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected Page<T> readPage(TypedQuery<T> query, Pageable pageable, Specification<T> spec) {

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery(getCountQuery(spec));
        List<T> content = total > pageable.getOffset() ? query.getResultList() : Collections.<T> emptyList();

        return new PageImpl<T>(content, pageable, total);
    }

    /**
     * Creates a new {@link TypedQuery} from the given {@link Specification}.
     * 
     * @param spec can be {@literal null}.
     * @param pageable can be {@literal null}.
     * @return
     */
    protected TypedQuery<T> getQuery(Specification<T> spec, Pageable pageable) {

        Sort sort = pageable == null ? null : pageable.getSort();
        return getQuery(spec, sort);
    }

    /**
     * Creates a {@link TypedQuery} for the given {@link Specification} and {@link Sort}.
     * 
     * @param spec can be {@literal null}.
     * @param sort can be {@literal null}.
     * @return
     */
    protected TypedQuery<T> getQuery(Specification<T> spec, Sort sort) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(getDomainClass());

        Root<T> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sort != null) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    /**
     * Creates a new count query for the given {@link Specification}.
     * 
     * @param spec can be {@literal null}.
     * @return
     */
    protected TypedQuery<Long> getCountQuery(Specification<T> spec) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<T> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return em.createQuery(query);
    }

    /**
     * Returns a {@link Map} with the query hints based on the current {@link CrudMethodMetadata} and potential {@link EntityGraph} information.
     * 
     * @return
     */
    protected Map<String, Object> getQueryHints() {

        if (metadata.getEntityGraph() == null) {
            return metadata.getQueryHints();
        }

        Map<String, Object> hints = new HashMap<String, Object>();
        hints.putAll(metadata.getQueryHints());
        hints.putAll(Jpa21Utils.tryGetFetchGraphHints(em, (JpaEntityGraph) metadata.getEntityGraph(),entityClass));

        return hints;
    }

    /**
     * Applies the given {@link Specification} to the given {@link CriteriaQuery}.
     * 
     * @param spec can be {@literal null}.
     * @param query must not be {@literal null}.
     * @return
     */
    private <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<T> root = query.from(getDomainClass());

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    private TypedQuery<T> applyRepositoryMethodMetadata(TypedQuery<T> query) {

        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        TypedQuery<T> toReturn = type == null ? query : query.setLockMode(type);

        applyQueryHints(toReturn);

        return toReturn;
    }

    private void applyQueryHints(Query query) {

        for (Entry<String, Object> hint : getQueryHints().entrySet()) {
            query.setHint(hint.getKey(), hint.getValue());
        }
    }

    /**
     * Executes a count query and transparently sums up all values returned.
     * 
     * @param query must not be {@literal null}.
     * @return
     */
    private static Long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query);

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    /**
     * Specification that gives access to the {@link Parameter} instance used to bind the ids for {@link SimpleJpaRepository#findAll(Iterable)}. Workaround for OpenJPA not binding collections to in-clauses
     * correctly when using by-name binding.
     * 
     * @see https://issues.apache.org/jira/browse/OPENJPA-2018?focusedCommentId=13924055
     * @author Oliver Gierke
     */
    @SuppressWarnings("rawtypes")
    private static final class ByIdsSpecification<T> implements Specification<T> {

        private final JpaEntityInformation<T, ?> entityInformation;

        ParameterExpression<Iterable> parameter;

        public ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = cb.parameter(Iterable.class);
            return path.in(parameter);
        }
    }
}