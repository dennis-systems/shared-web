package ru.dennis.systems.config;


import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.io.InputStreamReader;
import java.io.Reader;

public class ClassPathResourceSearcher implements PathSearcher {
    public static final ClassPathResourceSearcher INSTANCE = new ClassPathResourceSearcher();

    @Override
    public boolean hasPath(String path) {
        try {
            return new ClassPathResource(path).exists();
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public Reader reader(String path, SpringResourceTemplateResolver springResourceResolver) {
        try {
            return new InputStreamReader(new ClassPathResource(path).getInputStream());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PathSearcher next() {
        return JarResourceSearcher.INSTANCE;
    }
}
