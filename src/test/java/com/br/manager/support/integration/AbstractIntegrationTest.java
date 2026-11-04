package com.br.manager.support.integration;

import com.br.manager.support.container.MySQLTestContainer;
import com.br.manager.support.database.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import static io.restassured.RestAssured.given;

/**
 * Base para testes de integracao HTTP (RestAssured) contra a aplicacao real
 * rodando em porta aleatoria e conectada ao MySQL do Testcontainers.
 * Cada teste comeca com o banco limpo.
 */
@ActiveProfiles("test")
@Import(DatabaseCleaner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    protected static final String TENANT_HEADER = "x-tenant-id";
    protected static final String TEST_TENANT = "gerente";

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        MySQLContainer<?> mysql = MySQLTestContainer.getINSTANCE();
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeEach
    void setUpIntegrationTest() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
        databaseCleaner.clean();
    }

    protected RequestSpecification request() {
        return given().spec(new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader(TENANT_HEADER, TEST_TENANT)
                .build());
    }
}
