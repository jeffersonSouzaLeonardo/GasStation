package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.controller.AbstractControladorSincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.dto.FornecedorResponseDTO;
import com.br.manager.domain.operacional.usecase.FornecedorService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fornecedor-sincronizacao")
public class FornecedorControladorSincronizacao extends AbstractControladorSincronizacao<FornecedorResponseDTO> {

    private final FornecedorService fornecedorService;

    public FornecedorControladorSincronizacao(FornecedorService fornecedorService) {
        super(NomeTabelaSincronizacaoEnum.FORNECEDOR);
        this.fornecedorService = fornecedorService;
    }

    @Override
    protected List<FornecedorResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        return fornecedorService.buscarAlteradosDesdeSincronizacao(tabelaName);
    }
}
