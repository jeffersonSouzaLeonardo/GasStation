package com.br.manager.domain.turno_caixa.repository;

import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimentoCaixaRepository extends JpaRepository<MovimentoCaixa, UUID> {

    List<MovimentoCaixa> findByTurnoIdOrderByOcorridoEmAsc(UUID turnoId);
}
