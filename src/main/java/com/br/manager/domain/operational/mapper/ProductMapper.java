package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
    public Product productInputDTOToProduct(ProductInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Product entity = new Product();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public ProductResponseDTO productToProductResponseDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponseDTO dto = new ProductResponseDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }

    public List<ProductResponseDTO> listProductToListProductResponseDTO(List<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream().map(this::productToProductResponseDTO).toList();
    }

    public void updateProductFromDto(ProductInputDTO dto, Product entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
