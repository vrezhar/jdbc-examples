package com.example;

import java.sql.*;
import java.util.stream.IntStream;

public class JdbcSavepointDemo {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306", System.getenv("DATABASE_USER"), System.getenv("DATABASE_PASSWORD"))) {
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.execute("CREATE SCHEMA jdbc_test CHARSET=UTF8MB4");
            statement.execute("CREATE TABLE jdbc_test.example(id int primary key, name varchar(255))");
            Savepoint savepoint = null;
            for(int i = 1; i < 5; ++i) {
                try(PreparedStatement insert = connection.prepareStatement("INSERT INTO jdbc_test.example(id, name) VALUES(?, ?)")) {
                    insert.setInt(1, i);
                    insert.setString(2, "test" + i);
                    insert.executeUpdate();
                    if(i == 3) {
                        savepoint = connection.setSavepoint();
                    }
                } catch (SQLException sqlException) {
                    System.err.println(sqlException.getMessage());
                }
            }
            connection.rollback(savepoint);
            connection.commit();
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) as count FROM jdbc_test.example")) {
                try(final ResultSet rs = preparedStatement.executeQuery()) {
                    rs.next();
                    System.out.printf("Expecting row count of 3, actual - %s\n", rs.getInt("count"));
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
