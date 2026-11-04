package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.MovimentoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.mapper.MovimentoCaixaMapper;
import com.br.manager.domain.turno_caixa.repository.MovimentoCaixaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TurnoMovimentoCaixaService {

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    @Autowired
    private MovimentoCaixaRepository dinheiroMovimentoRepository;

    @Autowired
    private MovimentoCaixaMapper dinheiroMovimentoMapper;

    @Transactional
    public MovimentoCaixaResponseDTO registerMovimentoCaixa(UUID turnoId, MovimentoCaixaInputDTO request) {
        if (request == null) {
            throw new ExcecaoNegocio("O payload da movimentação de caixa é obrigatório.");
        }

        Turno turno = validacaoSuporte.getObrigatorioTurno(turnoId);
        validacaoSuporte.ensureTurnoIsAberto(turno);

        if (request.getTipoMovimento() == null) {
            throw new ExcecaoNegocio("O tipo de movimentação é obrigatório.");
        }
        if (request.getValor() == null || request.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocio("O valor deve ser maior ou igual a zero.");
        }

        Usuario criadoPor = validacaoSuporte.getObrigatorioUsuario(request.getCriadoPor(), "criadoPor");

        MovimentoCaixa movimento = dinheiroMovimentoMapper.toEntity(request, turno);
        MovimentoCaixa savedMovimento = dinheiroMovimentoRepository.saveAndFlush(movimento);
        return dinheiroMovimentoMapper.toResponse(savedMovimento);
    }
}
