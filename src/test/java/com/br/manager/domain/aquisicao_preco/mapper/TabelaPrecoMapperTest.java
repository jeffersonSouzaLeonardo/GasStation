package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import com.br.manager.domain.aquisicao_preco.fixture.AquisicaoPrecoReferenciaFixture;
import com.br.manager.domain.aquisicao_preco.fixture.ItemPrecoFixture;
import com.br.manager.domain.aquisicao_preco.fixture.TabelaPrecoFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TabelaPrecoMapperTest {

    private final TabelaPrecoMapper mapper = new TabelaPrecoMapper();

    @Test
    void shouldMapInputToEntityWithCreatorReferencia() {
        UUID creatorId = UUID.randomUUID();
        TabelaPrecoInputDTO input = TabelaPrecoFixture.validoInput(creatorId, List.of());
        input.setId(UUID.randomUUID());

        TabelaPreco entity = mapper.precoTabelaInputDTOToTabelaPreco(input);

        assertThat(entity.getId()).isEqualTo(input.getId());
        assertThat(entity.getNome()).isEqualTo(TabelaPrecoFixture.NAME);
        assertThat(entity.getValidoDe()).isEqualTo(TabelaPrecoFixture.VALIDO_DE);
        assertThat(entity.getValidoAte()).isEqualTo(TabelaPrecoFixture.VALIDO_ATE);
        assertThat(entity.getStatus()).isEqualTo(TabelaPrecoFixture.STATUS);
        assertThat(entity.getCriadoPor().getId()).isEqualTo(creatorId);
    }

    @Test
    void shouldNotSetCreatorWhenInputHasNoCreator() {
        TabelaPrecoInputDTO input = TabelaPrecoFixture.validoInput(null, List.of());

        TabelaPreco entity = mapper.precoTabelaInputDTOToTabelaPreco(input);

        assertThat(entity.getCriadoPor()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.precoTabelaInputDTOToTabelaPreco(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponseFormattingDatesAndNestedItens() {
        Usuario creator = AquisicaoPrecoReferenciaFixture.ativoUsuario();
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(creator);
        ItemPreco itemPreco = ItemPrecoFixture.persistedEntity(tabelaPreco);
        tabelaPreco.setItensPreco(Arrays.asList(itemPreco, null));

        TabelaPrecoResponseDTO response = mapper.precoTabelaToTabelaPrecoResponseDTO(tabelaPreco);

        assertThat(response.getId()).isEqualTo(tabelaPreco.getId());
        assertThat(response.getNome()).isEqualTo(TabelaPrecoFixture.NAME);
        assertThat(response.getValidoDe()).isEqualTo(TabelaPrecoFixture.VALIDO_DE.toString());
        assertThat(response.getValidoAte()).isEqualTo(TabelaPrecoFixture.VALIDO_ATE.toString());
        assertThat(response.getStatus()).isEqualTo(TabelaPrecoFixture.STATUS);
        assertThat(response.getCriadoPor()).isEqualTo(creator.getId());
        assertThat(response.getItensPreco()).singleElement()
                .usingRecursiveComparison()
                .isEqualTo(esperadoItemResponse(itemPreco));
    }

    @Test
    void shouldMapEntityWithoutOptionalDataToResponseWithNulls() {
        TabelaPreco tabelaPreco = new TabelaPreco();

        TabelaPrecoResponseDTO response = mapper.precoTabelaToTabelaPrecoResponseDTO(tabelaPreco);

        assertThat(response.getValidoDe()).isNull();
        assertThat(response.getValidoAte()).isNull();
        assertThat(response.getCriadoPor()).isNull();
        assertThat(response.getItensPreco()).isNull();
    }

    @Test
    void shouldMapNestedItemWithoutTabelaPrecoToNullTabelaPrecoId() {
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(null);
        tabelaPreco.setItensPreco(List.of(ItemPrecoFixture.persistedEntity(null)));

        TabelaPrecoResponseDTO response = mapper.precoTabelaToTabelaPrecoResponseDTO(tabelaPreco);

        assertThat(response.getItensPreco()).singleElement()
                .extracting(ItemPrecoResponseDTO::getPrecoTabelaId)
                .isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.precoTabelaToTabelaPrecoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListIgnoringNullElements() {
        TabelaPreco first = TabelaPrecoFixture.persistedEntity(null);
        TabelaPreco second = TabelaPrecoFixture.persistedEntity(null);

        List<TabelaPrecoResponseDTO> responses = mapper.listTabelaPrecoToListTabelaPrecoResponseDTO(Arrays.asList(first, null, second));

        assertThat(responses).extracting(TabelaPrecoResponseDTO::getId).containsExactly(first.getId(), second.getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listTabelaPrecoToListTabelaPrecoResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityWithProvidedFieldsKeepingIdAndCreator() {
        Usuario creator = AquisicaoPrecoReferenciaFixture.ativoUsuario();
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(creator);
        UUID originalId = tabelaPreco.getId();
        LocalDateTime newValidoTo = TabelaPrecoFixture.VALIDO_ATE.plusMonths(1);
        TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
        input.setId(UUID.randomUUID());
        input.setNome(TabelaPrecoFixture.OUTROS_NAME);
        input.setValidoDe(TabelaPrecoFixture.VALIDO_DE.plusDays(1));
        input.setValidoAte(newValidoTo);
        input.setStatus(TabelaPrecoFixture.INACTIVE_SITUACAO);
        input.setCriadoPor(UUID.randomUUID());

        mapper.updateTabelaPrecoFromDto(input, tabelaPreco);

        assertThat(tabelaPreco.getId()).isEqualTo(originalId);
        assertThat(tabelaPreco.getNome()).isEqualTo(TabelaPrecoFixture.OUTROS_NAME);
        assertThat(tabelaPreco.getValidoDe()).isEqualTo(TabelaPrecoFixture.VALIDO_DE.plusDays(1));
        assertThat(tabelaPreco.getValidoAte()).isEqualTo(newValidoTo);
        assertThat(tabelaPreco.getStatus()).isEqualTo(TabelaPrecoFixture.INACTIVE_SITUACAO);
        assertThat(tabelaPreco.getCriadoPor()).isSameAs(creator);
    }

    @Test
    void shouldKeepEntityValuesWhenInputFieldsAreNull() {
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(null);

        mapper.updateTabelaPrecoFromDto(new TabelaPrecoInputDTO(), tabelaPreco);

        assertThat(tabelaPreco.getNome()).isEqualTo(TabelaPrecoFixture.NAME);
        assertThat(tabelaPreco.getValidoDe()).isEqualTo(TabelaPrecoFixture.VALIDO_DE);
        assertThat(tabelaPreco.getValidoAte()).isEqualTo(TabelaPrecoFixture.VALIDO_ATE);
        assertThat(tabelaPreco.getStatus()).isEqualTo(TabelaPrecoFixture.STATUS);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        TabelaPreco tabelaPreco = TabelaPrecoFixture.persistedEntity(null);

        mapper.updateTabelaPrecoFromDto(null, tabelaPreco);
        mapper.updateTabelaPrecoFromDto(TabelaPrecoFixture.validoInput(null, null), null);

        assertThat(tabelaPreco.getNome()).isEqualTo(TabelaPrecoFixture.NAME);
    }

    private ItemPrecoResponseDTO esperadoItemResponse(ItemPreco itemPreco) {
        ItemPrecoResponseDTO esperado = new ItemPrecoResponseDTO();
        esperado.setId(itemPreco.getId());
        esperado.setPrecoTabelaId(itemPreco.getTabelaPreco().getId());
        esperado.setProdutoId(itemPreco.getProdutoId());
        esperado.setBicoId(itemPreco.getBicoId());
        esperado.setPrecoUnitario(ItemPrecoFixture.PRECO_UNITARIO);
        esperado.setPrecoMinimo(ItemPrecoFixture.PRECO_MINIMO);
        esperado.setDescontoMaximoPercentual(ItemPrecoFixture.DESCONTO_MAXIMO_PERCENTUAL);
        esperado.setAtivo(true);
        return esperado;
    }
}
