package com.br.manager.domain.organizacao.fixture;

import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.entity.Estacao;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Posto.
 */
public final class EstacaoFixture {

    public static final String NAME = "Posto Centro";
    public static final String MASKED_CNPJ = "12.345.678/0002-71";
    public static final String CNPJ = "12345678000271";
    public static final String MASKED_STATE_REGISTRATION = "987.654.321";
    public static final String STATE_REGISTRATION = "987654321";
    public static final String MASKED_CEP_CODE = "01310-100";
    public static final String CEP = "01310100";
    public static final String RUA = "Avenida Paulista";
    public static final String NUMERO = "1000";
    public static final String COMPLEMENT = "Loja 1";
    public static final String BAIRRO = "Bela Vista";
    public static final String CITY = "Sao Paulo";
    public static final String STATE = "SP";
    public static final String FusoHorario = "America/Sao_Paulo";
    public static final String OUTROS_TIMEZONE = "America/Manaus";

    private EstacaoFixture() {
    }

    public static EstacaoInputDTO validoInput(UUID empresaId) {
        EstacaoInputDTO input = new EstacaoInputDTO();
        input.setEmpresaId(empresaId);
        input.setNome(NAME);
        input.setCnpj(MASKED_CNPJ);
        input.setInscricaoEstadual(MASKED_STATE_REGISTRATION);
        input.setRua(RUA);
        input.setNumeroEndereco(NUMERO);
        input.setComplemento(COMPLEMENT);
        input.setBairro(BAIRRO);
        input.setCidade(CITY);
        input.setEstado(STATE);
        input.setCep(MASKED_CEP_CODE);
        input.setFusoHorario(FusoHorario);
        input.setAtivo(true);
        return input;
    }

    public static EstacaoInputDTO validoInput() {
        return validoInput(UUID.randomUUID());
    }

    public static Estacao persistedEntity() {
        Estacao estacao = new Estacao();
        estacao.setId(UUID.randomUUID());
        estacao.setEmpresaId(UUID.randomUUID());
        estacao.setNome(NAME);
        estacao.setCnpj(CNPJ);
        estacao.setInscricaoEstadual(STATE_REGISTRATION);
        estacao.setRua(RUA);
        estacao.setNumeroEndereco(NUMERO);
        estacao.setComplemento(COMPLEMENT);
        estacao.setBairro(BAIRRO);
        estacao.setCidade(CITY);
        estacao.setEstado(STATE);
        estacao.setCep(CEP);
        estacao.setFusoHorario(FusoHorario);
        estacao.setAtivo(true);
        return estacao;
    }
}
