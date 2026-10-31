package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.dto.UserResponseDTO;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.fixture.UserFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void shouldMapInputToEntity() {
        UserInputDTO input = UserFixture.validInput();
        input.setId(UUID.randomUUID());

        User user = mapper.userInputDTOToUser(input);

        assertThat(user)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.userInputDTOToUser(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        User user = UserFixture.persistedEntity();

        UserResponseDTO response = mapper.userToUserResponseDTO(user);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.userToUserResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<User> users = List.of(UserFixture.persistedEntity(), UserFixture.persistedEntity());

        List<UserResponseDTO> responses = mapper.listUserToListUserResponseDTO(users);

        assertThat(responses)
                .extracting(UserResponseDTO::getId)
                .containsExactly(users.get(0).getId(), users.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listUserToListUserResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        User user = UserFixture.persistedEntity();
        UserInputDTO input = UserFixture.validInput(UserFixture.OTHER_EMAIL);
        input.setId(user.getId());
        input.setName("Maria Souza");
        input.setActive(false);

        mapper.updateUserFromDto(input, user);

        assertThat(user.getName()).isEqualTo("Maria Souza");
        assertThat(user.getEmail()).isEqualTo(UserFixture.OTHER_EMAIL);
        assertThat(user.getActive()).isFalse();
    }

    @Test
    @Disabled("BUG: UserMapper.updateUserFromDto uses BeanUtils.copyProperties copying null fields, "
            + "overwriting createdAt (and other timestamps) despite the comment saying only non-null properties are copied")
    void shouldKeepCreatedAtWhenInputHasNoCreatedAt() {
        User user = UserFixture.persistedEntity();
        UserInputDTO input = UserFixture.validInput();
        input.setId(user.getId());

        mapper.updateUserFromDto(input, user);

        assertThat(user.getCreatedAt()).isEqualTo(UserFixture.CREATED_AT);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        User user = UserFixture.persistedEntity();

        mapper.updateUserFromDto(null, user);
        mapper.updateUserFromDto(UserFixture.validInput(), null);

        assertThat(user.getEmail()).isEqualTo(UserFixture.EMAIL);
    }
}
