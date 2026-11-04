package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.turno_caixa.dto.TurnoAlocacaoInputDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoAberturaRequestDTO;
import com.br.manager.domain.turno_caixa.dto.TurnoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.mapper.TurnoAlocacaoMapper;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoAlocacaoRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TurnoAberturaService {

    private final TurnoRepository turnoRepository;
    private final TurnoAlocacaoRepository alocacaoRepository;
    private final BicoLeituraRepository bicoLeituraRepository;
    private final SuporteValidacaoTurno validacaoSuporte;
    private final TurnoMapper turnoMapper;
    private final TurnoAlocacaoMapper turnoAlocacaoMapper;

    public TurnoAberturaService(
            TurnoRepository turnoRepository,
            TurnoAlocacaoRepository alocacaoRepository,
            BicoLeituraRepository bicoLeituraRepository,
            SuporteValidacaoTurno validacaoSuporte,
            TurnoMapper turnoMapper,
            TurnoAlocacaoMapper turnoAlocacaoMapper) {
        this.turnoRepository = turnoRepository;
        this.alocacaoRepository = alocacaoRepository;
        this.bicoLeituraRepository = bicoLeituraRepository;
        this.validacaoSuporte = validacaoSuporte;
        this.turnoMapper = turnoMapper;
        this.turnoAlocacaoMapper = turnoAlocacaoMapper;
    }

    @Transactional
    public TurnoResponseDTO abertoTurno(TurnoAberturaRequestDTO request) {
        if (request == null) {
            throw new ExcecaoNegocio("O payload de abertura do turno é obrigatório.");
        }

        validacaoSuporte.getObrigatorioEstacao(request.getEstacaoId());
        validacaoSuporte.getObrigatorioUsuario(request.getCaixeiroUsuarioId(), "caixeiroUsuarioId");
        validacaoSuporte.getObrigatorioUsuario(request.getOpenedBy(), "openedBy");
        validateNoAbertoTurnoForEstacaoAndCaixeiro(request);

        Turno turno = turnoMapper.toEntity(request);
        turno.setStatus(TurnoSituacaoEnum.ABERTO);
        turno.setOpenedAt(LocalDateTime.now());

        Turno savedTurno = turnoRepository.saveAndFlush(turno);
        registerAlocacoes(savedTurno, request);
        registerAberturaBicoLeituras(savedTurno, request.getOpenedBy());
        return turnoMapper.toResponse(savedTurno);
    }

    private void validateNoAbertoTurnoForEstacaoAndCaixeiro(TurnoAberturaRequestDTO request) {
        if (turnoRepository.existsByEstacaoIdAndStatus(request.getEstacaoId(), TurnoSituacaoEnum.ABERTO)) {
            throw new ExcecaoNegocio(String.format(
                    "Já existe um turno ABERTO para o posto %s. Apenas um turno pode estar ativo por estação.",
                    request.getEstacaoId()));
        }

        if (turnoRepository.existsByEstacaoIdAndCaixeiroUsuarioIdAndStatus(
                request.getEstacaoId(), request.getCaixeiroUsuarioId(), TurnoSituacaoEnum.ABERTO)) {
            throw new ExcecaoNegocio(String.format(
                    "Já existe um turno ABERTO para o posto %s e o caixa %s.",
                    request.getEstacaoId(), request.getCaixeiroUsuarioId()));
        }
    }

    private void registerAlocacoes(Turno turno, TurnoAberturaRequestDTO request) {
        if (request.getAlocacoes() == null) {
            return;
        }

        for (TurnoAlocacaoInputDTO alocacaoDto : request.getAlocacoes()) {
            if (alocacaoDto == null) {
                continue;
            }
            validacaoSuporte.getObrigatorioUsuario(alocacaoDto.getUsuarioId(), "alocacaoUsuarioId");
            AlocacaoTurno alocacao = turnoAlocacaoMapper.toEntity(alocacaoDto, turno);
            alocacaoRepository.saveAndFlush(alocacao);
        }
    }

    private void registerAberturaBicoLeituras(Turno turno, UUID registradoPor) {
        for (Bico bico : validacaoSuporte.findBicosByEstacaoId(turno.getEstacaoId())) {
            LeituraBico abertura = new LeituraBico();
            abertura.setTurno(turno);
            abertura.setBicoId(bico.getId());
            abertura.setTipoLeitura(TipoLeituraBicoEnum.ABERTURA);
            abertura.setLeituraMedidor(bico.getNumeroMedidor() == null ? BigDecimal.ZERO : bico.getNumeroMedidor());
            abertura.setLidoEm(LocalDateTime.now());
            abertura.setRegistradoPor(registradoPor);
            abertura.setObservacoes("Leitura de abertura do turno");
            bicoLeituraRepository.saveAndFlush(abertura);
        }
    }
}
