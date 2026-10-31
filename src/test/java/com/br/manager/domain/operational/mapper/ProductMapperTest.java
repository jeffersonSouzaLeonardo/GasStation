package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.fixture.ProductFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    @Test
    void shouldMapInputToEntity() {
        ProductInputDTO input = ProductFixture.fuelInput();
        input.setId(UUID.randomUUID());

        Product product = mapper.productInputDTOToProduct(input);

        assertThat(product)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "sku", "name", "productType", "unit", "anpCode", "ncm", "cest", "active")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.productInputDTOToProduct(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Product product = ProductFixture.persistedFuel();

        ProductResponseDTO response = mapper.productToProductResponseDTO(product);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(product);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.productToProductResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Product> products = List.of(ProductFixture.persistedFuel(), ProductFixture.persistedConvenience());

        List<ProductResponseDTO> responses = mapper.listProductToListProductResponseDTO(products);

        assertThat(responses)
                .extracting(ProductResponseDTO::getId)
                .containsExactly(products.get(0).getId(), products.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listProductToListProductResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Product product = ProductFixture.persistedFuel();
        ProductInputDTO input = ProductFixture.convenienceInput();
        input.setId(product.getId());
        input.setActive(false);

        mapper.updateProductFromDto(input, product);

        assertThat(product.getId()).isEqualTo(input.getId());
        assertThat(product.getName()).isEqualTo(ProductFixture.CONVENIENCE_NAME);
        assertThat(product.getProductType()).isEqualTo(ProductTypeEnum.CONVENIENCE);
        assertThat(product.getActive()).isFalse();
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Product product = ProductFixture.persistedFuel();

        mapper.updateProductFromDto(null, product);

        assertThat(product.getName()).isEqualTo(ProductFixture.FUEL_NAME);
    }
}
