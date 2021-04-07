package ru.dennis.systems.config;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UniversalSpringResourceResolver extends SpringResourceTemplateResolver {
    private final List<String> additionalResources = new ArrayList<>();
    

    @Override
    protected ITemplateResource computeTemplateResource(
            final IEngineConfiguration configuration, final String ownerTemplate, final String template, final String resourceName, final String characterEncoding, final Map<String, Object> templateResolutionAttributes) {

        return new UniversalSpringResourceTemplateResource(template, resourceName,  additionalResources, this);
    }
    public void additionalPaths(String path){
        additionalResources.add(path);
    }
}
