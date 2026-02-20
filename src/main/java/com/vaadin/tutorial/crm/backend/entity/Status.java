package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="RM_STATUS")
public class Status extends AbstractStatusEntity implements Cloneable {
    private String status = "";
    private String document_path = "";

    //@OneToMany(targetEntity = Contact.class, mappedBy = "id", orphanRemoval = false, fetch = FetchType.LAZY)
    //private Set<Contact> listContacts;

    @OneToMany(mappedBy = "status", fetch = FetchType.EAGER)
    private List<Contact> statuses = new LinkedList<>();

    //@OneToMany(targetEntity = RoleStatus.class, mappedBy = "id", orphanRemoval = false, fetch = FetchType.LAZY)
    //private Set<RoleStatus> listRoleStatus;

    @OneToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<RoleStatus> roleses = new LinkedList<>();

    public Status() {
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocument_path() { return document_path; }
    public void setDocument_path(String document_path) { this.document_path = document_path; }

    public List<Contact> getStatuses() {
        return statuses;
    }

}
