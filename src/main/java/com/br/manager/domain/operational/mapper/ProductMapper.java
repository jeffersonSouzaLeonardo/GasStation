package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    public abstract Product productInputDTOToProduct(ProductInputDTO inputDTO);

    public abstract ProductResponseDTO productToProductResponseDTO(Product product);

    public abstract List<ProductResponseDTO> listProductToListProductResponseDTO(List<Product> products);

    public abstract void updateProductFromDto(ProductInputDTO dto, @MappingTarget Product entity);
}
