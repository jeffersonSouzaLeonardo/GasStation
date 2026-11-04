package com.br.manager.domain.turno_caixa.repository;

import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FechamentoPagamentoRepository extends JpaRepository<FechamentoPagamento, UUID> {

    List<FechamentoPagamento> findByTurnoIdOrderByPagamentoMethodId(UUID turnoId);
}
