package com.vaadin.tutorial.crm.ui.views.list;

import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.entity.Status;
import com.vaadin.tutorial.crm.backend.service.AuditService;
import com.vaadin.tutorial.crm.backend.service.ContactService;
import com.vaadin.tutorial.crm.backend.service.StatusService;
import com.vaadin.tutorial.crm.controls.PaginatedGrid;
import com.vaadin.tutorial.crm.ui.MainLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.tutorial.crm.utility.DataUtility;
import com.vaadin.tutorial.crm.utility.FileSystemUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout.class)
@PageTitle("Reports | Report Manager")
public class ListView extends VerticalLayout {

    //Read value task_scheduler.import_from_bo from application.properties
    @Value("${task_scheduler.import_from_bo}")
    String task_scheduler;

    ContactForm form;
    //Grid<Contact> grid = new Grid<>(Contact.class);
    PaginatedGrid<Contact> grid = new PaginatedGrid<>(Contact.class);
    Dialog dialog = new Dialog();
    Dialog dialogBO = new Dialog();
    TextField filterBroker = new TextField();

    TextField filterName = new TextField();
    ComboBox<Status> filterStatus = new ComboBox<>();
    DatePicker filterDateFrom = new DatePicker();
    DatePicker filterDateTo = new DatePicker();
    ContactService contactService;
    StatusService statusService;
    FileSystemUtility fileSystemUtility;
    private String userName = "";
    private ApplicationContext applicationContext;
    private String formatDate;
    private String formatDateTime;
    private DataUtility dataUtility;
    private AuditService auditService;
    private Audit audit;
    private String sourceFile;
    private String targetFile;
    Notification notification = new Notification("Changes saved.", 3000, Notification.Position.BOTTOM_CENTER);

    @Autowired
    public void configure(DataUtility dataUtility, AuditService auditService, FileSystemUtility fileSystemUtility) {
        this.dataUtility = dataUtility;
        this.auditService = auditService;
        this.fileSystemUtility = fileSystemUtility;
    }

    public ListView(ContactService contactService, StatusService statusService, ApplicationContext applicationContext, @Value("${format.date}") String formatDate, @Value("${format.datetime}") String formatDateTime) {
        this.applicationContext = applicationContext;
        this.contactService = contactService;
        this.statusService = statusService;
        this.formatDate = formatDate;
        this.formatDateTime = formatDateTime;
        this.userName= SecurityContextHolder.getContext().getAuthentication().getName();

        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new ContactForm(applicationContext, statusService.findAll());
        form.addListener(ContactForm.SaveEvent.class, this::saveContact);
        form.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        form.addListener(ContactForm.CloseEvent.class, e -> closeEditor());

        //Div content = new Div(grid, form);
        //Div content = new Div(grid);
        VerticalLayout content = new VerticalLayout(grid);
        content.addClassName("content");
        content.setSizeFull();
        content.setMaxHeight("75%");
        dialog.setHeaderTitle("Report information");
        dialog.add(form);
        dialog.setResizable(true);
        dialog.setDraggable(true);
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        add(getToolBar(), content);
        //updateList();
        closeEditor();
    }

    private void deleteContact(ContactForm.DeleteEvent evt) {
        //contactService.delete(evt.getContact());
        //updateList();
        //closeEditor();
    }

