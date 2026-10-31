package com.br.manager.domain.price_acquisition.support;

import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;
import com.br.manager.domain.operational.repository.ProductRepository;
import com.br.manager.domain.operational.repository.SupplierRepository;
import com.br.manager.domain.operational.repository.TankRepository;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cria, via repositorios, os pre-requisitos de outros dominios (usuario, fornecedor,
 * produto e tanque) necessarios aos testes de integracao de precos e aquisicao.
 */
@TestComponent
public class PriceAcquisitionTestData {

    /** Faz o RestAssured ler numeros JSON como BigDecimal, permitindo comparar valores monetarios sem perda. */
    public static final RestAssuredConfig DECIMAL_CONFIG = RestAssuredConfig.config()
            .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TankRepository tankRepository;

    /** Serializa DTOs com datas ISO (o ObjectMapper padrao do RestAssured nao conhece java.time). */
    public static String toJson(Object body) {
        try {
            return JSON.writeValueAsString(body);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public UUID createUser() {
        return createUser(true);
    }

    public UUID createInactiveUser() {
        return createUser(false);
    }

    public UUID createSupplier() {
        return createSupplier(true);
    }

    public UUID createInactiveSupplier() {
        return createSupplier(false);
    }

    public UUID createProduct() {
        Product product = new Product();
        product.setSku("GAS-" + UUID.randomUUID().toString().substring(0, 8));
        product.setName("Gasolina Comum");
        product.setProductType(ProductTypeEnum.FUEL);
        product.setUnit(UnitTypeEnum.L);
        product.setActive(true);
        return productRepository.saveAndFlush(product).getId();
    }

    public UUID createTank(UUID productId) {
        Tank tank = new Tank();
        tank.setCode("TQ-" + UUID.randomUUID().toString().substring(0, 8));
        tank.setProductId(productId);
        tank.setCapacityLiters(new BigDecimal("30000.0000"));
        tank.setActive(true);
        return tankRepository.saveAndFlush(tank).getId();
    }

    private UUID createUser(boolean active) {
        User user = new User();
        user.setName("Comprador Teste");
        user.setEmail("comprador-" + UUID.randomUUID() + "@posto.com.br");
        user.setPasswordHash("hash");
        user.setActive(active);
        return userRepository.saveAndFlush(user).getId();
    }

    private UUID createSupplier(boolean active) {
        Supplier supplier = new Supplier();
        supplier.setLegalName("Distribuidora Teste SA");
        supplier.setCnpjCpf("12345678000190");
        supplier.setActive(active);
        return supplierRepository.saveAndFlush(supplier).getId();
    }
}
