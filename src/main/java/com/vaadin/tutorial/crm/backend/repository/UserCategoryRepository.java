package com.vaadin.tutorial.crm.backend.repository;
import com.vaadin.tutorial.crm.backend.entity.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long> {
    @Query("select uc from UserCategory uc where uc.name in ('RM_SUPERUSER','RM_USER')")
    List<UserCategory> searchAll();
}