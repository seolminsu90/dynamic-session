package com.dynamic.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.dynamic.model.GameDatasource;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.gamedb")
public class DynamicJDBCConfig {

    public List<GameDatasource> datasource;

    public void setDatasource(List<GameDatasource> datasource) {
        this.datasource = datasource;
    }

    @Order(1)
    @Bean(name = "dataSourceMap")
    public Map<String, DataSource> dataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        datasource.forEach(one -> {
            dataSourceMap.put(one.getWorld(),
                    DataSourceBuilder.create()
                                     .driverClassName(one.getDriverClassName())
                                     .url(one.getJdbcUrl())
                                     .username(one.getUsername())
                                     .password(one.getPassword())
                                     .build());
        });
        return dataSourceMap;
    }

    @Order(2)
    @Bean(name = "sqlSessionFactoryMap")
    public Map<String, SqlSessionFactory> sqlSessionFactoryMap(Map<String, DataSource> dataSourceMap,
            ApplicationContext applicationContext) {
        Map<String, SqlSessionFactory> sqlSessionFactoryMap = new HashMap<>();
        dataSourceMap.entrySet().forEach(entry -> {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(entry.getValue());
            try {
                sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:xml/*.xml"));
                sqlSessionFactoryMap.put(entry.getKey(), sqlSessionFactoryBean.getObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return sqlSessionFactoryMap;
    }

    @Order(2)
    @Bean(name = "transactionManagerMap")
    public Map<String, DataSourceTransactionManager> transactionManagerMap(Map<String, DataSource> dataSourceMap,
            ApplicationContext applicationContext) {
        Map<String, DataSourceTransactionManager> transactionManagerMap = new HashMap<>();
        dataSourceMap.entrySet()
                     .forEach(entry -> transactionManagerMap.put(entry.getKey(),
                             new DataSourceTransactionManager(entry.getValue())));
        return transactionManagerMap;
    }

    @Order(3)
    @Bean(name = "sqlSessionTemplateMap")
    public Map<String, SqlSessionTemplate> sqlSessionTemplateMap(Map<String, SqlSessionFactory> sqlSessionFactoryMap,
            ApplicationContext applicationContext) {
        Map<String, SqlSessionTemplate> sqlSessionTemplateMap = new HashMap<>();
        sqlSessionFactoryMap.entrySet().forEach(entry -> {
            sqlSessionTemplateMap.put(entry.getKey(), new SqlSessionTemplate(entry.getValue()));
        });
        return sqlSessionTemplateMap;
    }

}
