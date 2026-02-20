package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractUserCategoryEntity {
    @Id

    private String object_id;

    public String getObject_id() {
        return object_id;
    }

    public boolean isPersisted() {
        return object_id != null;
    }

    @Override
    public int hashCode() {
        if (getObject_id() != null) {
            return getObject_id().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractUserCategoryEntity other = (AbstractUserCategoryEntity) obj;
        if (getObject_id() == null || other.getObject_id() == null) {
            return false;
        }
        return getObject_id().equals(other.getObject_id());
    }
}
