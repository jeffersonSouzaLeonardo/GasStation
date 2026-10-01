package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.repository.SupplierRepository;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptInputDTO;
import com.br.manager.domain.price_acquisition.dto.GoodsReceiptResponseDTO;
import com.br.manager.domain.price_acquisition.entity.GoodsReceipt;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.mapper.GoodsReceiptMapper;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptItemRepository;
import com.br.manager.domain.price_acquisition.repository.GoodsReceiptRepository;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GoodsReceiptService {

    @Autowired
    private GoodsReceiptRepository goodsReceiptRepository;

    @Autowired
    private GoodsReceiptMapper goodsReceiptMapper;

    @Autowired
    private GoodsReceiptItemRepository goodsReceiptItemRepository;

    @Autowired
    private GoodsReceiptItemService goodsReceiptItemService;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private UserRepository userRepository;

    public GoodsReceiptResponseDTO create(GoodsReceiptInputDTO inputDTO) {
        validateCreate(inputDTO);

        try {
            GoodsReceipt goodsReceipt = goodsReceiptMapper.goodsReceiptInputDTOToGoodsReceipt(inputDTO);
            goodsReceipt.setId(null);
            goodsReceipt.setStatus(inputDTO.getStatus() != null && !inputDTO.getStatus().isBlank()
                    ? inputDTO.getStatus()
                    : GoodsReceipt.Status.DRAFT);
            goodsReceipt.setReceivedAt(inputDTO.getReceivedAt() != null ? inputDTO.getReceivedAt() : LocalDateTime.now());

            Supplier supplier = supplierRepository.findById(inputDTO.getSupplierId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Fornecedor com ID %s não encontrado", inputDTO.getSupplierId())));
            if (!Boolean.TRUE.equals(supplier.getActive())) {
                throw new NotFoundBusinessException(String.format("Fornecedor com ID %s não encontrado", inputDTO.getSupplierId()));
            }
            goodsReceipt.setSupplier(supplier);

            if (inputDTO.getPurchaseOrderId() != null) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(inputDTO.getPurchaseOrderId())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getPurchaseOrderId())));
                goodsReceipt.setPurchaseOrder(purchaseOrder);
            }

            User receivedBy = userRepository.findById(inputDTO.getReceivedBy())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getReceivedBy())));
            if (!Boolean.TRUE.equals(receivedBy.getActive())) {
                throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getReceivedBy()));
            }
            goodsReceipt.setReceivedBy(receivedBy);

            GoodsReceipt savedGoodsReceipt = goodsReceiptRepository.saveAndFlush(goodsReceipt);
            goodsReceiptItemService.saveGoodsReceiptItems(savedGoodsReceipt.getId(), inputDTO.getItems());
            savedGoodsReceipt.setItems(goodsReceiptItemRepository.findByGoodsReceiptId(savedGoodsReceipt.getId()));

            return goodsReceiptMapper.goodsReceiptToGoodsReceiptResponseDTO(savedGoodsReceipt);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String invoiceNumber = inputDTO != null && StringUtils.hasText(inputDTO.getInvoiceNumber()) ? inputDTO.getInvoiceNumber() : "";
            throw new BusinessException("Erro ao salvar recebimento de mercadorias " + invoiceNumber, e);
        }
    }

    public GoodsReceiptResponseDTO update(GoodsReceiptInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new BusinessException("O payload do recebimento de mercadorias é obrigatório.");
        }
        if (inputDTO.getId() == null) {
            throw new BusinessException("O ID do recebimento de mercadorias é obrigatório para atualização.");
        }

        try {
            GoodsReceipt goodsReceipt = goodsReceiptRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", inputDTO.getId())));

            if (inputDTO.getSupplierId() != null) {
                Supplier supplier = supplierRepository.findById(inputDTO.getSupplierId())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Fornecedor com ID %s não encontrado", inputDTO.getSupplierId())));
                if (!Boolean.TRUE.equals(supplier.getActive())) {
                    throw new NotFoundBusinessException(String.format("Fornecedor com ID %s não encontrado", inputDTO.getSupplierId()));
                }
                goodsReceipt.setSupplier(supplier);
            }

            if (inputDTO.getPurchaseOrderId() != null) {
                PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(inputDTO.getPurchaseOrderId())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getPurchaseOrderId())));
                goodsReceipt.setPurchaseOrder(purchaseOrder);
            }

            if (inputDTO.getReceivedBy() != null) {
                User receivedBy = userRepository.findById(inputDTO.getReceivedBy())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getReceivedBy())));
                if (!Boolean.TRUE.equals(receivedBy.getActive())) {
                    throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getReceivedBy()));
                }
                goodsReceipt.setReceivedBy(receivedBy);
            }

            goodsReceiptMapper.updateGoodsReceiptFromDto(inputDTO, goodsReceipt);
            if (inputDTO.getStatus() != null && !inputDTO.getStatus().isBlank()) {
                goodsReceipt.setStatus(inputDTO.getStatus());
            }
            if (inputDTO.getReceivedAt() != null) {
                goodsReceipt.setReceivedAt(inputDTO.getReceivedAt());
            }

            GoodsReceipt savedGoodsReceipt = goodsReceiptRepository.saveAndFlush(goodsReceipt);

            if (inputDTO.getItems() != null) {
                goodsReceiptItemService.replaceByGoodsReceipt(savedGoodsReceipt.getId(), inputDTO.getItems());
                savedGoodsReceipt.setItems(goodsReceiptItemRepository.findByGoodsReceiptId(savedGoodsReceipt.getId()));
            }

            return goodsReceiptMapper.goodsReceiptToGoodsReceiptResponseDTO(savedGoodsReceipt);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String invoiceNumber = inputDTO != null && StringUtils.hasText(inputDTO.getInvoiceNumber()) ? inputDTO.getInvoiceNumber() : "";
            throw new BusinessException("Erro ao atualizar recebimento de mercadorias " + invoiceNumber, e);
        }
    }

    public List<GoodsReceiptResponseDTO> findAll() {
        return goodsReceiptMapper.listGoodsReceiptToListGoodsReceiptResponseDTO(goodsReceiptRepository.findAll());
    }

    public List<GoodsReceiptResponseDTO> findBySupplier(UUID supplierId) {
        return goodsReceiptMapper.listGoodsReceiptToListGoodsReceiptResponseDTO(goodsReceiptRepository.findBySupplierId(supplierId));
    }

    public List<GoodsReceiptResponseDTO> findByPurchaseOrder(UUID purchaseOrderId) {
        return goodsReceiptMapper.listGoodsReceiptToListGoodsReceiptResponseDTO(goodsReceiptRepository.findByPurchaseOrderId(purchaseOrderId));
    }

    public GoodsReceiptResponseDTO find(UUID id) {
        GoodsReceipt entity = goodsReceiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", id)));
        return goodsReceiptMapper.goodsReceiptToGoodsReceiptResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            GoodsReceipt goodsReceipt = getRequiredEntity(id);
            goodsReceiptRepository.delete(goodsReceipt);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao excluir recebimento de mercadorias", e);
        }
    }

    private GoodsReceipt getRequiredEntity(UUID id) {
        return goodsReceiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Recebimento de mercadorias com ID %s não encontrado", id)));
    }

    private void validateCreate(GoodsReceiptInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new BusinessException("O payload do recebimento de mercadorias é obrigatório.");
        }
        if (inputDTO.getSupplierId() == null) {
            throw new BusinessException("O ID do fornecedor é obrigatório.");
        }
        if (inputDTO.getReceivedBy() == null) {
            throw new BusinessException("O usuário responsável pelo recebimento é obrigatório.");
        }
        if (inputDTO.getInvoiceNumber() == null || inputDTO.getInvoiceNumber().isBlank()) {
            throw new BusinessException("O número da nota fiscal é obrigatório.");
        }
        if (inputDTO.getInvoiceKey() == null || inputDTO.getInvoiceKey().isBlank()) {
            throw new BusinessException("A chave da nota fiscal é obrigatória.");
        }
        if (inputDTO.getItems() == null || inputDTO.getItems().isEmpty()) {
            throw new BusinessException("O recebimento de mercadorias deve conter pelo menos um item.");
        }
    }
}
