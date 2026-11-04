package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Abastecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AbastecimentoRepository extends JpaRepository<Abastecimento, UUID> {
}
