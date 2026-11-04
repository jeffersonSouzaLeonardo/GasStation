package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.operacional.dto.AbastecimentoRequestDTO;
import com.br.manager.domain.operacional.entity.Abastecimento;
import com.br.manager.domain.operacional.fixture.AbastecimentoFixture;
import com.br.manager.domain.operacional.mapper.AbastecimentoMapper;
import com.br.manager.domain.operacional.repository.AbastecimentoRepository;
import com.br.manager.domain.operacional.support.OperacionalBViolacoes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbastecimentoServiceTest {

    @Mock
    private AbastecimentoRepository abastecimentoRepository;

    @Spy
    private AbastecimentoMapper abastecimentoMapper = new AbastecimentoMapper();

    @InjectMocks
    private AbastecimentoService abastecimentoService;

    @Captor
    private ArgumentCaptor<List<Abastecimento>> abastecimentosCaptor;

    @Test
    void shouldPersistAllRecebidoAbastecimentosKeepingPdvIds() {
        List<AbastecimentoRequestDTO> requests = List.of(AbastecimentoFixture.validoRequest(), AbastecimentoFixture.validoRequest());

        abastecimentoService.saveAll(requests);

        verify(abastecimentoRepository).saveAllAndFlush(abastecimentosCaptor.capture());
        assertThat(abastecimentosCaptor.getValue())
                .extracting(Abastecimento::getId, Abastecimento::getVolume, Abastecimento::getValorTotal)
                .containsExactly(
                        tuple(requests.get(0).getId(), AbastecimentoFixture.VOLUME, AbastecimentoFixture.TOTAL_VALOR),
                        tuple(requests.get(1).getId(), AbastecimentoFixture.VOLUME, AbastecimentoFixture.TOTAL_VALOR));
    }

    @Test
    void shouldIgnoreNullBatch() {
        abastecimentoService.saveAll(null);

        verifyNoInteractions(abastecimentoRepository);
    }

    @Test
    void shouldIgnoreEmptyBatch() {
        abastecimentoService.saveAll(List.of());

        verifyNoInteractions(abastecimentoRepository);
    }

    @Test
    void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
        when(abastecimentoRepository.saveAllAndFlush(anyList())).thenThrow(OperacionalBViolacoes.violacoesOf(new Abastecimento()));
        List<AbastecimentoRequestDTO> requests = List.of(AbastecimentoFixture.validoRequest());

        assertThatThrownBy(() -> abastecimentoService.saveAll(requests))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("O volume é obrigatório.");
    }

    @Test
    void shouldWrapPersistenceErrorInExcecaoNegocio() {
        when(abastecimentoRepository.saveAllAndFlush(anyList())).thenThrow(new DataIntegrityViolationException("fk_abastecimentos_tanque"));
        List<AbastecimentoRequestDTO> requests = List.of(AbastecimentoFixture.validoRequest());

        assertThatThrownBy(() -> abastecimentoService.saveAll(requests))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessage("Erro ao salvar abastecimentos recebidos do PDV")
                .hasCauseInstanceOf(DataIntegrityViolationException.class);
    }
}
