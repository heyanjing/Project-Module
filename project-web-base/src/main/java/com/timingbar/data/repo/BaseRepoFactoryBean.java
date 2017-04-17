package com.timingbar.data.repo;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;


// http://www.javacodegeeks.com/2012/08/customizing-spring-data-jpa-repository.html
public class BaseRepoFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new BaseRepositoryFactory<T, I>(entityManager);
    }

//    private static class BaseRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {
//
//
//        public BaseRepositoryFactory(EntityManager entityManager) {
//            super(entityManager);
//        }
//
//        
//		@Override
//        protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
//            JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
//            return new BaseRepoImpl(entityInformation, entityManager,null);
//        }
//        
//
//        @Override
//        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
//            return BaseRepo.class;
//        }
//
//    }
}