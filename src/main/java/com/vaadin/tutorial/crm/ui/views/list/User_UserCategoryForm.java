package com.vaadin.tutorial.crm.ui.views.list;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.User;
import com.vaadin.tutorial.crm.backend.entity.UserCategory;
import com.vaadin.tutorial.crm.backend.entity.User_UserCategory;
import com.vaadin.tutorial.crm.backend.service.User_UserCategoryService;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class User_UserCategoryForm extends FormLayout {

    Select<User> user = new Select<>();
    Select<UserCategory> userCategory = new Select<>();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<User_UserCategory> binder = new BeanValidationBinder<>(User_UserCategory.class);
    private User_UserCategory user_UserCategory;
    private ApplicationContext applicationContext;
    private User_UserCategoryService user_userCategoryService;


    public User_UserCategoryForm(ApplicationContext applicationContext, List<User> users, List<UserCategory> userCategories) {
        addClassName("contact-form");
        this.applicationContext = applicationContext;

        binder.bindInstanceFields(this);

        user.setLabel("User");
        user.setItems(users);
        user.setItemLabelGenerator(User::getUser_id);
        user.addValueChangeListener(event -> checkButtons());
        user.setRequiredIndicatorVisible(true);

        userCategory.setLabel("Category");
        userCategory.setItems(userCategories);
        userCategory.setItemLabelGenerator(UserCategory::getName);
        userCategory.addValueChangeListener(event -> checkButtons());
        user.setRequiredIndicatorVisible(true);

        add(
                user,
                userCategory,
                createButtonsLayout()
        );
    }

    private void checkButtons() {
        if (!user.isEmpty() && !userCategory.isEmpty()){
            save.setEnabled(true);
            delete.setEnabled(true);
        } else {
            save.setEnabled(false);
            delete.setEnabled(false);
        }
    }

    public void setUser_UserCategory(User_UserCategory user_UserCategory) {
        this.user_UserCategory = user_UserCategory;
        binder.readBean(user_UserCategory);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.getStyle().set("cursor","pointer");
        delete.getStyle().set("cursor","pointer");
        close.getStyle().set("cursor","pointer");

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, user_UserCategory)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {

        try {
            binder.writeBean(user_UserCategory);
            fireEvent(new SaveEvent(this, user_UserCategory));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class User_UserCategoryFormEvent extends ComponentEvent<User_UserCategoryForm> {
        private User_UserCategory user_UserCategory;

        protected User_UserCategoryFormEvent(User_UserCategoryForm source, User_UserCategory user_UserCategory) {
            super(source, false);
            this.user_UserCategory = user_UserCategory;
        }

        public User_UserCategory getUser_UserCategory() {
            return user_UserCategory;
        }
    }

    public static class SaveEvent extends User_UserCategoryFormEvent {
        SaveEvent(User_UserCategoryForm source, User_UserCategory user_UserCategory) {
            super(source, user_UserCategory);
        }
    }

    public static class DeleteEvent extends User_UserCategoryFormEvent {
        DeleteEvent(User_UserCategoryForm source, User_UserCategory user_UserCategory) {
            super(source, user_UserCategory);
        }

    }

    public static class CloseEvent extends User_UserCategoryFormEvent {
        CloseEvent(User_UserCategoryForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
