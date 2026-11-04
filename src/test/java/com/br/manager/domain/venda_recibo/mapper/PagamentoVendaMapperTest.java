package com.br.manager.domain.venda_recibo.mapper;

import com.br.manager.domain.venda_recibo.dto.VendaPagamentoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaPagamentoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PagamentoVendaMapperTest {

    private final PagamentoVendaMapper mapper = new PagamentoVendaMapper();

    @Test
    void shouldMapInputToEntityWithoutVenda() {
        VendaPagamentoInputDTO input = VendaPagamentoFixture.input(UUID.randomUUID(), UUID.randomUUID());
        input.setId(UUID.randomUUID());

        PagamentoVenda pagamento = mapper.toEntity(input);

        assertThat(pagamento)
                .usingRecursiveComparison()
                .ignoringFields("venda")
                .isEqualTo(input);
        assertThat(pagamento.getVenda()).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        PagamentoVenda pagamento = VendaPagamentoFixture.persistedEntity(SituacaoPagamento.RECEBIDO);

        VendaPagamentoResponseDTO response = mapper.toResponse(pagamento);

        assertThat(response).usingRecursiveComparison().isEqualTo(pagamento);
    }

    @Test
    void shouldMapEntityListToResponseListKeepingOrder() {
        List<PagamentoVenda> pagamentos = List.of(
                VendaPagamentoFixture.persistedEntity(SituacaoPagamento.PENDENTE),
                VendaPagamentoFixture.persistedEntity(SituacaoPagamento.RECEBIDO));

        List<VendaPagamentoResponseDTO> responses = mapper.toResponseList(pagamentos);

        assertThat(responses)
                .extracting(VendaPagamentoResponseDTO::status)
                .containsExactly(SituacaoPagamento.PENDENTE, SituacaoPagamento.RECEBIDO);
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.toResponseList(List.of())).isEmpty();
    }
}
