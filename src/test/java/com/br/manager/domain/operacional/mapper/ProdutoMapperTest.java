package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.ProdutoInputDTO;
import com.br.manager.domain.operacional.dto.ProdutoResponseDTO;
import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import com.br.manager.domain.operacional.fixture.ProdutoFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProdutoMapperTest {

    private final ProdutoMapper mapper = new ProdutoMapper();

    @Test
    void shouldMapInputToEntity() {
        ProdutoInputDTO input = ProdutoFixture.combustivelInput();
        input.setId(UUID.randomUUID());

        Produto produto = mapper.produtoInputDTOToProduto(input);

        assertThat(produto)
                .usingRecursiveComparison()
                .comparingOnlyFields("id", "sku", "nome", "produtoType", "unidade", "anpCode", "ncm", "cest", "ativo")
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.produtoInputDTOToProduto(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Produto produto = ProdutoFixture.persistedCombustivel();

        ProdutoResponseDTO response = mapper.produtoToProdutoResponseDTO(produto);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(produto);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.produtoToProdutoResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Produto> produtos = List.of(ProdutoFixture.persistedCombustivel(), ProdutoFixture.persistedConveniencia());

        List<ProdutoResponseDTO> responses = mapper.listProdutoToListProdutoResponseDTO(produtos);

        assertThat(responses)
                .extracting(ProdutoResponseDTO::getId)
                .containsExactly(produtos.get(0).getId(), produtos.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listProdutoToListProdutoResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Produto produto = ProdutoFixture.persistedCombustivel();
        ProdutoInputDTO input = ProdutoFixture.convenienciaInput();
        input.setId(produto.getId());
        input.setAtivo(false);

        mapper.updateProdutoFromDto(input, produto);

        assertThat(produto.getId()).isEqualTo(input.getId());
        assertThat(produto.getNome()).isEqualTo(ProdutoFixture.CONVENIENCIA_NAME);
        assertThat(produto.getProdutoType()).isEqualTo(ProdutoTypeEnum.CONVENIENCIA);
        assertThat(produto.getAtivo()).isFalse();
    }

    @Test
    void shouldIgnoreUpdateWhenInputIsNull() {
        Produto produto = ProdutoFixture.persistedCombustivel();

        mapper.updateProdutoFromDto(null, produto);

        assertThat(produto.getNome()).isEqualTo(ProdutoFixture.COMBUSTIVEL_NAME);
    }
}
