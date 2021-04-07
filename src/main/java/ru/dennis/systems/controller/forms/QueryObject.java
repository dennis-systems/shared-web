package ru.dennis.systems.controller.forms;

public interface QueryObject<T> {
    T get(String key);
}
