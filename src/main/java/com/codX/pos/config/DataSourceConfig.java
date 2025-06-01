package com.codX.pos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(TenantDataSourceProperties.class)
public class DataSourceConfig {

    @Autowired
    private TenantDataSourceProperties tenantDataSourceProperties;

    @Bean
    public DataSource dataSource() {
        Map<Object, Object> dataSources = new HashMap<>();

        tenantDataSourceProperties.getTenants().forEach((tenantId, config) -> {
            DataSource dataSource = DataSourceBuilder.create()
                    .url(config.getUrl())
                    .username(config.getUsername())
                    .password(config.getPassword())
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
            dataSources.put(tenantId, dataSource);
        });

        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(dataSources.get("tenant1"));
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }
}