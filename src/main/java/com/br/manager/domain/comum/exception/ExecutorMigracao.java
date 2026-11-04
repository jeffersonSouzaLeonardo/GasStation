package com.br.manager.domain.comum.exception;

import com.br.manager.domain.configuracao.FabricaDataSource;
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
public class ExecutorMigracao {
    private static final Logger logger = LogManager.getLogger(ExecutorMigracao.class);

    @Value("${spring.flyway.enabled}")
    private boolean isFlyway;

    @Autowired
    private FabricaDataSource dataOrigemFactory;

    @PostConstruct
    public void migrations(){

        if ( isFlyway ) {
            logger.info("####### Inicio Migrações FLYWAY ####### ");
            for (Map.Entry<Object, Object> register : dataOrigemFactory.getDataSources().entrySet()) {

                logger.info("#######  Migração FLYWAY " + register.getKey());
                DataSource dataOrigem = (DataSource) register.getValue();
                Flyway flyway = Flyway.configure().dataSource(dataOrigem).load();
                flyway.migrate();

                logger.info("####### Final Migração FLYWAY " + register.getKey());
            }
        }
    }
}
