package com.he.module.entity;

import com.he.module.bean.BaseBean;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class BaseEntity<ID extends Serializable> extends BaseBean {
    public abstract ID getId();

    public abstract void setId(ID id);
}
