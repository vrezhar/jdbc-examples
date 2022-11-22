package com.example.spring.jdbc.plain;

import com.example.Example;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpringDataJdbcDemo {
    public static void main(String[] args) {
        final var context = new AnnotationConfigApplicationContext(ApplicationContextConfig.class);
        final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        final NamedParameterJdbcTemplate namedJdbcTemplate = context.getBean(NamedParameterJdbcTemplate.class);
        jdbcTemplate.execute("CREATE SCHEMA data_jdbc_test CHARSET=UTF8MB4");
        jdbcTemplate.execute("CREATE TABLE data_jdbc_test.example(id int primary key, name varchar(255))");
        int rowNumber = jdbcTemplate.update("INSERT INTO data_jdbc_test.example(id, name) VALUES(?, ?)", 1, "test");
        System.out.println("Rows inserted after singular update - " + rowNumber);
        int[] batchRows = jdbcTemplate.batchUpdate("INSERT INTO data_jdbc_test.example(id, name) VALUES(?, ?)",
                IntStream.range(2, 5).mapToObj(i -> new Object[]{i, "test" + i}).collect(Collectors.toList()));
        System.out.printf("Batch update count - %s, expecting 3\n", batchRows.length);
        Integer totalCount = jdbcTemplate.queryForObject("SELECT count(*) FROM data_jdbc_test.example", Integer.class);
        System.out.printf("Total row count - %s, expecting 4\n", totalCount);
        List<Example> allExamplesMapped = jdbcTemplate.query("SELECT * FROM data_jdbc_test.example", (resultSet, currentRowNumber) -> {
            Example example = new Example();
            example.id = resultSet.getInt("id");
            example.name = resultSet.getString("name");
            return example;
        });
        System.out.println("All examples - " + allExamplesMapped);
        List<Example> allExamplesExtracted = jdbcTemplate.query("SELECT * FROM data_jdbc_test.example WHERE id < ?", (resultSet) -> {
            final List<Example> examples = new ArrayList<>(3);
            while (resultSet.next()) {
                Example example = new Example();
                example.id = resultSet.getInt("id");
                example.name = resultSet.getString("name");
                examples.add(example);
            }
            return examples;
        }, 4);
        System.out.printf("Filtered out examples - %s(expecting 3)\n", allExamplesExtracted);
        Example singleExample = namedJdbcTemplate.queryForObject("SELECT * FROM data_jdbc_test.example WHERE id=:id", Map.of("id", 1), (resultSet, currentRowNumber) -> {
            Example example = new Example();
            example.id = resultSet.getInt("id");
            example.name = resultSet.getString("name");
            return example;
        });
        System.out.println("Fetched single example: " + singleExample);
        jdbcTemplate.execute("DROP SCHEMA data_jdbc_test");
        context.close();
    }
}
