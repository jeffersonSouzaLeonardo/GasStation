package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Bico;
import com.br.manager.domain.operacional.entity.Bomba;
import com.br.manager.domain.operacional.repository.BicoRepository;
import com.br.manager.domain.operacional.repository.BombaRepository;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SuporteValidacaoTurno {

    private final TurnoRepository turnoRepository;
    private final EstacaoRepository estacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BombaRepository bombaRepository;
    private final BicoRepository bicoRepository;

    public SuporteValidacaoTurno(
            TurnoRepository turnoRepository,
            EstacaoRepository estacaoRepository,
            UsuarioRepository usuarioRepository,
            BombaRepository bombaRepository,
            BicoRepository bicoRepository) {
        this.turnoRepository = turnoRepository;
        this.estacaoRepository = estacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.bombaRepository = bombaRepository;
        this.bicoRepository = bicoRepository;
    }

    public Turno getObrigatorioTurno(UUID turnoId) {
        return turnoRepository.findById(turnoId)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Turno com ID %s não encontrado", turnoId)));
    }

    public void ensureTurnoIsAberto(Turno turno) {
        if (turno.getStatus() == TurnoSituacaoEnum.FECHADO) {
            throw new ExcecaoNegocio(String.format("O turno %s está fechado e não pode ser alterado.", turno.getId()));
        }
    }

    public Estacao getObrigatorioEstacao(UUID estacaoId) {
        Estacao estacao = estacaoRepository.findById(estacaoId)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", estacaoId)));
        if (!Boolean.TRUE.equals(estacao.getAtivo())) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", estacaoId));
        }
        return estacao;
    }

    public Usuario getObrigatorioUsuario(UUID usuarioId, String fieldName) {
        String field = fieldName == null || fieldName.isBlank() ? "usuário" : fieldName;
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado no campo %s", usuarioId, field)));
        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado no campo %s", usuarioId, field));
        }
        return usuario;
    }

    public void validateBicoBelongsToTurnoEstacao(UUID estacaoId, UUID bicoId) {
        List<Bico> estacaoBicos = findBicosByEstacaoId(estacaoId);
        boolean exists = estacaoBicos.stream().anyMatch(item -> item.getId().equals(bicoId));
        if (!exists) {
            throw new ExcecaoNegocio(String.format("A bomba %s não pertence ao posto %s.", bicoId, estacaoId));
        }
    }

    public List<Bico> findBicosByEstacaoId(UUID estacaoId) {
        List<Bomba> bombas = bombaRepository.findAllByAtivoTrue();
        if (bombas.isEmpty()) {
            return List.of();
        }

        Set<UUID> seenBicos = new HashSet<>();
        List<Bico> estacaoBicos = new ArrayList<>();

        for (Bomba bomba : bombas) {
            List<Bico> bicos = bicoRepository.findByBombaIdAndAtivoTrue(bomba.getId());
            for (Bico bico : bicos) {
                if (seenBicos.add(bico.getId())) {
                    estacaoBicos.add(bico);
                }
            }
        }

        return estacaoBicos;
    }
}
