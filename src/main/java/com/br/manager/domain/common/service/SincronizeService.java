package com.br.manager.domain.common.service;

import com.br.manager.domain.common.SincronizeDTO;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.entity.Sincronize;
import com.br.manager.domain.common.entity.SincronizeNameTableEnum;
import com.br.manager.domain.common.repository.SincronizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SincronizeService {

    @Autowired
    private SincronizeRepository sincronizeRepository;

    /**
     * Consulta a entidade Sincronize pelo nome da tabela (como String).
     * Aceita valores que correspondam ao nome do enum SincronizeNameTableEnum (case-insensitive).
     * Lance NotFoundBusinessException se não encontrada ou argumento inválido.
     */
    public Sincronize findByTableName(String tableName) {
        if (!StringUtils.hasText(tableName)) {
            throw new NotFoundBusinessException("Nome da tabela não informado");
        }

        SincronizeNameTableEnum enumValue;
        try {
            enumValue = SincronizeNameTableEnum.valueOf(tableName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new NotFoundBusinessException(String.format("Nome da tabela inválido: %s", tableName));
        }

        var sincronize = sincronizeRepository.findByNameTable(enumValue)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Sincronize para tabela %s não encontrada", tableName)));
        return sincronize;
    }

    public Sincronize update(SincronizeDTO sincronizeDTO) {
        if (sincronizeDTO == null) {
            throw new NotFoundBusinessException("SincronizeDTO não informado");
        }

        String tableName = sincronizeDTO.getNameTable();
        if (!StringUtils.hasText(tableName)) {
            throw new NotFoundBusinessException("Nome da tabela não informado");
        }

        SincronizeNameTableEnum enumValue;
        try {
            enumValue = SincronizeNameTableEnum.valueOf(tableName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new NotFoundBusinessException(String.format("Nome da tabela inválido: %s", tableName));
        }

        Sincronize sincronize = sincronizeRepository.findByNameTable(enumValue)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Sincronize para tabela %s não encontrada", tableName)));

        if (sincronizeDTO.getLastSincronize() != null) {
            sincronize.setLastSincronize(sincronizeDTO.getLastSincronize());
        } else {
            sincronize.setLastSincronize(java.time.LocalDateTime.now());
        }

        return sincronizeRepository.save(sincronize);
    }
}
