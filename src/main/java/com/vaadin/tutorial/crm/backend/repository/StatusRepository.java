package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    @Query("select DISTINCT st from Status st, RoleStatus rs, Role ro where rs.status = st and rs.roles = ro and ro.role in :searchRole order by st.status")
    List<Status> searchAll(@Param("searchRole") List<String> searchRole);
}