package ru.dennis.systems.config;

import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.Reader;
import java.io.StringReader;

public class StringResourceSearcher implements PathSearcher {
    @Override
    public boolean hasPath(String path) {
        return path != null;
    }

    @Override
    public Reader reader(String path, SpringResourceTemplateResolver springResourceResolver) {
        springResourceResolver.setTemplateMode(TemplateMode.TEXT);
        if (path.endsWith(".html")){
            path = path.substring(0, path.length() - ".html".length());
        }
        return new StringReader(path.replaceFirst("templates/", ""));
    }

    @Override
    public PathSearcher next() {
        return null;
    }
}
