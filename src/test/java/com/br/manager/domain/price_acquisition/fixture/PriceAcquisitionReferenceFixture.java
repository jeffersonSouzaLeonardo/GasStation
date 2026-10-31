package com.br.manager.domain.price_acquisition.fixture;

import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.organization.entity.User;

import java.util.UUID;

/**
 * Entidades de outros dominios (usuario, fornecedor) usadas como referencia nos testes unitarios.
 */
public final class PriceAcquisitionReferenceFixture {

    public static final String USER_NAME = "Comprador Teste";
    public static final String SUPPLIER_NAME = "Distribuidora Teste SA";

    private PriceAcquisitionReferenceFixture() {
    }

    public static User activeUser() {
        return user(true);
    }

    public static User inactiveUser() {
        return user(false);
    }

    public static Supplier activeSupplier() {
        return supplier(true);
    }

    public static Supplier inactiveSupplier() {
        return supplier(false);
    }

    private static User user(boolean active) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(USER_NAME);
        user.setEmail("comprador@posto.com.br");
        user.setPasswordHash("hash");
        user.setActive(active);
        return user;
    }

    private static Supplier supplier(boolean active) {
        Supplier supplier = new Supplier();
        supplier.setId(UUID.randomUUID());
        supplier.setLegalName(SUPPLIER_NAME);
        supplier.setActive(active);
        return supplier;
    }
}
