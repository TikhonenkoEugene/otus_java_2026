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
import java.util.Set;

/**
 * Пропускает на защищённые страницы только запросы с уже аутентифицированной сессией администратора,
 * иначе перенаправляет на страницу логина.
 */
public class AdminSessionFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of("/login", "/logout");

    @Override
    public void init(FilterConfig filterConfig) {
        // Not implemented
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        if (isPublicPath(request) || isAuthenticated(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    private boolean isPublicPath(HttpServletRequest request) {
        return PUBLIC_PATHS.contains(request.getServletPath());
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        var session = request.getSession(false);
        return session != null && session.getAttribute(LoginServlet.SESSION_ATTR_ADMIN) != null;
    }

    @Override
    public void destroy() {
        // Not implemented
    }
}
