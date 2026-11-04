package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaFechamentoInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaInputDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaResponseDTO;
import com.br.manager.domain.turno_caixa.dto.SessaoCaixaSincronizacaoDTO;
import com.br.manager.domain.turno_caixa.entity.SessaoCaixa;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.mapper.SessaoCaixaMapper;
import com.br.manager.domain.turno_caixa.repository.SessaoCaixaRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SessaoCaixaService {

    @Autowired
    private SessaoCaixaRepository dinheiroSessaoRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private SessaoCaixaMapper dinheiroSessaoMapper;

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    public SessaoCaixaResponseDTO open(SessaoCaixaInputDTO request) {
        if (request == null) {
            throw new ExcecaoNegocio("O payload de abertura do caixa é obrigatório.");
        }

        Turno turno = turnoRepository.findByIdAndStatus(request.getTurnoId(), TurnoSituacaoEnum.ABERTO)
                .orElseThrow(() -> new ExcecaoNegocio(String.format(
                        "Não existe turno ABERTO com o id %s.", request.getTurnoId())));

        validacaoSuporte.getObrigatorioUsuario(request.getCaixeiroUsuarioId(), "caixeiroUsuarioId");

        if (dinheiroSessaoRepository.existsByCaixeiroUsuarioIdAndStatus(
                request.getCaixeiroUsuarioId(), SessaoCaixaSituacaoEnum.ABERTO)) {
            throw new ExcecaoNegocio(String.format(
                    "O usuário %s já possui uma sessão de caixa ABERTA.", request.getCaixeiroUsuarioId()));
        }

        SessaoCaixa sessao = dinheiroSessaoMapper.toEntity(request, turno);
        sessao.setId(UUID.randomUUID());
        sessao.setStatus(SessaoCaixaSituacaoEnum.ABERTO);
        sessao.setOpenedAt(LocalDateTime.now());

        return dinheiroSessaoMapper.toResponse(dinheiroSessaoRepository.saveAndFlush(sessao));
    }

    public SessaoCaixaResponseDTO close(UUID id, SessaoCaixaFechamentoInputDTO request) {
        SessaoCaixa sessao = dinheiroSessaoRepository.findByIdAndStatus(id, SessaoCaixaSituacaoEnum.ABERTO)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format(
                        "Sessão de caixa ABERTA não encontrada com o id %s.", id)));

        if (request != null) {
            sessao.setTotalDinheiro(request.getTotalDinheiro());
            sessao.setTotalCartao(request.getTotalCartao());
            sessao.setTotalPix(request.getTotalPix());
            sessao.setTotalSangria(request.getTotalSangria());
            if (request.getObservacoes() != null) {
                sessao.setObservacoes(request.getObservacoes());
            }
        }

        sessao.setStatus(SessaoCaixaSituacaoEnum.FECHADO);
        sessao.setFechadoAt(LocalDateTime.now());

        return dinheiroSessaoMapper.toResponse(dinheiroSessaoRepository.saveAndFlush(sessao));
    }

    public SessaoCaixaResponseDTO findById(UUID id) {
        SessaoCaixa sessao = dinheiroSessaoRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format(
                        "Sessão de caixa não encontrada com o id %s.", id)));
        return dinheiroSessaoMapper.toResponse(sessao);
    }

    public List<SessaoCaixaResponseDTO> findByTurno(UUID turnoId) {
        return dinheiroSessaoMapper.toResponseList(
                dinheiroSessaoRepository.findByTurno_IdOrderByOpenedAt(turnoId));
    }

    /**
     * Persiste as sessões de caixa recebidas do PDV. A operação é idempotente:
     * reenvios do mesmo lote apenas atualizam os registros já existentes.
     */
    public void saveAll(List<SessaoCaixaSincronizacaoDTO> sessoes) {
        if (sessoes == null || sessoes.isEmpty()) {
            return;
        }
        try {
            for (SessaoCaixaSincronizacaoDTO dto : sessoes) {
                if (dto == null) {
                    continue;
                }
                SessaoCaixa existing = dinheiroSessaoRepository.findById(dto.getId()).orElse(null);
                if (existing != null) {
                    dinheiroSessaoMapper.updateFromSincronizacao(dto, existing);
                    dinheiroSessaoRepository.saveAndFlush(existing);
                } else {
                    Turno turno = turnoRepository.findById(dto.getTurnoId())
                            .orElseThrow(() -> new ExcecaoNegocio(String.format(
                                    "Turno %s da sessão de caixa %s não encontrado. Sincronize os turnos antes das sessões.",
                                    dto.getTurnoId(), dto.getId())));
                    dinheiroSessaoRepository.saveAndFlush(dinheiroSessaoMapper.toEntity(dto, turno));
                }
            }
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        }
    }
}
