package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.repository.ContactRepository;
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
public class ContactService {
    private static final Logger LOGGER = Logger.getLogger(ContactService.class.getName());
    private ContactRepository contactRepository;
    private DataUtility dataUtility;
    private List<String> userRoles;
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Autowired
    public void configure(DataUtility dataUtility) {
        this.dataUtility = dataUtility;
    }

    public List<Contact> findAll() {
        userRoles = dataUtility.getCurrentUserRole(SecurityContextHolder.getContext().getAuthentication().getName());
        return contactRepository.searchAll(userRoles);
    }

    public List<Contact> findAll(String filterBroker, String filterName, Long filterStatus, String filterDateFrom, String filterDateTo) {
        Date dateFrom = null;
        Date dateTo = null;

        if (filterBroker.isEmpty() && filterName.isEmpty() && filterStatus==null && filterDateFrom.isEmpty() && filterDateTo.isEmpty()) {
            userRoles = dataUtility.getCurrentUserRole(SecurityContextHolder.getContext().getAuthentication().getName());
            return contactRepository.searchAll(userRoles);
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
            //userRoles = dataUtility.getCurrentUserRole(SecurityContextHolder.getContext().getAuthentication().getName());
            return contactRepository.search(filterBroker, filterName, filterStatus, dateFrom, dateTo, userRoles);
            //return contactRepository.search(filterBroker, dateFrom, dateTo, userRoles);
        }
    }

    public long count() {
        return contactRepository.count();
    }

    public void delete(Contact contact) {
        contactRepository.delete(contact);
    }

    public void save(Contact contact) {
        if (contact == null) {
            LOGGER.log(Level.SEVERE,
                "Contact is null. Are you sure you have connected your form to the application?");
            return;
        }
        contactRepository.save(contact);
    }
}
