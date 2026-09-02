package com.br.manager.domain.operational.dto;

import com.br.manager.domain.operational.enums.ProductTypeEnum;
import com.br.manager.domain.operational.enums.UnitTypeEnum;

import java.util.UUID;

public class ProductResponseDTO {
    private UUID id;
    private UUID companyId;
    private String sku;
    private String name;
    private ProductTypeEnum productType;
    private UnitTypeEnum unit;
    private String anpCode;
    private String ncm;
    private String cest;
    private Boolean active;

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
