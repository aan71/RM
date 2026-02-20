package com.vaadin.tutorial.crm.ui.views.login;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Report Manager")
public class LoginView extends Div implements BeforeEnterObserver {
    LoginOverlay loginOverlay = new LoginOverlay();
    public LoginView() {
        loginOverlay.setTitle("Report Manager");
        loginOverlay.setDescription("Built by DXC Technology");
        loginOverlay.setForgotPasswordButtonVisible(false);
        loginOverlay.setAction("login");
        add(loginOverlay);
        loginOverlay.setOpened(true);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginOverlay.setError(true);
        }
    }
}
