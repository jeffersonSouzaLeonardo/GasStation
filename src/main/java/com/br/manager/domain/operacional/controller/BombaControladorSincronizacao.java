package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.controller.AbstractControladorSincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.dto.BombaResponseDTO;
import com.br.manager.domain.operacional.usecase.BombaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bomba-sincronizacao")
public class BombaControladorSincronizacao extends AbstractControladorSincronizacao<BombaResponseDTO> {

    private final BombaService bombaService;

    public BombaControladorSincronizacao(BombaService bombaService) {
        super(NomeTabelaSincronizacaoEnum.BOMBA);
        this.bombaService = bombaService;
    }

    @Override
    protected List<BombaResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        return bombaService.buscarAlteradosDesdeSincronizacao(tabelaName);
    }
}
