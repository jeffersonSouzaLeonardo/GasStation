package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.entity.UserLink;
import com.br.manager.domain.organization.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organization.fixture.UserLinkFixture;
import com.br.manager.domain.organization.mapper.UserLinkMapper;
import com.br.manager.domain.organization.repository.UserLinkRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLinkServiceTest {

    private static final String ROLE_REQUIRED = "O papel é obrigatório.";

    @Mock
    private UserLinkRepository userLinkRepository;

    @Spy
    private UserLinkMapper userLinkMapper = new UserLinkMapper();

    @InjectMocks
    private UserLinkService userLinkService;

    private static ConstraintViolationException roleViolation() {
        UserLink invalid = UserLinkFixture.persistedEntity();
        invalid.setRole(null);
        return ConstraintViolationFixture.violationsOf(invalid);
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnUserLink() {
            when(userLinkRepository.saveAndFlush(any(UserLink.class))).thenAnswer(invocation -> {
                UserLink userLink = invocation.getArgument(0);
                userLink.setId(UUID.randomUUID());
                return userLink;
            });
            UserLinkInputDTO input = UserLinkFixture.validInput();

            UserLinkResponseDTO response = userLinkService.create(input);

            assertThat(response.getId()).isNotNull();
            assertThat(response.getUserId()).isEqualTo(input.getUserId());
            assertThat(response.getRole()).isEqualTo(UserLinkFixture.ROLE);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            when(userLinkRepository.saveAndFlush(any(UserLink.class))).thenThrow(roleViolation());

            assertThatThrownBy(() -> userLinkService.create(UserLinkFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ROLE_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessExceptionWithRole() {
            when(userLinkRepository.saveAndFlush(any(UserLink.class)))
                    .thenThrow(new DataIntegrityViolationException("fk user"));

            assertThatThrownBy(() -> userLinkService.create(UserLinkFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserLinkFixture.ROLE.name())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void shouldWrapErrorWithoutRoleWhenRoleIsNull() {
            UserLinkInputDTO input = UserLinkFixture.validInput();
            input.setRole(null);
            when(userLinkRepository.saveAndFlush(any(UserLink.class)))
                    .thenThrow(new DataIntegrityViolationException("role null"));

            assertThatThrownBy(() -> userLinkService.create(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao salvar vínculo de usuário ");
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingUserLink() {
            UserLink existing = UserLinkFixture.persistedEntity();
            UserLinkInputDTO input = UserLinkFixture.validInput();
            input.setId(existing.getId());
            input.setRole(UserLinkFixture.OTHER_ROLE);
            when(userLinkRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(userLinkRepository.saveAndFlush(existing)).thenReturn(existing);

            UserLinkResponseDTO response = userLinkService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getRole()).isEqualTo(UserLinkFixture.OTHER_ROLE);
        }

        @Test
        void shouldThrowNotFoundWhenUserLinkDoesNotExist() {
            UserLinkInputDTO input = UserLinkFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> userLinkService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(userLinkRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            UserLink existing = UserLinkFixture.persistedEntity();
            UserLinkInputDTO input = UserLinkFixture.validInput();
            input.setId(existing.getId());
            when(userLinkRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(userLinkRepository.saveAndFlush(existing)).thenThrow(roleViolation());

            assertThatThrownBy(() -> userLinkService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ROLE_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            UserLink existing = UserLinkFixture.persistedEntity();
            UserLinkInputDTO input = UserLinkFixture.validInput();
            input.setId(existing.getId());
            when(userLinkRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(userLinkRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> userLinkService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(UserLinkFixture.ROLE.name())
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveUserLinks() {
            when(userLinkRepository.findAllByActiveTrue()).thenReturn(List.of(UserLinkFixture.persistedEntity()));

            assertThat(userLinkService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByRoleText() {
            when(userLinkRepository.findByRoleTextAndActiveTrue("mana"))
                    .thenReturn(List.of(UserLinkFixture.persistedEntity()));

            assertThat(userLinkService.findByName("mana")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(userLinkRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(userLinkService.findByDescription(null)).isEmpty();
            verify(userLinkRepository, never()).findByRoleTextAndActiveTrue(any());
        }

        @Test
        void shouldFindByRole() {
            when(userLinkRepository.findByRoleAndActiveTrue(UserLinkFixture.ROLE))
                    .thenReturn(List.of(UserLinkFixture.persistedEntity()));

            assertThat(userLinkService.findByRole(UserLinkFixture.ROLE))
                    .extracting(UserLinkResponseDTO::getRole)
                    .containsExactly(UserLinkFixture.ROLE);
        }

        @Test
        void shouldFindById() {
            UserLink existing = UserLinkFixture.persistedEntity();
            when(userLinkRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(userLinkService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnEntityById() {
            UserLink existing = UserLinkFixture.persistedEntity();
            when(userLinkRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);

            assertThat(userLinkService.getUserLinkEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();

            assertThatThrownBy(() -> userLinkService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> userLinkService.getUserLinkEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateUserLink() {
            UserLink existing = UserLinkFixture.persistedEntity();
            when(userLinkRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            userLinkService.delete(existing.getId());

            ArgumentCaptor<UserLink> captor = ArgumentCaptor.forClass(UserLink.class);
            verify(userLinkRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownUserLink() {
            UUID id = UUID.randomUUID();
            when(userLinkRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userLinkService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            UserLink existing = UserLinkFixture.persistedEntity();
            when(userLinkRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userLinkRepository.saveAndFlush(existing)).thenThrow(roleViolation());

            assertThatThrownBy(() -> userLinkService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ROLE_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            UserLink existing = UserLinkFixture.persistedEntity();
            when(userLinkRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userLinkRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> userLinkService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir vínculo de usuário")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }
}
