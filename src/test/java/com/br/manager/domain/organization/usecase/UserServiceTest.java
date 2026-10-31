package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.common.service.SynchronizeService;
import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.fixture.ConstraintViolationFixture;
import com.br.manager.domain.organization.fixture.UserFixture;
import com.br.manager.domain.organization.mapper.UserMapper;
import com.br.manager.domain.organization.repository.UserRepository;
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

import java.time.LocalDateTime;
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
class UserServiceTest {

    private static final String USER_TABLE = "USER";
    private static final String EMAIL_REQUIRED = "O e-mail do usuário é obrigatório.";
    private static final LocalDateTime DEFAULT_SINCE = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    private static final LocalDateTime LAST_SYNCHRONIZE = LocalDateTime.of(2026, 6, 1, 12, 0);

    @Mock
    private UserRepository userRepository;

    @Mock
    private SynchronizeService synchronizeService;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @InjectMocks
    private UserService userService;

    private static ConstraintViolationException emailViolation() {
        User invalid = UserFixture.persistedEntity();
        invalid.setEmail(null);
        return ConstraintViolationFixture.violationsOf(invalid);
    }

    private static User inactiveUser() {
        User user = UserFixture.persistedEntity();
        user.setActive(false);
        return user;
    }

    private static Synchronize synchronizeAt(LocalDateTime lastSynchronize) {
        Synchronize synchronize = new Synchronize();
        synchronize.setLastSynchronize(lastSynchronize);
        return synchronize;
    }

    @Nested
    class Create {

        @Test
        void shouldPersistAndReturnUser() {
            when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(UUID.randomUUID());
                return user;
            });

            UserResponseDTO response = userService.create(UserFixture.validInput());

            assertThat(response.getId()).isNotNull();
            assertThat(response.getEmail()).isEqualTo(UserFixture.EMAIL);
            assertThat(response.getActive()).isTrue();
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            when(userRepository.saveAndFlush(any(User.class))).thenThrow(emailViolation());

