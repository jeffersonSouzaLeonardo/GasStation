package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.turno_caixa.dto.TurnoSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoSincronizacaoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Spy
    private TurnoMapper turnoMapper = new TurnoMapper();

    @InjectMocks
    private TurnoSincronizacaoService turnoSincronizacaoService;

    @Test
    void shouldInsertTurnoWhenItDoesNotExist() {
        TurnoSincronizacaoDTO dto = TurnoFixture.abertoSincronizacao();
        when(turnoRepository.findById(dto.getId())).thenReturn(Optional.empty());

        turnoSincronizacaoService.saveAll(List.of(dto));

        ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(dto.getId());
        assertThat(captor.getValue().getCaixeiroUsuarioId()).isEqualTo(dto.getOpenedBy());
        assertThat(captor.getValue().getFundoAbertura()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(captor.getValue().getStatus()).isEqualTo(TurnoSituacaoEnum.ABERTO);
    }

    @Test
    void shouldUpdateExistingTurnoKeepingAberturaDinheiro() {
        Turno existing = TurnoFixture.abertoTurno();
        TurnoSincronizacaoDTO dto = TurnoFixture.fechadoSincronizacao(existing.getId(), existing.getEstacaoId(), existing.getOpenedBy());
        when(turnoRepository.findById(existing.getId())).thenReturn(Optional.of(existing));

        turnoSincronizacaoService.saveAll(List.of(dto));

        ArgumentCaptor<Turno> captor = ArgumentCaptor.forClass(Turno.class);
        verify(turnoRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue()).isSameAs(existing);
        assertThat(existing.getStatus()).isEqualTo(TurnoSituacaoEnum.FECHADO);
        assertThat(existing.getFechadoAt()).isEqualTo(TurnoFixture.FECHADO_AT);
        assertThat(existing.getFundoAbertura()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
    }

    @Test
    void shouldIgnoreNullList() {
        turnoSincronizacaoService.saveAll(null);

        verifyNoInteractions(turnoRepository);
    }

    @Test
    void shouldIgnoreEmptyList() {
        turnoSincronizacaoService.saveAll(List.of());

        verifyNoInteractions(turnoRepository);
    }

    @Test
    void shouldSkipNullEntries() {
        TurnoSincronizacaoDTO dto = TurnoFixture.abertoSincronizacao();
        when(turnoRepository.findById(dto.getId())).thenReturn(Optional.empty());

        turnoSincronizacaoService.saveAll(Arrays.asList(null, dto));

        verify(turnoRepository, times(1)).saveAndFlush(any(Turno.class));
    }

    @Test
    void shouldWrapConstraintViolationInExcecaoNegocio() {
        TurnoSincronizacaoDTO dto = TurnoFixture.abertoSincronizacao();
        when(turnoRepository.findById(dto.getId())).thenReturn(Optional.empty());
        when(turnoRepository.saveAndFlush(any(Turno.class))).thenThrow(invalidoTurnoViolation());

        assertThatThrownBy(() -> turnoSincronizacaoService.saveAll(List.of(dto)))
                .isInstanceOf(ExcecaoNegocio.class)
                .hasMessageContaining("O ID do posto é obrigatório.");
    }

    private static ConstraintViolationException invalidoTurnoViolation() {
        Turno invalido = TurnoFixture.abertoTurno();
        invalido.setEstacaoId(null);
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Turno>> violacoes = validator.validate(invalido);
            return new ConstraintViolationException(violacoes);
        }
    }
}
