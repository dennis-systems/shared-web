package ru.dennis.systems.utils.connections.atlassian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Execution implements Closeable {
    private String name;

    private List<Execution> executions = new ArrayList<>();

    private final long start = new Date().getTime();

    private long executionTime;

    public Execution(String name) {
        this.name = name;
    }

    public Execution addExecution(String name){
        Execution execution = new Execution(name);
        executions.add(execution);
        return execution;
    }

    public Execution finish(){
        this.executionTime = new Date().getTime() - start;
        return this;
    }

    @Override
    public void close() {
        finish();
    }

    public String toString(){
        return name + executions.size();
    }
}
