package com.br.manager.domain.turno_caixa.repository;

import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, UUID> {

    Optional<Turno> findByIdAndStatus(UUID id, TurnoSituacaoEnum status);

    boolean existsByEstacaoIdAndCaixeiroUsuarioIdAndStatus(UUID estacaoId, UUID caixeiroUsuarioId, TurnoSituacaoEnum status);

    boolean existsByEstacaoIdAndStatus(UUID estacaoId, TurnoSituacaoEnum status);
}
