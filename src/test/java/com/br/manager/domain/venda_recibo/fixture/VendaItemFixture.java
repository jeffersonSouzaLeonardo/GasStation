package com.br.manager.domain.venda_recibo.fixture;

import com.br.manager.domain.venda_recibo.dto.VendaItemInputDTO;
import com.br.manager.domain.venda_recibo.entity.VendaItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Massa de dados padrao para itens de venda.
 */
public final class VendaItemFixture {

    public static final int LINHA_NUMERO = 1;
    public static final int SECOND_LINHA_NUMERO = 2;
    public static final BigDecimal QUANTITY = new BigDecimal("2.0000");
    public static final BigDecimal PRECO_UNITARIO = new BigDecimal("5.9900");
    public static final BigDecimal DESCONTO = new BigDecimal("0.5000");
    public static final BigDecimal MEDIDOR_START = new BigDecimal("1000.0000");
    public static final BigDecimal MEDIDOR_END = new BigDecimal("1040.0000");
    public static final BigDecimal COMBUSTIVEL_QUANTITY = new BigDecimal("40.0000");

    private VendaItemFixture() {
    }

    public static VendaItemInputDTO produtoInput(UUID produtoId) {
        VendaItemInputDTO input = new VendaItemInputDTO();
        input.setLinhaNumero(LINHA_NUMERO);
        input.setProdutoId(produtoId);
        input.setQuantidade(QUANTITY);
        input.setPrecoUnitario(PRECO_UNITARIO);
        input.setValorDesconto(DESCONTO);
        return input;
    }

    public static VendaItemInputDTO produtoInput(UUID produtoId, int linhaNumero) {
        VendaItemInputDTO input = produtoInput(produtoId);
        input.setLinhaNumero(linhaNumero);
        return input;
    }

    public static VendaItemInputDTO combustivelInput(UUID produtoId, UUID bicoId) {
        VendaItemInputDTO input = produtoInput(produtoId);
        input.setBicoId(bicoId);
        input.setQuantidade(null);
        input.setMedidorInicial(MEDIDOR_START);
        input.setMedidorFinal(MEDIDOR_END);
        return input;
    }

    public static VendaItem persistedEntity(UUID produtoId) {
        VendaItem item = new VendaItem();
        item.setId(UUID.randomUUID());
        item.setLinhaNumero(LINHA_NUMERO);
        item.setProdutoId(produtoId);
        item.setQuantidade(QUANTITY);
        item.setPrecoUnitario(PRECO_UNITARIO);
        item.setValorDesconto(DESCONTO);
        return item;
    }

    public static VendaItem persistedCombustivelEntity() {
        VendaItem item = persistedEntity(UUID.randomUUID());
        item.setBicoId(UUID.randomUUID());
        item.setTanqueId(UUID.randomUUID());
        item.setQuantidade(COMBUSTIVEL_QUANTITY);
        item.setMedidorInicial(MEDIDOR_START);
        item.setMedidorFinal(MEDIDOR_END);
        return item;
    }
}
