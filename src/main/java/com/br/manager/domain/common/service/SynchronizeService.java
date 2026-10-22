package com.br.manager.domain.common.service;

import com.br.manager.domain.common.dto.SincronizeDTO;
import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import com.br.manager.domain.common.repository.SynchronizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SynchronizeService {

    @Autowired
    private SynchronizeRepository synchronizeRepository;

    /**
     * Consulta a entidade Sincronize pelo nome da tabela (como String).
     * Aceita valores que correspondam ao nome do enum SincronizeNameTableEnum (case-insensitive).
     * Lance NotFoundBusinessException se não encontrada ou argumento inválido.
     */
    @Transactional(readOnly = true)
    public Synchronize findByTableName(String tableName) {
        if (!StringUtils.hasText(tableName)) {
            throw new NotFoundBusinessException("Nome da tabela não informado");
        }

        SynchronizeNameTableEnum enumValue;
        try {
            enumValue = SynchronizeNameTableEnum.valueOf(tableName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new NotFoundBusinessException(String.format("Nome da tabela inválido: %s", tableName));
        }

        var synchronize = synchronizeRepository.findByNameTable(enumValue)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Sincronize para tabela %s não encontrada", tableName)));
        return synchronize;
    }

    @Transactional
    public Synchronize update(SincronizeDTO sincronizeDTO) {
        if (sincronizeDTO == null) {
            throw new NotFoundBusinessException("SincronizeDTO não informado");
        }

        String tableName = sincronizeDTO.getNameTable();
        if (!StringUtils.hasText(tableName)) {
            throw new NotFoundBusinessException("Nome da tabela não informado");
        }

        SynchronizeNameTableEnum enumValue;
        try {
            enumValue = SynchronizeNameTableEnum.valueOf(tableName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new NotFoundBusinessException(String.format("Nome da tabela inválido: %s", tableName));
        }

        Synchronize synchronize = synchronizeRepository.findByNameTable(enumValue)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Sincronize para tabela %s não encontrada", tableName)));

        java.time.LocalDateTime lastSynchronize = sincronizeDTO.getLastSincronize() != null
                ? sincronizeDTO.getLastSincronize()
                : java.time.LocalDateTime.now();
        synchronize.setLastSynchronize(lastSynchronize);
        synchronizeRepository.updateLastSynchronize(enumValue, lastSynchronize);

        return synchronize;
    }
}
