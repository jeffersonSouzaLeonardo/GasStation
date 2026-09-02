package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.ProductInputDTO;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.mapper.ProductMapper;
import com.br.manager.domain.operational.repository.ProductRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public ProductResponseDTO create(ProductInputDTO inputDTO) {
        try {
            Product product = productMapper.productInputDTOToProduct(inputDTO);
            if (product.getId() == null) {
                product.setId(UUID.randomUUID());
            }
            return productMapper.productToProductResponseDTO(productRepository.saveAndFlush(product));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String productName = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Error while saving product " + productName, e);
        }
    }

    public ProductResponseDTO update(ProductInputDTO inputDTO) {
        try {
            Product product = productRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (product == null) {
                throw new NotFoundBusinessException(String.format("Product with ID %s not found", inputDTO.getId()));
            }
            productMapper.updateProductFromDto(inputDTO, product);
            return productMapper.productToProductResponseDTO(productRepository.saveAndFlush(product));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String productName = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Error while saving product " + productName, e);
        }
    }

    public List<ProductResponseDTO> findAll() {
        return productMapper.listProductToListProductResponseDTO(productRepository.findAllByActiveTrue());
    }

    public List<ProductResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return productMapper.listProductToListProductResponseDTO(
                productRepository.findByNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public ProductResponseDTO find(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id);
        if (product == null) {
            throw new NotFoundBusinessException(String.format("Product with ID %s not found", id));
        }
        return productMapper.productToProductResponseDTO(product);
    }

    public void delete(UUID id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Product with ID %s not found", id)));
            product.setActive(false);
            productRepository.saveAndFlush(product);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting product", e);
        }
    }

    public Product getProductEntityById(UUID id) {
        Product product = productRepository.findByIdAndActiveTrue(id);
        if (product == null) {
            throw new NotFoundBusinessException(String.format("Product with ID %s not found", id));
        }
        return product;
    }
}
