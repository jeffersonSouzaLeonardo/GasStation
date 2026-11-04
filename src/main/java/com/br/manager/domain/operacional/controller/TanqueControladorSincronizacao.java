package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.controller.AbstractControladorSincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.dto.TanqueResponseDTO;
import com.br.manager.domain.operacional.usecase.TanqueService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tanque-sincronizacao")
public class TanqueControladorSincronizacao extends AbstractControladorSincronizacao<TanqueResponseDTO> {

    private final TanqueService tanqueService;

    public TanqueControladorSincronizacao(TanqueService tanqueService) {
        super(NomeTabelaSincronizacaoEnum.TANQUE);
        this.tanqueService = tanqueService;
    }

    @Override
    protected List<TanqueResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        return tanqueService.buscarAlteradosDesdeSincronizacao(tabelaName);
    }
}
