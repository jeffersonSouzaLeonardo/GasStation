package com.br.manager.domain.venda_recibo.repository;

import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendaReembolsoRepository extends JpaRepository<ReembolsoVenda, UUID> {
    List<ReembolsoVenda> findByVendaId(UUID vendaId);
}
