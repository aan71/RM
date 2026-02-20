package com.vaadin.tutorial.crm.backend.entity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name="USER_CATEGORY")
public class UserCategory extends AbstractUserCategoryEntity implements Cloneable {

    private String name = "";

    @OneToMany(mappedBy = "userCategory", fetch = FetchType.EAGER)
    private List<User_UserCategory> userCategories = new LinkedList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<User_UserCategory> getUserCategories() {
        return userCategories;
    }
}