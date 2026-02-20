package com.vaadin.tutorial.crm.ui.views.list;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import org.springframework.context.ApplicationContext;

public class AuditForm extends FormLayout {

    TextField table_name = new TextField("Table");
    TextField entity_name = new TextField("Field");
    TextField new_value = new TextField("New value");
    TextField old_value = new TextField("Old value");
    TextField user_name = new TextField("User");
    TextField operation = new TextField("Operation");

    Button save = new Button("Save");
    //Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Audit> binder = new BeanValidationBinder<>(Audit.class);
    private Audit audit;
    private ApplicationContext applicationContext;

    public AuditForm(ApplicationContext applicationContext) {
        addClassName("contact-form");

        this.applicationContext = applicationContext;

        binder.bindInstanceFields(this);

        add(
                //table_name,
                entity_name,
                new_value,
                old_value,
                //user_name,
                operation,
                createButtonsLayout()
        );
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
        binder.readBean(audit);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.getStyle().set("cursor","pointer");
        close.getStyle().set("cursor","pointer");

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        //delete.addClickListener(click -> fireEvent(new DeleteEvent(this, audit)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        //return new HorizontalLayout(save, delete, close);
        return new HorizontalLayout(save, close);
    }

    private void validateAndSave() {

        try {
            binder.writeBean(audit);
            fireEvent(new SaveEvent(this, audit));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class AuditFormEvent extends ComponentEvent<AuditForm> {
        private Audit audit;

        protected AuditFormEvent(AuditForm source, Audit audit) {
            super(source, false);
            this.audit = audit;
        }

        public Audit getAudit() {
            return audit;
        }
    }

    public static class SaveEvent extends AuditFormEvent {
        SaveEvent(AuditForm source, Audit audit) {
            super(source, audit);
        }
    }

    public static class DeleteEvent extends AuditFormEvent {
        DeleteEvent(AuditForm source, Audit audit) {
            super(source, audit);
        }

    }

    public static class CloseEvent extends AuditFormEvent {
        CloseEvent(AuditForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
