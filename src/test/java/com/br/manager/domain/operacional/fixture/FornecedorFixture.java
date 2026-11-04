package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.entity.Fornecedor;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Fornecedor.
 */
public final class FornecedorFixture {

    public static final String LEGAL_NAME = "Distribuidora Petro Sul LTDA";
    public static final String OUTROS_LEGAL_NAME = "Comercial Lubrificantes Norte SA";
    public static final String MASKED_CNPJ = "12.345.678/0001-90";
    public static final String CNPJ = "12345678000190";
    public static final String OUTROS_MASKED_CNPJ = "98.765.432/0001-10";
    public static final String OUTROS_CNPJ = "98765432000110";
    public static final String MASKED_STATE_REGISTRATION = "123.456.789";
    public static final String STATE_REGISTRATION = "123456789";
    public static final String EMAIL = "compras@petrosul.com.br";
    public static final String MASKED_PHONE = "(51) 3333-4444";
    public static final String PHONE = "5133334444";
    public static final String RUA = "Avenida das Industrias";
    public static final String NUMERO_ENDERECO = "1500";
    public static final String COMPLEMENTO = "Galpao 3";
    public static final String BAIRRO = "Distrito Industrial";
    public static final String CIDADE = "Porto Alegre";
    public static final String ESTADO = "RS";
    public static final String MASKED_CEP_CODE = "90200-000";
    public static final String CEP = "90200000";

    private FornecedorFixture() {
    }

    public static FornecedorInputDTO validoInput() {
        FornecedorInputDTO input = new FornecedorInputDTO();
        input.setLegalName(LEGAL_NAME);
        input.setCnpjCpf(MASKED_CNPJ);
        input.setInscricaoEstadual(MASKED_STATE_REGISTRATION);
        input.setEmail(EMAIL);
        input.setTelefone(MASKED_PHONE);
        input.setRua(RUA);
        input.setNumeroEndereco(NUMERO_ENDERECO);
        input.setComplemento(COMPLEMENTO);
        input.setBairro(BAIRRO);
        input.setCidade(CIDADE);
        input.setEstado(ESTADO);
        input.setCep(MASKED_CEP_CODE);
        input.setAtivo(true);
        return input;
    }

    public static FornecedorInputDTO outrosInput() {
        FornecedorInputDTO input = validoInput();
        input.setLegalName(OUTROS_LEGAL_NAME);
        input.setCnpjCpf(OUTROS_MASKED_CNPJ);
        return input;
    }

    public static Fornecedor persistedEntity() {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(UUID.randomUUID());
        fornecedor.setLegalName(LEGAL_NAME);
        fornecedor.setCnpjCpf(CNPJ);
        fornecedor.setInscricaoEstadual(STATE_REGISTRATION);
        fornecedor.setEmail(EMAIL);
        fornecedor.setTelefone(PHONE);
        fornecedor.setRua(RUA);
        fornecedor.setNumeroEndereco(NUMERO_ENDERECO);
        fornecedor.setComplemento(COMPLEMENTO);
        fornecedor.setBairro(BAIRRO);
        fornecedor.setCidade(CIDADE);
        fornecedor.setEstado(ESTADO);
        fornecedor.setCep(CEP);
        fornecedor.setAtivo(true);
        return fornecedor;
    }
}
