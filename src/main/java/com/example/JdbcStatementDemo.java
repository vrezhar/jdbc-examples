package com.example;

import java.sql.*;

public class JdbcStatementDemo {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", System.getenv("DATABASE_USER"), System.getenv("DATABASE_PASSWORD"))) {
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA jdbc_test CHARSET=UTF8MB4");
            statement.execute("CREATE TABLE jdbc_test.example(id int primary key, name varchar(255))");
            statement.execute("INSERT INTO jdbc_test.example(id, name) VALUES (1, 'test'), (2, 'test2')");
            statement.execute("SELECT * FROM jdbc_test.example");
            try (final ResultSet rs = statement.getResultSet()) {
                while (rs.next()) {
                    System.out.printf("Example row - id: %s, name: %s\n", rs.getInt("id"), rs.getString("name"));
                }
            }
            System.out.println("Success!");
            statement.execute("DROP SCHEMA jdbc_test");
            statement.close();
        } catch (SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }
    }
}
