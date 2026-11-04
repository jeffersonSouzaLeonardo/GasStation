package com.br.manager.domain.operacional.controller;

import com.br.manager.domain.comum.controller.AbstractControladorSincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.operacional.dto.ProdutoResponseDTO;
import com.br.manager.domain.operacional.usecase.ProdutoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produto-sincronizacao")
public class ProdutoControladorSincronizacao extends AbstractControladorSincronizacao<ProdutoResponseDTO> {

    private final ProdutoService produtoService;

    public ProdutoControladorSincronizacao(ProdutoService produtoService) {
        super(NomeTabelaSincronizacaoEnum.PRODUTO);
        this.produtoService = produtoService;
    }

    @Override
    protected List<ProdutoResponseDTO> buscarAlteradosDesdeSincronizacao(String tabelaName) {
        return produtoService.buscarAlteradosDesdeSincronizacao(tabelaName);
    }
}
