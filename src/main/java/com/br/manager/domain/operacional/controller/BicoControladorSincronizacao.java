package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.controller.AbstractControladorSincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.dto.BicoResponseDTO;
import com.br.manager.domain.operacional.usecase.BicoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bico-sincronizacao")
public class BicoControladorSincronizacao extends AbstractControladorSincronizacao<BicoResponseDTO> {

    private final BicoService bicoService;

    public BicoControladorSincronizacao(BicoService bicoService) {
        super(NomeTabelaSincronizacaoEnum.BICO);
        this.bicoService = bicoService;
    }

    @Override
    protected List<BicoResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        return bicoService.buscarAlteradosDesdeSincronizacao(tabelaName);
    }
}
