package com.br.manager.domain.venda_recibo.repository;

import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendaPagamentoRepository extends JpaRepository<PagamentoVenda, UUID> {
    List<PagamentoVenda> findByVendaId(UUID vendaId);
}
