package com.br.manager.infra.multitenant;

import com.br.manager.config.DataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

@Profile({"dev", "prod"})
@Configuration
public class DefinitionDataSourceConfiguration {
    private static final Logger logger = LogManager.getLogger(DefinitionDataSourceConfiguration.class);

    private final static String DEFAULT_TENANT = "manager";

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Bean
    public DataSource dataSourceConfig() {

        Map<Object, Object> resolvedDataSources = dataSourceFactory.getDataSources();
        AbstractRoutingDataSource dataSource = new MultitenantDataSource();
        logger.info("####### DataBase default: " + DEFAULT_TENANT);
        dataSource.setDefaultTargetDataSource(resolvedDataSources.get(DEFAULT_TENANT));
        dataSource.setTargetDataSources(resolvedDataSources);
        logger.info("####### Lista de DataBases: " + resolvedDataSources.toString());
        dataSource.afterPropertiesSet();
        return dataSource;
    }

}