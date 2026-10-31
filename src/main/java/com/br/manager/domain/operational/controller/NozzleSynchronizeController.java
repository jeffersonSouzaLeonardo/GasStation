package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.controller.AbstractSynchronizeController;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.dto.NozzleResponseDTO;
import com.br.manager.domain.operational.usecase.NozzleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nozzle-synchronize")
public class NozzleSynchronizeController extends AbstractSynchronizeController<NozzleResponseDTO> {

    private final NozzleService nozzleService;

    public NozzleSynchronizeController(NozzleService nozzleService) {
        super(SynchronizeNameTableEnum.NOZZLE);
        this.nozzleService = nozzleService;
    }

    @Override
    protected List<NozzleResponseDTO> findChangedSinceSynchronize(String tableName) {
        return nozzleService.findChangedSinceSynchronize(tableName);
    }
}
