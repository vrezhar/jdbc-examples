package com.example.spring.jdbc.transactional;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringDataJdbcTransactionDemo {
    public static void main(String[] args) {
        final var context = new AnnotationConfigApplicationContext(ApplicationContextConfig.class);
        final ExampleRepository exampleRepository = context.getBean(ExampleRepository.class);
        System.out.println("Data currently in DB - " + exampleRepository.findAll());
        exampleRepository.insertRecord(1, "test");
        System.out.println("Data after one insert - " + exampleRepository.findAll());
        exampleRepository.insertTwoValues(2, "test1", 3, "test2");
        context.close();
    }
}
