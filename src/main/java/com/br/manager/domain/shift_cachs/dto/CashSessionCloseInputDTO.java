package com.br.manager.domain.shift_cachs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payload de fechamento de uma sessão de caixa, com os totais consolidados.
 */
public class CashSessionCloseInputDTO {

    private UUID id;

    @DecimalMin(value = "0.00", message = "O total em dinheiro não pode ser negativo.")
    private BigDecimal totalCash;

    @DecimalMin(value = "0.00", message = "O total em cartão não pode ser negativo.")
    private BigDecimal totalCard;

    @DecimalMin(value = "0.00", message = "O total em Pix não pode ser negativo.")
    private BigDecimal totalPix;

    @DecimalMin(value = "0.00", message = "O total de sangrias não pode ser negativo.")
    private BigDecimal totalWithdrawal;

    @Size(max = 500, message = "As observações não podem exceder 500 caracteres.")
    private String notes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(BigDecimal totalCash) {
        this.totalCash = totalCash;
    }

    public BigDecimal getTotalCard() {
        return totalCard;
    }

    public void setTotalCard(BigDecimal totalCard) {
        this.totalCard = totalCard;
    }

    public BigDecimal getTotalPix() {
        return totalPix;
    }

    public void setTotalPix(BigDecimal totalPix) {
        this.totalPix = totalPix;
    }

    public BigDecimal getTotalWithdrawal() {
        return totalWithdrawal;
    }

    public void setTotalWithdrawal(BigDecimal totalWithdrawal) {
        this.totalWithdrawal = totalWithdrawal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
