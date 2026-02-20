package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Status;
import com.vaadin.tutorial.crm.backend.repository.StatusRepository;
import com.vaadin.tutorial.crm.utility.DataUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class StatusService {
    private static final Logger LOGGER = Logger.getLogger(StatusService.class.getName());
    private StatusRepository statusRepository;
    private DataUtility dataUtility;
    private List<String> userRoles;
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Autowired
    public void configure(DataUtility dataUtility) {
        this.dataUtility = dataUtility;
    }

    public List<Status> findAll() {
        userRoles = dataUtility.getCurrentUserRole(SecurityContextHolder.getContext().getAuthentication().getName());
        return statusRepository.searchAll(userRoles);
    }

    public long count() {
        return statusRepository.count();
    }

    public void delete(Status status) {
        statusRepository.delete(status);
    }

    public void save(Status status) {
        if (status == null) {
            LOGGER.log(Level.SEVERE,
                    "Status is null. Are you sure you have connected your form to the application?");
            return;
        }
        statusRepository.save(status);
    }

}
