package com.br.manager.domain.price_acquisition.repository;

import com.br.manager.domain.price_acquisition.entity.GoodsReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoodsReceiptItemRepository extends JpaRepository<GoodsReceiptItem, UUID> {
    List<GoodsReceiptItem> findByGoodsReceiptId(UUID goodsReceiptId);
    List<GoodsReceiptItem> findByProductId(UUID productId);
    List<GoodsReceiptItem> findByTankId(UUID tankId);
}
