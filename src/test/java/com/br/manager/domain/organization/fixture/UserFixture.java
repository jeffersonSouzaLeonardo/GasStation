package com.br.manager.domain.organization.fixture;

import com.br.manager.domain.organization.dto.UserInputDTO;
import com.br.manager.domain.organization.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Massa de dados padrao para cenarios de Usuario.
 */
public final class UserFixture {

    public static final String NAME = "Joao da Silva";
    public static final String EMAIL = "joao.silva@postobom.com.br";
    public static final String OTHER_EMAIL = "maria.souza@postobom.com.br";
    public static final String PASSWORD_HASH = "$2a$10$hashDeSenhaFicticio";
    public static final String PHONE = "11987654321";
    public static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 3, 10, 8, 0);

    private UserFixture() {
    }

    public static UserInputDTO validInput() {
        return validInput(EMAIL);
    }

    public static UserInputDTO validInput(String email) {
        UserInputDTO input = new UserInputDTO();
        input.setName(NAME);
        input.setEmail(email);
        input.setPasswordHash(PASSWORD_HASH);
        input.setPhone(PHONE);
        input.setActive(true);
        return input;
    }

    public static User persistedEntity() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(NAME);
        user.setEmail(EMAIL);
        user.setPasswordHash(PASSWORD_HASH);
        user.setPhone(PHONE);
        user.setActive(true);
        user.setCreatedAt(CREATED_AT);
        return user;
    }
}
