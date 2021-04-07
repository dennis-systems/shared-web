package ru.dennis.systems.config;

import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.io.Reader;

public class JarResourceSearcher implements PathSearcher {
    public static final PathSearcher INSTANCE = new JarResourceSearcher();

    @Override
    public boolean hasPath(String path) {
        return false;
    }

    @Override
    public Reader reader(String path, SpringResourceTemplateResolver springResourceResolver) {
        return null;
    }

    @Override
    public PathSearcher next() {
        return new StringResourceSearcher();
    }
}
