package com.br.manager.domain.configuracao.multitenant;

import com.br.manager.domain.configuracao.FabricaDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

@Profile({"dev"})
@Configuration
public class ConfiguracaoDefinicaoDataSource {
    private static final Logger logger = LogManager.getLogger(ConfiguracaoDefinicaoDataSource.class);

    private final static String DEFAULT_TENANT = "manager";

    @Autowired
    private FabricaDataSource dataOrigemFactory;

    @Bean
    public DataSource dataOrigemConfig() {

        Map<Object, Object> resolvedDataSources = dataOrigemFactory.getDataSources();
        AbstractRoutingDataSource dataOrigem = new MultitenantDataSource();
        logger.info("####### DataBase default: " + DEFAULT_TENANT);
        dataOrigem.setDefaultTargetDataSource(resolvedDataSources.get(DEFAULT_TENANT));
        dataOrigem.setTargetDataSources(resolvedDataSources);
        logger.info("####### Lista de DataBases: " + resolvedDataSources.toString());
        dataOrigem.afterPropertiesSet();
        return dataOrigem;
    }

}