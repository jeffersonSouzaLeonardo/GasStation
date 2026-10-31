package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.controller.AbstractSynchronizeController;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.usecase.SupplierService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/supplier-synchronize")
public class SupplierSynchronizeController extends AbstractSynchronizeController<SupplierResponseDTO> {

    private final SupplierService supplierService;

    public SupplierSynchronizeController(SupplierService supplierService) {
        super(SynchronizeNameTableEnum.SUPPLIER);
        this.supplierService = supplierService;
    }

    @Override
    protected List<SupplierResponseDTO> findChangedSinceSynchronize(String tableName) {
        return supplierService.findChangedSinceSynchronize(tableName);
    }
}
