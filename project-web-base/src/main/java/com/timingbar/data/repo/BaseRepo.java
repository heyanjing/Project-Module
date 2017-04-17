package com.timingbar.data.repo;

import com.timingbar.data.jdbc.JdbcTemplateSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * http://static.springsource.org/spring-data/data-jpa/docs/current/reference/html/repositories.html#d0e724
 * 
 * @author Administrator
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface BaseRepo<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * 获取JdbcTemplateSupport
     */
    public JdbcTemplateSupport getJdbcTemplateSupport();

    /**
     * 新建实体
     */
    public <S extends T> S create(S entity);

    /**
     * 新建实体并提交
     */
    public T createAndFlush(T entity);

    /**
     * 新建实体
     */
    public <S extends T> List<S> create(Iterable<S> entities);

    /**
     * 新建实体并提交
     */
    public <S extends T> List<S> createAndFlush(Iterable<S> entities);

    /**
     * 保存实体并提交
     */
    public <S extends T> List<S> saveAndFlush(Iterable<S> entities);

    /**
     * 更新实体
     */
    public <S extends T> S update(S entity);

    /**
     * 更新实体
     */
    public <S extends T> List<S> update(Iterable<S> entities);

    /**
     * 删除实体
     */
    public void delete(ID[] ids);

    /**
     * 删除实体
     */
    public void remove(ID id);

    /**
     * 删除实体
     */
    public void remove(T entity);

    /**
     * 删除实体
     */
    public void remove(Iterable<T> entities);

    /**
     * 删除实体
     */
    public void remove(ID[] ids);

    /**
     * 获得指定ID的实体
     */
    public T get(ID id);

    /**
     * 根据key和value获得实体
     */
    public T get(String key, Object value);

    /**
     * 根据filter获得实体
     */
    public T get(Map<String, ?> filter);

    /**
     * 查询所有
     */
    public List<T> find();

    /**
     * 根据key,value查询 查询所有
     */
    public List<T> find(String key, Object value);

    /**
     * 查询指定个数
     */
    public List<T> find(int size);

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter);

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, int size);

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like);

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like, int size);

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort);

    /**
     * 查询方法
     */
    public List<T> find(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int size);

    /**
     * 分页方法
     */
    public Page<T> page(int pageNumber, int pageSize);

    /**
     * 分页方法
     */
    public Page<T> page(String key, Object value, int pageNumber, int pageSize);

    /**
     * 分页方法
     */
    public Page<T> page(Map<String, ?> filter, int pageNumber, int pageSize);

    /**
     * 分页方法
     * 
     * @param filter:匹配:=
     * @param like:匹配:like
     */
    public Page<T> page(Map<String, ?> filter, Map<String, String> like, int pageNumber, int pageSize);

    /**
     * 分页方法
     * 
     * @param filter:匹配:=
     * @param like:匹配:like
     * @param sort:匹配:order by
     */
    public Page<T> page(Map<String, ?> filter, Map<String, String> like, Map<String, String> sort, int pageNumber, int pageSize);

    /**
     * 获取记录数
     */
    public Long getCount();

    /**
     * 获取记录数
     */
    public Long getCount(String key, Object value);

    /**
     * 获取记录数
     */
    public Long getCount(Map<String, ?> filter);

    /**
     * 获取记录数
     */
    public Long getCount(Map<String, ?> filter, Map<String, String> like);

    /**
     * 判断是否存在
     */
    public boolean isExists(ID id);

    /**
     * 判断是否存在
     */
    public boolean isExists(String key, Object value);

    /**
     * 根据filter判断是否存在
     */
    public boolean isExists(Map<String, ?> filter);

    /**
     * 根据filter判断是否存在
     */
    public boolean isExists(Map<String, ?> filter, Map<String, String> like);

    /**
     * 批量插入
     */
    public void batchInsert(List<T> list);

    /**
     * 批量更新
     */
    public void batchUpdate(List<T> list);

    /*----------------------------------------------------------------------------------------
    |            find by sql
    ========================================================================================*/
    public List<T> findBySql(String sql);

    public List<T> findBySql(String sql, Map<String, ?> params);

    public List<T> findBySql(String sql, Object... params);

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Map<String, ?> params);

    public Page<T> pageBySql(String sql, int pageNumber, int pageSize, Object... params);

    /*----------------------------------------------------------------------------------------
    |            find by sql and entityClass for DTO
    ========================================================================================*/
    public <E> List<E> findBySql(String sql, Class<E> entityClass);

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Map<String, ?> params);

    public <E> List<E> findBySql(String sql, Class<E> entityClass, Object... params);

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params);

    public <E> Page<E> pageBySql(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params);

}