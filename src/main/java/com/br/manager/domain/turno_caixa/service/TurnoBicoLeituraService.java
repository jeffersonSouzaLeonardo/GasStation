package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraInputDTO;
import com.br.manager.domain.turno_caixa.dto.BicoLeituraResponseDTO;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.mapper.BicoLeituraMapper;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TurnoBicoLeituraService {

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    @Autowired
    private BicoLeituraRepository bicoLeituraRepository;

    @Autowired
    private BicoLeituraMapper bicoLeituraMapper;

    @Transactional
    public BicoLeituraResponseDTO registerBicoLeitura(UUID turnoId, BicoLeituraInputDTO request) {
        if (request == null) {
            throw new ExcecaoNegocio("O payload da leitura da bomba é obrigatório.");
        }

        Turno turno = validacaoSuporte.getObrigatorioTurno(turnoId);
        validacaoSuporte.ensureTurnoIsAberto(turno);
        validacaoSuporte.validateBicoBelongsToTurnoEstacao(turno.getEstacaoId(), request.getBicoId());

        if (request.getTipoLeitura() == TipoLeituraBicoEnum.ABERTURA &&
                bicoLeituraRepository.existsByTurnoIdAndBicoIdAndTipoLeitura(turnoId, request.getBicoId(), TipoLeituraBicoEnum.ABERTURA)) {
            throw new ExcecaoNegocio(String.format("Já existe uma leitura de abertura para a bomba %s neste turno.", request.getBicoId()));
        }

        if (request.getTipoLeitura() == TipoLeituraBicoEnum.FECHAMENTO &&
                bicoLeituraRepository.existsByTurnoIdAndBicoIdAndTipoLeitura(turnoId, request.getBicoId(), TipoLeituraBicoEnum.FECHAMENTO)) {
            throw new ExcecaoNegocio(String.format("Já existe uma leitura de fechamento para a bomba %s neste turno.", request.getBicoId()));
        }

        LeituraBico leitura = bicoLeituraMapper.toEntity(request, turno);
        LeituraBico savedLeitura = bicoLeituraRepository.saveAndFlush(leitura);
        return bicoLeituraMapper.toResponse(savedLeitura);
    }
}
