package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.AuditoriaInputDTO;
import com.br.manager.domain.organizacao.dto.AuditoriaResponseDTO;
import com.br.manager.domain.organizacao.entity.Auditoria;
import com.br.manager.domain.organizacao.fixture.AuditoriaFixture;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditoriaMapperTest {

    private final AuditoriaMapper mapper = new AuditoriaMapper();

    @Test
    void shouldMapInputToEntity() {
        AuditoriaInputDTO input = AuditoriaFixture.validoInput();
        input.setId(UUID.randomUUID());
        input.setUsuarioId(UUID.randomUUID());

        Auditoria auditoria = mapper.auditoriaInputDTOToAuditoria(input);

        assertThat(auditoria)
                .usingRecursiveComparison()
                .isEqualTo(input);
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.auditoriaInputDTOToAuditoria(null)).isNull();
    }

    @Test
    void shouldMapEntityToResponse() {
        Auditoria auditoria = AuditoriaFixture.persistedEntity();

        AuditoriaResponseDTO response = mapper.auditoriaToAuditoriaResponseDTO(auditoria);

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("empresaId")
                .isEqualTo(auditoria);
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.auditoriaToAuditoriaResponseDTO(null)).isNull();
    }

    @Test
    void shouldMapEntityListToResponseList() {
        List<Auditoria> auditorias = List.of(AuditoriaFixture.persistedEntity(), AuditoriaFixture.persistedEntity());

        List<AuditoriaResponseDTO> responses = mapper.listAuditoriaToListAuditoriaResponseDTO(auditorias);

        assertThat(responses)
                .extracting(AuditoriaResponseDTO::getId)
                .containsExactly(auditorias.get(0).getId(), auditorias.get(1).getId());
    }

    @Test
    void shouldReturnNullWhenEntityListIsNull() {
        assertThat(mapper.listAuditoriaToListAuditoriaResponseDTO(null)).isNull();
    }

    @Test
    void shouldUpdateEntityFromInput() {
        Auditoria auditoria = AuditoriaFixture.persistedEntity();
        AuditoriaInputDTO input = AuditoriaFixture.validoInput();
        input.setId(auditoria.getId());
        input.setAcao("DELETE");

        mapper.updateAuditoriaFromDto(input, auditoria);

        assertThat(auditoria.getAcao()).isEqualTo("DELETE");
        assertThat(auditoria.getId()).isEqualTo(input.getId());
    }

    @Test
    void shouldIgnoreUpdateWhenInputOrEntityIsNull() {
        Auditoria auditoria = AuditoriaFixture.persistedEntity();

        mapper.updateAuditoriaFromDto(null, auditoria);
        mapper.updateAuditoriaFromDto(AuditoriaFixture.validoInput(), null);

        assertThat(auditoria.getAcao()).isEqualTo(AuditoriaFixture.ACAO);
    }
}
