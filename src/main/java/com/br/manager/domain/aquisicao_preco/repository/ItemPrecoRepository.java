package com.br.manager.domain.aquisicao_preco.repository;

import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPrecoRepository extends JpaRepository<ItemPreco, UUID> {
    @Query("select i from ItemPreco i where i.ativo = true")
    List<ItemPreco> findAllByAtivoTrue();

    @Query("select i from ItemPreco i where i.id = :id and i.ativo = true")
    ItemPreco findByIdAndAtivoTrue(UUID id);

    @Query("select i from ItemPreco i where i.tabelaPreco.id = :precoTabelaId and i.ativo = true")
    List<ItemPreco> findByTabelaPrecoIdAndAtivoTrue(UUID precoTabelaId);

    @Modifying(clearAutomatically = true)
    @Query("delete from ItemPreco i where i.tabelaPreco = :tabelaPreco")
    void deleteByTabelaPreco(TabelaPreco tabelaPreco);
}
