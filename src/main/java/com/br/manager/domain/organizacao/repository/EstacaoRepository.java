package com.br.manager.domain.organizacao.repository;

import com.br.manager.domain.organizacao.entity.Estacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstacaoRepository extends JpaRepository<Estacao, UUID> {
    @Query("SELECT e FROM Estacao e WHERE e.ativo = true")
    List<Estacao> findAllByAtivoTrue();

    @Query("SELECT e FROM Estacao e WHERE e.id = :id AND e.ativo = true")
    Estacao findByIdAndAtivoTrue(UUID id);

    @Query("SELECT e FROM Estacao e WHERE LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND e.ativo = true")
    List<Estacao> findByNameContainingIgnoreCaseAndAtivoTrue(String nome);

    @Query("SELECT e FROM Estacao e WHERE e.empresaId = :empresaId AND e.ativo = true")
    List<Estacao> findByEmpresaIdAndAtivoTrue(UUID empresaId);
}
