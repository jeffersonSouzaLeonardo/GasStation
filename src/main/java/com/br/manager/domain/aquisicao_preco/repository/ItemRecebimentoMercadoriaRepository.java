package com.br.manager.domain.aquisicao_preco.repository;

import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoriaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRecebimentoMercadoriaRepository extends JpaRepository<RecebimentoMercadoriaItem, UUID> {
    @Query("SELECT i FROM RecebimentoMercadoriaItem i WHERE i.recebimentoMercadoria.id = :mercadoriaReciboId")
    List<RecebimentoMercadoriaItem> findByRecebimentoMercadoriaId(UUID mercadoriaReciboId);

    @Query("SELECT i FROM RecebimentoMercadoriaItem i WHERE i.produtoId = :produtoId")
    List<RecebimentoMercadoriaItem> findByProdutoId(UUID produtoId);

    @Query("SELECT i FROM RecebimentoMercadoriaItem i WHERE i.tanqueId = :tanqueId")
    List<RecebimentoMercadoriaItem> findByTanqueId(UUID tanqueId);
}
