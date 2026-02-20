package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.User_UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface User_UserCategoryRepository extends JpaRepository<User_UserCategory, Long> {
    @Query("select DISTINCT uu from User_UserCategory uu, User us, UserCategory uc where uu.user = us and uu.userCategory=uc and uc.name in ('RM_SUPERUSER','RM_USER')")
    List<User_UserCategory> searchAll();
}