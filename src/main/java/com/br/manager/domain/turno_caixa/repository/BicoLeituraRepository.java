package com.br.manager.domain.turno_caixa.repository;

import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BicoLeituraRepository extends JpaRepository<LeituraBico, UUID> {

    List<LeituraBico> findByTurnoId(UUID turnoId);

    List<LeituraBico> findByTurnoIdAndBicoId(UUID turnoId, UUID bicoId);

    boolean existsByTurnoIdAndBicoIdAndTipoLeitura(UUID turnoId, UUID bicoId, TipoLeituraBicoEnum tipoLeitura);

    List<LeituraBico> findByTurnoIdAndTipoLeitura(UUID turnoId, TipoLeituraBicoEnum tipoLeitura);
}
