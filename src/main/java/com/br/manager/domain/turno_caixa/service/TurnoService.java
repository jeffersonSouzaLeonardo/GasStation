package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.turno_caixa.dto.*;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoFechamentoRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TurnoService {

    @Autowired
    private TurnoAberturaService turnoAberturaService;

    @Autowired
    private TurnoAlocacaoService turnoAlocacaoService;

    @Autowired
    private TurnoBicoLeituraService turnoBicoLeituraService;

    @Autowired
    private TurnoMovimentoCaixaService turnoMovimentoCaixaService;

    @Autowired
    private TurnoFechamentoPagamentoService turnoFechamentoPagamentoService;

    @Autowired
    private TurnoFechamentoService turnoFechamentoService;

    @Autowired
    private TurnoResumoService turnoResumoService;

    public TurnoResponseDTO abertoTurno(TurnoAberturaRequestDTO request) {
        return turnoAberturaService.abertoTurno(request);
    }

    public TurnoAlocacaoResponseDTO addOrUpdateAlocacao(UUID turnoId, TurnoAlocacaoInputDTO request) {
        return turnoAlocacaoService.addOrUpdateAlocacao(turnoId, request);
    }

    public BicoLeituraResponseDTO registerBicoLeitura(UUID turnoId, BicoLeituraInputDTO request) {
        return turnoBicoLeituraService.registerBicoLeitura(turnoId, request);
    }

    public MovimentoCaixaResponseDTO registerMovimentoCaixa(UUID turnoId, MovimentoCaixaInputDTO request) {
        return turnoMovimentoCaixaService.registerMovimentoCaixa(turnoId, request);
    }

    public FechamentoPagamentoResponseDTO registerFechamentoPagamento(UUID turnoId, FechamentoPagamentoInputDTO request) {
        return turnoFechamentoPagamentoService.registerFechamentoPagamento(turnoId, request);
    }

    public TurnoResponseDTO closeTurno(UUID turnoId, TurnoFechamentoRequestDTO request) {
        return turnoFechamentoService.closeTurno(turnoId, request);
    }

    public TurnoResumoResponseDTO getTurnoResumo(UUID turnoId) {
        return turnoResumoService.getTurnoResumo(turnoId);
    }
}
