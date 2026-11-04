package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import com.br.manager.domain.turno_caixa.mapper.TurnoAlocacaoMapper;
import com.br.manager.domain.turno_caixa.repository.TurnoAlocacaoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TurnoAlocacaoService {

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    @Autowired
    private TurnoAlocacaoRepository alocacaoRepository;

    @Autowired
    private TurnoAlocacaoMapper alocacaoMapper;

    @Transactional
    public TurnoAlocacaoResponseDTO addOrUpdateAlocacao(UUID turnoId, TurnoAlocacaoInputDTO request) {
        if (request == null) {
            throw new ExcecaoNegocio("O payload da alocação é obrigatório.");
        }

        Turno turno = validacaoSuporte.getObrigatorioTurno(turnoId);
        validacaoSuporte.ensureTurnoIsAberto(turno);

        Usuario alocacaoUsuario = validacaoSuporte.getObrigatorioUsuario(request.getUsuarioId(), "alocacaoUsuarioId");

        AlocacaoTurno existing = alocacaoRepository.findByTurnoIdAndUsuarioId(turnoId, request.getUsuarioId()).orElse(null);
        if (existing != null) {
            existing.setCargo(request.getCargo());
            return alocacaoMapper.toResponse(alocacaoRepository.saveAndFlush(existing));
        }

        return alocacaoMapper.toResponse(alocacaoRepository.saveAndFlush(alocacaoMapper.toEntity(request, turno)));
    }
}
