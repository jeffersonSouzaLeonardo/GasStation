package com.br.manager.domain.operational.entity;

import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Company ID is required.")
    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @NotBlank(message = "SKU is required.")
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @NotBlank(message = "Product name is required.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Product type is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductTypeEnum productType;

    @NotNull(message = "Product unit is required.")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private UnitTypeEnum unit;

    @Column(name = "anp_code", length = 20)
    private String anpCode;

    @Column(name = "ncm", length = 10)
    private String ncm;

    @Column(name = "cest", length = 10)
    private String cest;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ProductTypeEnum getProductType() { return productType; }
    public void setProductType(ProductTypeEnum productType) { this.productType = productType; }
    public UnitTypeEnum getUnit() { return unit; }
    public void setUnit(UnitTypeEnum unit) { this.unit = unit; }
    public String getAnpCode() { return anpCode; }
    public void setAnpCode(String anpCode) { this.anpCode = anpCode; }
    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }
    public String getCest() { return cest; }
    public void setCest(String cest) { this.cest = cest; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