    private void saveContact(ContactForm.SaveEvent evt) {
        Dialog saveDialog = new Dialog(new Paragraph("Are you sure you want to save this data?"));
        Button confirmButton = new Button("Confirm");
        Button cancelButton = new Button("Cancel");
        saveDialog.setModal(true);
        saveDialog.setCloseOnEsc(false);
        saveDialog.setCloseOnOutsideClick(false);

        //confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        confirmButton.getStyle().set("cursor","pointer");
        cancelButton.getStyle().set("cursor","pointer");
        //FlexLayout cancelButtonWrapper = new FlexLayout(cancelButton);
        //cancelButtonWrapper.setJustifyContentMode(JustifyContentMode.END);
        //HorizontalLayout dialogButtons = new HorizontalLayout(confirmButton, cancelButtonWrapper);
        //dialogButtons.expand(cancelButtonWrapper);

        //saveDialog.add(dialogButtons);
        saveDialog.getFooter().add(confirmButton, cancelButton);
        cancelButton.addClickListener(e -> {
            saveDialog.removeAll();
            saveDialog.getFooter().removeAll();
            saveDialog.close();
        });

        confirmButton.addClickListener(e -> {
            sourceFile = (evt.getContact().getDocument_path()).replace("\\","/");
            targetFile = (evt.getContact().getStatus().getDocument_path()+evt.getContact().getDocument_name()).replace("\\","/");

            if (!sourceFile.equals(targetFile)){
                if (Files.exists(Paths.get(sourceFile))) {
                    if (!fileSystemUtility.isFileLocked(new File(sourceFile))) {
                        if (fileSystemUtility.copyFile(sourceFile, targetFile)) {
                            //evt.getContact().setDocument_path(targetFile);
                            contactService.save(evt.getContact());
                            grid.getDataProvider().refreshItem(evt.getContact());
                            closeEditor();
                            saveDialog.removeAll();
                            saveDialog.getFooter().removeAll();
                            saveDialog.close();
                            notification.open();
                        } else {
                            Dialog dialog = new Dialog(new Paragraph("ERROR! Report not found!"));
                            Button confirm = new Button("Close");
                            confirm.getStyle().set("cursor","pointer");
                            dialog.setModal(true);
                            dialog.setCloseOnEsc(false);
                            dialog.setCloseOnOutsideClick(false);
                            //HorizontalLayout myButtons = new HorizontalLayout(confirm);
                            //dialog.add(myButtons);
                            dialog.getFooter().add(confirm);
                            confirm.addClickListener(event -> {
                                dialog.removeAll();
                                dialog.getFooter().removeAll();
                                dialog.close();
                                saveDialog.close();
                            });
                            dialog.open();
                        }
                    } else {
                        Dialog dialog = new Dialog(new Paragraph("ERROR! Report is locked!"));
                        Button confirm = new Button("Close");
                        confirm.getStyle().set("cursor","pointer");
                        dialog.setModal(true);
                        dialog.setCloseOnEsc(false);
                        dialog.setCloseOnOutsideClick(false);
                        //HorizontalLayout myButtons = new HorizontalLayout(confirm);
                        //dialog.add(myButtons);
                        dialog.getFooter().add(confirm);
                        confirm.addClickListener(event -> {
                            dialog.removeAll();
                            dialog.getFooter().removeAll();
                            dialog.close();
                            saveDialog.close();
                        });
                        dialog.open();
                    }
                } else {
                    Dialog dialog = new Dialog(new Paragraph("ERROR! Report not found!"));
                    Button confirm = new Button("Close");
                    confirm.getStyle().set("cursor","pointer");
                    dialog.setModal(true);
                    dialog.setCloseOnEsc(false);
                    dialog.setCloseOnOutsideClick(false);
                    //HorizontalLayout myButtons = new HorizontalLayout(confirm);
                    //dialog.add(myButtons);
                    dialog.getFooter().add(confirm);
                    confirm.addClickListener(event -> {
                        dialog.removeAll();
                        dialog.getFooter().removeAll();
                        dialog.close();
                        saveDialog.close();
                    });
                    dialog.open();
                }
            } else {
                contactService.save(evt.getContact());
                grid.getDataProvider().refreshItem(evt.getContact());
                closeEditor();
                saveDialog.removeAll();
                saveDialog.getFooter().removeAll();
                saveDialog.close();
                notification.open();
            }
        });
        saveDialog.open();

    }

