package com.br.manager.domain.turno_caixa.repository;

import com.br.manager.domain.turno_caixa.entity.AlocacaoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TurnoAlocacaoRepository extends JpaRepository<AlocacaoTurno, UUID> {

    List<AlocacaoTurno> findByTurnoId(UUID turnoId);

    Optional<AlocacaoTurno> findByTurnoIdAndUsuarioId(UUID turnoId, UUID usuarioId);
}
