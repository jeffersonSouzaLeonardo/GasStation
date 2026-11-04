package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.FechamentoPagamentoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.mapper.FechamentoPagamentoMapper;
import com.br.manager.domain.turno_caixa.repository.FechamentoPagamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TurnoFechamentoPagamentoService {

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    @Autowired
    private FechamentoPagamentoRepository pagamentoFechamentoRepository;

    @Autowired
    private FechamentoPagamentoMapper pagamentoFechamentoMapper;

    @Transactional
    public FechamentoPagamentoResponseDTO registerFechamentoPagamento(UUID turnoId, FechamentoPagamentoInputDTO request) {
        if (request == null) {
            throw new ExcecaoNegocio("O payload do fechamento de pagamento é obrigatório.");
        }

        Turno turno = validacaoSuporte.getObrigatorioTurno(turnoId);
        validacaoSuporte.ensureTurnoIsAberto(turno);

        if (request.getValorEsperado() == null || request.getValorEsperado().compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocio("O valor esperado é obrigatório e deve ser maior ou igual a zero.");
        }
        if (request.getValorDeclarado() == null || request.getValorDeclarado().compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocio("O valor declarado é obrigatório e deve ser maior ou igual a zero.");
        }

        List<FechamentoPagamento> existingClosings = pagamentoFechamentoRepository.findByTurnoIdOrderByPagamentoMethodId(turnoId);
        FechamentoPagamento fechamentoPagamento = existingClosings.stream()
                .filter(item -> item.getPagamentoMethodId().equals(request.getPagamentoMethodId()))
                .findFirst()
                .orElse(null);

        if (fechamentoPagamento == null) {
            fechamentoPagamento = new FechamentoPagamento();
            fechamentoPagamento.setTurno(turno);
            fechamentoPagamento.setPagamentoMethodId(request.getPagamentoMethodId());
        }

        fechamentoPagamento.setValorEsperado(request.getValorEsperado());
        fechamentoPagamento.setValorDeclarado(request.getValorDeclarado());
        fechamentoPagamento.setValorDiferenca(request.getValorDeclarado().subtract(request.getValorEsperado()));
        fechamentoPagamento.setReferenciaComprovante(request.getReferenciaComprovante());

        FechamentoPagamento savedFechamento = pagamentoFechamentoRepository.saveAndFlush(fechamentoPagamento);
        return pagamentoFechamentoMapper.toResponse(savedFechamento);
    }
}
