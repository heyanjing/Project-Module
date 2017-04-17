package com.he.module.spring.data;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class BaseRepoFactory extends JpaRepositoryFactory {

    public BaseRepoFactory(EntityManager entityManager) {
        super(entityManager);
    }

    // 1.9.2版本用这个(jrebel会报个异常)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        return new BaseRepoImpl(entityInformation, entityManager);
    }

    // 1.8.0版本用这个
    // @SuppressWarnings({ "rawtypes", "unchecked" })
    // @Override
    // protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryMetadata metadata, EntityManager entityManager) {
    // JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
    // return new BaseDaoImpl(entityInformation, entityManager);
    // }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseRepoImpl.class;
    }
}
