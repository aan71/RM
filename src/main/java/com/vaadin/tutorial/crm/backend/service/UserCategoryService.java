package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.UserCategory;
import com.vaadin.tutorial.crm.backend.repository.UserCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class UserCategoryService {
    private static final Logger LOGGER = Logger.getLogger(UserCategoryService.class.getName());
    private UserCategoryRepository userCategoryRepository;

    public UserCategoryService(UserCategoryRepository userCategoryRepository) {
        this.userCategoryRepository = userCategoryRepository;
    }

    public List<UserCategory> findAll() {
        return userCategoryRepository.searchAll();
    }

    public long count() {
        return userCategoryRepository.count();
    }

    public void delete(UserCategory userCategory) {
        userCategoryRepository.delete(userCategory);
    }

    public void save(UserCategory userCategory) {
        if (userCategory == null) {
            LOGGER.log(Level.SEVERE,
                    "UserCategory is null. Are you sure you have connected your form to the application?");
            return;
        }
        userCategoryRepository.save(userCategory);
    }

}
