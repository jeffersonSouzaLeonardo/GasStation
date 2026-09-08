package com.br.manager.domain.price_acquisition.repository;

import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceItemRepository extends JpaRepository<PriceItem, UUID> {
    List<PriceItem> findAllByActiveTrue();

    PriceItem findByIdAndActiveTrue(UUID id);

    List<PriceItem> findByPriceTableIdAndActiveTrue(UUID priceTableId);
}
