package com.br.manager.infra.flyway;

import com.br.manager.config.DataSourceFactory;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

@Component
@Profile({"dev", "prod"})
public class MigrationExecute {
    private static final Logger logger = LogManager.getLogger(MigrationExecute.class);

    @Value("${spring.flyway.enabled}")
    private boolean isFlyway;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @PostConstruct
    public void migrations(){

        if ( isFlyway ) {
            logger.info("####### Inicio Migrações FLYWAY ####### ");
            for (Map.Entry<Object, Object> register : dataSourceFactory.getDataSources().entrySet()) {

                logger.info("#######  Migração FLYWAY " + register.getKey());
                DataSource dataSource = (DataSource) register.getValue();
                Flyway flyway = Flyway.configure().dataSource(dataSource).load();
                flyway.migrate();

                logger.info("####### Final Migração FLYWAY " + register.getKey());
            }
        }
    }
}
