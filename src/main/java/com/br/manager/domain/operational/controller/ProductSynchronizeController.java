package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.controller.AbstractSynchronizeController;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.dto.ProductResponseDTO;
import com.br.manager.domain.operational.usecase.ProductService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-synchronize")
public class ProductSynchronizeController extends AbstractSynchronizeController<ProductResponseDTO> {

    private final ProductService productService;

    public ProductSynchronizeController(ProductService productService) {
        super(SynchronizeNameTableEnum.PRODUCT);
        this.productService = productService;
    }

    @Override
    protected List<ProductResponseDTO> findChangedSinceSynchronize(String tableName) {
        return productService.findChangedSinceSynchronize(tableName);
    }
}
