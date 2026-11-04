package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.enums.TipoPessoaEnum;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Cliente.
 */
public final class ClienteFixture {

    public static final String NAME = "Joao da Silva Transportes";
    public static final String OUTROS_NAME = "Maria Oliveira";
    public static final TipoPessoaEnum PERSON_TYPE = TipoPessoaEnum.JURIDICA;
    public static final String CNPJ = "12345678000190";
    public static final String MASKED_CNPJ = "12.345.678/0001-90";
    public static final String CPF = "12345678909";
    public static final String STATE_REGISTRATION = "123456789";
    public static final String EMAIL = "financeiro@silvatransportes.com.br";
    public static final String PHONE = "11987654321";
    public static final String MASKED_PHONE = "(11) 98765-4321";
    public static final String RUA = "Rua das Flores";
    public static final String NUMERO_ENDERECO = "100";
    public static final String COMPLEMENTO = "Sala 2";
    public static final String BAIRRO = "Centro";
    public static final String CIDADE = "Sao Paulo";
    public static final String ESTADO = "SP";
    public static final String CEP = "01310100";
    public static final String MASKED_CEP_CODE = "01310-100";
    public static final BigDecimal CREDITO_LIMITE = new BigDecimal("1500.00");

    private ClienteFixture() {
    }

    public static ClienteInputDTO validoInput() {
        ClienteInputDTO input = new ClienteInputDTO();
        input.setNome(NAME);
        input.setPersonType(PERSON_TYPE);
        input.setCnpjCpf(CNPJ);
        input.setInscricaoEstadual(STATE_REGISTRATION);
        input.setEmail(EMAIL);
        input.setTelefone(PHONE);
        input.setRua(RUA);
        input.setNumeroEndereco(NUMERO_ENDERECO);
        input.setComplemento(COMPLEMENTO);
        input.setBairro(BAIRRO);
        input.setCidade(CIDADE);
        input.setEstado(ESTADO);
        input.setCep(CEP);
        input.setLimiteCredito(CREDITO_LIMITE);
        input.setAtivo(true);
        return input;
    }

    public static ClienteInputDTO maskedInput() {
        ClienteInputDTO input = validoInput();
        input.setCnpjCpf(MASKED_CNPJ);
        input.setTelefone(MASKED_PHONE);
        input.setCep(MASKED_CEP_CODE);
        return input;
    }

    public static ClienteInputDTO outrosInput() {
        ClienteInputDTO input = validoInput();
        input.setNome(OUTROS_NAME);
        input.setPersonType(TipoPessoaEnum.FISICA);
        input.setCnpjCpf(CPF);
        return input;
    }

    public static Cliente persistedEntity() {
        Cliente cliente = new Cliente();
        cliente.setId(UUID.randomUUID());
        cliente.setNome(NAME);
        cliente.setPersonType(PERSON_TYPE);
        cliente.setCnpjCpf(CNPJ);
        cliente.setInscricaoEstadual(STATE_REGISTRATION);
        cliente.setEmail(EMAIL);
        cliente.setTelefone(PHONE);
        cliente.setRua(RUA);
        cliente.setNumeroEndereco(NUMERO_ENDERECO);
        cliente.setComplemento(COMPLEMENTO);
        cliente.setBairro(BAIRRO);
        cliente.setCidade(CIDADE);
        cliente.setEstado(ESTADO);
        cliente.setCep(CEP);
        cliente.setLimiteCredito(CREDITO_LIMITE);
        cliente.setAtivo(true);
        return cliente;
    }
}
