package com.br.manager.domain.comum.service;

import com.br.manager.domain.comum.dto.SincronizacaoDTO;
import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.repository.RepositorioSincronizacao;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicoSincronizacaoTest {

    private static final NomeTabelaSincronizacaoEnum TABLE = NomeTabelaSincronizacaoEnum.PRODUTO;
    private static final String INVALIDO_TABELA = "INEXISTENTE";
    private static final LocalDateTime INFORMED_DATE = LocalDateTime.of(2026, 7, 15, 10, 30);

    @Mock
    private RepositorioSincronizacao sincronizacaoRepository;

    @InjectMocks
    private ServicoSincronizacao sincronizacaoService;

    private static Sincronizacao sincronizacaoOf(NomeTabelaSincronizacaoEnum tabela) {
        Sincronizacao sincronizacao = new Sincronizacao();
        ReflectionTestUtils.setField(sincronizacao, "nomeTabela", tabela);
        return sincronizacao;
    }

    private static SincronizacaoDTO dto(String nomeTabela, LocalDateTime ultimaSincronize) {
        SincronizacaoDTO dto = new SincronizacaoDTO();
        dto.setNomeTabela(nomeTabela);
        dto.setUltimaSincronize(ultimaSincronize);
        return dto;
    }

    @Nested
    class FindByTabelaName {

        @Test
        void shouldFindSincronizacaoIgnoringCaseAndSpaces() {
            Sincronizacao sincronizacao = sincronizacaoOf(TABLE);
            when(sincronizacaoRepository.findByNomeTabela(TABLE)).thenReturn(Optional.of(sincronizacao));

            assertThat(sincronizacaoService.findByTabelaName("  produto ")).isSameAs(sincronizacao);
        }

        @Test
        void shouldThrowNotFoundWhenTabelaNameIsVazio() {
            assertThatThrownBy(() -> sincronizacaoService.findByTabelaName(" "))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessage("Nome da tabela não informado");
            verifyNoInteractions(sincronizacaoRepository);
        }

        @Test
        void shouldThrowNotFoundWhenTabelaNameIsInvalido() {
            assertThatThrownBy(() -> sincronizacaoService.findByTabelaName(INVALIDO_TABELA))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(INVALIDO_TABELA);
            verifyNoInteractions(sincronizacaoRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSincronizacaoRowDoesNotExist() {
            when(sincronizacaoRepository.findByNomeTabela(TABLE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sincronizacaoService.findByTabelaName(TABLE.name()))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(TABLE.name());
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateWithInformedDate() {
            when(sincronizacaoRepository.findByNomeTabela(TABLE)).thenReturn(Optional.of(sincronizacaoOf(TABLE)));

            Sincronizacao atualizado = sincronizacaoService.update(dto("produto", INFORMED_DATE));

            assertThat(atualizado.getUltimaSincronizacao()).isEqualTo(INFORMED_DATE);
            assertThat(atualizado.getNomeTabela()).isEqualTo(TABLE);
            verify(sincronizacaoRepository).updateUltimaSincronizacao(TABLE, INFORMED_DATE);
        }

        @Test
        void shouldUpdateWithCurrentDateWhenDateIsNotInformed() {
            LocalDateTime before = LocalDateTime.now();
            when(sincronizacaoRepository.findByNomeTabela(TABLE)).thenReturn(Optional.of(sincronizacaoOf(TABLE)));

            sincronizacaoService.update(dto(TABLE.name(), null));

            ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(sincronizacaoRepository).updateUltimaSincronizacao(eq(TABLE), captor.capture());
            assertThat(captor.getValue()).isAfterOrEqualTo(before).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        void shouldThrowNotFoundWhenDtoIsNull() {
            assertThatThrownBy(() -> sincronizacaoService.update(null))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessage("DTOSincronizacao não informado");
            verifyNoInteractions(sincronizacaoRepository);
        }

        @Test
        void shouldThrowNotFoundWhenTabelaNameIsVazio() {
            assertThatThrownBy(() -> sincronizacaoService.update(dto(null, INFORMED_DATE)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessage("Nome da tabela não informado");
            verifyNoInteractions(sincronizacaoRepository);
        }

        @Test
        void shouldThrowNotFoundWhenTabelaNameIsInvalido() {
            assertThatThrownBy(() -> sincronizacaoService.update(dto(INVALIDO_TABELA, INFORMED_DATE)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(INVALIDO_TABELA);
            verifyNoInteractions(sincronizacaoRepository);
        }

        @Test
        void shouldThrowNotFoundWhenSincronizacaoRowDoesNotExist() {
            when(sincronizacaoRepository.findByNomeTabela(TABLE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sincronizacaoService.update(dto(TABLE.name(), INFORMED_DATE)))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(TABLE.name());
            verify(sincronizacaoRepository, never()).updateUltimaSincronizacao(any(), any());
        }
    }
}
