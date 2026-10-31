package com.br.manager.domain.operational.fixture;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Produto.
 */
public final class ProductFixture {

    public static final String FUEL_SKU = "GAS-COMUM";
    public static final String FUEL_NAME = "Gasolina Comum";
    public static final String CONVENIENCE_SKU = "REFRI-350";
    public static final String CONVENIENCE_NAME = "Refrigerante Lata 350ml";
    public static final String ANP_CODE = "320102001";
    public static final String NCM = "27101259";
    public static final String CEST = "0600200";

    private ProductFixture() {
    }

    public static ProductInputDTO fuelInput() {
        ProductInputDTO input = new ProductInputDTO();
        input.setSku(FUEL_SKU);
        input.setName(FUEL_NAME);
        input.setProductType(ProductTypeEnum.FUEL);
        input.setUnit(UnitTypeEnum.L);
        input.setAnpCode(ANP_CODE);
        input.setNcm(NCM);
        input.setCest(CEST);
        input.setActive(true);
        return input;
    }

    public static ProductInputDTO convenienceInput() {
        ProductInputDTO input = fuelInput();
        input.setSku(CONVENIENCE_SKU);
        input.setName(CONVENIENCE_NAME);
        input.setProductType(ProductTypeEnum.CONVENIENCE);
        input.setUnit(UnitTypeEnum.UN);
        input.setAnpCode(null);
        return input;
    }

    public static ProductInputDTO inputWithoutName() {
        ProductInputDTO input = fuelInput();
        input.setName(null);
        return input;
    }

    public static Product persistedFuel() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setSku(FUEL_SKU);
        product.setName(FUEL_NAME);
        product.setProductType(ProductTypeEnum.FUEL);
        product.setUnit(UnitTypeEnum.L);
        product.setAnpCode(ANP_CODE);
        product.setNcm(NCM);
        product.setCest(CEST);
        product.setActive(true);
        return product;
    }

    public static Product persistedConvenience() {
        Product product = persistedFuel();
        product.setSku(CONVENIENCE_SKU);
        product.setName(CONVENIENCE_NAME);
        product.setProductType(ProductTypeEnum.CONVENIENCE);
        product.setUnit(UnitTypeEnum.UN);
        return product;
    }
}
