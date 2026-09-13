package ru.otus.crm.controllers;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Пропускает на защищённые страницы только запросы с уже аутентифицированной сессией администратора,
 * иначе перенаправляет на страницу логина.
 */
public class AdminSessionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Not implemented
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        var session = request.getSession(false);
        if (session != null && session.getAttribute(LoginServlet.SESSION_ATTR_ADMIN) != null) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Not implemented
    }
}
