package com.br.manager.domain.operacional.fixture;

import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.enums.TipoUnidadeEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Produto.
 */
public final class ProdutoFixture {

    public static final String COMBUSTIVEL_SKU = "GAS-COMUM";
    public static final String COMBUSTIVEL_NAME = "Gasolina Comum";
    public static final String CONVENIENCIA_SKU = "REFRI-350";
    public static final String CONVENIENCIA_NAME = "Refrigerante Lata 350ml";
    public static final String ANP_CODE = "320102001";
    public static final String NCM = "27101259";
    public static final String CEST = "0600200";

    private ProdutoFixture() {
    }

    public static ProdutoInputDTO combustivelInput() {
        ProdutoInputDTO input = new ProdutoInputDTO();
        input.setSku(COMBUSTIVEL_SKU);
        input.setNome(COMBUSTIVEL_NAME);
        input.setProdutoType(ProdutoTypeEnum.COMBUSTIVEL);
        input.setUnidade(TipoUnidadeEnum.L);
        input.setAnpCode(ANP_CODE);
        input.setNcm(NCM);
        input.setCest(CEST);
        input.setAtivo(true);
        return input;
    }

    public static ProdutoInputDTO convenienciaInput() {
        ProdutoInputDTO input = combustivelInput();
        input.setSku(CONVENIENCIA_SKU);
        input.setNome(CONVENIENCIA_NAME);
        input.setProdutoType(ProdutoTypeEnum.CONVENIENCIA);
        input.setUnidade(TipoUnidadeEnum.UN);
        input.setAnpCode(null);
        return input;
    }

    public static ProdutoInputDTO inputWithoutName() {
        ProdutoInputDTO input = combustivelInput();
        input.setNome(null);
        return input;
    }

    public static Produto persistedCombustivel() {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setSku(COMBUSTIVEL_SKU);
        produto.setNome(COMBUSTIVEL_NAME);
        produto.setProdutoType(ProdutoTypeEnum.COMBUSTIVEL);
        produto.setUnidade(TipoUnidadeEnum.L);
        produto.setAnpCode(ANP_CODE);
        produto.setNcm(NCM);
        produto.setCest(CEST);
        produto.setAtivo(true);
        return produto;
    }

    public static Produto persistedConveniencia() {
        Produto produto = persistedCombustivel();
        produto.setSku(CONVENIENCIA_SKU);
        produto.setNome(CONVENIENCIA_NAME);
        produto.setProdutoType(ProdutoTypeEnum.CONVENIENCIA);
        produto.setUnidade(TipoUnidadeEnum.UN);
        return produto;
    }
}
