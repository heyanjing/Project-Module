package com.timingbar.data.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * <p>
 * 抽象实体基类，提供统一的ID，和相关的基本功能方法
 * <p>
 * 如果是如mysql这种自动生成主键的，请参考{@link BaseEntity}
 * <p/>
 * 子类只需要在类头上加 @SequenceGenerator(name="seq", sequenceName="你的sequence名字")
 * <p/>
 */
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class BaseOracleEntity<PK extends Serializable> extends AbstractEntity<PK> {

    @Id
    protected PK id;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    public PK getId() {
        return id;
    }

    public void setId(PK id) {
        this.id = id;
    }
}