package com.br.manager.domain.common.controller;

import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
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
public abstract class AbstractSynchronizeController<T> {

    private final SynchronizeNameTableEnum tableName;

    protected AbstractSynchronizeController(SynchronizeNameTableEnum tableName) {
        this.tableName = tableName;
    }

    @GetMapping
    public ResponseEntity<List<T>> getChangedSinceLastSynchronize() {
        return ResponseEntity.ok(findChangedSinceSynchronize(tableName.name()));
    }

    protected abstract List<T> findChangedSinceSynchronize(String tableName);
}
