package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.PaymentMethod;
import com.br.manager.domain.operational.enums.PaymentMethodKindEnum;
import com.br.manager.domain.operational.repository.PaymentMethodRepository;
import com.br.manager.domain.sale_receipt.dto.SalePaymentInputDTO;
import com.br.manager.domain.sale_receipt.dto.SalePaymentResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SalePayment;
import com.br.manager.domain.sale_receipt.enums.PaymentStatus;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.mapper.SalePaymentMapper;
import com.br.manager.domain.sale_receipt.repository.SalePaymentRepository;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class SalePaymentService {

    private static final Set<PaymentStatus> SETTLED_STATUSES = Set.of(PaymentStatus.AUTHORIZED, PaymentStatus.RECEIVED);

    @Autowired
    private SalePaymentRepository salePaymentRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private SalePaymentMapper salePaymentMapper;

    public SalePaymentResponseDTO create(SalePaymentInputDTO inputDTO) {
        try {
            Sale sale = saleRepository.findDetailedById(inputDTO.getSaleId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", inputDTO.getSaleId())));
            if (sale.isFinanciallyLocked()) {
                throw new BusinessException("Vendas pagas ou com crédito pendente não podem ser alteradas.");
            }

            PaymentMethod paymentMethod = paymentMethodRepository.findById(inputDTO.getPaymentMethodId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Forma de pagamento com ID %s não encontrada", inputDTO.getPaymentMethodId())));
            if (!Boolean.TRUE.equals(paymentMethod.getActive())) {
                throw new NotFoundBusinessException(String.format("Forma de pagamento com ID %s não encontrada", inputDTO.getPaymentMethodId()));
            }
            if (inputDTO.getAmount() == null || inputDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("O valor do pagamento deve ser maior que zero.");
            }
            if (Boolean.TRUE.equals(paymentMethod.getRequiresReference())
                    && (inputDTO.getTransactionReference() == null || inputDTO.getTransactionReference().isBlank())) {
                throw new BusinessException("A referência da transação é obrigatória para a forma de pagamento selecionada.");
            }
            if (isCreditPayment(paymentMethod) && (sale.getCustomerId() == null || sale.getVehicleId() == null)) {
                throw new BusinessException("Cliente e veículo são obrigatórios para pagamentos a crédito ou convênio.");
            }

            SalePayment payment = salePaymentMapper.toEntity(inputDTO);
            payment.setId(null);
            payment.setSale(sale);
            SalePayment savedPayment = salePaymentRepository.saveAndFlush(payment);

            BigDecimal settledAmount = salePaymentRepository.findBySaleId(sale.getId()).stream()
                    .filter(item -> SETTLED_STATUSES.contains(item.getStatus()))
                    .map(SalePayment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (isCreditPayment(paymentMethod)) {
                sale.setStatus(SaleStatus.PENDING_CREDIT);
            } else {
                sale.setStatus(SaleStatus.OPEN);
            }
            saleRepository.saveAndFlush(sale);
            return salePaymentMapper.toResponse(savedPayment);
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Erro ao salvar pagamento da venda", e);
        }
    }

    public List<SalePaymentResponseDTO> findBySale(UUID saleId) {
        return salePaymentMapper.toResponseList(salePaymentRepository.findBySaleId(saleId));
    }

    public SalePaymentResponseDTO find(UUID id) {
        SalePayment payment = salePaymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Pagamento da venda com ID %s não encontrado", id)));
        return salePaymentMapper.toResponse(payment);
    }

    private boolean isCreditPayment(PaymentMethod paymentMethod) {
        return paymentMethod.getKind() == PaymentMethodKindEnum.CONVENIO
                || paymentMethod.getSettlementDays() != null && paymentMethod.getSettlementDays() > 0;
    }
}
