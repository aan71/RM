package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.User_UserCategory;
import com.vaadin.tutorial.crm.backend.repository.User_UserCategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class User_UserCategoryService {
    private static final Logger LOGGER = Logger.getLogger(User_UserCategoryService.class.getName());
    private User_UserCategoryRepository user_UserCategoryRepository;

    public User_UserCategoryService(User_UserCategoryRepository user_UserCategoryRepository) {
        this.user_UserCategoryRepository = user_UserCategoryRepository;
    }

    public List<User_UserCategory> findAll() {
        return user_UserCategoryRepository.searchAll();
    }

    public long count() {
        return user_UserCategoryRepository.count();
    }

    public void delete(User_UserCategory user_UserCategory) {
        user_UserCategoryRepository.delete(user_UserCategory);
    }

    public void save(User_UserCategory user_UserCategory) {
        if (user_UserCategory == null) {
            LOGGER.log(Level.SEVERE,
                    "User_UserCategory is null. Are you sure you have connected your form to the application?");
            return;
        }
        user_UserCategoryRepository.save(user_UserCategory);
    }

}
