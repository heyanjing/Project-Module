package com.he.module.spring.data;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class BaseRepoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepo<T, ID> {
    private final EntityManager em;
    private final Class<T>      entityClass;
    private final String        entityName;

    // protected DataSource dataSource;
    // protected JdbcTemplateSupport jdbcTemplateSupport;

    public BaseRepoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
        this.entityClass = entityInformation.getJavaType();
        this.entityName = entityInformation.getEntityName();

        // this.dataSource = ((EntityManagerFactoryInfo) entityManager.getEntityManagerFactory()).getDataSource();
        // this.jdbcTemplateSupport = new JdbcTemplateSupport(this.dataSource);
    }

    public BaseRepoImpl(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }

    @Override
    public T get(ID id) {
        System.out.println(em);
        System.out.println(entityClass);
        System.out.println(entityName);
        return super.findOne(id);
    }

    // @Override
    // public JdbcTemplateSupport getJdbcTemplateSupport() {
    // return this.jdbcTemplateSupport;
    // }

}
