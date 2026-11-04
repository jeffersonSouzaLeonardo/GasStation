package com.br.manager.domain.venda_recibo.repository;

import com.br.manager.domain.venda_recibo.entity.Venda;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VendaRepository extends JpaRepository<Venda, UUID> {

    boolean existsByEstacaoIdAndNumeroVenda(UUID estacaoId, String numeroVenda);

    @EntityGraph(attributePaths = {"itens", "pagamentos", "reembolsos"})
    Optional<Venda> findDetailedById(UUID id);

    List<Venda> findByEstacaoId(UUID estacaoId);
}
