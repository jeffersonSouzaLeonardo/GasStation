package com.br.manager.domain.turno_caixa.repository;

import com.br.manager.domain.turno_caixa.entity.SessaoCaixa;
import com.br.manager.domain.turno_caixa.enums.SessaoCaixaSituacaoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoCaixaRepository extends JpaRepository<SessaoCaixa, UUID> {

    Optional<SessaoCaixa> findByIdAndStatus(UUID id, SessaoCaixaSituacaoEnum status);

    List<SessaoCaixa> findByTurno_IdOrderByOpenedAt(UUID turnoId);

    List<SessaoCaixa> findByCaixeiroUsuarioIdAndStatus(UUID caixeiroUsuarioId, SessaoCaixaSituacaoEnum status);

    boolean existsByCaixeiroUsuarioIdAndStatus(UUID caixeiroUsuarioId, SessaoCaixaSituacaoEnum status);
}
