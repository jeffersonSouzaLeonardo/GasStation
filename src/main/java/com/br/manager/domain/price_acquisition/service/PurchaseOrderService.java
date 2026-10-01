package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Supplier;
import com.br.manager.domain.operational.repository.SupplierRepository;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderItemInputDTO;
import com.br.manager.domain.price_acquisition.dto.PurchaseOrderResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PurchaseOrder;
import com.br.manager.domain.price_acquisition.mapper.PurchaseOrderMapper;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderItemRepository;
import com.br.manager.domain.price_acquisition.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PurchaseOrderResponseDTO create(PurchaseOrderInputDTO inputDTO) {
        validateCreate(inputDTO);

        try {
            PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(inputDTO);
            purchaseOrder.setId(null);
            purchaseOrder.setStatus(PurchaseOrder.Status.DRAFT);
            purchaseOrder.setOrderedAt(inputDTO.getOrderedAt() != null ? inputDTO.getOrderedAt() : LocalDateTime.now());
            purchaseOrder.setExpectedAt(inputDTO.getExpectedAt());

            Supplier supplier = supplierRepository.findById(inputDTO.getSupplierId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Fornecedor com ID %s não encontrado", inputDTO.getSupplierId())));
            if (!Boolean.TRUE.equals(supplier.getActive())) {
                throw new NotFoundBusinessException(String.format("Fornecedor com ID %s não encontrado", inputDTO.getSupplierId()));
            }
            purchaseOrder.setSupplier(supplier);
            if (inputDTO.getCreatedBy() != null) {
                User createdBy = userRepository.findById(inputDTO.getCreatedBy())
                        .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getCreatedBy())));
                if (!Boolean.TRUE.equals(createdBy.getActive())) {
                    throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getCreatedBy()));
                }
                purchaseOrder.setCreatedBy(createdBy);
            }

            PurchaseOrder saved = purchaseOrderRepository.saveAndFlush(purchaseOrder);
            // save items via service
            purchaseOrderItemService.savePurchaseOrderItems(saved.getId(), inputDTO.getItems());
            saved.setItems(purchaseOrderItemRepository.findByPurchaseOrderId(saved.getId()));
            return purchaseOrderMapper.toResponseDTO(saved);
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar pedido de compra", e);
        }
    }

    public PurchaseOrderResponseDTO findById(UUID id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", id)));
        purchaseOrder.setItems(purchaseOrderItemRepository.findByPurchaseOrderId(id));
        return purchaseOrderMapper.toResponseDTO(purchaseOrder);
    }

    public List<PurchaseOrderResponseDTO> findAll() {
        return purchaseOrderRepository.findAll().stream()
                .peek(order -> order.setItems(purchaseOrderItemRepository.findByPurchaseOrderId(order.getId())))
                .map(purchaseOrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseOrderResponseDTO update(PurchaseOrderInputDTO inputDTO) {
        try {
            UUID id = inputDTO.getId();
            PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", id)));

            if (inputDTO == null) {
                throw new BusinessException("O payload do pedido de compra é obrigatório.");
            }

            if (inputDTO.getNumber() != null && !inputDTO.getNumber().isBlank()) {
                purchaseOrder.setNumber(inputDTO.getNumber());
            }
            if (inputDTO.getOrderedAt() != null) {
                purchaseOrder.setOrderedAt(inputDTO.getOrderedAt());
            }
            if (inputDTO.getExpectedAt() != null) {
                purchaseOrder.setExpectedAt(inputDTO.getExpectedAt());
            }
            if (inputDTO.getStatus() != null && !inputDTO.getStatus().isBlank()) {
                purchaseOrder.setStatus(inputDTO.getStatus());
            }

            PurchaseOrder saved = purchaseOrderRepository.saveAndFlush(purchaseOrder);

            if (inputDTO.getItems() != null) {
                // replace items using service
                purchaseOrderItemService.replaceByPurchaseOrder(saved, inputDTO.getItems());
                saved.setItems(purchaseOrderItemRepository.findByPurchaseOrderId(saved.getId()));
            }

            return purchaseOrderMapper.toResponseDTO(saved);
        } catch (jakarta.validation.ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao atualizar pedido de compra", e);
        }
    }

    @Transactional
    public PurchaseOrderResponseDTO approve(UUID id) {
        PurchaseOrder purchaseOrder = getRequiredEntity(id);
        validateStatusTransition(purchaseOrder, PurchaseOrder.Status.DRAFT, PurchaseOrder.Status.APPROVED);
        purchaseOrder.setStatus(PurchaseOrder.Status.APPROVED);
        return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.saveAndFlush(purchaseOrder));
    }

    @Transactional
    public PurchaseOrderResponseDTO send(UUID id) {
        PurchaseOrder purchaseOrder = getRequiredEntity(id);
        validateStatusTransition(purchaseOrder, PurchaseOrder.Status.APPROVED, PurchaseOrder.Status.SENT);
        purchaseOrder.setStatus(PurchaseOrder.Status.SENT);
        return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.saveAndFlush(purchaseOrder));
    }

    @Transactional
    public PurchaseOrderResponseDTO complete(UUID id) {
        PurchaseOrder purchaseOrder = getRequiredEntity(id);
        validateStatusTransition(purchaseOrder, PurchaseOrder.Status.SENT, PurchaseOrder.Status.COMPLETED);
        purchaseOrder.setStatus(PurchaseOrder.Status.COMPLETED);
        return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.saveAndFlush(purchaseOrder));
    }

    @Transactional
    public PurchaseOrderResponseDTO cancel(UUID id) {
        PurchaseOrder purchaseOrder = getRequiredEntity(id);
        if (!List.of(PurchaseOrder.Status.DRAFT, PurchaseOrder.Status.APPROVED, PurchaseOrder.Status.SENT).contains(purchaseOrder.getStatus())) {
            throw new BusinessException("Apenas pedidos em rascunho, aprovados ou enviados podem ser cancelados.");
        }
        purchaseOrder.setStatus(PurchaseOrder.Status.CANCELED);
        return purchaseOrderMapper.toResponseDTO(purchaseOrderRepository.saveAndFlush(purchaseOrder));
    }

    @Transactional
    public void delete(UUID id) {
        PurchaseOrder purchaseOrder = getRequiredEntity(id);
        purchaseOrderRepository.delete(purchaseOrder);
    }

    private PurchaseOrder getRequiredEntity(UUID id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Pedido de compra com ID %s não encontrado", id)));
    }

    private void saveItemsForPurchaseOrder(UUID purchaseOrderId, List<PurchaseOrderItemInputDTO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        items.stream()
                .filter(Objects::nonNull)
                .forEach(itemDto -> {
                    itemDto.setPurchaseOrderId(purchaseOrderId);
                    purchaseOrderItemService.create(itemDto);
                });
    }

    private void validateCreate(PurchaseOrderInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new BusinessException("O payload do pedido de compra é obrigatório.");
        }
        if (inputDTO.getSupplierId() == null) {
            throw new BusinessException("O ID do fornecedor é obrigatório.");
        }
        if (inputDTO.getCreatedBy() == null) {
            throw new BusinessException("O usuário criador é obrigatório.");
        }
        if (inputDTO.getNumber() == null || inputDTO.getNumber().isBlank()) {
            throw new BusinessException("O número do pedido de compra é obrigatório.");
        }
        if (inputDTO.getExpectedAt() == null) {
            throw new BusinessException("A data prevista é obrigatória.");
        }
        if (inputDTO.getItems() == null || inputDTO.getItems().isEmpty()) {
            throw new BusinessException("O pedido de compra deve conter pelo menos um item.");
        }
    }

    private void validateStatusTransition(PurchaseOrder purchaseOrder, String expectedStatus, String nextStatus) {
        if (!Objects.equals(purchaseOrder.getStatus(), expectedStatus)) {
            throw new BusinessException(String.format("O pedido de compra só pode mudar de %s para %s.", expectedStatus, nextStatus));
        }
    }
}
