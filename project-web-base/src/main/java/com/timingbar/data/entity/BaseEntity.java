package com.timingbar.data.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 统一定义id的entity基类.
 * <p>
 * 抽象实体基类，提供统一的ID，和相关的基本功能方。如果是Oracle需要每个Entity独立定义id的SEQUCENCE时。请参考{@link BaseOracleEntity}
 * <p>
 */
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseEntity<ID extends Serializable> extends AbstractEntity<ID> {

    @Id
    protected ID id;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

}
