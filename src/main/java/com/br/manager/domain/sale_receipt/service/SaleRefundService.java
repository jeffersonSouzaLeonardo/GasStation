package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.sale_receipt.dto.SaleRefundInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleRefundResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.entity.SaleRefund;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.mapper.SaleRefundMapper;
import com.br.manager.domain.sale_receipt.repository.SaleRefundRepository;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import com.br.manager.domain.shift_cachs.entity.CashMovement;
import com.br.manager.domain.shift_cachs.enums.CashMovementTypeEnum;
import com.br.manager.domain.shift_cachs.repository.CashMovementRepository;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SaleRefundService {

    @Autowired
    private SaleRefundRepository saleRefundRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private CashMovementRepository cashMovementRepository;
    @Autowired
    private SaleRefundMapper saleRefundMapper;

    public SaleRefundResponseDTO create(SaleRefundInputDTO inputDTO) {
        try {
            Sale sale = saleRepository.findDetailedById(inputDTO.getSaleId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", inputDTO.getSaleId())));
            if (sale.getStatus() != SaleStatus.PAID && sale.getStatus() != SaleStatus.PENDING_CREDIT) {
                throw new BusinessException("Apenas vendas pagas ou com crédito pendente podem ser estornadas.");
            }

            User requestedBy = userRepository.findById(inputDTO.getRequestedBy())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getRequestedBy())));
            User approvedBy = userRepository.findById(inputDTO.getApprovedBy())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getApprovedBy())));
            if (!Boolean.TRUE.equals(requestedBy.getActive()) || !Boolean.TRUE.equals(approvedBy.getActive())) {
                throw new BusinessException("Os usuários solicitante e aprovador precisam estar ativos.");
            }
            if (requestedBy.getId().equals(approvedBy.getId())) {
                throw new BusinessException("A aprovação do estorno deve ser feita por um usuário diferente.");
            }

            SaleRefund refund = saleRefundMapper.toEntity(inputDTO);
            refund.setId(null);
            refund.setSale(sale);
            refund.setRefundedAt(inputDTO.getRefundedAt() != null ? inputDTO.getRefundedAt() : Instant.now());
            SaleRefund savedRefund = saleRefundRepository.saveAndFlush(refund);

            for (SalePayment payment : sale.getPayments()) {
                if (payment.getStatus() == PaymentStatus.AUTHORIZED || payment.getStatus() == PaymentStatus.RECEIVED) {
                    payment.setStatus(PaymentStatus.REFUNDED);
                }
            }

            sale.setStatus(sale.getPayments().isEmpty() ? SaleStatus.CANCELLED : SaleStatus.REFUNDED);
            saleRepository.saveAndFlush(sale);

            shiftRepository.findById(sale.getShiftId()).ifPresent(shift -> {
                CashMovement movement = new CashMovement();
                movement.setShift(shift);
                movement.setMovementType(CashMovementTypeEnum.REFUND);
                movement.setOccurredAt(LocalDateTime.now());
                movement.setReference(sale.getId().toString());
                movement.setReason("Reversão de estorno de venda");
                movement.setCreatedBy(requestedBy.getId());
                movement.setApprovedBy(approvedBy.getId());
                cashMovementRepository.saveAndFlush(movement);
            });

            return saleRefundMapper.toResponse(savedRefund);
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao estornar venda", e);
        }
    }

    public List<SaleRefundResponseDTO> findBySale(UUID saleId) {
        return saleRefundMapper.toResponseList(saleRefundRepository.findBySaleId(saleId));
    }
}
