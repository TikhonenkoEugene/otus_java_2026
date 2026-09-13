package ru.otus.crm.controllers;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import ru.otus.crm.service.AdminAuthService;
import ru.otus.crm.web.TemplateProcessor;

@SuppressWarnings({"java:S1989"})
public class LoginServlet extends HttpServlet {

    public static final String SESSION_ATTR_ADMIN = "admin";

    private static final String LOGIN_PAGE_TEMPLATE = "login";
    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";
    private static final String TEMPLATE_ATTR_ERROR = "error";

    private final transient TemplateProcessor templateProcessor;
    private final transient AdminAuthService adminAuthService;

    public LoginServlet(TemplateProcessor templateProcessor, AdminAuthService adminAuthService) {
        this.templateProcessor = templateProcessor;
        this.adminAuthService = adminAuthService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        renderLoginPage(response, Map.of());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter(PARAM_LOGIN);
        String password = request.getParameter(PARAM_PASSWORD);

        if (adminAuthService.authenticate(login, password)) {
            var session = request.getSession();
            session.setAttribute(SESSION_ATTR_ADMIN, login);
            response.sendRedirect(request.getContextPath() + "/clients");
        } else {
            renderLoginPage(response, Map.of(TEMPLATE_ATTR_ERROR, "Неверный логин или пароль"));
        }
    }

    private void renderLoginPage(HttpServletResponse response, Map<String, Object> extraParams) throws IOException {
        var paramsMap = new HashMap<>(extraParams);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println(templateProcessor.getPage(LOGIN_PAGE_TEMPLATE, paramsMap));
    }
}
