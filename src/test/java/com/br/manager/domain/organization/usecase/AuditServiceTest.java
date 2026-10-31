package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.entity.Audit;
import com.br.manager.domain.organization.fixture.AuditFixture;
import com.br.manager.domain.organization.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organization.mapper.AuditMapper;
import com.br.manager.domain.organization.repository.AuditRepository;
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
class AuditServiceTest {

    private static final String ACTION_REQUIRED = "A ação é obrigatória.";

    @Mock
    private AuditRepository auditRepository;

    @Spy
    private AuditMapper auditMapper = new AuditMapper();

    @InjectMocks
    private AuditService auditService;

    private static ConstraintViolationException actionViolation() {
        Audit invalid = AuditFixture.persistedEntity();
        invalid.setAction(null);
        return ConstraintViolationFixture.violationsOf(invalid);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnAudit() {
            when(auditRepository.saveAndFlush(any(Audit.class))).thenAnswer(invocation -> {
                Audit audit = invocation.getArgument(0);
                audit.setId(UUID.randomUUID());
                return audit;
            });

            AuditResponseDTO response = auditService.create(AuditFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getEntityName()).isEqualTo(AuditFixture.ENTITY_NAME);
            assertThat(response.getCreatedAt()).isEqualTo(AuditFixture.CREATED_AT);
        }

        @Test
        void shouldFillCreatedAtWhenNotInformed() {
            AuditInputDTO input = AuditFixture.validInput();
            input.setCreatedAt(null);
            when(auditRepository.saveAndFlush(any(Audit.class))).thenAnswer(invocation -> invocation.getArgument(0));

            auditService.create(input);

            ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
            verify(auditRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getCreatedAt()).isNotNull();
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            when(auditRepository.saveAndFlush(any(Audit.class))).thenThrow(actionViolation());

            assertThatThrownBy(() -> auditService.create(AuditFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ACTION_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(auditRepository.saveAndFlush(any(Audit.class)))
                    .thenThrow(new DataIntegrityViolationException("company_id null"));

            assertThatThrownBy(() -> auditService.create(AuditFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(AuditFixture.ENTITY_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapErrorWhenInputIsNull() {
            assertThatThrownBy(() -> auditService.create(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar auditoria ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingAudit() {
            Audit existing = AuditFixture.persistedEntity();
            AuditInputDTO input = AuditFixture.validInput();
            input.setId(existing.getId());
            input.setAction("DELETE");
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(auditRepository.saveAndFlush(existing)).thenReturn(existing);

            AuditResponseDTO response = auditService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getAction()).isEqualTo("DELETE");
        }

        @Test
        void shouldThrowNotFoundWhenAuditDoesNotExist() {
            AuditInputDTO input = AuditFixture.validInput();
            input.setId(UUID.randomUUID());
            when(auditRepository.findById(input.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> auditService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(auditRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Audit existing = AuditFixture.persistedEntity();
            AuditInputDTO input = AuditFixture.validInput();
            input.setId(existing.getId());
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(auditRepository.saveAndFlush(existing)).thenThrow(actionViolation());

            assertThatThrownBy(() -> auditService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ACTION_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Audit existing = AuditFixture.persistedEntity();
            AuditInputDTO input = AuditFixture.validInput();
            input.setId(existing.getId());
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(auditRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> auditService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(AuditFixture.ENTITY_NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListAuditsOrderedByCreation() {
            when(auditRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(AuditFixture.persistedEntity()));

            assertThat(auditService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByEntityName() {
            when(auditRepository.findByEntityNameContainingIgnoreCaseAndOrderByCreatedAtDesc("Comp"))
                    .thenReturn(List.of(AuditFixture.persistedEntity()));

            assertThat(auditService.findByName("Comp")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(auditRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

            assertThat(auditService.findByDescription(" ")).isEmpty();
            verify(auditRepository, never()).findByEntityNameContainingIgnoreCaseAndOrderByCreatedAtDesc(any());
        }

        @Test
        void shouldFindById() {
            Audit existing = AuditFixture.persistedEntity();
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(auditService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            Audit existing = AuditFixture.persistedEntity();
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(auditService.getAuditEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(auditRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> auditService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> auditService.getAuditEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldPhysicallyDeleteAudit() {
            Audit existing = AuditFixture.persistedEntity();
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            auditService.delete(existing.getId());

            verify(auditRepository).delete(existing);
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownAudit() {
            UUID id = UUID.randomUUID();
            when(auditRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> auditService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
            verify(auditRepository, never()).delete(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            Audit existing = AuditFixture.persistedEntity();
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(actionViolation()).when(auditRepository).delete(existing);

            assertThatThrownBy(() -> auditService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ACTION_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            Audit existing = AuditFixture.persistedEntity();
            when(auditRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            doThrow(new DataIntegrityViolationException("fk")).when(auditRepository).delete(existing);

            assertThatThrownBy(() -> auditService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir auditoria")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
