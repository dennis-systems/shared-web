package ru.dennis.systems.entity.db;

import lombok.Data;

import java.util.List;

@Data
public class DbInjectionModel {
    private List<DbInjection> scripts;
}
