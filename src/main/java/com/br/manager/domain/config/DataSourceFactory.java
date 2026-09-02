package com.br.manager.domain.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        config.setJdbcUrl(resolveJdbcUrl(dataSourceDefaultConfig.getUrl(), tenant));
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

    private String resolveJdbcUrl(String baseUrl, String tenant) {
        int queryStart = baseUrl.indexOf('?');
        String queryString = queryStart >= 0 ? baseUrl.substring(queryStart) : "";
        String urlWithoutQuery = queryStart >= 0 ? baseUrl.substring(0, queryStart) : baseUrl;

        if (urlWithoutQuery.contains("{tenant}")) {
            return urlWithoutQuery.replace("{tenant}", tenant) + queryString;
        }

        int protocolSeparator = urlWithoutQuery.indexOf("://");
        int firstPathSeparator = protocolSeparator >= 0
                ? urlWithoutQuery.indexOf('/', protocolSeparator + 3)
                : -1;

        if (firstPathSeparator < 0) {
            return urlWithoutQuery + "/" + tenant + queryString;
        }

        String urlHostPrefix = urlWithoutQuery.substring(0, firstPathSeparator + 1);
        return urlHostPrefix + tenant + queryString;
    }
}
