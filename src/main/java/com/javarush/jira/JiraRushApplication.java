package com.javarush.jira;

import com.javarush.jira.common.internal.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableCaching
public class JiraRushApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(JiraRushApplication.class, args);
        DataSourceProperties dataSource = run.getBean(DataSourceProperties.class);
        System.out.println("Database URL: " + dataSource.getUrl());
        System.out.println("Database Username: " + dataSource.getUsername());
    }
}
