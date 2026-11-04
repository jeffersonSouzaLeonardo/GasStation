package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraResponseDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoResponseDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoFechamentoRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResumoResponseDTO;
import com.br.manager.domain.turno_caixa.fixture.TurnoAlocacaoFixture;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    private static final UUID TURNO_ID = UUID.randomUUID();

    @Mock
    private TurnoAberturaService turnoAberturaService;

    @Mock
    private TurnoAlocacaoService turnoAlocacaoService;

    @Mock
    private TurnoBicoLeituraService turnoBicoLeituraService;

    @Mock
    private TurnoMovimentoCaixaService turnoMovimentoCaixaService;

    @Mock
    private TurnoFechamentoPagamentoService turnoFechamentoPagamentoService;

    @Mock
    private TurnoFechamentoService turnoFechamentoService;

    @Mock
    private TurnoResumoService turnoResumoService;

    @InjectMocks
    private TurnoService turnoService;

    @Test
    void shouldDelegateAbertoTurnoToAberturaService() {
        TurnoAberturaRequestDTO request = TurnoFixture.abertoRequest();
        TurnoResponseDTO esperado = new TurnoResponseDTO();
        when(turnoAberturaService.abertoTurno(request)).thenReturn(esperado);

        assertThat(turnoService.abertoTurno(request)).isSameAs(esperado);
    }

    @Test
    void shouldDelegateAlocacaoToAlocacaoService() {
        TurnoAlocacaoInputDTO request = TurnoAlocacaoFixture.validoInput();
        TurnoAlocacaoResponseDTO esperado = new TurnoAlocacaoResponseDTO();
        when(turnoAlocacaoService.addOrUpdateAlocacao(TURNO_ID, request)).thenReturn(esperado);

        assertThat(turnoService.addOrUpdateAlocacao(TURNO_ID, request)).isSameAs(esperado);
    }

    @Test
    void shouldDelegateBicoLeituraToBicoLeituraService() {
        BicoLeituraInputDTO request = new BicoLeituraInputDTO();
        BicoLeituraResponseDTO esperado = new BicoLeituraResponseDTO();
        when(turnoBicoLeituraService.registerBicoLeitura(TURNO_ID, request)).thenReturn(esperado);

        assertThat(turnoService.registerBicoLeitura(TURNO_ID, request)).isSameAs(esperado);
    }

    @Test
    void shouldDelegateMovimentoCaixaToMovimentoCaixaService() {
        MovimentoCaixaInputDTO request = new MovimentoCaixaInputDTO();
        MovimentoCaixaResponseDTO esperado = new MovimentoCaixaResponseDTO();
        when(turnoMovimentoCaixaService.registerMovimentoCaixa(TURNO_ID, request)).thenReturn(esperado);

        assertThat(turnoService.registerMovimentoCaixa(TURNO_ID, request)).isSameAs(esperado);
    }

    @Test
    void shouldDelegateFechamentoPagamentoToFechamentoPagamentoService() {
        FechamentoPagamentoInputDTO request = new FechamentoPagamentoInputDTO();
        FechamentoPagamentoResponseDTO esperado = new FechamentoPagamentoResponseDTO();
        when(turnoFechamentoPagamentoService.registerFechamentoPagamento(TURNO_ID, request)).thenReturn(esperado);

        assertThat(turnoService.registerFechamentoPagamento(TURNO_ID, request)).isSameAs(esperado);
    }

    @Test
    void shouldDelegateCloseTurnoToFechamentoService() {
        TurnoFechamentoRequestDTO request = TurnoFixture.closeRequest(UUID.randomUUID());
        TurnoResponseDTO esperado = new TurnoResponseDTO();
        when(turnoFechamentoService.closeTurno(TURNO_ID, request)).thenReturn(esperado);

        assertThat(turnoService.closeTurno(TURNO_ID, request)).isSameAs(esperado);
    }

    @Test
    void shouldDelegateResumoToResumoService() {
        TurnoResumoResponseDTO esperado = new TurnoResumoResponseDTO();
        when(turnoResumoService.getTurnoResumo(TURNO_ID)).thenReturn(esperado);

        assertThat(turnoService.getTurnoResumo(TURNO_ID)).isSameAs(esperado);
    }
}
