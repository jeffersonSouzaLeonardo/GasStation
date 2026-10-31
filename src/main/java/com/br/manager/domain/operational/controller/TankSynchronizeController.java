package com.br.manager.domain.operational.controller;

import com.br.manager.domain.common.controller.AbstractSynchronizeController;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.usecase.TankService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tank-synchronize")
public class TankSynchronizeController extends AbstractSynchronizeController<TankResponseDTO> {

    private final TankService tankService;

    public TankSynchronizeController(TankService tankService) {
        super(SynchronizeNameTableEnum.TANK);
        this.tankService = tankService;
    }

    @Override
    protected List<TankResponseDTO> findChangedSinceSynchronize(String tableName) {
        return tankService.findChangedSinceSynchronize(tableName);
    }
}
