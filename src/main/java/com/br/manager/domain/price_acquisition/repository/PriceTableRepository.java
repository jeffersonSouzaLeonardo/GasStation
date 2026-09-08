package com.br.manager.domain.price_acquisition.repository;

import com.br.manager.domain.price_acquisition.entity.PriceTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceTableRepository extends JpaRepository<PriceTable, UUID> {
    List<PriceTable> findByNameContainingIgnoreCase(String name);
}
