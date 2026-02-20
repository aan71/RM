package com.vaadin.tutorial.crm.ui.views.list;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.entity.Status;
import org.springframework.context.ApplicationContext;
import java.util.List;

public class ContactForm extends Div {

    TextField system_source = new TextField("System");
    TextField broker_identifier = new TextField("Broker");
    TextField reinsurer_identifier = new TextField("Reinsurer");
    TextField document_type = new TextField("Type");
    TextField document_name = new TextField("Name");
    Select<Status> status = new Select<>();
    TextField user_name = new TextField("User name");

    Button save = new Button("Save");
    //Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);
    private Contact contact;
    private ApplicationContext applicationContext;

    public ContactForm(ApplicationContext applicationContext, List<Status> statuses) {
        addClassName("contact-form");
        this.applicationContext = applicationContext;

        binder.bindInstanceFields(this);

        status.setLabel("Status");
        status.setItems(statuses);
        status.setItemLabelGenerator(Status::getStatus);
        //status.setItemEnabledProvider(item->!"Delivered".equals(item.getStatus()));

        //add(createTitle());
        //add(system_source, broker_identifier, reinsurer_identifier, document_type, status);
        add(createFormLayout());
        add(createButtonsLayout());
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        binder.readBean(contact);
    }

    private Component createTitle() {
        return new H3("Report information");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(system_source, broker_identifier, reinsurer_identifier, document_type, document_name, status);
        return formLayout;
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.getStyle().set("cursor","pointer");
        close.getStyle().set("cursor","pointer");

        save.addClickShortcut(Key.ENTER);
        //close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());
        //delete.addClickListener(click -> fireEvent(new DeleteEvent(this, contact)));
        //close.addClickListener(click -> fireEvent(new CloseEvent(this)));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        //return new HorizontalLayout(save, delete, close);
        return new HorizontalLayout(save, close);
    }

    private void validateAndSave() {

      try {
        binder.writeBean(contact);
        fireEvent(new SaveEvent(this, contact));
      } catch (ValidationException e) {
        e.printStackTrace();
      }
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
      private Contact contact;

      protected ContactFormEvent(ContactForm source, Contact contact) {
        super(source, false);
        this.contact = contact;
      }

      public Contact getContact() {
        return contact;
      }
    }

    public static class SaveEvent extends ContactFormEvent {
      SaveEvent(ContactForm source, Contact contact) {
        super(source, contact);
      }
    }

    public static class DeleteEvent extends ContactFormEvent {
      DeleteEvent(ContactForm source, Contact contact) {
        super(source, contact);
      }

    }

    public static class CloseEvent extends ContactFormEvent {
      CloseEvent(ContactForm source) { super(source, null); }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
      return getEventBus().addListener(eventType, listener);
    }
}
