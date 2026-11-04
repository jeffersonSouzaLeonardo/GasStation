package com.br.manager.domain.aquisicao_preco.repository;

import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TabelaPrecoRepository extends JpaRepository<TabelaPreco, UUID> {
    @Query("SELECT tp FROM TabelaPreco tp WHERE LOWER(tp.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<TabelaPreco> findByNameContainingIgnoreCase(String nome);
}
