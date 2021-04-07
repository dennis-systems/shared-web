package ru.dennis.systems.utils;

import com.sun.istack.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SimpleEvaluator {

    private final Environment environment;

    public SimpleEvaluator(Environment environment) {
        this.environment = environment;
    }

    public String evaluate(String sql, int position, @NotNull Map<String, String> params) {
        if (!sql.contains("${")) {
            return sql.replace("::", "\\:\\:");
        }

        int newPosition = sql.indexOf("${", position);

        int newEndPosition = sql.indexOf("}", newPosition);

        if (newEndPosition == -1) {
            return sql;
        }

        String expr = sql.substring(newPosition, newEndPosition + 1);


        String key = expr.substring(2, expr.length() - 1);

        String tagValue = params.get(key);

        if (tagValue == null) {
            tagValue = environment.getProperty(key);
        }

        if (tagValue != null) {
            sql = sql.replace(expr, tagValue);
        } else {

            throw new IllegalArgumentException("expression : " + expr + " not found in Properties");
        }

        return evaluate(sql, newEndPosition, params);
    }
}
