package com.timingbar.data.jdbc;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.he.module.util.Reflections;

/**
 * 针对实体的jdbc，省去了传递entityClass参数。使用默认的DataSource，子类如果是其他dataSource则需要覆盖默认的DataSource。
 * 
 * @author YRain
 */
public class BaseJdbcDao<T> extends BaseJdbc {

    private Class<T> entityClass;

    public BaseJdbcDao() {
        this.entityClass = Reflections.getClassGenricType(getClass());
    }

    protected Class<T> getEntityClass() {
        return this.entityClass;
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql        
    ========================================================================================*/
    public List<T> find(String sql) {
        return super.find(sql, this.entityClass);
    }

    public List<T> find(String sql, Map<String, ?> params) {
        return super.find(sql, this.entityClass, params);
    }

    public List<T> find(String sql, Object... params) {
        return super.find(sql, this.entityClass, params);
    }

    public Page<T> page(String sql, int pageNumber, int pageSize, Map<String, ?> params) {
        return super.page(sql, this.entityClass, pageNumber, pageSize, params);
    }

    public Page<T> page(String sql, int pageNumber, int pageSize, Object... params) {
        return super.page(sql, this.entityClass, pageNumber, pageSize, params);
    }

}
