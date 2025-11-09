package com.br.manager.config;

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
                dataSourceBuilder.driverClassName(dataSourceDefaultConfig.getDriverClassName());
                dataSourceBuilder.username(dataSourceDefaultConfig.getUsername());
                dataSourceBuilder.password(dataSourceDefaultConfig.getPassword());
                dataSourceBuilder.url(dataSourceDefaultConfig.getUrl() + tenant  );
                resolvedDataSources.put(tenant, dataSourceBuilder.build());
            } catch (Exception exp) {
                throw new RuntimeException("Problem in tenant datasource:" + exp);
            }
        }
        return resolvedDataSources;
    }
}
