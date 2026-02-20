package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractAuditEntity {
    @Id
    @SequenceGenerator(name = "RM_AUDIT_seq", sequenceName = "RM_AUDIT_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RM_AUDIT_seq")

    private Long id;

    public Long getId() {
        return id;
    }

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
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
        AbstractAuditEntity other = (AbstractAuditEntity) obj;
        if (getId() == null || other.getId() == null) {
            return false;
        }
        return getId().equals(other.getId());
    }
}
