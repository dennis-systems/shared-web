package ru.dennis.systems.config;

import lombok.SneakyThrows;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class FileResourceSearcher implements PathSearcher {

    private List<String> additionalResources;
    private String template;
    private String realDestination = "./";
    private SpringResourceTemplateResolver springResourceResolver;
    private boolean foundResource;

    public FileResourceSearcher(List<String> additionalResources, String template, SpringResourceTemplateResolver springResourceResolver) {
        this.additionalResources = additionalResources;
        this.template = template;
        realDestination = template;
        this.springResourceResolver = springResourceResolver;
    }

    @Override
    public boolean hasPath(String path) {
        return new File(path).exists() || inOtherLocationsExists(template);
    }

    private boolean inOtherLocationsExists(String template) {
        if (foundResource){
            return true;
        }
        for (String path : additionalResources ){
            String newPath = path + template;
            if (new File(newPath).exists()){
                realDestination = newPath ;
                this.foundResource = true;
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    @Override
    public Reader reader(String path, SpringResourceTemplateResolver springResourceResolver) {
        return new FileReader(realDestination);
    }

    @Override
    public PathSearcher next() {
        return ClassPathResourceSearcher.INSTANCE;
    }
}
