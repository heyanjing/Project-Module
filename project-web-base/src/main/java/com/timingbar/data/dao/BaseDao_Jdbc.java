package com.timingbar.data.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.timingbar.data.jdbc.BaseJdbcDao;

/**
 * 每个实体少写一个UserJdbcDao.java的临时解决办法，保留方法
 * 
 * @author YRain
 */
@Component
public abstract class BaseDao_Jdbc<T, ID extends Serializable> {

    @Autowired
    protected BaseJdbcDao<T> jdbcDao;

    /*----------------------------------------------------------------------------------------
    |            find by sql         
    ========================================================================================*/
    public List<T> find(String sql) {
        return jdbcDao.find(sql);
    }

    public List<T> find(String sql, Map<String, ?> params) {
        return jdbcDao.find(sql, params);
    }

    public List<T> find(String sql, Object... params) {
        return jdbcDao.find(sql, params);
    }

    public Page<T> page(String sql, int pageNumber, int pageSize, Map<String, ?> params) {
        return jdbcDao.page(sql, pageNumber, pageSize, params);
    }

    public Page<T> page(String sql, int pageNumber, int pageSize, Object... params) {
        return jdbcDao.page(sql, pageNumber, pageSize, params);
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql and entityClass for DTO        
    ========================================================================================*/
    public <E> List<E> find(String sql, Class<E> entityClass) {
        return jdbcDao.find(sql, entityClass);
    }

    public <E> List<E> find(String sql, Class<E> entityClass, Map<String, ?> params) {
        return jdbcDao.find(sql, entityClass, params);
    }

    public <E> List<E> find(String sql, Class<E> entityClass, Object... params) {
        return jdbcDao.find(sql, entityClass, params);
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
        return jdbcDao.page(sql, entityClass, pageNumber, pageSize, params);
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
        return jdbcDao.page(sql, entityClass, pageNumber, pageSize, params);
    }

}
