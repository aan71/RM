package com.vaadin.tutorial.crm.ui.views.list;

import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import com.vaadin.tutorial.crm.backend.entity.Contact;
import com.vaadin.tutorial.crm.backend.service.AuditService;
import com.vaadin.tutorial.crm.controls.PaginatedGrid;
import com.vaadin.tutorial.crm.ui.MainLayout;
import com.vaadin.tutorial.crm.utility.DataUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.vaadin.tutorial.crm.utility.FileSystemUtility;


@Component
@Scope("prototype")
@Route(value = "ListAudit", layout = MainLayout.class)
@PageTitle("Audit | Report Manager")
public class ListAudit extends VerticalLayout {

    //AuditForm form;
    //Grid<Audit> grid = new Grid<>(Audit.class);
    PaginatedGrid<Audit> grid = new PaginatedGrid<>(Audit.class);
    TextField filterUser = new TextField();
    DatePicker filterDateFrom = new DatePicker();
    DatePicker filterDateTo = new DatePicker();
    AuditService auditService;
    FileSystemUtility fileSystemUtility;

    private ApplicationContext applicationContext;
    private String formatDate;
    private String formatDateTime;
    private DataUtility dataUtility;
    private String documentPath;
    private String documentFormat;
    private String documentName;

    @Autowired
    public void configure(DataUtility dataUtility, AuditService auditService, FileSystemUtility fileSystemUtility) {
        this.fileSystemUtility = fileSystemUtility;
        this.dataUtility = dataUtility;
        this.auditService = auditService;
    }

    public ListAudit(ApplicationContext applicationContext, @Value("${format.date}") String formatDate, @Value("${format.datetime}") String formatDateTime) {
        this.applicationContext = applicationContext;
        this.formatDate = formatDate;
        this.formatDateTime = formatDateTime;
        addClassName("list-view");
        setSizeFull();
        configureGridAudit();

        //form = new AuditForm(applicationContext);
        //form.addListener(AuditForm.SaveEvent.class, this::saveAudit);
        //form.addListener(AuditForm.DeleteEvent.class, this::deleteAudit);
        //form.addListener(AuditForm.CloseEvent.class, e -> closeEditor());

        //Div content = new Div(grid, form);
        //Div content = new Div(grid);
        VerticalLayout content = new VerticalLayout(grid);
        content.addClassName("content");
        content.setSizeFull();
        content.setMaxHeight("75%");

        add(getToolBar(), content);
        //updateList();
        closeEditor();
    }

    private void deleteAudit(AuditForm.DeleteEvent evt) {
        auditService.delete(evt.getAudit());
        updateList();
        closeEditor();
    }

    private void saveAudit(AuditForm.SaveEvent evt) {
        auditService.save(evt.getAudit());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getToolBar() {
        filterUser.setPlaceholder("Filter by ...");
        filterUser.setClearButtonVisible(true);
        filterUser.setValueChangeMode(ValueChangeMode.LAZY);
        filterUser.setLabel("User");
        filterUser.addValueChangeListener(e -> updateList());

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

        //Button addAuditButton = new Button("Add audit", click -> addAudit());

        Button refresh = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refresh.getStyle().set("cursor","pointer");
        refresh.addClickListener(
                event -> grid.setItems(auditService.findAll(
                        filterUser.getValue()==null?"":filterUser.getValue().trim().toLowerCase(),
                        filterDateFrom.getValue()==null?"":filterDateFrom.getValue().toString(),
                        filterDateTo.getValue()==null?"":filterDateTo.getValue().toString()
                ))
        );

        HorizontalLayout toolbar = new HorizontalLayout(filterUser, filterDateFrom, filterDateTo, refresh);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(Alignment.END);

        return toolbar;
    }

    private void addAudit() {
        grid.asSingleSelect().clear();
        editAudit(new Audit());
    }

    private void configureGridAudit() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn("record_id").setHeader("Id");
        grid.addColumn("entity_name").setHeader("Field");
        grid.addColumn("new_value").setHeader("New value");
        grid.addColumn("old_value").setHeader("Old value");
        grid.addColumn("user_name").setHeader("User");
        grid.addColumn(item->item.getFormattedDate(formatDateTime), "entryDate").setHeader("When").setKey("entry_date");
        grid.addColumn("operation").setHeader("Operation");

        grid.addComponentColumn(item -> {
            if ((item.getRecord_id() != null) && (!item.getOperation().equals("DELETE"))) {
                String sPathDecodedURI = "";
                Anchor anchorRep;
                anchorRep = new Anchor("javascript:;", "Download (file not found!)");
                documentPath = dataUtility.getDocumentPath(item.getRecord_id());
                if (!documentPath.equals("")) {
                    if (Files.exists(Paths.get(documentPath))) {
                        try {
                            sPathDecodedURI = URLEncoder.encode(documentPath, StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if (item.getDocument_format() != null){
                            switch (item.getDocument_format().toLowerCase()) {
                                case "xlsx":
                                    anchorRep = new Anchor(applicationContext.getApplicationName() + "/downloadExcelFile/file?sPath=" + sPathDecodedURI + "&sId=-1", "Download");
                                    break;
                                case "pdf":
                                    anchorRep = new Anchor(applicationContext.getApplicationName() + "/downloadPdfFile/file?sPath=" + sPathDecodedURI + "&sId=-1", "Download");
                                    break;
                                case "txt":
                                    anchorRep = new Anchor(applicationContext.getApplicationName() + "/downloadTxtFile/file?sPath=" + sPathDecodedURI + "&sId=-1", "Download");
                                    break;
                                default:
                                    anchorRep = new Anchor("javascript:;", "Download (unrecognized file format!)");
                            }
                        } else {
                            anchorRep = new Anchor("javascript:;", "Download (unrecognized file format!)");
                        }
                    }
                }
                anchorRep.getElement().setAttribute("router-ignore", true);
                return anchorRep;
            } else {
                Label operation = new Label("");
                return operation;
            }
        }).setHeader("").setKey("document_download");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setResizable(true));
        //grid.asSingleSelect().addValueChangeListener(evt -> editAudit(evt.getValue()));
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);
        //grid.setPageSize(15);
        grid.setPaginatorSize(1);
        grid.setPaginatorTexts("Page","of");
    }

    private void editAudit(Audit audit) {
        if (audit == null) {
            closeEditor();
        } else {
            //form.setAudit(audit);
            //form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        //form.setAudit(null);
        //form.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        grid.setItems(auditService.findAll(
                filterUser.getValue()==null?"":filterUser.getValue().trim().toLowerCase(),
                filterDateFrom.getValue()==null?"":filterDateFrom.getValue().toString(),
                filterDateTo.getValue()==null?"":filterDateTo.getValue().toString()
                )
        );
    }

}
