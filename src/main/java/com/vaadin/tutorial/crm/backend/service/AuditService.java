package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Audit;
import com.vaadin.tutorial.crm.backend.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

@Service
public class AuditService {
    private static final Logger LOGGER = Logger.getLogger(AuditService.class.getName());
    private AuditRepository auditRepository;

    @Autowired
    private JdbcTemplate template;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public List<Audit> findAll() {
        return auditRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }

    public List<Audit> findAll(String filterUser, String filterDateFrom, String filterDateTo) {
        Date dateFrom = null;
        Date dateTo = null;

        if (filterUser.isEmpty() && filterDateFrom.isEmpty() && filterDateTo.isEmpty()) {
            return auditRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        } else  {
            if ( !filterDateFrom.isEmpty()) {
                filterDateFrom = filterDateFrom + " 00:00:00";
                try {
                    dateFrom=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filterDateFrom);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if ( !filterDateTo.isEmpty()) {
                filterDateTo = filterDateTo + " 23:59:59";
                try {
                    dateTo=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filterDateTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return auditRepository.search(filterUser, dateFrom, dateTo);
        }
    }

    @Async
    public List<Audit> findAllAsync() {
        return auditRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
    }
    
    @Async
    public List<Audit> findAllAsync(String filterUser, String filterDateFrom, String filterDateTo) {
        Date dateFrom = null;
        Date dateTo = null;

        if (filterUser.isEmpty() && filterDateFrom.isEmpty() && filterDateTo.isEmpty()) {
            return auditRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        } else  {
            if ( !filterDateFrom.isEmpty()) {
                filterDateFrom = filterDateFrom + " 00:00:00";
                try {
                    dateFrom=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filterDateFrom);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if ( !filterDateTo.isEmpty()) {
                filterDateTo = filterDateTo + " 23:59:59";
                try {
                    dateTo=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filterDateTo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return auditRepository.search(filterUser, dateFrom, dateTo);
        }
    }

    public long count() {
        return auditRepository.count();
    }

    public void delete(Audit audit) {
        auditRepository.delete(audit);
    }

    public void save(Audit audit) {
        if (audit == null) {
            LOGGER.log(Level.SEVERE,
                    "Audit is null. Are you sure you have connected your form to the application?");
            return;
        }
        auditRepository.save(audit);
    }

}
