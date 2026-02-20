package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("select DISTINCT c from Contact c, Status st, RoleStatus rs, Role ro where c.status = st and rs.status = st and rs.roles = ro and ro.role in :searchRole " +
            "and (:searchBroker is null or lower(c.broker_identifier) like lower(concat('%', :searchBroker, '%'))) " +
            "and (:searchName is null or lower(c.document_name) like lower(concat('%', :searchName, '%'))) " +
            "and (:searchStatus is null or st.id = :searchStatus) " +
            "and (:searchDateFrom is null or c.document_creation_date >= :searchDateFrom) " +
            "and (:searchDateTo is null or c.document_creation_date <= :searchDateTo) order by c.id desc"
    )
    List<Contact> search(@Param("searchBroker") String searchBroker,
                         @Param("searchName") String searchName,
                         @Param("searchStatus") Long searchStatus,
                         @Param("searchDateFrom") Date searchDateFrom,
                         @Param("searchDateTo") Date searchDateTo,
                         @Param("searchRole") List<String> searchRole
    );

    @Query("select DISTINCT c from Contact c, Status st, RoleStatus rs, Role ro where c.status = st and rs.status = st and rs.roles = ro and ro.role in :searchRole order by c.id desc")
    List<Contact> searchAll(@Param("searchRole") List<String> searchRole);
}