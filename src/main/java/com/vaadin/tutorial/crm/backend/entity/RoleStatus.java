package com.vaadin.tutorial.crm.backend.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
@Table(name="RM_ROLE_STATUS")
public class RoleStatus extends AbstractRoleEntity implements Cloneable {

    private Long fk_role;
    private Long fk_status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_status", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private com.vaadin.tutorial.crm.backend.entity.Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_role", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private com.vaadin.tutorial.crm.backend.entity.Role roles;

}