    private HorizontalLayout getToolBar() {
        filterBroker.setPlaceholder("Filter by ...");
        filterBroker.setClearButtonVisible(true);
        filterBroker.setValueChangeMode(ValueChangeMode.LAZY);
        filterBroker.setLabel("Broker");
        filterBroker.addValueChangeListener(e -> updateList());

        filterName.setPlaceholder("Filter by ...");
        filterName.setClearButtonVisible(true);
        filterName.setValueChangeMode(ValueChangeMode.LAZY);
        filterName.setLabel("Name");
        filterName.addValueChangeListener(e -> updateList());

        filterDateFrom.setPlaceholder("Filter by ...");
        filterDateFrom.setClearButtonVisible(true);
        filterDateFrom.setLocale(Locale.ITALIAN);
        filterDateFrom.setLabel("Creation Date (From)");
        filterDateFrom.addValueChangeListener(e -> updateList());

        filterDateTo.setPlaceholder("Filter by ...");
        filterDateTo.setClearButtonVisible(true);
        filterDateTo.setLocale(Locale.ITALIAN);
        filterDateTo.setLabel("Creation Date (To)");
        filterDateTo.addValueChangeListener(e -> updateList());

        filterStatus.setPlaceholder("Filter by ...");
        filterStatus.setClearButtonVisible(true);
        filterStatus.setLabel("Status");
        filterStatus.setItems(statusService.findAll());
        filterStatus.setItemLabelGenerator(Status::getStatus);
        filterStatus.addValueChangeListener(e -> updateList());

        //Button addContactButton = new Button("Add report", click -> addContact());

        Button refresh = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refresh.getStyle().set("cursor","pointer");
        refresh.addClickListener(
                event -> {
                    grid.setItems(contactService.findAll(
                            filterBroker.getValue()==null?"":filterBroker.getValue().trim().toLowerCase(),
                            filterName.getValue()==null?"":filterName.getValue().trim().toLowerCase(),
                            filterStatus.getValue()==null?null:filterStatus.getValue().getId(),
                            filterDateFrom.getValue()==null?"":filterDateFrom.getValue().toString(),
                            filterDateTo.getValue()==null?"":filterDateTo.getValue().toString()
                    ));

                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();
                    Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

                    audit = new Audit();
                    audit.setOperation("Refresh");
                    audit.setEntry_date(currentTimestamp);
                    audit.setUser_name(this.userName);
                    auditService.save(audit);
                }
        );

        Button bo = new Button("Force Update BO", e -> {
            config();
        });
        bo.getStyle().set("cursor","pointer");

        HorizontalLayout toolbar = new HorizontalLayout(filterBroker, filterName, filterDateFrom, filterDateTo, filterStatus, refresh, bo);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(Alignment.END);

        return toolbar;
    }

    private void addContact() {
        grid.asSingleSelect().clear();
        editContact(new Contact());
    }

