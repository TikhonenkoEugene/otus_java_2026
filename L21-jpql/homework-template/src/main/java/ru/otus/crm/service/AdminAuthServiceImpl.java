package ru.otus.crm.service;

import java.util.Objects;

/**
 * Простейшая реализация аутентификации администратора с захардкоженными учётными данными.
 * Отдельного хранилища пользователей в этом ДЗ нет, поэтому логин/пароль администратора заданы константами.
 */
public class AdminAuthServiceImpl implements AdminAuthService {

    private final String adminLogin;
    private final String adminPassword;

    public AdminAuthServiceImpl(String adminLogin, String adminPassword) {
        this.adminLogin = adminLogin;
        this.adminPassword = adminPassword;
    }

    @Override
    public boolean authenticate(String login, String password) {
        return Objects.equals(adminLogin, login) && Objects.equals(adminPassword, password);
    }
}
