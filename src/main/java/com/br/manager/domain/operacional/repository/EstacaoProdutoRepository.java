package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.EstacaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstacaoProdutoRepository extends JpaRepository<EstacaoProduto, UUID> {
    @Query("SELECT ep FROM EstacaoProduto ep WHERE ep.ativo = true")
    List<EstacaoProduto> findAllByAtivoTrue();

    @Query("SELECT ep FROM EstacaoProduto ep WHERE ep.id = :id AND ep.ativo = true")
    EstacaoProduto findByIdAndAtivoTrue(UUID id);

    @Query("SELECT ep FROM EstacaoProduto ep WHERE ep.produtoId = :produtoId AND ep.ativo = true")
    List<EstacaoProduto> findByProdutoIdAndAtivoTrue(UUID produtoId);
}
