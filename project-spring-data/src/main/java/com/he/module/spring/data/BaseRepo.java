package com.he.module.spring.data;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepo<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    /*
     * 声明持久层的接口，该接口继承 Repository，Repository 是一个标记型接口，它不包含任何方法，当然如果有需要，
     * Spring Data 也提供了若干 Repository 子接口，其中定义了一些常用的增删改查，以及分页相关的方法。
     * 
     * 在接口中声明需要的业务方法。Spring Data 将根据给定的策略（具体策略稍后讲解）来为其生成实现代码。
     * 
     * 在 Spring 配置文件中增加一行声明，让 Spring 为声明的接口创建代理对象。配置了 <jpa:repositories> 后，
     * Spring 初始化容器时将会扫描 base-package 指定的包目录及其子目录，为继承 Repository 或其子接口的接口创建代理对象，
     * 并将代理对象注册为 Spring Bean，业务层便可以通过 Spring 自动封装的特性来直接使用该对象。
     * 
     */

    /*
     * JpaRepository和JpaSpecificationExecutor 接口的实现类为 SimpleJpaRepository
     * 所以BaseDao可以自定义一些方法，然后BaseDao的实现类继承SimpleJpaRepository类实现BaseDao即可
     * 注意：SimpleJpaRepository类中有2个构造方法
     * 
     * 比如说BaseDao接口中有个get接口方法
     * 
     */
    /**
     * 获取JdbcTemplateSupport
     */
    // public JdbcTemplateSupport getJdbcTemplateSupport();

    public T get(ID id);
}
