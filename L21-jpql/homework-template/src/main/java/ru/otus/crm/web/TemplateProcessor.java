package ru.otus.crm.web;

import java.util.Map;

public interface TemplateProcessor {

    String getPage(String templateName, Map<String, Object> data);
}
