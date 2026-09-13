package ru.otus.crm.web;

import jakarta.servlet.DispatcherType;
import java.util.EnumSet;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.server.Server;
import ru.otus.crm.controllers.AdminSessionFilter;
import ru.otus.crm.controllers.ClientsServlet;
import ru.otus.crm.controllers.LoginServlet;
import ru.otus.crm.controllers.LogoutServlet;
import ru.otus.crm.service.AdminAuthService;
import ru.otus.crm.service.DBServiceClient;

public class CrmWebServer {

    private static final String TEMPLATES_DIR = "templates/";
    private static final String PROTECTED_PATH = "/clients";

    private final Server server;

    public CrmWebServer(int port, DBServiceClient dbServiceClient, AdminAuthService adminAuthService) {
        this.server = new Server(port);

        TemplateProcessor templateProcessor = new ThymeleafTemplateProcessor(TEMPLATES_DIR);

        var servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        configureSessionCookie(servletContextHandler);

        var loginServletHolder = new ServletHolder(new LoginServlet(templateProcessor, adminAuthService));
        // Стартовая страница ("/") и есть страница аутентификации администратора
        servletContextHandler.addServlet(loginServletHolder, "/");
        servletContextHandler.addServlet(loginServletHolder, "/login");
        servletContextHandler.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        servletContextHandler.addServlet(
                new ServletHolder(new ClientsServlet(templateProcessor, dbServiceClient)), PROTECTED_PATH);
        servletContextHandler.addFilter(
                new FilterHolder(new AdminSessionFilter()), PROTECTED_PATH, EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(servletContextHandler);
    }

    private void configureSessionCookie(ServletContextHandler servletContextHandler) {
        var sessionHandler = servletContextHandler.getSessionHandler();
        sessionHandler.setHttpOnly(true);
        sessionHandler.setSameSite(HttpCookie.SameSite.LAX);
        sessionHandler.setSecureRequestOnly(true);
    }

    public void start() throws Exception {
        server.start();
    }

    public void join() throws Exception {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
