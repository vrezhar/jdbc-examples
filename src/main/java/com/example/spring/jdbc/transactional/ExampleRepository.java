package com.example.spring.jdbc.transactional;

import com.example.Example;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Repository
public class ExampleRepository {
    private final JdbcTemplate jdbcTemplate;

    public ExampleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void insertTwoValues(int id1, String name1, int id2, String name2) {
        System.out.println("Beginning transaction to insert two values");
        insertRecord(id1, name1);
        insertRecord(id2, name2);
        System.out.println("Inserted two values");
    }

    public void insertRecord(int id, String name) {
        jdbcTemplate.update("INSERT INTO data_jdbc_test.example VALUES(?, ?)", id, name);
    }

    public List<Example> findAll() {
        return jdbcTemplate.query("SELECT * FROM data_jdbc_test.example", (rs, num) -> {
            Example example = new Example();
            example.id = rs.getInt("id");
            example.name = rs.getString("name");
            return example;
        });
    }

    @PostConstruct
    public void initDb() {
        jdbcTemplate.execute("CREATE SCHEMA data_jdbc_test CHARSET=UTF8MB4");
        jdbcTemplate.execute("CREATE TABLE data_jdbc_test.example(id int primary key, name varchar(255))");
    }

    @PreDestroy
    public void cleanup() {
        jdbcTemplate.execute("DROP SCHEMA data_jdbc_test");
    }
}
