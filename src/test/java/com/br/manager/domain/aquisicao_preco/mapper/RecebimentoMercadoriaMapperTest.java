package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.ItemRecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.RecebimentoMercadoriaItemFixture;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.PedidoCompraFixture;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecebimentoMercadoriaMapperTest {

    private final RecebimentoMercadoriaMapper mapper = new RecebimentoMercadoriaMapper();

    @Test
    void shouldMapInputToEntityWithReferencias() {
        UUID fornecedorId = UUID.randomUUID();
        UUID recebidoPor = UUID.randomUUID();
        UUID compraOrderId = UUID.randomUUID();
        RecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaFixture.validoInput(fornecedorId, recebidoPor, List.of());
        input.setId(UUID.randomUUID());
        input.setCompraOrderId(compraOrderId);

        RecebimentoMercadoria entity = mapper.mercadoriaReciboInputDTOToRecebimentoMercadoria(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getFornecedor().getId()).isEqualTo(fornecedorId);
        assertThat(entity.getPedidoCompra().getId()).isEqualTo(compraOrderId);
        assertThat(entity.getRecebidoPor().getId()).isEqualTo(recebidoPor);
        assertThat(entity.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA);
        assertThat(entity.getChaveNota()).isEqualTo(RecebimentoMercadoriaFixture.CHAVE_NOTA);
        assertThat(entity.getRecebidoEm()).isEqualTo(RecebimentoMercadoriaFixture.RECEBIDO_EM);
        assertThat(entity.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.RECEBIDO);
        assertThat(entity.getValorFrete()).isEqualTo(RecebimentoMercadoriaFixture.VALOR_FRETE);
        assertThat(entity.getOutrosCustos()).isEqualTo(RecebimentoMercadoriaFixture.OUTROS_CUSTOS);
    }

    @Test
    void shouldNotSetReferenciasWhenIdsAreNull() {
        RecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaFixture.validoInput(null, null, List.of());

        RecebimentoMercadoria entity = mapper.mercadoriaReciboInputDTOToRecebimentoMercadoria(input);

        assertThat(entity.getFornecedor()).isNull();
        assertThat(entity.getPedidoCompra()).isNull();
        assertThat(entity.getRecebidoPor()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.mercadoriaReciboInputDTOToRecebimentoMercadoria(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseWithNestedItens() {
        Fornecedor fornecedor = AquisicaoPrecoReferenciaFixture.ativoFornecedor();
        Usuario recebidoPor = AquisicaoPrecoReferenciaFixture.ativoUsuario();
        PedidoCompra pedidoCompra = PedidoCompraFixture.persistedEntity(PedidoCompra.Situacao.SENT);
        RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity(fornecedor, recebidoPor);
        recebimentoMercadoria.setPedidoCompra(pedidoCompra);
        RecebimentoMercadoriaItem item = RecebimentoMercadoriaItemFixture.persistedEntity(null);
        recebimentoMercadoria.setItens(List.of(item));

        RecebimentoMercadoriaResponseDTO response = mapper.mercadoriaReciboToRecebimentoMercadoriaResponseDTO(recebimentoMercadoria);

        assertThat(response.getId()).isEqualTo(recebimentoMercadoria.getId());
        assertThat(response.getFornecedorId()).isEqualTo(fornecedor.getId());
        assertThat(response.getCompraOrderId()).isEqualTo(pedidoCompra.getId());
        assertThat(response.getRecebidoPor()).isEqualTo(recebidoPor.getId());
        assertThat(response.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA);
        assertThat(response.getChaveNota()).isEqualTo(RecebimentoMercadoriaFixture.CHAVE_NOTA);
        assertThat(response.getRecebidoEm()).isEqualTo(RecebimentoMercadoriaFixture.RECEBIDO_EM);
        assertThat(response.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.RECEBIDO);
        assertThat(response.getValorFrete()).isEqualTo(RecebimentoMercadoriaFixture.VALOR_FRETE);
        assertThat(response.getOutrosCustos()).isEqualTo(RecebimentoMercadoriaFixture.OUTROS_CUSTOS);
        assertThat(response.getItens()).singleElement()
                .usingRecursiveComparison()
                .isEqualTo(esperadoItemResponse(item, recebimentoMercadoria.getId()));
    }

    @Test
    void shouldMapEntityWithoutRelationsToResponseWithNullIds() {
        RecebimentoMercadoria recebimentoMercadoria = new RecebimentoMercadoria();
        recebimentoMercadoria.setItens(null);

        RecebimentoMercadoriaResponseDTO response = mapper.mercadoriaReciboToRecebimentoMercadoriaResponseDTO(recebimentoMercadoria);

        assertThat(response.getFornecedorId()).isNull();
        assertThat(response.getCompraOrderId()).isNull();
        assertThat(response.getRecebidoPor()).isNull();
        assertThat(response.getItens()).isEmpty();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.mercadoriaReciboToRecebimentoMercadoriaResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        RecebimentoMercadoria first = RecebimentoMercadoriaFixture.persistedEntity();
        RecebimentoMercadoria second = RecebimentoMercadoriaFixture.persistedEntity();

        assertThat(mapper.listRecebimentoMercadoriaToListRecebimentoMercadoriaResponseDTO(Arrays.asList(first, null, second)))
                .extracting(RecebimentoMercadoriaResponseDTO::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listRecebimentoMercadoriaToListRecebimentoMercadoriaResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingRelations() {
        RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity();
        Fornecedor fornecedor = recebimentoMercadoria.getFornecedor();
        UUID originalId = recebimentoMercadoria.getId();
        RecebimentoMercadoriaInputDTO input = RecebimentoMercadoriaFixture.validoInput(UUID.randomUUID(), UUID.randomUUID(), null);
        input.setId(UUID.randomUUID());
        input.setNumeroNota(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO);
        input.setStatus(RecebimentoMercadoria.Situacao.CANCELADO);
        input.setRecebidoEm(RecebimentoMercadoriaFixture.RECEBIDO_EM.plusDays(1));
        input.setValorFrete(BigDecimal.TEN);
        input.setOutrosCustos(BigDecimal.ONE);

        mapper.updateRecebimentoMercadoriaFromDto(input, recebimentoMercadoria);

        assertThat(recebimentoMercadoria.getId()).isEqualTo(originalId);
        assertThat(recebimentoMercadoria.getFornecedor()).isSameAs(fornecedor);
        assertThat(recebimentoMercadoria.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA_ATUALIZADO);
        assertThat(recebimentoMercadoria.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.CANCELADO);
        assertThat(recebimentoMercadoria.getRecebidoEm()).isEqualTo(RecebimentoMercadoriaFixture.RECEBIDO_EM.plusDays(1));
        assertThat(recebimentoMercadoria.getValorFrete()).isEqualTo(BigDecimal.TEN);
        assertThat(recebimentoMercadoria.getOutrosCustos()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity();

        mapper.updateRecebimentoMercadoriaFromDto(new RecebimentoMercadoriaInputDTO(), recebimentoMercadoria);

        assertThat(recebimentoMercadoria.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA);
        assertThat(recebimentoMercadoria.getChaveNota()).isEqualTo(RecebimentoMercadoriaFixture.CHAVE_NOTA);
        assertThat(recebimentoMercadoria.getRecebidoEm()).isEqualTo(RecebimentoMercadoriaFixture.RECEBIDO_EM);
        assertThat(recebimentoMercadoria.getStatus()).isEqualTo(RecebimentoMercadoria.Situacao.RECEBIDO);
        assertThat(recebimentoMercadoria.getValorFrete()).isEqualTo(RecebimentoMercadoriaFixture.VALOR_FRETE);
        assertThat(recebimentoMercadoria.getOutrosCustos()).isEqualTo(RecebimentoMercadoriaFixture.OUTROS_CUSTOS);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        RecebimentoMercadoria recebimentoMercadoria = RecebimentoMercadoriaFixture.persistedEntity();

        mapper.updateRecebimentoMercadoriaFromDto(null, recebimentoMercadoria);
        mapper.updateRecebimentoMercadoriaFromDto(RecebimentoMercadoriaFixture.validoInput(null, null, null), null);

        assertThat(recebimentoMercadoria.getNumeroNota()).isEqualTo(RecebimentoMercadoriaFixture.NUMERO_NOTA);
    }

    private ItemRecebimentoMercadoriaResponseDTO esperadoItemResponse(RecebimentoMercadoriaItem item, UUID mercadoriaReciboId) {
        ItemRecebimentoMercadoriaResponseDTO esperado = new ItemRecebimentoMercadoriaResponseDTO();
        esperado.setId(item.getId());
        esperado.setMercadoriaReciboId(mercadoriaReciboId);
        esperado.setProdutoId(item.getProdutoId());
        esperado.setTanqueId(item.getTanqueId());
        esperado.setQuantidade(RecebimentoMercadoriaItemFixture.QUANTITY);
        esperado.setCustoUnitario(RecebimentoMercadoriaItemFixture.CUSTO_UNITARIO);
        return esperado;
    }
}
