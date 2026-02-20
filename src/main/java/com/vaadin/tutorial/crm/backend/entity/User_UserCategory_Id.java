package com.vaadin.tutorial.crm.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class User_UserCategory_Id implements Serializable {
    private String user;
    private String userCategory;

    public User_UserCategory_Id() {
    }

    public User_UserCategory_Id(String user, String userCategory) {
        this.user = user;
        this.userCategory = userCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User_UserCategory_Id user_UserCategory_Id = (User_UserCategory_Id) o;
        return user.equals(user_UserCategory_Id.user) &&
                userCategory.equals(user_UserCategory_Id.userCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, userCategory);
    }
}