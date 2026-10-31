package com.br.manager.domain.shift_cachs.dto;

import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.CashierRoleEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CashSessionResponseDTO {

    private UUID id;
    private UUID shiftId;
    private UUID cashierUserId;
    private CashierRoleEnum role;
    private BigDecimal openingCash;
    private CashSessionStatusEnum status;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal totalCash;
    private BigDecimal totalCard;
    private BigDecimal totalPix;
    private BigDecimal totalWithdrawal;
    private String notes;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public CashSessionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CashSessionStatusEnum status) {
        this.status = status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(LocalDateTime openedAt) {
        this.openedAt = openedAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
