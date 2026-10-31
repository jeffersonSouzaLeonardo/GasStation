package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.dto.UserLinkResponseDTO;
import com.br.manager.domain.organization.entity.UserLink;
import com.br.manager.domain.organization.fixture.UserLinkFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserLinkMapperTest {

    private final UserLinkMapper mapper = new UserLinkMapper();

    @Test
    void shouldMapInputToEntity() {
        UserLinkInputDTO input = UserLinkFixture.validInput();
        input.setId(UUID.randomUUID());

        UserLink userLink = mapper.userLinkInputDTOToUserLink(input);

        assertThat(userLink)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.userLinkInputDTOToUserLink(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        UserLink userLink = UserLinkFixture.persistedEntity();

        UserLinkResponseDTO response = mapper.userLinkToUserLinkResponseDTO(userLink);

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(userLink);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.userLinkToUserLinkResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<UserLink> userLinks = List.of(UserLinkFixture.persistedEntity(), UserLinkFixture.persistedEntity());

        List<UserLinkResponseDTO> responses = mapper.listUserLinkToListUserLinkResponseDTO(userLinks);

        assertThat(responses)
                .extracting(UserLinkResponseDTO::getId)
                .containsExactly(userLinks.get(0).getId(), userLinks.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listUserLinkToListUserLinkResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        UserLink userLink = UserLinkFixture.persistedEntity();
        UserLinkInputDTO input = UserLinkFixture.validInput();
        input.setId(userLink.getId());
        input.setRole(UserLinkFixture.OTHER_ROLE);
        input.setActive(false);

        mapper.updateUserLinkFromDto(input, userLink);

        assertThat(userLink)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        UserLink userLink = UserLinkFixture.persistedEntity();

        mapper.updateUserLinkFromDto(null, userLink);
        mapper.updateUserLinkFromDto(UserLinkFixture.validInput(), null);

        assertThat(userLink.getRole()).isEqualTo(UserLinkFixture.ROLE);
    }
}
