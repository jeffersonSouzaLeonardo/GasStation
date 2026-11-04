package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Produto;
import com.br.manager.domain.operacional.enums.ProdutoTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    @Query("SELECT p FROM Produto p WHERE p.ativo = true")
    List<Produto> findAllByAtivoTrue();

    @Query("SELECT p FROM Produto p WHERE p.id = :id AND p.ativo = true")
    Produto findByIdAndAtivoTrue(UUID id);

    @Query("SELECT p FROM Produto p WHERE p.produtoType = :type AND p.ativo = true")
    List<Produto> findByProdutoTypeAndAtivoTrue(ProdutoTypeEnum type);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND p.ativo = true")
    List<Produto> findByNameContainingIgnoreCaseAndAtivoTrue(String nome);

    // Busca registros criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (atualizadoEm nulo).
    @Query("SELECT p FROM Produto p WHERE (( p.atualizadoEm > :since OR p.excluidoEm > :since ) OR ( p.atualizadoEm IS NULL AND p.criadoEm > :since ))")
    List<Produto> findAllChangedSince(@Param("since") LocalDateTime since);

}
