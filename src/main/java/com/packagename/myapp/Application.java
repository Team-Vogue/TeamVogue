package com.packagename.myapp;

import Database.DatabaseFunctions;
import armdb.SQLQueryException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.sql.SQLException;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) throws SQLException, SQLQueryException {
        SpringApplication.run(Application.class, args);
    }

}
