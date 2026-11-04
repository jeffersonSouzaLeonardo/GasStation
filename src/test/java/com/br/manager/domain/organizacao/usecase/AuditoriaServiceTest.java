package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.AuditoriaInputDTO;
import com.br.manager.domain.organizacao.dto.AuditoriaResponseDTO;
import com.br.manager.domain.organizacao.entity.Auditoria;
import com.br.manager.domain.organizacao.fixture.AuditoriaFixture;
import com.br.manager.domain.organizacao.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organizacao.mapper.AuditoriaMapper;
import com.br.manager.domain.organizacao.repository.AuditoriaRepository;
import jakarta.validation.ConstraintViolationException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    private static final String ACAO_OBRIGATORIO = "A ação é obrigatória.";

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Spy
    private AuditoriaMapper auditoriaMapper = new AuditoriaMapper();

    @InjectMocks
    private AuditoriaService auditoriaService;

    private static ConstraintViolationException acaoViolation() {
        Auditoria invalido = AuditoriaFixture.persistedEntity();
        invalido.setAcao(null);
        return ConstraintViolationFixture.violacoesOf(invalido);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnAuditoria() {
            when(auditoriaRepository.saveAndFlush(any(Auditoria.class))).thenAnswer(invocation -> {
                Auditoria auditoria = invocation.getArgument(0);
                auditoria.setId(UUID.randomUUID());
                return auditoria;
            });

            AuditoriaResponseDTO response = auditoriaService.create(AuditoriaFixture.validoInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getEntityName()).isEqualTo(AuditoriaFixture.ENTITY_NAME);
            assertThat(response.getCriadoEm()).isEqualTo(AuditoriaFixture.CRIADO_EM);
        }

        @Test
        void shouldFillCriadoEmWhenNotInformed() {
            AuditoriaInputDTO input = AuditoriaFixture.validoInput();
            input.setCriadoEm(null);
            when(auditoriaRepository.saveAndFlush(any(Auditoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

            auditoriaService.create(input);

            ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
            verify(auditoriaRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getCriadoEm()).isNotNull();
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            when(auditoriaRepository.saveAndFlush(any(Auditoria.class))).thenThrow(acaoViolation());

            assertThatThrownBy(() -> auditoriaService.create(AuditoriaFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(ACAO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            when(auditoriaRepository.saveAndFlush(any(Auditoria.class)))
                    .thenThrow(new DataIntegrityViolationException("empresa_id null"));

            assertThatThrownBy(() -> auditoriaService.create(AuditoriaFixture.validoInput()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(AuditoriaFixture.ENTITY_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapErrorWhenInputIsNull() {
            assertThatThrownBy(() -> auditoriaService.create(null))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao salvar auditoria ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingAuditoria() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            AuditoriaInputDTO input = AuditoriaFixture.validoInput();
            input.setId(existing.getId());
            input.setAcao("DELETE");
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(auditoriaRepository.saveAndFlush(existing)).thenReturn(existing);

            AuditoriaResponseDTO response = auditoriaService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getAcao()).isEqualTo("DELETE");
        }

        @Test
        void shouldThrowNotFoundWhenAuditoriaDoesNotExist() {
            AuditoriaInputDTO input = AuditoriaFixture.validoInput();
            input.setId(UUID.randomUUID());
            when(auditoriaRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> auditoriaService.update(input))
                    .isInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(input.getId().toString());
            verify(auditoriaRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            AuditoriaInputDTO input = AuditoriaFixture.validoInput();
            input.setId(existing.getId());
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(auditoriaRepository.saveAndFlush(existing)).thenThrow(acaoViolation());

            assertThatThrownBy(() -> auditoriaService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(ACAO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            AuditoriaInputDTO input = AuditoriaFixture.validoInput();
            input.setId(existing.getId());
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(auditoriaRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> auditoriaService.update(input))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .isNotInstanceOf(ExcecaoNegocioNaoEncontrado.class)
                    .hasMessageContaining(AuditoriaFixture.ENTITY_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAuditoriasPedidoByCreation() {
            when(auditoriaRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of(AuditoriaFixture.persistedEntity()));

            assertThat(auditoriaService.findAll()).hasSize(1);
        }

        @Test
        void shouldBuscaByEntityName() {
            when(auditoriaRepository.findByEntityNameContainingIgnoreCaseAndOrderByCriadoEmDesc("Comp"))
                    .thenReturn(List.of(AuditoriaFixture.persistedEntity()));

            assertThat(auditoriaService.findByName("Comp")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescricaoIsVazio() {
            when(auditoriaRepository.findAllByOrderByCriadoEmDesc()).thenReturn(List.of());

            assertThat(auditoriaService.findByDescricao(" ")).isEmpty();
            verify(auditoriaRepository, never()).findByEntityNameContainingIgnoreCaseAndOrderByCriadoEmDesc(any());
        }

        @Test
        void shouldFindById() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(auditoriaService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(auditoriaService.getAuditoriaEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(auditoriaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> auditoriaService.find(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            assertThatThrownBy(() -> auditoriaService.getAuditoriaEntityById(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldPhysicallyDeleteAuditoria() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            auditoriaService.delete(existing.getId());

            verify(auditoriaRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownAuditoria() {
            UUID id = UUID.randomUUID();
            when(auditoriaRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> auditoriaService.delete(id)).isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
            verify(auditoriaRepository, never()).delete(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoExcecaoNegocio() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(acaoViolation()).when(auditoriaRepository).delete(existing);

            assertThatThrownBy(() -> auditoriaService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessageContaining(ACAO_OBRIGATORIO);
        }

        @Test
        void shouldWrapPersistenceErrorInExcecaoNegocio() {
            Auditoria existing = AuditoriaFixture.persistedEntity();
            when(auditoriaRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(auditoriaRepository).delete(existing);

            assertThatThrownBy(() -> auditoriaService.delete(existing.getId()))
                    .isInstanceOf(ExcecaoNegocio.class)
                    .hasMessage("Erro ao excluir auditoria")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
