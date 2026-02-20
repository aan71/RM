package com.vaadin.tutorial.crm.backend.repository;
import com.vaadin.tutorial.crm.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u")
    List<User> searchAll();
}