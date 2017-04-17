package com.timingbar.data.service;

import com.timingbar.data.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class BaseService<T, ID extends Serializable> {

    @Autowired
    protected BaseDao<T, ID> dao;

    public BaseDao<T, ID> getDao() {
        return dao;
    }

    public void setDao(BaseDao<T, ID> dao) {
        this.dao = dao;
    }

    /*************************************************************************************************
     * SAVE
     ************************************************************************************************/
    /**
     * 保存实体
     */
    public <S extends T> S save(S entity) {
        return dao.save(entity);
    }

    /**
     * 保存实体(多个)
     */
    public <S extends T> List<S> save(Iterable<S> entities) {
        return dao.save(entities);
    }

    /**
     * 保存实体并提交
     */
    public T saveAndFlush(T entity) {
        return dao.saveAndFlush(entity);
    }

    /**
     * 保存实体并提交(多个)
     */
    public <S extends T> List<S> saveAndFlush(Iterable<S> entities) {
        return dao.saveAndFlush(entities);
    }

    /*************************************************************************************************
     * CREATE
     ************************************************************************************************/
    /**
     * 新建实体
     */
    public <S extends T> S create(S entity) {
        return dao.create(entity);
    }

    /**
     * 新建实体并提交
     */
    public T createAndFlush(T entity) {
        return dao.createAndFlush(entity);
    }

    /**
     * 新建实体(多个)
     */
    public <S extends T> List<S> create(Iterable<S> entities) {
        return dao.create(entities);
    }

    /**
     * 新建实体并提交(多个)
     */
    public <S extends T> List<S> createAndFlush(Iterable<S> entities) {
        return dao.createAndFlush(entities);
    }

    /*************************************************************************************************
     * UPDATE
     ************************************************************************************************/
    /**
     * 更新实体
     */
    public <S extends T> S update(S entity) {
        return dao.update(entity);
    }

    /**
     * 更新实体（多个）
     */
    public <S extends T> List<S> update(Iterable<S> entities) {
        return dao.update(entities);
    }

    /*************************************************************************************************
     * DELETE
     ************************************************************************************************/
    /**
     * 删除实体
     */
    public void delete(ID id) {
        dao.delete(id);
    }

    /**
     * 删除实体
     */
    public void delete(ID[] ids) {
        dao.delete(ids);
    }

    /**
     * 删除实体
     */
    public void delete(T entity) {
        dao.delete(entity);
    }

    /**
     * 删除实体
     */
    public void delete(Iterable<T> entities) {
        dao.delete(entities);
    }

    /**
     * 删除实体
     */
    public void remove(ID id) {
        dao.remove(id);
    }

    /**
     * 删除实体
     */
    public void remove(ID[] ids) {
        dao.remove(ids);
    }

    /**
     * 删除实体
     */
    public void remove(T entity) {
        dao.remove(entity);
    }

    /**
     * 删除实体
     */
    public void remove(Iterable<T> entities) {
        dao.remove(entities);
    }

    /*************************************************************************************************
     * GET
     ************************************************************************************************/
    /**
     * 根据条件查询一个
     */
    public T get(ID id) {
        return dao.findOne(id);
    }

    /**
     * 根据条件查询一个
     */
    public T get(String key, Object value) {
        return dao.get(key, value);
    }

    /**
     * 根据条件查询一个
     */
    public T get(Map<String, ?> filter) {
        return dao.get(filter);
    }

    /*************************************************************************************************
     * FIND
     ************************************************************************************************/
    /**
     * 查询所有
     */
    public List<T> find() {
        return dao.find();
    }

    /**
     * 查询指定个数
     */
    public List<T> find(int size) {
        return dao.find(size);
    }

    /**
     * 根据条件查询
     */
    public List<T> find(String key, Object value) {
        return dao.find(key, value);
    }

    /**
     * 根据条件查询
     */
    public List<T> find(Map<String, ?> filter) {
        return dao.find(filter);
    }

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, int size) {
        return dao.find(filter, size);
    }

    /**
     * 根据条件查询
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like) {
        return dao.find(filter, like);
    }

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like, int size) {
        return dao.find(filter, like, size);
    }

    /**
     * 根据条件查询
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort) {
        return dao.find(filter, like, sort);
    }

    /**
     * 根据条件查询
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int size) {
        return dao.find(filter, like, sort, size);
    }

    /*************************************************************************************************
     * LIST
     ************************************************************************************************/

    /**
     * 查询所有
     */
    public List<T> list() {
        return this.find();
    }

    /**
     * 查询指定个数
     */
    public List<T> list(int size) {
        return this.find(size);
    }

    /**
     * 根据条件查询
     */
    public List<T> list(String key, Object value) {
        return this.find(key, value);
    }

    /**
     * 根据条件查询
     */
    public List<T> list(Map<String, ?> filter) {
        return this.find(filter);
    }

    /**
     * 查询方法
     */
    public List<T> list(Map<String, ?> filter, int size) {
        return this.find(filter, size);
    }

    /**
     * 根据条件查询
     */
    public List<T> list(Map<String, ?> filter, Map<String, String> like) {
        return this.find(filter, like);
    }

    /**
     * 查询方法
     */
    public List<T> list(Map<String, ?> filter, Map<String, String> like, int size) {
        return this.find(filter, like, size);
    }

    /**
     * 根据条件查询
     */
    public List<T> list(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort) {
        return this.find(filter, like, sort);
    }

    /**
     * 根据条件查询
     */
    public List<T> list(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int size) {
        return this.find(filter, like, sort, size);
    }

    /*************************************************************************************************
     * PAGE
     ************************************************************************************************/
    /**
     * 根据条件分页查询
     */
    public Page<T> page(int pageNumber, int pageSize) {
        return dao.page(pageNumber, pageSize);
    }

    /**
     * 根据条件分页查询
     */
    public Page<T> page(String key, Object value, int pageNumber, int pageSize) {
        return dao.page(key, value, pageNumber, pageSize);
    }

    /**
     * 根据条件分页查询
     */
    public Page<T> page(Map<String, ?> filter, int pageNumber, int pageSize) {
        return dao.page(filter, pageNumber, pageSize);
    }

    /**
     * 根据条件分页查询
     */
    public Page<T> page(Map<String, ?> filter, Map<String, String> like, int pageNumber, int pageSize) {
        return dao.page(filter, like, pageNumber, pageSize);
    }

    /**
     * 根据条件分页查询
     */
    public Page<T> page(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int pageNumber, int pageSize) {
        return dao.page(filter, like, sort, pageNumber, pageSize);
    }

    /*************************************************************************************************
     * OTHER
     ************************************************************************************************/
    /**
     * 获取所有记录数
     */
    public Long getCount() {
        return dao.getCount();
    }

    /**
     * 根据条件获取记录数
     */
    public Long getCount(String key, Object value) {
        return dao.getCount(key, value);
    }

    /**
     * 根据条件获取记录数
     */
    public Long getCount(Map<String, ?> filter) {
        return dao.getCount(filter);
    }

    /**
     * 根据条件获取记录数
     */
    public Long getCount(Map<String, ?> filter, Map<String, String> like) {
        return dao.getCount(filter, like);
    }

    /**
     * 根据条件判断是否存在
     */
    public boolean isExists(ID id) {
        return dao.isExists(id);
    }

    /**
     * 根据条件判断是否存在
     */
    public boolean isExists(String key, Object value) {
        return dao.isExists(key, value);
    }

    /**
     * 根据条件判断是否存在
     */
    public boolean isExists(Map<String, ?> filter) {
        return dao.isExists(filter);
    }

    /**
     * 根据条件判断是否存在
     */
    public boolean isExists(Map<String, ?> filter, Map<String, String> like) {
        return dao.isExists(filter, like);
    }

    /**
     * 刷新并提交
     */
    public void flush() {
        dao.flush();
    }
}