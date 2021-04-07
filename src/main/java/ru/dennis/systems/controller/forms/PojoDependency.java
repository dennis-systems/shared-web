package ru.dennis.systems.controller.forms;

public interface PojoDependency <T> {
    Class<? extends T> getPojoClass();
}
