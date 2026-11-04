package com.br.manager.domain.operacional.support;

import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

/**
 * Pre-requisitos fora do dominio operacional (contcargo de sincronismo)
 * e ajustes de auditoria que nao possuem endpoint publico.
 */
@TestComponent
public class OperacionalADadosTeste {

    private final JdbcTemplate jdbcTemplate;

    public OperacionalADadosTeste(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static Sincronizacao sincronizacaoAt(LocalDateTime ultimaSincronizacao) {
        Sincronizacao sincronizacao = new Sincronizacao();
        sincronizacao.setUltimaSincronizacao(ultimaSincronizacao);
        return sincronizacao;
    }

    /**
     * Gera a mesma excecao que o Hibernate Validator lanca ao persistir uma entidade invalida.
     */
    public static ConstraintViolationException constraintViolationOf(Object invalidoEntity) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return new ConstraintViolationException(factory.getValidator().validate(invalidoEntity));
        }
    }

    public void setUltimaSincronizacao(NomeTabelaSincronizacaoEnum tabela, LocalDateTime ultimaSincronizacao) {
        jdbcTemplate.update("UPDATE sincronizacao SET ultima_sincronizacao = ? WHERE nome = ?", ultimaSincronizacao, tabela.name());
    }

    /**
     * Simula um registro alterado pela ultima vez em {@codigo changedAt} (antes do ultimo sincronismo).
     */
    public void backdate(String tabela, String id, LocalDateTime changedAt) {
        jdbcTemplate.update("UPDATE `" + tabela + "` SET criado_em = ?, atualizado_em = ?, excluido_em = NULL WHERE id = ?",
                changedAt, changedAt, id);
    }
}
