package ru.otus.crm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.ClientService;

@Controller
public class ClientController {

    private static final String CLIENTS_VIEW = "clients";
    private static final String TEMPLATE_ATTR_CLIENTS = "clients";

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping({"/", "/clients"})
    public String clients(Model model) {
        model.addAttribute(TEMPLATE_ATTR_CLIENTS, clientService.findAll());
        return CLIENTS_VIEW;
    }

    @PostMapping("/clients")
    public String createClient(@RequestParam String name) {
        if (name != null && !name.isBlank()) {
            clientService.save(new Client(null, name.trim()));
        }
        return "redirect:/clients";
    }
}
