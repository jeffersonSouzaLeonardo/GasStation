package com.br.manager.domain.organization.mapper;

import com.br.manager.domain.organization.dto.AuditInputDTO;
import com.br.manager.domain.organization.dto.AuditResponseDTO;
import com.br.manager.domain.organization.entity.Audit;
import com.br.manager.domain.organization.fixture.AuditFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditMapperTest {

    private final AuditMapper mapper = new AuditMapper();

    @Test
    void shouldMapInputToEntity() {
        AuditInputDTO input = AuditFixture.validInput();
        input.setId(UUID.randomUUID());
        input.setUserId(UUID.randomUUID());

        Audit audit = mapper.auditInputDTOToAudit(input);

        assertThat(audit)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.auditInputDTOToAudit(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Audit audit = AuditFixture.persistedEntity();

        AuditResponseDTO response = mapper.auditToAuditResponseDTO(audit);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("companyId")
                .isEqualTo(audit);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.auditToAuditResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Audit> audits = List.of(AuditFixture.persistedEntity(), AuditFixture.persistedEntity());

        List<AuditResponseDTO> responses = mapper.listAuditToListAuditResponseDTO(audits);

        assertThat(responses)
                .extracting(AuditResponseDTO::getId)
                .containsExactly(audits.get(0).getId(), audits.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listAuditToListAuditResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Audit audit = AuditFixture.persistedEntity();
        AuditInputDTO input = AuditFixture.validInput();
        input.setId(audit.getId());
        input.setAction("DELETE");

        mapper.updateAuditFromDto(input, audit);

        assertThat(audit.getAction()).isEqualTo("DELETE");
        assertThat(audit.getId()).isEqualTo(input.getId());
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        Audit audit = AuditFixture.persistedEntity();

        mapper.updateAuditFromDto(null, audit);
        mapper.updateAuditFromDto(AuditFixture.validInput(), null);

        assertThat(audit.getAction()).isEqualTo(AuditFixture.ACTION);
    }
}
