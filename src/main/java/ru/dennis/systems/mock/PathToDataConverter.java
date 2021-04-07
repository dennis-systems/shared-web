package ru.dennis.systems.mock;

import org.springframework.http.HttpMethod;

public interface PathToDataConverter {
     String convert(String s, HttpMethod query, Object data);
}
