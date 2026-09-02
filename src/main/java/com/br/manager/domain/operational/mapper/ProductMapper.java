package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product productInputDTOToProduct(ProductInputDTO inputDTO);

    ProductResponseDTO productToProductResponseDTO(Product product);

    List<ProductResponseDTO> listProductToListProductResponseDTO(List<Product> products);

    void updateProductFromDto(ProductInputDTO dto, @MappingTarget Product entity);
}
