package com.vaadin.tutorial.crm.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarGroup.AvatarGroupItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.component.html.Image;
import com.vaadin.tutorial.crm.backend.entity.Audit;
import com.vaadin.tutorial.crm.backend.service.AuditService;
import com.vaadin.tutorial.crm.ui.views.list.ListAudit;
import com.vaadin.tutorial.crm.ui.views.list.ListUser_UserCategory;
import com.vaadin.tutorial.crm.ui.views.list.ListView;
import com.vaadin.tutorial.crm.utility.DataUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.component.tabs.Tab;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.icon.Icon;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    private String userName = "";
    private DataUtility dataUtility;
    private H1 viewUser;

    private AuditService auditService;
    private Audit audit;

    @Autowired
    public void configure(AuditService auditService) {
        this.auditService = auditService;
    }

    public MainLayout(@Autowired DataUtility dataUtility) {
        this.userName= SecurityContextHolder.getContext().getAuthentication().getName();
        this.dataUtility=dataUtility;

        createHeader();
    }

    private void createHeader() {
        Image img = new Image("images/logo-lusitania-seguros.png", "logo");
        img.setWidth("132px");
        img.setHeight("23px");

        H1 tit = new H1("Report Manager - Environment: " + this.dataUtility.getCurrentDatabase());
        tit.addClassName("logo");

        //Anchor logout = new Anchor("/logout", "Log out");
        /*
        Button logout = new Button("Logout", click -> {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

            audit=new Audit();
            audit.setOperation("Logout");
            audit.setEntry_date(currentTimestamp);
            audit.setUser_name(this.userName);
            auditService.save(audit);

            UI.getCurrent().getPage().executeJs("location.assign('logout')");
        });
        logout.getStyle().set("cursor","pointer");
         */

        H1 usr = new H1(" (" + this.userName + ")");
        usr.addClassName("logo");

        //HorizontalLayout header1 = new HorizontalLayout(img, tit, logout, usr, new Avatar(this.userName));
        String name = this.userName;
        Avatar avatar = new Avatar(name.toUpperCase());
        //avatar.setTooltipEnabled(true);
        MenuBar userMenu = new MenuBar();
        userMenu.setThemeName("tertiary-inline contrast");
        MenuItem userName = userMenu.addItem("");
        Div div = new Div();
        div.add(avatar);
        div.add(this.userName);
        div.add(new Icon("lumo", "dropdown"));
        div.getElement().getStyle().set("display", "flex");
        div.getElement().getStyle().set("align-items", "center");
        div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
        userName.add(div);
        userName.getSubMenu().addItem("Logout", e -> {
            Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();
            Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

            audit=new Audit();
            audit.setOperation("Logout");
            audit.setEntry_date(currentTimestamp);
            audit.setUser_name(this.userName);
            auditService.save(audit);

            UI.getCurrent().getPage().executeJs("location.assign('logout')");
        });
        //HorizontalLayout header1 = new HorizontalLayout(img, tit, logout, avatar);
        HorizontalLayout header1 = new HorizontalLayout(img, tit, userMenu);
        header1.addClassName("header");
        header1.setWidth("100%");
        header1.expand(tit);
        header1.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Tabs tabs = new Tabs();
        Tab tab1 = new Tab(new RouterLink("Report List", ListView.class));
        Tab tab2 = new Tab(new RouterLink("Audit List", ListAudit.class));
        Tab tab3 = new Tab(new RouterLink("User List", ListUser_UserCategory.class));
        tab2.setEnabled(false);
        tab3.setEnabled(false);

        if (dataUtility.hasRole("RM_SUPERUSER")){
            tab2.setEnabled(true);
            tab3.setEnabled(true);
        }
        tabs.add(tab1, tab2, tab3);
        tabs.setSelectedTab(tab1);

        HorizontalLayout header2 = new HorizontalLayout(tabs);
        header2.setWidth("100%");
        header2.setPadding(false);
        header2.setMargin(false);
        header2.setSpacing(true);

        VerticalLayout layout = new VerticalLayout();
        layout.add(header1, header2);

        addToNavbar(layout);
    }
}
