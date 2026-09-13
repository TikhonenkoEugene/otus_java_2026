package ru.otus.crm.web;

import java.util.Map;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ThymeleafTemplateProcessor implements TemplateProcessor {

    private final TemplateEngine templateEngine;

    public ThymeleafTemplateProcessor(String templatesDir) {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(templatesDir);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @Override
    public String getPage(String templateName, Map<String, Object> data) {
        var context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateName, context);
    }
}