    private void config() {
        TextArea ta = new TextArea();
        ta.setWidthFull();
        //ta.setHeightFull();
        //ta.setMinWidth("350px");
        //ta.setMaxWidth("350px");
        ta.setMinHeight("250px");
        ta.setMaxHeight("250px");
        ta.setReadOnly(true);

        dialogBO.setCloseOnEsc(false);
        dialogBO.setCloseOnOutsideClick(false);
        dialogBO.setHeaderTitle("Force Update BO");
        dialogBO.add(new Paragraph("This action will force the loading of reports generated by the BO scheduler into the Report Manager."));
        dialogBO.add(new Paragraph("Click 'Confirm' if you want to proceed."));
        dialogBO.add(new Paragraph("Click 'Close' to exit without performing any action."));
        dialogBO.add(ta);

        Button confirmButton = new Button("Confirm", buttonClickEvent -> {
            try {
                Runtime rn = Runtime.getRuntime();
                //Process pr = rn.exec("SCHTASKS.EXE /RUN /TN RM_IMPORT_FROM_BO");
                Process pr = rn.exec("SCHTASKS.EXE /RUN /TN " + task_scheduler);
                int exitVal = pr.waitFor();

                StringBuilder output = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }
                if (exitVal == 0) {
                    ta.setValue("" +
                            "The loading process from the Business Object scheduler was started correctly.\n\n" +
                            "Press 'Close', wait a few minutes then press 'Refresh' to see the newly loaded reports."
                    );
                } else {
                    ta.setValue("Something went wrong.\n\n" + "Please contact DXC technical support.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        confirmButton.setDisableOnClick(true);

        Button closeButton = new Button("Close", nativeButtonClickEvent -> {
            dialogBO.removeAll();
            dialogBO.getFooter().removeAll();
            dialogBO.close();
        });

        //confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        //closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        confirmButton.getStyle().set("cursor","pointer");
        closeButton.getStyle().set("cursor","pointer");

        //FlexLayout cancelButtonWrapper = new FlexLayout(closeButton);
        //cancelButtonWrapper.setJustifyContentMode(JustifyContentMode.END);
        //HorizontalLayout dialogButtons = new HorizontalLayout(confirmButton, cancelButtonWrapper);
        //dialogButtons.expand(cancelButtonWrapper);

        //dialogBO.add(dialogButtons);
        dialogBO.getFooter().add(confirmButton, closeButton);
        dialogBO.open();
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        grid.addColumn("id").setHeader("Id");
        grid.addColumn("system_source").setHeader("System");
        grid.addColumn("broker_identifier").setHeader("Broker");
        grid.addColumn("reinsurer_identifier").setHeader("Reinsurer");
        grid.addColumn("document_type").setHeader("Type");
        grid.addColumn("document_name").setHeader("Name");
        grid.addColumn(item->item.getFormattedDate(formatDate),"creationDate").setHeader("Creation date").setKey("document_creation_date");
        grid.addColumn(contact -> {
            Status status = contact.getStatus();
            return status == null ? "-" : status.getStatus();
        }).setHeader("Status").setKey("status");
        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, Contact) -> {
                    button.getStyle().set("cursor","pointer");
                    button.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    button.addClickListener(e -> this.editContact(Contact));
                    button.setIcon(new Icon(VaadinIcon.CLIPBOARD_TEXT));
                    button.setTooltipText("Click to open the report information.");
                })).setHeader("Action");

        grid.addColumn(
                new ComponentRenderer<>(Button::new, (button, Contact) -> {
                    if (Files.exists(Paths.get(Contact.getDocument_path()))) {
                        if (Contact.getDocument_format().toLowerCase().equals("pdf")) {
                            button.setVisible(true);
                            button.getStyle().set("cursor", "pointer");
                            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_ERROR,
                                    ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> this.showReportDialog(Contact));
                            button.setIcon(new Icon(VaadinIcon.EYE));
                            button.setTooltipText("Click to open the report.");
                        } else {
                            button.setVisible(false);
                        }
                    } else {
                        button.setVisible(true);
                        button.addThemeVariants(ButtonVariant.LUMO_ICON,
                                ButtonVariant.LUMO_ERROR,
                                ButtonVariant.LUMO_TERTIARY);
                        button.setIcon(new Icon(VaadinIcon.BAN));
                        button.setTooltipText("File not found!");
                    }
                })).setHeader("");

        grid.addComponentColumn(item -> {
            String sPathDecodedURI = "";
            try {
                sPathDecodedURI = URLEncoder.encode(item.getDocument_path(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Anchor anchorRep = new Anchor("javascript:;", "Download (file not found!)");
            if (Files.exists(Paths.get(item.getDocument_path()))) {
                if (item.getDocument_format() != null){
                    switch (item.getDocument_format().toLowerCase()) {
                        case "xlsx":
                            anchorRep = new Anchor(applicationContext.getApplicationName() + "/downloadExcelFile/file?sPath=" + sPathDecodedURI + "&sId=" + item.getId().toString(), "Download");
                            break;
                        case "pdf":
                            anchorRep = new Anchor(applicationContext.getApplicationName() + "/downloadPdfFile/file?sPath=" + sPathDecodedURI + "&sId=" + item.getId().toString(), "Download");
                            break;
                        case "txt":
                            anchorRep = new Anchor(applicationContext.getApplicationName() + "/downloadTxtFile/file?sPath=" + sPathDecodedURI + "&sId=" + item.getId().toString(), "Download");
                            break;
                        default:
                            anchorRep = new Anchor("javascript:;", "Download (unrecognized file format!)");
                    }
                } else {
                    anchorRep = new Anchor("javascript:;", "Download (unrecognized file format!)");
                }
            }
            anchorRep.getElement().setAttribute("router-ignore", true);
            return anchorRep;
        }).setHeader("").setKey("document_download");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setResizable(true));
        //grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));
         grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);
        //grid.setPageSize(15);
        grid.setPaginatorSize(1);
        grid.setPaginatorTexts("Page","of");
    }

    private void showReportDialog(Contact contact) {
        grid.select(contact);

        Dialog dialogRE = new Dialog();

        dialogRE.setHeaderTitle("File viewer");
        dialogRE.setResizable(true);
        dialogRE.setDraggable(true);
        dialogRE.setModal(true);
        dialogRE.setCloseOnEsc(false);
        dialogRE.setCloseOnOutsideClick(false);
        dialogRE.setWidth("80%");
        dialogRE.setHeight("80%");

        PdfViewer pdfViewer = new PdfViewer();
        StreamResource resource = new StreamResource(contact.getDocument_name(), () -> fileSystemUtility.firmwareToByteArray(contact.getDocument_path()));
        pdfViewer.setSrc(resource);
        pdfViewer.setWidth("100%");
        pdfViewer.setHeight("100%");
        pdfViewer.setZoom("1.25");
        pdfViewer.setAddDownloadButton(false);

        Button closeButton = new Button("Close", event -> dialogRE.close());
        closeButton.getStyle().set("cursor","pointer");

        dialogRE.add(pdfViewer);
        dialogRE.getFooter().add(closeButton);

        dialogRE.open();
    }

    private void editContact(Contact contact) {
        grid.select(contact);

        if (contact == null) {
            closeEditor();
        } else {
            form.setContact(contact);
            form.user_name.setValue(SecurityContextHolder.getContext().getAuthentication().getName());
            form.user_name.setVisible(false);
            form.document_name.setReadOnly(true);

            if (contact.getStatus().getStatus().equals("Delivered")){
                form.system_source.setEnabled(false);
                form.broker_identifier.setEnabled(false);
                form.reinsurer_identifier.setEnabled(false);
                form.document_type.setEnabled(false);
                form.status.setEnabled(false);

                form.save.setVisible(false);
            } else {
                form.system_source.setEnabled(true);
                form.broker_identifier.setEnabled(true);
                form.reinsurer_identifier.setEnabled(true);
                form.document_type.setEnabled(true);
                form.status.setEnabled(true);

                form.save.setVisible(true);
            }

            if (dataUtility.hasRole("RM_USER")) {
                if (contact.getStatus().getStatus().equals("Delivered"))  {
                    form.system_source.setEnabled(false);
                    form.broker_identifier.setEnabled(false);
                    form.reinsurer_identifier.setEnabled(false);
                    form.document_type.setEnabled(false);
                    form.status.setEnabled(false);

                    form.save.setVisible(false);
                } else {
                    form.system_source.setEnabled(true);
                    form.broker_identifier.setEnabled(true);
                    form.reinsurer_identifier.setEnabled(true);
                    form.document_type.setEnabled(true);
                    form.status.setEnabled(true);

                    form.save.setVisible(true);
                }
            }
            //form.setVisible(true);
            dialog.open();
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setContact(null);
        //form.setVisible(false);
        dialog.close();
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(contactService.findAll(
                filterBroker.getValue()==null?"":filterBroker.getValue().trim().toLowerCase(),
                filterName.getValue()==null?"":filterName.getValue().trim().toLowerCase(),
                filterStatus.getValue()==null?null:filterStatus.getValue().getId(),
                filterDateFrom.getValue()==null?"":filterDateFrom.getValue().toString(),
                filterDateTo.getValue()==null?"":filterDateTo.getValue().toString()
                )
        );
    }

}
