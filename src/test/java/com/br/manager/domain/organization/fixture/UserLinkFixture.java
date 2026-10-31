package com.br.manager.domain.organization.fixture;

import com.br.manager.domain.organization.dto.UserLinkInputDTO;
import com.br.manager.domain.organization.entity.UserLink;
import com.br.manager.domain.organization.enums.RoleTypeEnum;

import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Vinculo de usuario (usuario x empresa/posto x papel).
 */
public final class UserLinkFixture {

    public static final RoleTypeEnum ROLE = RoleTypeEnum.MANAGER;
    public static final RoleTypeEnum OTHER_ROLE = RoleTypeEnum.CASHIER;

    private UserLinkFixture() {
    }

    public static UserLinkInputDTO validInput(UUID userId, UUID companyId, UUID stationId) {
        UserLinkInputDTO input = new UserLinkInputDTO();
        input.setUserId(userId);
        input.setCompanyId(companyId);
        input.setStationId(stationId);
        input.setRole(ROLE);
        input.setActive(true);
        return input;
    }

    public static UserLinkInputDTO validInput() {
        return validInput(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    }

    public static UserLink persistedEntity() {
        UserLink userLink = new UserLink();
        userLink.setId(UUID.randomUUID());
        userLink.setUserId(UUID.randomUUID());
        userLink.setCompanyId(UUID.randomUUID());
        userLink.setStationId(UUID.randomUUID());
        userLink.setRole(ROLE);
        userLink.setActive(true);
        return userLink;
    }
}
