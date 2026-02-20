package com.vaadin.tutorial.crm.backend.entity;

import org.springframework.data.repository.Repository;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name="CNU_USER")
public class User extends AbstractUserEntity implements Cloneable {

    private String user_id = "";

    private String firstname = "";

    private String lastname = "";

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<User_UserCategory> users = new LinkedList<>();

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public List<User_UserCategory> getUsers() {
        return users;
    }

}
