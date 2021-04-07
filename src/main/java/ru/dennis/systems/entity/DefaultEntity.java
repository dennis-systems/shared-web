package ru.dennis.systems.entity;

import java.io.Serializable;

public interface DefaultEntity extends Serializable {
    public static final String ID_FIELD= "id";
    Long getId();
}
