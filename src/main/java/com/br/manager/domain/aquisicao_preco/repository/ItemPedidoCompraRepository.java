package com.br.manager.domain.aquisicao_preco.repository;

import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompraItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPedidoCompraRepository extends JpaRepository<PedidoCompraItem, UUID> {

    @Query("SELECT i FROM PedidoCompraItem i WHERE i.pedidoCompra.id = :compraOrderId")
    List<PedidoCompraItem> findByPedidoCompraId(UUID compraOrderId);

    void deleteByPedidoCompra(PedidoCompra pedidoCompra);
}
