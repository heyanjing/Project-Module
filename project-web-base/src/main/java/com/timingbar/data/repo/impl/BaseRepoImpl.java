package com.timingbar.data.repo.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.he.module.util.Beans;
import com.timingbar.data.jdbc.JdbcTemplateSupport;
import com.timingbar.data.repo.BaseRepo;
import com.timingbar.data.util.Querys;

@NoRepositoryBean
@SuppressWarnings("serial")
public class BaseRepoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepo<T, ID>, Serializable {

    protected final EntityManager em;

    protected final JpaEntityInformation<T, ?> entityInformation;

    protected Class<?> springDataRepositoryInterface;

    protected Class<T> entityClass;
    protected String entityName;
    protected String entityIdName;

    protected DataSource dataSource;
    protected JdbcTemplateSupport jdbcTemplateSupport;

    public BaseRepoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this(entityInformation, entityManager, null);
    }

    public BaseRepoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, Class<?> springDataRepositoryInterface) {
        super(entityInformation, entityManager);
        this.em = entityManager;
        this.entityInformation = entityInformation;
        this.entityClass = this.entityInformation.getJavaType();
        this.entityName = this.entityInformation.getEntityName();
        this.entityIdName = this.entityInformation.getIdAttributeNames().iterator().next();

        this.springDataRepositoryInterface = springDataRepositoryInterface;

        this.dataSource = getDataSource();
        this.jdbcTemplateSupport = new JdbcTemplateSupport(this.dataSource);
    }

    protected Class<?> getSpringDataRepositoryInterface() {
        return springDataRepositoryInterface;
    }

    protected void setSpringDataRepositoryInterface(Class<?> springDataRepositoryInterface) {
        this.springDataRepositoryInterface = springDataRepositoryInterface;
    }

    public JdbcTemplateSupport getJdbcTemplateSupport() {
        return this.jdbcTemplateSupport;
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
        return getEntityManagerFactoryInfo().getDataSource();
    }

    @SuppressWarnings("unused")
    protected void getHibernamteProperties() {
        SessionFactory sessionFactory = ((HibernateEntityManagerFactory) getEntityManagerFactoryInfo()).getSessionFactory();
        Properties properties = ((SessionFactoryImpl) sessionFactory).getProperties();

        String url = (String) properties.get("hibernate.connection.url");
        String username = (String) properties.get("hibernate.connection.username");
        String password = (String) properties.get("hibernate.connection.password");
    }

    /**
     * 获取 Session
     */
    protected Session getSession() {
        return (Session) getEntityManager().getDelegate();
    }

    /**
     * 获取实体类型
     */
    protected Class<T> getEntityClass() {
        return this.entityInformation.getJavaType();
    }

    /**
     * 获取实体名称
     */
    protected String getEntityName() {
        return this.entityName;
    }

    /*************************************************************************************************
     * CREATE
     ************************************************************************************************/
    @Override
    @Transactional
    public <S extends T> S create(S entity) {
        return this.save(entity);
    }

    @Override
    @Transactional
    public T createAndFlush(T entity) {
        return this.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public <S extends T> List<S> create(Iterable<S> entities) {
        return this.save(entities);
    }

    @Override
    @Transactional
    public <S extends T> List<S> createAndFlush(Iterable<S> entities) {
        return saveAndFlush(entities);
    }

    @Override
    @Transactional
    public <S extends T> List<S> saveAndFlush(Iterable<S> entities) {
        List<S> result = this.save(entities);
        this.flush();
        return result;
    }

    /*************************************************************************************************
     * UPDATE
     ************************************************************************************************/
    @Override
    @Transactional
    public <S extends T> S update(S entity) {
        return this.save(entity);
    }

    @Override
    @Transactional
    public <S extends T> List<S> update(Iterable<S> entities) {
        return this.save(entities);
    }

    /*************************************************************************************************
     * DELETE
     ************************************************************************************************/
    @Override
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

    @Override
    @Transactional
    public void remove(ID[] ids) {
        this.delete(ids);
    }

    @Override
    @Transactional
    public void remove(T entity) {
        this.delete(entity);
    }

    @Override
    @Transactional
    public void remove(ID id) {
        super.delete(id);
    }

    @Override
    @Transactional
    public void remove(Iterable<T> entities) {
        this.delete(entities);
    }

    /*************************************************************************************************
     * GET
     ************************************************************************************************/

    @Override
    public T get(ID id) {
        return null != id ? this.findOne(id) : null;
    }

    @Override
    public T get(String key, Object value) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.get(filter);
    }

    @Override
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

    /*************************************************************************************************
     * FIND
     ************************************************************************************************/
    @Override
    public List<T> find() {
        return this.findAll();
    }

    @Override
    public List<T> find(String key, Object value) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.find(filter);
    }

    @Override
    public List<T> find(int size) {
        return this.page(1, size).getContent();
    }

    @Override
    public List<T> find(Map<String, ?> filter) {
        return this.find(filter, null);
    }

    @Override
    public List<T> find(Map<String, ?> filter, int size) {
        return this.find(filter, null, size);
    }

    @Override
    public List<T> find(Map<String, ?> filter, Map<String, String> like) {
        return this.find(filter, like, null);
    }

    @Override
    public List<T> find(Map<String, ?> filter, Map<String, String> like, int size) {
        return this.find(filter, like, null, size);
    }

    @Override
    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort) {
        return super.findAll(Querys.createMapFilter(this.entityClass, filter, like), Querys.createSortFilter(sort));
    }

    @Override
    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int size) {
        return this.page(filter, like, sort, 1, size).getContent();
    }

    /*************************************************************************************************
     * PAGE
     ************************************************************************************************/
    @Override
    public Page<T> page(int pageNumber, int pageSize) {
        return this.page(null, pageNumber, pageSize);
    }

    @Override
    public Page<T> page(String key, Object value, int pageNumber, int pageSize) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.page(filter, pageNumber, pageSize);
    }

    @Override
    public Page<T> page(Map<String, ?> filter, int pageNumber, int pageSize) {
        return this.page(filter, null, null, pageNumber, pageSize);
    }

    @Override
    public Page<T> page(Map<String, ?> filter, Map<String, String> like, int pageNumber, int pageSize) {
        return this.page(filter, like, null, pageNumber, pageSize);
    }

    @Override
    public Page<T> page(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int pageNumber, int pageSize) {
        return super.findAll(Querys.createMapFilter(this.entityClass, filter, like), new PageRequest(pageNumber - 1, pageSize, Querys.createSortFilter(sort)));
    }

    /*************************************************************************************************
     * OTHER
     ************************************************************************************************/
    @Override
    public Long getCount() {
        return super.count();
    }

    @Override
    public Long getCount(String key, Object value) {
        Map<String, Object> filter = Maps.newHashMap();
        filter.put(key, value);
        return this.getCount(filter);
    }

    @Override
    public Long getCount(Map<String, ?> filter) {
        return this.getCount(filter, null);
    }

    @Override
    public Long getCount(Map<String, ?> filter, Map<String, String> like) {
        return this.count(Querys.createMapFilter(this.entityClass, filter, like));
    }

    @Override
    public boolean isExists(ID id) {
        return this.exists(id);
    }

    @Override
    public boolean isExists(String key, Object value) {
        return (this.getCount(key, value) > 0);
    }

    @Override
    public boolean isExists(Map<String, ?> filter) {
        return (this.getCount(filter) > 0);
    }

    @Override
    public boolean isExists(Map<String, ?> filter, Map<String, String> like) {
        return (this.getCount(filter, like) > 0);
    }

    @Override
    @Transactional
    public void batchInsert(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            em.persist(list.get(i));
            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    @Override
    @Transactional
    public void batchUpdate(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            em.merge(list.get(i));
            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    //
    // 每个实体少写一个UserJdbcDao.java的临时解决办法
    // 通过jdbcTemplateSupport扩展jdbc操作，
    // --------------------------------------------------------------------------------------------------------------------------------
    /*----------------------------------------------------------------------------------------
    |            find by sql
    ========================================================================================*/
    public List<T> findBySql(String sql) {
        return this.jdbcTemplateSupport.find(sql, entityClass);
    }

    public List<T> findBySql(String sql, Map<String, ?> params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    public List<T> findBySql(String sql, Object... params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Map<String, ?> params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Object... params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql and entityClass for DTO        
    ========================================================================================*/
    public <E> List<E> findBySql(String sql, Class<E> entityClass) {
        return this.jdbcTemplateSupport.find(sql, entityClass);
    }

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Map<String, ?> params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Object... params) {
        return this.jdbcTemplateSupport.find(sql, entityClass, params);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
        return this.jdbcTemplateSupport.page(sql, entityClass, pageNumber, pageSize, params);
    }
}