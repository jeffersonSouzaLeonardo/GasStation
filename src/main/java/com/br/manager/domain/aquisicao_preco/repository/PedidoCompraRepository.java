package com.br.manager.domain.aquisicao_preco.repository;

import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, UUID> {

    @Query("select p from PedidoCompra p where p.numero = :numero")
    Optional<PedidoCompra> findByNumero(String numero);

    @Query("select p from PedidoCompra p where p.fornecedor.id = :fornecedorId")
    List<PedidoCompra> findByFornecedorId(UUID fornecedorId);
}
