package com.vaadin.tutorial.crm.backend.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="USER_USERCATEGORY")
@IdClass(User_UserCategory_Id.class)
public class User_UserCategory {

    //@Id
    //@Column(name="rowid")
    //private String rowid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user", referencedColumnName="object_id", nullable=false)
    @Fetch(FetchMode.JOIN)
    @Id
    private com.vaadin.tutorial.crm.backend.entity.User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_usercategory", referencedColumnName="object_id", nullable=false)
    @Fetch(FetchMode.JOIN)
    @Id
    private com.vaadin.tutorial.crm.backend.entity.UserCategory userCategory;

    public void setUser(User user) { this.user = user; }
    public User getUser() { return user; }

    public void setUserCategory(UserCategory userCategory) { this.userCategory = userCategory; }
    public UserCategory getUserCategory() { return userCategory; }

}
