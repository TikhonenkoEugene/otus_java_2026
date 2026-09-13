package ru.otus.crm.service;

public interface AdminAuthService {

    boolean authenticate(String login, String password);
}
