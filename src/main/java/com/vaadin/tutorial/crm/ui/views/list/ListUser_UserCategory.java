package com.vaadin.tutorial.crm.ui.views.list;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.tutorial.crm.backend.entity.User_UserCategory;
import com.vaadin.tutorial.crm.backend.entity.User;
import com.vaadin.tutorial.crm.backend.entity.UserCategory;
import com.vaadin.tutorial.crm.backend.service.UserCategoryService;
import com.vaadin.tutorial.crm.backend.service.UserService;
import com.vaadin.tutorial.crm.backend.service.User_UserCategoryService;
import com.vaadin.tutorial.crm.ui.MainLayout;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Route(value = "ListUser_UserCategory", layout = MainLayout.class)
@PageTitle("User_UserCategory | Report Manager")
public class ListUser_UserCategory extends VerticalLayout {
    Grid<User_UserCategory> grid = new Grid<>(User_UserCategory.class);
    User_UserCategoryForm form;
    User_UserCategoryService user_UserCategoryService;
    UserService userService;
    UserCategoryService userCategoryService;
    private ApplicationContext applicationContext;
    Notification notification = new Notification("Changes saved.", 3000, Notification.Position.BOTTOM_CENTER);

    public ListUser_UserCategory(User_UserCategoryService user_UserCategoryService, ApplicationContext applicationContext, UserService userService, UserCategoryService userCategoryService) {
        this.applicationContext = applicationContext;
        this.user_UserCategoryService = user_UserCategoryService;
        this.userService = userService;
        this.userCategoryService = userCategoryService;

        addClassName("list-view");
        setSizeFull();
        configureGrid();

        form = new User_UserCategoryForm(applicationContext, userService.findAll(), userCategoryService.findAll());
        form.addListener(User_UserCategoryForm.SaveEvent.class, this::saveUser_UserCategory);
        form.addListener(User_UserCategoryForm.DeleteEvent.class, this::deleteUser_UserCategory);
        form.addListener(User_UserCategoryForm.CloseEvent.class, e -> closeEditor());

        Div content = new Div(grid, form);
        content.addClassName("content_normal");
        content.setSizeFull();

        add(getToolBar(),content);
        updateList();
        closeEditor();
    }

    private void deleteUser_UserCategory(User_UserCategoryForm.DeleteEvent evt) {
        user_UserCategoryService.delete(evt.getUser_UserCategory());
        updateList();
        closeEditor();
        notification.open();
    }

    private void saveUser_UserCategory(User_UserCategoryForm.SaveEvent evt) {
        user_UserCategoryService.save(evt.getUser_UserCategory());
        updateList();
        closeEditor();
        notification.open();
    }

    private HorizontalLayout getToolBar() {
        Button addUser_UserCategoryButton = new Button("Add User Category", click -> addUser_UserCategory());
        addUser_UserCategoryButton.getStyle().set("cursor","pointer");

        Button refresh = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refresh.getStyle().set("cursor","pointer");
        refresh.addClickListener(
                event -> grid.setItems(user_UserCategoryService.findAll())
        );

        HorizontalLayout toolbar = new HorizontalLayout(addUser_UserCategoryButton, refresh);
        toolbar.addClassName("toolbar");
        toolbar.setAlignItems(Alignment.END);

        return toolbar;
    }

    private void addUser_UserCategory() {
        grid.asSingleSelect().clear();
        editUser_UserCategory(new User_UserCategory());
        form.save.setEnabled(false);
        form.delete.setEnabled(false);
    }

    private void editUser_UserCategory(User_UserCategory user_UserCategory) {
        if (user_UserCategory == null) {
            closeEditor();
        } else {
            form.setUser_UserCategory(user_UserCategory);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setUser_UserCategory(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        grid.removeAllColumns();

        grid.addColumn(item -> {
            User user = item.getUser();
            return user == null ? "-" : user.getUser_id();
        }).setHeader("User id").setKey("user_id");

        grid.addColumn(item -> {
            User user = item.getUser();
            return user == null ? "-" : user.getFirstname();
        }).setHeader("First name").setKey("firstname");

        grid.addColumn(item -> {
            User user = item.getUser();
            return user == null ? "-" : user.getLastname();
        }).setHeader("Last name").setKey("lastname");

        grid.addColumn(item -> {
            UserCategory userCategory = item.getUserCategory();
            return userCategory == null ? "-" : userCategory.getName();
        }).setHeader("Category").setKey("category");

        grid.getColumns().forEach(col -> col.setAutoWidth(true).setResizable(true).setSortable(true));
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setColumnReorderingAllowed(true);
        grid.asSingleSelect().addValueChangeListener(evt -> editUser_UserCategory(evt.getValue()));

    }

    private void updateList() {
        grid.setItems(user_UserCategoryService.findAll());
    }

}
