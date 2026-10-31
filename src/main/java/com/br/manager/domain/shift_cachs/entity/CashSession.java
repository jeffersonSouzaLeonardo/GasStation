package com.br.manager.domain.shift_cachs.entity;

import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import com.br.manager.domain.shift_cachs.enums.CashierRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Sessão de caixa do operador (caixa do PDV central ou frentista), atrelada a um turno.
 * O identificador é gerado pelo PDV e replicado aqui (sem geração automática).
 */
@Entity
@Table(name = "cash_sessions")
public class CashSession {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @NotNull(message = "O ID do usuário caixa é obrigatório.")
    @Column(name = "cashier_user_id", nullable = false)
    private UUID cashierUserId;

    @NotNull(message = "O papel do operador é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private CashierRoleEnum role;

    @NotNull(message = "O fundo de troco é obrigatório.")
    @DecimalMin(value = "0.00", message = "O fundo de troco não pode ser negativo.")
    @Column(name = "opening_cash", nullable = false, precision = 15, scale = 2)
    private BigDecimal openingCash = BigDecimal.ZERO;

    @NotNull(message = "O status da sessão é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CashSessionStatusEnum status;

    @NotNull(message = "A data de abertura é obrigatória.")
    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "total_cash", precision = 15, scale = 2)
    private BigDecimal totalCash;

    @Column(name = "total_card", precision = 15, scale = 2)
    private BigDecimal totalCard;

    @Column(name = "total_pix", precision = 15, scale = 2)
    private BigDecimal totalPix;

    @Column(name = "total_withdrawal", precision = 15, scale = 2)
    private BigDecimal totalWithdrawal;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