            assertThatThrownBy(() -> userService.create(UserFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(EMAIL_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            when(userRepository.saveAndFlush(any(User.class)))
                    .thenThrow(new DataIntegrityViolationException("duplicate email"));

            assertThatThrownBy(() -> userService.create(UserFixture.validInput()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(UserFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Update {

        @Test
        void shouldUpdateExistingUser() {
            User existing = UserFixture.persistedEntity();
            UserInputDTO input = UserFixture.validInput(UserFixture.OTHER_EMAIL);
            input.setId(existing.getId());
            when(userRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(userRepository.saveAndFlush(existing)).thenReturn(existing);

            UserResponseDTO response = userService.update(input);

            assertThat(response.getId()).isEqualTo(existing.getId());
            assertThat(response.getEmail()).isEqualTo(UserFixture.OTHER_EMAIL);
        }

        @Test
        void shouldThrowNotFoundWhenUserDoesNotExist() {
            UserInputDTO input = UserFixture.validInput();
            input.setId(UUID.randomUUID());

            assertThatThrownBy(() -> userService.update(input))
                    .isInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(input.getId().toString());
            verify(userRepository, never()).saveAndFlush(any());
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            User existing = UserFixture.persistedEntity();
            UserInputDTO input = UserFixture.validInput();
            input.setId(existing.getId());
            when(userRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(userRepository.saveAndFlush(existing)).thenThrow(emailViolation());

            assertThatThrownBy(() -> userService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(EMAIL_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            User existing = UserFixture.persistedEntity();
            UserInputDTO input = UserFixture.validInput();
            input.setId(existing.getId());
            when(userRepository.findByIdAndActiveTrue(existing.getId())).thenReturn(existing);
            when(userRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("duplicate"));

            assertThatThrownBy(() -> userService.update(input))
                    .isInstanceOf(BusinessException.class)
                    .isNotInstanceOf(NotFoundBusinessException.class)
                    .hasMessageContaining(UserFixture.NAME)
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class Queries {

        @Test
        void shouldListActiveUsers() {
            when(userRepository.findAllByActiveTrue()).thenReturn(List.of(UserFixture.persistedEntity()));

            assertThat(userService.findAll()).hasSize(1);
        }

        @Test
        void shouldSearchByName() {
            when(userRepository.findByNameContainingIgnoreCaseAndActiveTrue("joao"))
                    .thenReturn(List.of(UserFixture.persistedEntity()));

            assertThat(userService.findByName("joao")).hasSize(1);
        }

        @Test
        void shouldFallbackToFindAllWhenDescriptionIsBlank() {
            when(userRepository.findAllByActiveTrue()).thenReturn(List.of());

            assertThat(userService.findByDescription("")).isEmpty();
            verify(userRepository, never()).findByNameContainingIgnoreCaseAndActiveTrue(any());
        }

        @Test
        void shouldFindActiveUserById() {
            User existing = UserFixture.persistedEntity();
            when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(userService.find(existing.getId()).getId()).isEqualTo(existing.getId());
        }

        @Test
        void shouldReturnActiveEntityById() {
            User existing = UserFixture.persistedEntity();
            when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            assertThat(userService.getUserEntityById(existing.getId())).isSameAs(existing);
        }

        @Test
        void shouldThrowNotFoundWhenIdDoesNotExist() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.find(id)).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> userService.getUserEntityById(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldThrowNotFoundWhenUserIsInactive() {
            User inactive = inactiveUser();
            when(userRepository.findById(inactive.getId())).thenReturn(Optional.of(inactive));

            assertThatThrownBy(() -> userService.find(inactive.getId())).isInstanceOf(NotFoundBusinessException.class);
            assertThatThrownBy(() -> userService.getUserEntityById(inactive.getId())).isInstanceOf(NotFoundBusinessException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldInactivateUserAndFillDeletedAt() {
            User existing = UserFixture.persistedEntity();
            when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

            userService.delete(existing.getId());

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).saveAndFlush(captor.capture());
            assertThat(captor.getValue().getActive()).isFalse();
            assertThat(captor.getValue().getDeletedAt()).isNotNull();
        }

        @Test
        void shouldThrowNotFoundWhenDeletingUnknownUser() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete(id)).isInstanceOf(NotFoundBusinessException.class);
        }

        @Test
        void shouldTranslateConstraintViolationIntoBusinessException() {
            User existing = UserFixture.persistedEntity();
            when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userRepository.saveAndFlush(existing)).thenThrow(emailViolation());

            assertThatThrownBy(() -> userService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(EMAIL_REQUIRED);
        }

        @Test
        void shouldWrapPersistenceErrorInBusinessException() {
            User existing = UserFixture.persistedEntity();
            when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
            when(userRepository.saveAndFlush(existing)).thenThrow(new DataIntegrityViolationException("fk"));

            assertThatThrownBy(() -> userService.delete(existing.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("Erro ao excluir usuário")
                    .hasCauseInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    class ChangedSinceSynchronize {

        @Test
        void shouldSearchUsersChangedSinceLastSynchronize() {
            when(synchronizeService.findByTableName(USER_TABLE)).thenReturn(synchronizeAt(LAST_SYNCHRONIZE));
            when(userRepository.findAllChangedSince(LAST_SYNCHRONIZE)).thenReturn(List.of(UserFixture.persistedEntity()));

            List<UserResponseDTO> changed = userService.findChangedSinceSynchronize(USER_TABLE);

            assertThat(changed).hasSize(1);
        }

        @Test
        void shouldUseDefaultDateWhenNeverSynchronized() {
            when(synchronizeService.findByTableName(USER_TABLE)).thenReturn(synchronizeAt(null));
            when(userRepository.findAllChangedSince(any())).thenReturn(List.of());

            userService.findChangedSinceSynchronize(USER_TABLE);

            ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(userRepository).findAllChangedSince(captor.capture());
            assertThat(captor.getValue()).isEqualTo(DEFAULT_SINCE);
        }

        @Test
        void shouldPropagateNotFoundWhenSynchronizeIsMissing() {
            when(synchronizeService.findByTableName(USER_TABLE)).thenThrow(new NotFoundBusinessException("missing"));

            assertThatThrownBy(() -> userService.findChangedSinceSynchronize(USER_TABLE))
                    .isInstanceOf(NotFoundBusinessException.class);
            verify(userRepository, never()).findAllChangedSince(any());
        }
    }
}
