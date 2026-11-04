package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.turno_caixa.dto.TurnoFechamentoRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TurnoFechamentoService {

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private BicoLeituraRepository bicoLeituraRepository;

    @Autowired
    private TurnoMapper turnoMapper;

    @Transactional
    public TurnoResponseDTO closeTurno(UUID turnoId, TurnoFechamentoRequestDTO request) {
        if (request == null || request.getFechadoBy() == null) {
            throw new ExcecaoNegocio("O usuário de fechamento é obrigatório.");
        }

        Turno turno = validacaoSuporte.getObrigatorioTurno(turnoId);
        if (turno.getStatus() == TurnoSituacaoEnum.FECHADO) {
            throw new ExcecaoNegocio(String.format("O turno %s já está fechado.", turnoId));
        }

        for (var bico : validacaoSuporte.findBicosByEstacaoId(turno.getEstacaoId())) {
            boolean hasFechamentoLeitura = bicoLeituraRepository.existsByTurnoIdAndBicoIdAndTipoLeitura(
                    turnoId, bico.getId(), TipoLeituraBicoEnum.FECHAMENTO);
            if (!hasFechamentoLeitura) {
                throw new ExcecaoNegocio(String.format(
                        "A leitura de fechamento é obrigatória para a bomba %s antes de encerrar o turno.", bico.getId()));
            }
        }

        Usuario fechamentoUsuario = validacaoSuporte.getObrigatorioUsuario(request.getFechadoBy(), "fechadoBy");

        turno.setStatus(TurnoSituacaoEnum.FECHADO);
        turno.setFechadoAt(LocalDateTime.now());
        turno.setFechadoBy(request.getFechadoBy());

        Turno savedTurno = turnoRepository.saveAndFlush(turno);
        return turnoMapper.toResponse(savedTurno);
    }
}
