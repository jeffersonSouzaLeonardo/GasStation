package com.br.manager.domain.aquisicao_preco.repository;

import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecebimentoMercadoriaRepository extends JpaRepository<RecebimentoMercadoria, UUID> {
    @Query("select r from RecebimentoMercadoria r where r.fornecedor.id = :fornecedorId")
    List<RecebimentoMercadoria> findByFornecedorId(UUID fornecedorId);

    @Query("select r from RecebimentoMercadoria r where r.pedidoCompra.id = :compraOrderId")
    List<RecebimentoMercadoria> findByPedidoCompraId(UUID compraOrderId);
}
