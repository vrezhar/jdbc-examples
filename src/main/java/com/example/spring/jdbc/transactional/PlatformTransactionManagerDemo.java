package com.example.spring.jdbc.transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class PlatformTransactionManagerDemo {
    public static void main(String[] args) {
        final var context = new AnnotationConfigApplicationContext(ApplicationContextConfig.class);
        final ExampleRepository exampleRepository = context.getBean(ExampleRepository.class);
        final var platformTransactionManager = context.getBean(PlatformTransactionManager.class);
        final var template = new TransactionTemplate(platformTransactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        System.out.println("Data currently in DB - " + exampleRepository.findAll());
        template.execute(status -> {
            exampleRepository.insertRecord(1, "test1");
            exampleRepository.insertRecord(2, "test2");
            return 0;
        });
        System.out.println("Data in DB after update - " + exampleRepository.findAll());
        template.execute(status -> {
            exampleRepository.insertRecord(3, "test3");
            exampleRepository.insertRecord(4, "test4");
            status.setRollbackOnly();
            return 0;
        });
        System.out.println("Data in DB after rolled back transaction - " + exampleRepository.findAll());
        template.execute(status -> {
            exampleRepository.insertRecord(3, "test3");
            exampleRepository.insertRecord(4, "test4");
            final var save = status.createSavepoint();
            exampleRepository.insertRecord(5, "test5");
            status.rollbackToSavepoint(save);
            return 0;
        });
        System.out.println("Data in DB after partially rolled back transaction - " + exampleRepository.findAll());
        context.close();
    }
}
