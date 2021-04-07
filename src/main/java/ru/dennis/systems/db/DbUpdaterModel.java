package ru.dennis.systems.db;

import ru.dennis.systems.entity.db.DbInjection;
import lombok.Data;

import java.util.List;

@Data
public class DbUpdaterModel {
    List<DbInjection> dbInjectionList;
}