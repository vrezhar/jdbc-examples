package com.example;

import java.sql.*;
import java.util.stream.IntStream;

public class JdbcPreparedStatementDemo {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", System.getenv("DATABASE_USER"), System.getenv("DATABASE_PASSWORD"))) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA jdbc_test CHARSET=UTF8MB4");
            statement.execute("CREATE TABLE jdbc_test.example(id int primary key, name varchar(255))");
            IntStream.range(1, 5).forEach(i -> {
                try(PreparedStatement insert = connection.prepareStatement("INSERT INTO jdbc_test.example(id, name) VALUES(?, ?)")) {
                    insert.setInt(1, i);
                    insert.setString(2, "test" + i);
                    insert.executeUpdate();
                } catch (SQLException sqlException) {
                    System.err.println(sqlException.getMessage());
                }
            });
            connection.commit();
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM jdbc_test.example WHERE id = ?")) {
               preparedStatement.setInt(1, 1);
               try(final ResultSet rs = preparedStatement.executeQuery()) {
                   rs.next();
                   System.out.printf("Expecting row with id 1, actual - %s\n", rs.getInt("id"));
                   System.out.printf("Should have no more rows(result set has next element - %s)\n", rs.next());
               }
            } catch (SQLException sqlException) {
                System.err.println(sqlException.getMessage());
            }
            statement.execute("DROP SCHEMA jdbc_test");
            statement.close();
        } catch (SQLException sqlException) {
            System.err.println(sqlException.getMessage());
        }
    }
}
