package com.br.manager.domain.venda_recibo.dto;

import com.br.manager.domain.venda_recibo.enums.SituacaoFiscal;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.fixture.VendaFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaItemFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaPagamentoFixture;
import com.br.manager.domain.venda_recibo.fixture.VendaReembolsoFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReciboVendaValidacaoRequestDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    private static <T> Set<String> messages(T dto) {
        return validator.validate(dto).stream().map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    private static CriarItemVendaDTO validoItem() {
        return new CriarItemVendaDTO(VendaItemFixture.LINHA_NUMERO, UUID.randomUUID(), null, null,
                VendaItemFixture.QUANTITY, VendaItemFixture.PRECO_UNITARIO, VendaItemFixture.DESCONTO, null, null);
    }

    private static CriarVendaRequestDTO validoVenda(List<CriarItemVendaDTO> itens) {
        return new CriarVendaRequestDTO(UUID.randomUUID(), UUID.randomUUID(), VendaFixture.NUMERO_VENDA, VendaFixture.VENDIDO_EM,
                null, null, null, UUID.randomUUID(), VendaFixture.DESCONTO, SituacaoFiscal.EMISSAO_PENDENTE,
                VendaFixture.OBSERVACOES, itens);
    }

    @Test
    void shouldAcceptValidoCreateVendaRequest() {
        assertThat(validator.validate(validoVenda(List.of(validoItem())))).isEmpty();
    }

    @Test
    void shouldReportAllMissingObrigatorioFieldsOfCreateVendaRequest() {
        CriarVendaRequestDTO dto = new CriarVendaRequestDTO(null, null, " ", null, null, null, null, null,
                null, null, null, List.of());

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                "O ID de Estacao é obrigatório.",
                "O ID de Turno é obrigatório.",
                "Venda numero é obrigatório.",
                "Vendido at é obrigatório.",
                "O ID de Caixeiro usuario é obrigatório.",
                "Venda deve contain at least one item.");
    }

    @Test
    void shouldCascadeValidacaoToVendaItens() {
        CriarItemVendaDTO invalidoItem = new CriarItemVendaDTO(null, null, null, null, null, null, null, null, null);

        assertThat(messages(validoVenda(List.of(invalidoItem)))).containsExactlyInAnyOrder(
                "Linha numero é obrigatório.",
                "O ID de Produto é obrigatório.",
                "Quantity é obrigatório.",
                "Unidade preco é obrigatório.");
    }

    @Test
    void shouldAcceptValidoPagamentoRequest() {
        AdicionarPagamentoVendaRequestDTO dto = new AdicionarPagamentoVendaRequestDTO(UUID.randomUUID(), VendaPagamentoFixture.VALOR,
                SituacaoPagamento.RECEBIDO, VendaPagamentoFixture.REFERENCIA_TRANSACAO, null, null);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldReportMissingFieldsOfPagamentoRequest() {
        AdicionarPagamentoVendaRequestDTO dto = new AdicionarPagamentoVendaRequestDTO(null, null, null, null, null, null);

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                "O ID de Pagamento method é obrigatório.",
                "Valor é obrigatório.",
                "Situacao é obrigatório.");
    }

    @Test
    void shouldAcceptValidoReembolsoRequest() {
        ReembolsarVendaRequestDTO dto = new ReembolsarVendaRequestDTO(VendaReembolsoFixture.REASON, UUID.randomUUID(), UUID.randomUUID(),
                null, VendaReembolsoFixture.OBSERVACOES);

        assertThat(validator.validate(dto)).isEmpty();
    }

    @Test
    void shouldReportMissingFieldsOfReembolsoRequest() {
        ReembolsarVendaRequestDTO dto = new ReembolsarVendaRequestDTO("", null, null, null, null);

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                "Reason é obrigatório.",
                "Requested by é obrigatório.",
                "Aprovado by é obrigatório.");
    }
}
