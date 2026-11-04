package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
    @Query("SELECT c FROM Combustivel c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND c.excluido IS NULL")
    List<Combustivel> findByNameContainingIgnoreCaseAndExcluidoIsNull(String nome);

    @Query("SELECT c FROM Combustivel c WHERE c.id = :id AND c.excluido IS NULL")
    Combustivel findByIdAndExcluidoIsNull(Long id);

    @Query("SELECT c FROM Combustivel c WHERE c.excluido IS NULL")
    List<Combustivel> findAllByExcluidoIsNull();
}
