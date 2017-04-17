package com.timingbar.data.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Jdbc查询,使用默认的DataSource，子类如果是其他dataSource则需要覆盖默认的DataSource。
 * 可以注解或者在xml文件中注入dataSource，如下：
 * 注解：
 * 
 * <pre>
 * &#064;Repository
 * public class WellJdbc extends BaseJdbc {
 * 
 *     &#064;Autowired
 *     &#064;Qualifier(&quot;wellDs&quot;)
 *     public void setDataSource(DataSource dataSource) {
 *         super.setDataSource(dataSource);
 *     }
 * 
 *     // 使用@Resource或者@Autowired皆可
 *     // @Resource(name = &quot;wellDs&quot;)
 *     // public void setDataSource(DataSource dataSource) {
 *     // super.setDataSource(dataSource);
 *     // }
 * 
 * }
 * </pre>
 * 
 * xml：
 * 
 * <pre>
 * <bean id="wellJdbc" class="com.timingbar.showcase.well.dao.WellJdbc">
 *      <property name="dataSource" ref="wellDs"/>
 * </bean>
 * </pre>
 * 
 * @author YRain
 */
public class BaseJdbc {

    private JdbcTemplateSupport jdbcTemplateSupport;

    public BaseJdbc() {
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplateSupport = new JdbcTemplateSupport(dataSource);
    }

    public JdbcTemplateSupport getJdbcTemplateSupport() {
        return jdbcTemplateSupport;
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql and entityClass           
    ========================================================================================*/
    public <E> List<E> find(String sql, Class<E> entityClass) {
        return getJdbcTemplateSupport().find(sql, entityClass);
    }

    public <E> List<E> find(String sql, Class<E> entityClass, Map<String, ?> params) {
        return getJdbcTemplateSupport().find(sql, entityClass, params);
    }

    public <E> List<E> find(String sql, Class<E> entityClass, Object... params) {
        return getJdbcTemplateSupport().find(sql, entityClass, params);
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Map<String, ?> params) {
        return getJdbcTemplateSupport().page(sql, entityClass, pageNumber, pageSize, params);
    }

    public <E> Page<E> page(String sql, Class<E> entityClass, int pageNumber, int pageSize, Object... params) {
        return getJdbcTemplateSupport().page(sql, entityClass, pageNumber, pageSize, params);
    }

    /*----------------------------------------------------------------------------------------
    |            find by sql and RowMapper           
    ========================================================================================*/
    public <E> List<E> find(String sql, RowMapper<E> mapper) {
        return getJdbcTemplateSupport().find(sql, mapper);
    }

    public <E> List<E> find(String sql, RowMapper<E> mapper, Map<String, ?> params) {
        return getJdbcTemplateSupport().find(sql, mapper, params);
    }

    public <E> List<E> find(String sql, RowMapper<E> mapper, Object... params) {
        return getJdbcTemplateSupport().find(sql, mapper, params);
    }

    public <E> Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize);
    }

    public <E> Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Map<String, ?> params) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize, params);
    }

    public <E> Page<E> page(String sql, RowMapper<E> mapper, int pageNumber, int pageSize, Object... params) {
        return getJdbcTemplateSupport().page(sql, mapper, pageNumber, pageSize, params);
    }

    /*----------------------------------------------------------------------------------------
    |            some other       
    ========================================================================================*/
    public void execute(String sql) {
        getJdbcTemplateSupport().execute(sql);
    }
}
