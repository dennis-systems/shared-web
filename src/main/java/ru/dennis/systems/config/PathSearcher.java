package ru.dennis.systems.config;

import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.io.Reader;

public interface PathSearcher {
    boolean hasPath(String path);
    Reader reader(String path, SpringResourceTemplateResolver springResourceResolver);
    PathSearcher next();
    default Reader get(String path, SpringResourceTemplateResolver springResourceResolver){
        if (hasPath(path)){
            return reader(path, springResourceResolver);
        } else{
            return next() == null ? null : next().get(path, springResourceResolver);
        }
    }

    default boolean has(String path){
        if (!hasPath(path)){
            return next() != null && next().has(path);
        } else {
            return true;
        }
    }
}
