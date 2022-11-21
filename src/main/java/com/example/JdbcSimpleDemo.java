package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcSimpleDemo {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", System.getenv("DATABASE_USER"), System.getenv("DATABASE_PASSWORD"))) {
            connection.setAutoCommit(false);
            System.out.println("Success!");
        } catch (SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }
    }
}
