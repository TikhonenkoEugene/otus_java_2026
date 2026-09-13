package ru.otus.crm.service;

import java.util.List;
import ru.otus.crm.model.Client;

public interface ClientService {

    List<Client> findAll();

    Client save(Client client);
}
