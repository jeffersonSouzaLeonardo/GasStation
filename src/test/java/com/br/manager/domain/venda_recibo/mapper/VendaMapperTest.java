package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaItemResponseDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.dto.VendaResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaItemFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaReembolsoFixture;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VendaMapperTest {

    private final VendaMapper mapper = new VendaMapper();

    @Test
    void shouldMapVendaHeaderToResponse() {
        Venda venda = VendaFixture.persistedEntity(VendaSituacao.ABERTO);
        venda.setClienteId(UUID.randomUUID());
        venda.setVeiculoId(UUID.randomUUID());
        venda.setFrentistaUsuarioId(UUID.randomUUID());

        VendaResponseDTO response = mapper.toResponse(venda);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("itens", "pagamentos", "reembolsos")
                .isEqualTo(venda);
    }

    @Test
    void shouldMapNestedCollectionsOfVenda() {
        Venda venda = VendaFixture.persistedEntity(VendaSituacao.PAGO);
        VendaItem item = VendaItemFixture.persistedEntity(UUID.randomUUID());
        PagamentoVenda pagamento = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.RECEBIDO);
        ReembolsoVenda reembolso = VendaReembolsoFixture.persistedEntity();
        venda.adicionarItem(item);
        venda.adicionarPagamento(pagamento);
        venda.adicionarReembolso(reembolso);

        VendaResponseDTO response = mapper.toResponse(venda);

        assertThat(response.itens()).extracting(VendaItemResponseDTO::id).containsExactly(item.getId());
        assertThat(response.pagamentos()).extracting(VendaPagamentoResponseDTO::id).containsExactly(pagamento.getId());
        assertThat(response.reembolsos()).extracting(VendaReembolsoResponseDTO::id).containsExactly(reembolso.getId());
    }

    @Test
    void shouldMapVendaWithoutChildrenToEmptyLists() {
        VendaResponseDTO response = mapper.toResponse(VendaFixture.persistedEntity(VendaSituacao.ABERTO));

        assertThat(response.itens()).isEmpty();
        assertThat(response.pagamentos()).isEmpty();
        assertThat(response.reembolsos()).isEmpty();
    }

    @Test
    void shouldMapItemToResponse() {
        VendaItem item = VendaItemFixture.persistedCombustivelEntity();

        VendaItemResponseDTO response = mapper.toItemResponse(item);

        assertThat(response).usingRecursiveComparison().isEqualTo(item);
    }

    @Test
    void shouldMapPagamentoToResponse() {
        PagamentoVenda pagamento = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.AUTORIZADO);

        VendaPagamentoResponseDTO response = mapper.toPagamentoResponse(pagamento);

        assertThat(response).usingRecursiveComparison().isEqualTo(pagamento);
    }

    @Test
    void shouldMapReembolsoToResponse() {
        ReembolsoVenda reembolso = VendaReembolsoFixture.persistedEntity();

        VendaReembolsoResponseDTO response = mapper.toReembolsoResponse(reembolso);

        assertThat(response).usingRecursiveComparison().isEqualTo(reembolso);
    }
}
