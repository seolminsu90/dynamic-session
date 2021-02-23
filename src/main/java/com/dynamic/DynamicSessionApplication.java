package com.dynamic;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import com.dynamic.repository.GameMapper;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class DynamicSessionApplication implements CommandLineRunner {

    @Autowired
    @Qualifier("dataSourceMap")
    private Map<String, DataSource> datasource;

    @Autowired
    private GameMapper gameMapper;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DynamicSessionApplication.class);
        app.run();
    }

    // EXAMPLE
    public void run(String... args) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        datasource.entrySet().forEach(e -> {
            System.out.println(e.getKey());
            System.out.println(gameMapper.selectQuery(params, e.getKey()));
        });

    }
}