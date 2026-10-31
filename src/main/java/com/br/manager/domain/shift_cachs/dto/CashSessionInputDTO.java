package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.CashierRoleEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payload de abertura de uma sessão de caixa.
 */
public class CashSessionInputDTO {

    @NotNull(message = "O ID do turno é obrigatório.")
    private UUID shiftId;

    @NotNull(message = "O ID do usuário caixa é obrigatório.")
    private UUID cashierUserId;

    @NotNull(message = "O papel do operador é obrigatório.")
    private CashierRoleEnum role;

    @NotNull(message = "O fundo de troco é obrigatório.")
    @DecimalMin(value = "0.00", message = "O fundo de troco não pode ser negativo.")
    private BigDecimal openingCash;

    @Size(max = 500, message = "As observações não podem exceder 500 caracteres.")
    private String notes;

    public UUID getShiftId() {
        return shiftId;
    }

    public void setShiftId(UUID shiftId) {
        this.shiftId = shiftId;
    }

    public UUID getCashierUserId() {
        return cashierUserId;
    }

    public void setCashierUserId(UUID cashierUserId) {
        this.cashierUserId = cashierUserId;
    }

    public CashierRoleEnum getRole() {
        return role;
    }

    public void setRole(CashierRoleEnum role) {
        this.role = role;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
