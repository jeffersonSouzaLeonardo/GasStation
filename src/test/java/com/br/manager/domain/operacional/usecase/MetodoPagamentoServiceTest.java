package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.dto.MetodoPagamentoResponseDTO;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.enums.TipoMetodoPagamentoEnum;
import com.br.manager.domain.operacional.fixture.MetodoPagamentoFixture;
import com.br.manager.domain.operacional.mapper.MetodoPagamentoMapper;
import com.br.manager.domain.operacional.repository.MetodoPagamentoRepository;
import com.br.manager.domain.operacional.support.OperacionalBViolacoes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetodoPagamentoServiceTest {

    @Mock
    private MetodoPagamentoRepository pagamentoMethodRepository;

    @Spy
    private MetodoPagamentoMapper pagamentoMethodMapper = new MetodoPagamentoMapper();

    @InjectMocks
    private MetodoPagamentoService pagamentoMethodService;

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnMetodoPagamento() {
            when(pagamentoMethodRepository.saveAndFlush(any(MetodoPagamento.class))).thenAnswer(invocation -> {
                MetodoPagamento metodoPagamento = invocation.getArgument(0);
                metodoPagamento.setId(UUID.randomUUID());
                return metodoPagamento;
            });

            MetodoPagamentoResponseDTO response = pagamentoMethodService.create(MetodoPagamentoFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getTipo()).isEqualTo(MetodoPagamentoFixture.TIPO);
            assertThat(response.getDiasLiquidacao()).isEqualTo(MetodoPagamentoFixture.LIQUIDACAO_DIAS);
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            when(pagamentoMethodRepository.saveAndFlush(any(MetodoPagamento.class)))
                    .thenThrow(OperacionalBViolacoes.violacoesOf(new MetodoPagamento()));

            assertThatThrownBy(() -> pagamentoMethodService.create(MetodoPagamentoFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Pagamento method tipo é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(pagamentoMethodRepository.saveAndFlush(any(MetodoPagamento.class)))
                    .thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> pagamentoMethodService.create(MetodoPagamentoFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar forma de pagamento")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingMetodoPagamento() {
            MetodoPagamento existing = MetodoPagamentoFixture.persistedEntity();
            MetodoPagamentoInputDTO input = MetodoPagamentoFixture.outrosInput();
            input.setId(existing.getId());
            when(pagamentoMethodRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(pagamentoMethodRepository.saveAndFlush(existing)).thenReturn(existing);

            MetodoPagamentoResponseDTO response = pagamentoMethodService.update(input);

            assertThat(response.getNome()).isEqualTo(MetodoPagamentoFixture.OUTROS_NAME);
            assertThat(response.getTipo()).isEqualTo(TipoMetodoPagamentoEnum.DINHEIRO);
        }

        @Test
        void shouldThrowNotFoundWhenMetodoPagamentoDoesNotExist() {
            MetodoPagamentoInputDTO input = MetodoPagamentoFixture.validoInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> pagamentoMethodService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(pagamentoMethodRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldConvertConstraintViolacoesIntoExcecaoNegocio() {
            MetodoPagamento existing = MetodoPagamentoFixture.persistedEntity();
            MetodoPagamentoInputDTO input = MetodoPagamentoFixture.validoInput();
            input.setId(existing.getId());
            when(pagamentoMethodRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(pagamentoMethodRepository.saveAndFlush(existing))
                    .thenThrow(OperacionalBViolacoes.violacoesOf(new MetodoPagamento()));

            assertThatThrownBy(() -> pagamentoMethodService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining("Pagamento method nome é obrigatório.");
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            MetodoPagamento existing = MetodoPagamentoFixture.persistedEntity();
            MetodoPagamentoInputDTO input = MetodoPagamentoFixture.validoInput();
            input.setId(existing.getId());
            when(pagamentoMethodRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);
            when(pagamentoMethodRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("too long"));

            assertThatThrownBy(() -> pagamentoMethodService.update(input))
                    .isExactlyInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao atualizar forma de pagamento")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAtivoMetodoPagamentos() {
            when(pagamentoMethodRepository.findAllByAtivoTrue()).thenReturn(List.of(MetodoPagamentoFixture.persistedEntity()));

            assertThat(pagamentoMethodService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByName() {
            when(pagamentoMethodRepository.findByNameContainingIgnoreCaseAndAtivoTrue("visa"))
                    .thenReturn(List.of(MetodoPagamentoFixture.persistedEntity()));

            assertThat(pagamentoMethodService.findByDescricao("visa"))
                    .extracting(MetodoPagamentoResponseDTO::getNome)
                    .containsExactly(MetodoPagamentoFixture.NAME);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(pagamentoMethodRepository.findAllByAtivoTrue()).thenReturn(List.of());

            assertThat(pagamentoMethodService.findByDescricao(" ")).isEmpty();
            verify(pagamentoMethodRepository, never()).findByNameContainingIgnoreCaseAndAtivoTrue(any());
        }

        @Test
        void shouldFindById() {
            MetodoPagamento existing = MetodoPagamentoFixture.persistedEntity();
            when(pagamentoMethodRepository.findByIdAndAtivoTrue(existing.getId())).thenReturn(existing);

            assertThat(pagamentoMethodService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> pagamentoMethodService.find(id))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(id.toString());
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateMetodoPagamento() {
            MetodoPagamento existing = MetodoPagamentoFixture.persistedEntity();
            when(pagamentoMethodRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            pagamentoMethodService.delete(existing.getId());

            ArgumentCaptor<MetodoPagamento> captor = ArgumentCaptor.forClass(MetodoPagamento.class);
            verify(pagamentoMethodRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getAtivo()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownMetodoPagamento() {
            UUID id = UUID.randomUUID();
            when(pagamentoMethodRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> pagamentoMethodService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(pagamentoMethodRepository, never()).saveAndFlush(any());
        }
    }
}
