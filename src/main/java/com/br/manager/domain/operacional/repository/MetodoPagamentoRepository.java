package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.MetodoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetodoPagamentoRepository extends JpaRepository<MetodoPagamento, UUID> {
    @Query("SELECT m FROM MetodoPagamento m WHERE m.ativo = true")
    List<MetodoPagamento> findAllByAtivoTrue();

    @Query("SELECT m FROM MetodoPagamento m WHERE m.id = :id AND m.ativo = true")
    MetodoPagamento findByIdAndAtivoTrue(UUID id);

    @Query("SELECT m FROM MetodoPagamento m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND m.ativo = true")
    List<MetodoPagamento> findByNameContainingIgnoreCaseAndAtivoTrue(String nome);

}
