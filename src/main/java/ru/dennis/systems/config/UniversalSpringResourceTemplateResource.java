package ru.dennis.systems.config;

import lombok.SneakyThrows;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.Reader;
import java.util.List;

public class UniversalSpringResourceTemplateResource implements ITemplateResource {
    private final String template;
    private final String localPath;
    private String resourceName;

    FileResourceSearcher fileResourceSearcher;
    private SpringResourceTemplateResolver springResourceResolver;


    public UniversalSpringResourceTemplateResource(String template, String resourceName, List<String> additionalResources, SpringResourceTemplateResolver springResourceResolver) {
        this.springResourceResolver = springResourceResolver;
        if (!template.endsWith(".html")) {
            template = template + ".html";
        }
        this.template = template;

        this.resourceName = resourceName;
        this.localPath = transform(resourceName);
        fileResourceSearcher = new FileResourceSearcher(additionalResources, template, springResourceResolver);
    }

    private String transform(String resourceName) {
        if (resourceName == null) {
            return "";
        }
        return resourceName.replace("classpath:", "").replace("//", "/");

    }

    @Override
    public String getDescription() {
        return "Resource: " + resourceName;
    }

    @Override
    public String getBaseName() {
        return template;
    }

    @Override
    public boolean exists() {
        return fileResourceSearcher.has(resourceName);
    }

    @Override
    public Reader reader() {
        return fileResourceSearcher.get(localPath, springResourceResolver);
    }

    @SneakyThrows
    @Override
    public ITemplateResource relative(String relativeLocation) {
        this.resourceName = relativeLocation;
        return this;
    }
}
