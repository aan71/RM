package com.vaadin.tutorial.crm.backend.repository;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Long> {
    @Query("select c from Audit c where " +
            "(:searchUser is null or lower(c.user_name) like lower(concat('%', :searchUser, '%'))) " +
            "and (:searchDateFrom is null or c.entry_date >= :searchDateFrom) " +
            "and (:searchDateTo is null or c.entry_date <= :searchDateTo) order by id desc"
    )
    List<Audit> search(@Param("searchUser") String searchUser,
                         @Param("searchDateFrom") Date searchDateFrom,
                         @Param("searchDateTo") Date searchDateTo
    );
}