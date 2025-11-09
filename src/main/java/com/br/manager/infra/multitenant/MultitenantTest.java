package com.br.manager.infra.multitenant;

import com.br.manager.config.DataSourceDefaultConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile({"dev", "prod"})
@Configuration
public class MultitenantTest {

    @Value("${tenants}")
    private List<String> tenants;

    @Autowired
    private DataSourceDefaultConfig dataSourceDefaultConfig;

    private Map<Object, Object> resolvedDataSources = new HashMap<>();

    @Bean
    public Map<Object, Object> dataSources() {

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