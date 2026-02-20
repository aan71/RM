package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="RM_ROLE")
public class Role extends AbstractRoleEntity implements Cloneable {

    private String role = "";

    @OneToMany(targetEntity = RoleStatus.class, mappedBy = "id", orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<RoleStatus> listRoleStatus;

    public Role() {
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}
