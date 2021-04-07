package ru.dennis.systems.beans;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Service

public class IsolatedDatabaseBean {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.database}")
    private String database;


    @SneakyThrows
    public void sqlWithConnection(String sql) {
        this.sqlWithConnection(sql, false);
    }

    @SneakyThrows
    public void sqlWithConnection(String sql, boolean root) {

        String databaseURL = root ? url.replaceAll("/" + database, "/") : url;

        try (Connection connection = DriverManager.getConnection(databaseURL, userName, password)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(sql);

            }

        }
    }

    @SneakyThrows
    public boolean sqlWithResult(String sql, boolean root) {

        String databaseURL = root ? url.replaceAll("/" + database, "/") : url;

        try (Connection connection = DriverManager.getConnection(databaseURL, userName, password)) {

            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery(sql)) {
                    return result.next();
                }
            }
        }
    }

    @SneakyThrows
    public boolean sqlWithResultOnDb(String db, String sql, boolean root) {

        String databaseURL = root ? url.replaceAll("/" + database, "/" + db) : url;

        try (Connection connection = DriverManager.getConnection(databaseURL, userName, password)) {

            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery(sql)) {
                    return result.next();
                }

            }


        }
    }
    @SneakyThrows
    public void sqlWithConnection(String db, String sql, boolean root) {

        String databaseURL = root ? url.replaceAll("/" + database, "/" + db) : url;


        try (Connection connection = DriverManager.getConnection(databaseURL, userName, password)) {
            connection.setAutoCommit(true);
            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(sql);

            }

        }
    }


}
