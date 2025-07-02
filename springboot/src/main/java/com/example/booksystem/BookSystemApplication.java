package com.example.booksystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BookSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookSystemApplication.class, args);
        System.out.println("图书管理系统启动成功！");
        System.out.println("API文档地址: http://localhost:8080/api");
    }
}