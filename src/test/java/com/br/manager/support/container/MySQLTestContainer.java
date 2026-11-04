package com.br.manager.support.container;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Container MySQL exclusivo para testes, compartilhado por toda a execucao da JVM
 * (singleton container pattern) para evitar subir um banco por classe de teste.
 * A imagem pode ser sobrescrita via -Dtest.mysql.image=mysql:8.4
 */
public final class MySQLTestContainer {

    private static final String DEFAULT_IMAGE = "mysql:8.4";
    private static final String DATABASE_NAME = "gerente_test";

    private static final MySQLContainer<?> INSTANCE = createContainer();

    private MySQLTestContainer() {
    }

    public static MySQLContainer<?> getINSTANCE() {
        return INSTANCE;
    }

    private static MySQLContainer<?> createContainer() {
        String image = System.getProperty("test.mysql.image", DEFAULT_IMAGE);
        MySQLContainer<?> container = new MySQLContainer<>(DockerImageName.parse(image).asCompatibleSubstituteFor("mysql"))
                .withDatabaseName(DATABASE_NAME)
                .withUsername("test")
                .withPassword("test")
                .withUrlParam("serverTimezone", "UTC")
                .withUrlParam("allowPublicKeyRetrieval", "true")
                .withUrlParam("useSSL", "false");
        container.start();
        return container;
    }
}
