package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Product;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.repository.NozzleRepository;
import com.br.manager.domain.operational.repository.ProductRepository;
import com.br.manager.domain.operational.repository.TankRepository;
import com.br.manager.domain.sale_receipt.dto.SaleItemInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleItemResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import com.br.manager.domain.sale_receipt.mapper.SaleItemMapper;
import com.br.manager.domain.sale_receipt.repository.SaleItemRepository;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class SaleItemService {

    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleItemMapper saleItemMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private NozzleRepository nozzleRepository;
    @Autowired
    private TankRepository tankRepository;

    public List<SaleItemResponseDTO> saveSaleItems(UUID saleId, List<SaleItemInputDTO> inputDTOs) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", saleId)));
        validateSaleMutable(sale);
        validateLineNumbers(inputDTOs);

        List<SaleItem> items = inputDTOs.stream().map(dto -> buildSaleItem(sale, dto)).toList();
        return saleItemMapper.toResponseList(saleItemRepository.saveAllAndFlush(items));
    }

    public List<SaleItemResponseDTO> findBySale(UUID saleId) {
        return saleItemMapper.toResponseList(saleItemRepository.findBySaleIdOrderByLineNumber(saleId));
    }

    private SaleItem buildSaleItem(Sale sale, SaleItemInputDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Produto com ID %s não encontrado", dto.getProductId())));
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new NotFoundBusinessException(String.format("Produto com ID %s não encontrado", dto.getProductId()));
        }

        SaleItem item = saleItemMapper.toEntity(dto);
        item.setSale(sale);
        BigDecimal discount = dto.getDiscountAmount() == null ? BigDecimal.ZERO : dto.getDiscountAmount();
        item.setDiscountAmount(discount);

        if (dto.getNozzleId() != null) {
            Nozzle nozzle = nozzleRepository.findById(dto.getNozzleId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Bomba com ID %s não encontrada", dto.getNozzleId())));
            Tank tank = tankRepository.findById(nozzle.getTankId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Tanque com ID %s não encontrado", nozzle.getTankId())));

            if (dto.getMeterStart() == null || dto.getMeterEnd() == null) {
                throw new BusinessException("Itens de combustível exigem leitura inicial e final do medidor.");
            }
            if (dto.getMeterEnd().compareTo(dto.getMeterStart()) <= 0) {
                throw new BusinessException("A leitura final deve ser maior que a leitura inicial.");
            }
            item.setTankId(tank.getId());
            item.setQuantity(dto.getMeterEnd().subtract(dto.getMeterStart()));
        } else {
            if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("A quantidade do item deve ser maior que zero.");
            }
            item.setQuantity(dto.getQuantity());
        }

        if (dto.getUnitPrice() == null || dto.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O preço unitário deve ser maior que zero.");
        }
        return item;
    }

    private void validateSaleMutable(Sale sale) {
        if (sale.isFinanciallyLocked()) {
            throw new BusinessException("Vendas pagas ou com crédito pendente não podem ser alteradas.");
        }
    }

    private void validateLineNumbers(List<SaleItemInputDTO> inputDTOs) {
        Set<Integer> lines = new HashSet<>();
        for (SaleItemInputDTO dto : inputDTOs) {
            if (!lines.add(dto.getLineNumber())) {
                throw new BusinessException("Os números de linha dos itens da venda devem ser únicos.");
            }
        }
    }
}
