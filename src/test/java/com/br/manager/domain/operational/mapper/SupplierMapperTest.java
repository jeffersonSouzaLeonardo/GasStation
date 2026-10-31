package com.br.manager.domain.operational.mapper;

import com.br.manager.domain.operational.dto.SupplierInputDTO;
import com.br.manager.domain.operational.dto.SupplierResponseDTO;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.fixture.SupplierFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierMapperTest {

    private final SupplierMapper mapper = new SupplierMapper();

    @Test
    void shouldMapInputToEntityRemovingMasks() {
        SupplierInputDTO input = SupplierFixture.validInput();
        input.setId(UUID.randomUUID());

        Supplier supplier = mapper.supplierInputDTOToSupplier(input);

        assertThat(supplier.getId()).isEqualTo(input.getId());
        assertThat(supplier.getLegalName()).isEqualTo(SupplierFixture.LEGAL_NAME);
        assertThat(supplier.getCnpjCpf()).isEqualTo(SupplierFixture.CNPJ);
        assertThat(supplier.getStateRegistration()).isEqualTo(SupplierFixture.STATE_REGISTRATION);
        assertThat(supplier.getPhone()).isEqualTo(SupplierFixture.PHONE);
        assertThat(supplier.getAddressZipCode()).isEqualTo(SupplierFixture.ZIP_CODE);
        assertThat(supplier.getEmail()).isEqualTo(SupplierFixture.EMAIL);
        assertThat(supplier.getAddressStreet()).isEqualTo(SupplierFixture.ADDRESS_STREET);
        assertThat(supplier.getAddressNumber()).isEqualTo(SupplierFixture.ADDRESS_NUMBER);
        assertThat(supplier.getAddressComplement()).isEqualTo(SupplierFixture.ADDRESS_COMPLEMENT);
        assertThat(supplier.getAddressNeighborhood()).isEqualTo(SupplierFixture.ADDRESS_NEIGHBORHOOD);
        assertThat(supplier.getAddressCity()).isEqualTo(SupplierFixture.ADDRESS_CITY);
        assertThat(supplier.getAddressState()).isEqualTo(SupplierFixture.ADDRESS_STATE);
        assertThat(supplier.getActive()).isTrue();
    }

    @Test
    void shouldMapEntityToResponse() {
        Supplier supplier = SupplierFixture.persistedEntity();

        SupplierResponseDTO response = mapper.supplierToSupplierResponseDTO(supplier);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "deletedAt")
                .isEqualTo(supplier);
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Supplier> suppliers = List.of(SupplierFixture.persistedEntity(), SupplierFixture.persistedEntity());

        List<SupplierResponseDTO> responses = mapper.listSupplierToListSupplierResponseDTO(suppliers);

        assertThat(responses)
                .extracting(SupplierResponseDTO::getId)
                .containsExactly(suppliers.get(0).getId(), suppliers.get(1).getId());
    }

    @Test
    void shouldMapEmptyListToEmptyResponseList() {
        assertThat(mapper.listSupplierToListSupplierResponseDTO(List.of())).isEmpty();
    }

    @Test
    void shouldUpdateEntityFromInputKeepingIdAndRemovingMasks() {
        Supplier supplier = SupplierFixture.persistedEntity();
        UUID originalId = supplier.getId();
        SupplierInputDTO input = SupplierFixture.otherInput();
        input.setId(UUID.randomUUID());
        input.setActive(false);

        mapper.updateSupplierFromDto(input, supplier);

        assertThat(supplier.getId()).isEqualTo(originalId);
        assertThat(supplier.getLegalName()).isEqualTo(SupplierFixture.OTHER_LEGAL_NAME);
        assertThat(supplier.getCnpjCpf()).isEqualTo(SupplierFixture.OTHER_CNPJ);
        assertThat(supplier.getPhone()).isEqualTo(SupplierFixture.PHONE);
        assertThat(supplier.getAddressZipCode()).isEqualTo(SupplierFixture.ZIP_CODE);
        assertThat(supplier.getActive()).isFalse();
    }
}
