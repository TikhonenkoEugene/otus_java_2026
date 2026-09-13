package ru.otus.crm.controllers;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.web.TemplateProcessor;

@SuppressWarnings({"java:S1989"})
public class ClientsServlet extends HttpServlet {

    private static final String CLIENTS_PAGE_TEMPLATE = "clients";
    private static final String PARAM_NAME = "name";
    private static final String TEMPLATE_ATTR_CLIENTS = "clients";

    private final transient DBServiceClient dbServiceClient;
    private final transient TemplateProcessor templateProcessor;

    public ClientsServlet(TemplateProcessor templateProcessor, DBServiceClient dbServiceClient) {
        this.templateProcessor = templateProcessor;
        this.dbServiceClient = dbServiceClient;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        renderClientsPage(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter(PARAM_NAME);
        if (name != null && !name.isBlank()) {
            dbServiceClient.saveClient(new Client(name.trim()));
        }
        // Post/Redirect/Get, чтобы обновление страницы не приводило к повторному созданию клиента
        response.sendRedirect(request.getContextPath() + "/clients");
    }

    private void renderClientsPage(HttpServletResponse response) throws IOException {
        var clients = dbServiceClient.findAll();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter()
                .println(templateProcessor.getPage(CLIENTS_PAGE_TEMPLATE, Map.of(TEMPLATE_ATTR_CLIENTS, clients)));
    }
}
