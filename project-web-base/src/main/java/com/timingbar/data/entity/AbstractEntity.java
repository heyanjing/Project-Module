package com.timingbar.data.entity;

import java.io.Serializable;

import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.he.module.bean.BaseBean;

@SuppressWarnings("serial")
public abstract class AbstractEntity<ID extends Serializable> extends BaseBean implements Persistable<ID> {

    public abstract ID getId();

    // 屏蔽掉，FastJSON反序列化的时候会出错
    // public abstract void setId(final ID id);

    @Transient
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isNew() {
        return null == getId();
    }

    @Override
    public boolean equals(Object obj) {

        if (null == obj) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        AbstractEntity<?> that = (AbstractEntity<?>) obj;

        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {

        int hashCode = 17;

        hashCode += null == getId() ? 0 : getId().hashCode() * 31;

        return hashCode;
    }

}
