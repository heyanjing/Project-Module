package com.timingbar.data.jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.timingbar.data.util.Sqls;

public class JdbcTemplateSupport extends JdbcTemplate {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcTemplateSupport(DataSource dataSource) {
        super(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    //
    // find by sql and entityClass for DTO
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> List<E> find(String sql, Class<E> entityClass) {
        return this.query(sql, BeanPropertyRowMapper.newInstance(entityClass));
    }

    public <E> List<E> find(String sql, Class<E> entityClass, Map<String, ?> params) {
        return this.namedParameterJdbcTemplate.query(sql, params, BeanPropertyRowMapper.newInstance(entityClass));
    }

    public <E> List<E> find(String sql, Class<E> entityClass, Object... params) {
        return super.query(sql, BeanPropertyRowMapper.newInstance(entityClass), params);
    }

    //
    // page by sql and entityClass for DTO
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize) {
        return this.page(sql, entityClass, pageNumber, pageSize, Maps.newHashMap());
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
        int count = this.queryForCount(sql, params);
        List<E> entities = Lists.newArrayList();
        if (count > 0) {
            entities = this.find(Sqls.buildPaginateSql(sql, pageNumber, pageSize), entityClass, params);
        }
        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
        int count = this.queryForCount(sql, params);
        List<E> entities = Lists.newArrayList();
        if (count > 0) {
            entities = this.find(Sqls.buildPaginateSql(sql, pageNumber, pageSize), entityClass, params);
        }
        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
    }

    //
    // find by sql and RowMapper
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> List<E> find(String sql, RowMapper<E> mapper) {
        return super.query(sql, mapper);
    }

    public <E> List<E> find(String sql, RowMapper<E> mapper, Map<String, ?> params) {
        return this.namedParameterJdbcTemplate.query(sql, params, mapper);
    }

    public <E> List<E> find(String sql, RowMapper<E> mapper, Object... params) {
        return super.query(sql, mapper, params);
    }

    //
    // page by sql and RowMapper
    // --------------------------------------------------------------------------------------------------------------------------------
    public <E> Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize) {
        return this.page(sql, mapper, pageNumber, pageSize, new Object[] {});
    }

    public <E> Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Map<String, ?> params) {
        int count = this.queryForCount(sql, params);
        List<E> entities = Lists.newArrayList();
        if (count > 0) {
            entities = this.find(Sqls.buildPaginateSql(sql, pageNumber, pageSize), mapper, params);
        }
        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
    }

    public <E> Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Object... params) {
        int count = this.queryForCount(sql, params);
        List<E> entities = Lists.newArrayList();
        if (count > 0) {
            entities = this.find(Sqls.buildPaginateSql(sql, pageNumber, pageSize), mapper, params);
        }
        return new PageImpl<E>(entities, new PageRequest(pageNumber - 1, pageSize), count);
    }

    //
    // query for count
    // --------------------------------------------------------------------------------------------------------------------------------
    private Integer queryForCount(String sql, Map<String, ?> params) {
        return this.namedParameterJdbcTemplate.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
    }

    private Integer queryForCount(String sql, Object... params) {
        return super.queryForObject(Sqls.buildCountSql(sql), params, Integer.class);
    }

}
