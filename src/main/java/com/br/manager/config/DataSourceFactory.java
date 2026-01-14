package com.br.manager.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataSourceFactory {
    @Value("${tenants}")
    private List<String> tenants;

    @Autowired
    private DataSourceDefaultConfig dataSourceDefaultConfig;

    private Map<Object, Object> resolvedDataSources = new HashMap<>();

    public Map<Object, Object> getDataSources(){
        for (String tenant : tenants) {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            try {
                HikariDataSource ds = getHikariDataSource(tenant);
                resolvedDataSources.put(tenant, ds);
            } catch (Exception exp) {
                throw new RuntimeException("Problem in tenant datasource:" + exp);
            }
        }
        return resolvedDataSources;
    }

    private HikariDataSource getHikariDataSource(String tenant) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dataSourceDefaultConfig.getUrl() + tenant);
        config.setUsername(dataSourceDefaultConfig.getUsername());
        config.setPassword(dataSourceDefaultConfig.getPassword());
        config.setDriverClassName(dataSourceDefaultConfig.getDriverClassName());

        // 2. CONFIGURAÇÃO DO POOL
        config.setMaximumPoolSize(5);  // Define o máximo de conexões (Ex: 5)
        config.setMinimumIdle(2);      // Conexões mínimas sempre abertas
        config.setPoolName("HikariPool-" + tenant); // Facilita ver no Log qual banco é qual

        HikariDataSource ds = new HikariDataSource(config);
        return ds;
    }
}
