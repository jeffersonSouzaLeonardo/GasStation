package com.br.manager.domain.comum.controller;

import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Base para os endpoints de sincronismo retaguarda -> PDV.
 * Expõe os registros criados, alterados ou excluídos desde a última
 * sincronização confirmada pelo PDV para a tabela informada.
 *
 * @param <T> DTO trafegado no endpoint
 */
public abstract class AbstractControladorSincronizacao<T> {

    private final NomeTabelaSincronizacaoEnum tabelaName;

    protected AbstractControladorSincronizacao(NomeTabelaSincronizacaoEnum tabelaName) {
        this.tabelaName = tabelaName;
    }

    @GetMapping
    public ResponseEntity<List<T>> getAlteradosDesdeUltimaSincronizacao() {
        return ResponseEntity.ok(buscarAlteradosDesdeSincronizacao(tabelaName.name()));
    }

    protected abstract List<T> buscarAlteradosDesdeSincronizacao(String tabelaName);
}
