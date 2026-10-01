package com.br.manager.domain.operational.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tanks")
public class Tank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank(message = "O código do tanque é obrigatório.")
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @NotNull(message = "O ID do produto é obrigatório.")
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotNull(message = "A capacidade do tanque é obrigatória.")
    @DecimalMin(value = "0.0001", message = "A capacidade deve ser maior que zero.")
    @Column(name = "capacity_liters", nullable = false, precision = 15, scale = 4)
    private BigDecimal capacityLiters;

    @Column(name = "dead_stock_liters", precision = 15, scale = 4)
    private BigDecimal deadStockLiters = BigDecimal.ZERO;

    @NotNull(message = "Os litros contabilizados atuais são obrigatórios.")
    @Column(name = "current_book_liters", nullable = false, precision = 15, scale = 4)
    private BigDecimal currentBookLiters = BigDecimal.ZERO;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;
public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getCapacityLiters() { return capacityLiters; }
    public void setCapacityLiters(BigDecimal capacityLiters) { this.capacityLiters = capacityLiters; }
    public BigDecimal getDeadStockLiters() { return deadStockLiters; }
    public void setDeadStockLiters(BigDecimal deadStockLiters) { this.deadStockLiters = deadStockLiters; }
    public BigDecimal getCurrentBookLiters() { return currentBookLiters; }
    public void setCurrentBookLiters(BigDecimal currentBookLiters) { this.currentBookLiters = currentBookLiters; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
