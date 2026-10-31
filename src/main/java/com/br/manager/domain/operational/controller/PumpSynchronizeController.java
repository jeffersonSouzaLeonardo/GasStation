package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.controller.AbstractSynchronizeController;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.dto.PumpResponseDTO;
import com.br.manager.domain.operational.usecase.PumpService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pump-synchronize")
public class PumpSynchronizeController extends AbstractSynchronizeController<PumpResponseDTO> {

    private final PumpService pumpService;

    public PumpSynchronizeController(PumpService pumpService) {
        super(SynchronizeNameTableEnum.PUMP);
        this.pumpService = pumpService;
    }

    @Override
    protected List<PumpResponseDTO> findChangedSinceSynchronize(String tableName) {
        return pumpService.findChangedSinceSynchronize(tableName);
    }
}
