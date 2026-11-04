package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Recebimento de Mercadorias.
 */
public final class RecebimentoMercadoriaFixture {

    public static final String NUMERO_NOTA = "NF-000123";
    public static final String NUMERO_NOTA_ATUALIZADO = "NF-000999";
    public static final String CHAVE_NOTA = "35260112345678000190550010000001231000001234";
    public static final LocalDateTime RECEBIDO_EM = LocalDateTime.of(2026, 1, 10, 15, 45);
    public static final BigDecimal VALOR_FRETE = new BigDecimal("150.00");
    public static final BigDecimal OUTROS_CUSTOS = new BigDecimal("25.00");

    private RecebimentoMercadoriaFixture() {
    }

    public static RecebimentoMercadoriaInputDTO validoInput(UUID fornecedorId, UUID recebidoPor, List<ItemRecebimentoMercadoriaInputDTO> itens) {
        RecebimentoMercadoriaInputDTO input = new RecebimentoMercadoriaInputDTO();
        input.setFornecedorId(fornecedorId);
        input.setRecebidoPor(recebidoPor);
        input.setNumeroNota(NUMERO_NOTA);
        input.setChaveNota(CHAVE_NOTA);
        input.setRecebidoEm(RECEBIDO_EM);
        input.setStatus(RecebimentoMercadoria.Situacao.RECEBIDO);
        input.setValorFrete(VALOR_FRETE);
        input.setOutrosCustos(OUTROS_CUSTOS);
        input.setItens(itens);
        return input;
    }

    public static RecebimentoMercadoria persistedEntity(Fornecedor fornecedor, Usuario recebidoPor) {
        RecebimentoMercadoria recebimentoMercadoria = new RecebimentoMercadoria();
        recebimentoMercadoria.setId(UUID.randomUUID());
        recebimentoMercadoria.setFornecedor(fornecedor);
        recebimentoMercadoria.setRecebidoPor(recebidoPor);
        recebimentoMercadoria.setNumeroNota(NUMERO_NOTA);
        recebimentoMercadoria.setChaveNota(CHAVE_NOTA);
        recebimentoMercadoria.setRecebidoEm(RECEBIDO_EM);
        recebimentoMercadoria.setStatus(RecebimentoMercadoria.Situacao.RECEBIDO);
        recebimentoMercadoria.setValorFrete(VALOR_FRETE);
        recebimentoMercadoria.setOutrosCustos(OUTROS_CUSTOS);
        return recebimentoMercadoria;
    }

    public static RecebimentoMercadoria persistedEntity() {
        return persistedEntity(AquisicaoPrecoReferenciaFixture.ativoFornecedor(), AquisicaoPrecoReferenciaFixture.ativoUsuario());
    }
}
