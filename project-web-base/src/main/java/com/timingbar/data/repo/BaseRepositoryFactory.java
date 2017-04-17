package com.timingbar.data.repo;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import com.timingbar.data.repo.impl.BaseRepoImpl;

public class BaseRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {


    public BaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    
	@SuppressWarnings({ "rawtypes", "unchecked", "hiding" })
	@Override
    protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        return new BaseRepoImpl(entityInformation, entityManager);
    }
    

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseRepo.class;
    }

}