package com.br.manager.domain.aquisicao_preco.fixture;

import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Tabela de Precos.
 */
public final class TabelaPrecoFixture {

    public static final String NAME = "Tabela Combustiveis Janeiro";
    public static final String OUTROS_NAME = "Tabela Lubrificantes";
    public static final String STATUS = "ATIVO";
    public static final String INACTIVE_SITUACAO = "INACTIVE";
    public static final LocalDateTime VALIDO_DE = LocalDateTime.of(2026, 1, 1, 0, 0);
    public static final LocalDateTime VALIDO_ATE = LocalDateTime.of(2026, 1, 31, 23, 59);

    private TabelaPrecoFixture() {
    }

    public static TabelaPrecoInputDTO validoInput(UUID criadoPor, List<ItemPrecoInputDTO> itensPreco) {
        TabelaPrecoInputDTO input = new TabelaPrecoInputDTO();
        input.setNome(NAME);
        input.setValidoDe(VALIDO_DE);
        input.setValidoAte(VALIDO_ATE);
        input.setStatus(STATUS);
        input.setCriadoPor(criadoPor);
        input.setItensPreco(itensPreco);
        return input;
    }

    public static TabelaPreco persistedEntity(Usuario criadoPor) {
        TabelaPreco tabelaPreco = new TabelaPreco();
        tabelaPreco.setId(UUID.randomUUID());
        tabelaPreco.setNome(NAME);
        tabelaPreco.setValidoDe(VALIDO_DE);
        tabelaPreco.setValidoAte(VALIDO_ATE);
        tabelaPreco.setStatus(STATUS);
        tabelaPreco.setCriadoPor(criadoPor);
        return tabelaPreco;
    }
}
