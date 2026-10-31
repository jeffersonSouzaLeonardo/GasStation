package com.br.manager.domain.operational.support;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.PumpInputDTO;
import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.fixture.ProductFixture;
import com.br.manager.domain.operational.fixture.PumpFixture;
import com.br.manager.domain.operational.fixture.TankFixture;
import com.br.manager.support.integration.AbstractIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

/**
 * Base dos ITs de Produto/Tanque/Bomba/Bico: centraliza a criacao via API
 * dos cadastros pre-requisito.
 */
@Import(OperationalATestData.class)
public abstract class OperationalAIntegrationTest extends AbstractIntegrationTest {

    protected static final String PRODUCTS = "/api/products";
    protected static final String STATION_PRODUCTS = "/api/station-products";
    protected static final String TANKS = "/api/tanks";
    protected static final String PUMPS = "/api/pumps";
    protected static final String NOZZLES = "/api/nozzles";

    @Autowired
    protected OperationalATestData testData;

    protected String createProduct(ProductInputDTO input) {
        return create(PRODUCTS, input);
    }

    protected UUID createFuelProduct() {
        return UUID.fromString(createProduct(ProductFixture.fuelInput()));
    }

    protected UUID createConvenienceProduct() {
        return UUID.fromString(createProduct(ProductFixture.convenienceInput()));
    }

    protected String createTank(TankInputDTO input) {
        return create(TANKS, input);
    }

    protected UUID createTank(UUID fuelProductId) {
        return UUID.fromString(createTank(TankFixture.validInput(fuelProductId)));
    }

    protected String createPump(PumpInputDTO input) {
        return create(PUMPS, input);
    }

    protected UUID createPump() {
        return UUID.fromString(createPump(PumpFixture.validInput()));
    }

    protected String create(String path, Object input) {
        return request().body(input).when().post(path).then().statusCode(201).extract().path("id");
    }

    protected void delete(String path, String id) {
        request().when().delete(path + "/{id}", id).then().statusCode(204);
    }
}
