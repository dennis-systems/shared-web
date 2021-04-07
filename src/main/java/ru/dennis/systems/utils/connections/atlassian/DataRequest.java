package ru.dennis.systems.utils.connections.atlassian;

import ru.dennis.systems.db.JobElement;
import lombok.Data;

@Data
public class DataRequest {
    private Class<?> aClass;
    private LoginProvider provider;
    private Execution execution = new Execution();
    private String path = "";
    private JobElement jobElement =  (JobElement) () -> 0L;;
}
