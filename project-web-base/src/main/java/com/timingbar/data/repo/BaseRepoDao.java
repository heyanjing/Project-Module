package com.timingbar.data.repo;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.he.module.Constants;
import com.he.module.util.Reflections;
import com.he.module.util.Strings;
import com.timingbar.data.jdbc.JdbcTemplateSupport;
import com.timingbar.data.util.Querys;

/**
 * BaseRepositoryCustomDao
 * 如果是多数据源的项目，则子类继承并注入不同的em
 * 
 * @author YRain
 */
@Transactional
public class BaseRepoDao<T> {

    protected EntityManager entityManager;

    protected JpaEntityInformation<T, ?> entityInformation;

    // Entity Info
    protected Class<T> entityClass;
    protected String entityName;
    protected String entityIdName;

    // Data Info
    protected DataSource dataSource;
    protected JdbcTemplateSupport jdbcTemplateSupport;
    protected Session session;

    @PostConstruct
    public void postConstruct() {
        // Entity Info
        this.entityClass = Reflections.getClassGenricType(getClass());
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(entityClass, entityManager);
        this.entityName = this.entityInformation.getEntityName();
        this.entityIdName = this.entityInformation.getIdAttributeNames().iterator().next();

        // Data Info
        this.session = (Session) getEntityManager().getDelegate();
        this.dataSource = getEntityManagerFactoryInfo().getDataSource();
        this.jdbcTemplateSupport = new JdbcTemplateSupport(this.dataSource);
    }

    /**
     * 必须将注解@PersistenceContext放在set方法上，若注解在field上会报错说有多个entityManager
     * entityManager默认名称为em
     */
    @PersistenceContext(unitName = "em")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    private EntityManagerFactoryInfo getEntityManagerFactoryInfo() {
        return (EntityManagerFactoryInfo) this.entityManager.getEntityManagerFactory();
    }

    protected DataSource getDataSource() {
        return this.dataSource;
    }

    protected JdbcTemplateSupport getJdbcTemplateSupport() {
        return this.jdbcTemplateSupport;
    }

    protected Session getSession() {
        return this.session;
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

    /*----------------------------------------------------------------------------------------
    |            find by hql     
    ========================================================================================*/
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

    /*----------------------------------------------------------------------------------------
    |            execute 
    ========================================================================================*/
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

    private Query createHqlQuery(String hql, Object params) {
        return Querys.createHqlQuery(getSession(), hql, params);
    }

    private Query createSqlQuery(String sql, Object params) {
        return Querys.createSqlQuery(getSession(), sql, params);
    }

    /*----------------------------------------------------------------------------------------
    |            copy from BaseJdbc and BaseJdbcDao 
    |
    ========================================================================================*/

    /*----------------------------------------------------------------------------------------
    |            find by sql           
    ========================================================================================*/
    public List<T> findBySql(String sql) {
        return getJdbcTemplateSupport().find(sql, this.entityClass);
    }

    public List<T> findBySql(String sql, Map<String, ?> params) {
        return getJdbcTemplateSupport().find(sql, this.entityClass, params);
    }

    public List<T> findBySql(String sql, Object... params) {
        return getJdbcTemplateSupport().find(sql, this.entityClass, params);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize) {
        return getJdbcTemplateSupport().page(sql, this.entityClass, pageNumber, pageSize);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Map<String, ?> params) {
        return getJdbcTemplateSupport().page(sql, this.entityClass, pageNumber, pageSize, params);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Object... params) {
        return getJdbcTemplateSupport().page(sql, this.entityClass, pageNumber, pageSize, params);
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql and entityClass           
    ========================================================================================*/
    public <E> List<E> findBySql(String sql, Class<E> entityClass) {
        return getJdbcTemplateSupport().find(sql, entityClass);
    }

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Map<String, ?> params) {
        return getJdbcTemplateSupport().find(sql, entityClass, params);
    }

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Object... params) {
        return getJdbcTemplateSupport().find(sql, entityClass, params);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize) {
        return getJdbcTemplateSupport().page(sql, entityClass, pageNumber, pageSize);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
        return getJdbcTemplateSupport().page(sql, entityClass, pageNumber, pageSize, params);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
        return getJdbcTemplateSupport().page(sql, entityClass, pageNumber, pageSize, params);
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql and RowMapper           
    ========================================================================================*/
    public <E> List<E> findBySql(String sql, RowMapper<E> mapper) {
        return getJdbcTemplateSupport().find(sql, mapper);
    }

    public <E> List<E> findBySql(String sql, RowMapper<E> mapper, Map<String, ?> params) {
        return getJdbcTemplateSupport().find(sql, mapper, params);
    }

    public <E> List<E> findBySql(String sql, RowMapper<E> mapper, Object... params) {
        return getJdbcTemplateSupport().find(sql, mapper, params);
    }

    public <E> Page<E> pageBySql(String sql, RowMapper<E> mapper, int pageNumber, int pageSize) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize);
    }

    public <E> Page<E> pageBySql(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Map<String, ?> params) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize, params);
    }

    public <E> Page<E> pageBySql(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Object... params) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize, params);
    }

}
