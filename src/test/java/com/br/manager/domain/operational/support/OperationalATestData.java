package com.br.manager.domain.operational.support;

import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

/**
 * Pre-requisitos fora do dominio operacional (controle de sincronismo)
 * e ajustes de auditoria que nao possuem endpoint publico.
 */
@TestComponent
public class OperationalATestData {

    private final JdbcTemplate jdbcTemplate;

    public OperationalATestData(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static Synchronize synchronizeAt(LocalDateTime lastSynchronize) {
        Synchronize synchronize = new Synchronize();
        synchronize.setLastSynchronize(lastSynchronize);
        return synchronize;
    }

    /**
     * Gera a mesma excecao que o Hibernate Validator lanca ao persistir uma entidade invalida.
     */
    public static ConstraintViolationException constraintViolationOf(Object invalidEntity) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return new ConstraintViolationException(factory.getValidator().validate(invalidEntity));
        }
    }

    public void setLastSynchronize(SynchronizeNameTableEnum table, LocalDateTime lastSynchronize) {
        jdbcTemplate.update("UPDATE synchronize SET last_synchronize = ? WHERE name = ?", lastSynchronize, table.name());
    }

    /**
     * Simula um registro alterado pela ultima vez em {@code changedAt} (antes do ultimo sincronismo).
     */
    public void backdate(String table, String id, LocalDateTime changedAt) {
        jdbcTemplate.update("UPDATE `" + table + "` SET created_at = ?, updated_at = ?, deleted_at = NULL WHERE id = ?",
                changedAt, changedAt, id);
    }
}
