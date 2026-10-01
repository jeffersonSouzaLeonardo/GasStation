package com.br.manager.domain.price_acquisition.entity;

import com.br.manager.domain.organization.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "price_tables")
public class PriceTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false)
    private UUID id;


    @NotBlank(message = "O nome é obrigatório.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "A data de início de validade é obrigatória.")
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @NotNull(message = "A data de fim de validade é obrigatória.")
    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @NotBlank(message = "O status é obrigatório.")
    @Column(name = "status", length = 20)
    private String status;

    @NotNull(message = "O usuário de criação é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "priceTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PriceItem> priceItems;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<PriceItem> getPriceItems() {
        return priceItems;
    }

    public void setPriceItems(List<PriceItem> priceItems) {
        this.priceItems = priceItems;
    }
}
