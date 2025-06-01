package com.codX.pos.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@Data
@ConfigurationProperties(prefix = "multi-tenancy")
public class TenantDataSourceProperties {

    private Map<String, DataSourceConfig> tenants;

    @Data
    public static class DataSourceConfig {
        private String url;
        private String username;
        private String password;
    }
}