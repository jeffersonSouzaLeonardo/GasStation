package com.br.manager.domain.operational.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "Company ID is required.")
    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @NotBlank(message = "Legal name is required.")
    @Column(name = "legal_name", nullable = false)
    private String legalName;

    @Column(name = "cnpj_cpf", length = 14)
    private String cnpjCpf;

    @Column(name = "state_registration", length = 30)
    private String stateRegistration;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address_street")
    private String addressStreet;

    @Column(name = "address_number", length = 20)
    private String addressNumber;

    @Column(name = "address_complement", length = 100)
    private String addressComplement;

    @Column(name = "address_neighborhood", length = 100)
    private String addressNeighborhood;

    @Column(name = "address_city", length = 100)
    private String addressCity;

    @Column(name = "address_state", length = 2)
    private String addressState;

    @Column(name = "address_zip_code", length = 10)
    private String addressZipCode;

    @NotNull(message = "Active flag is required.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    public void ensureId() { if (this.id == null) { this.id = UUID.randomUUID(); } }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCompanyId() { return companyId; }
    public void setCompanyId(UUID companyId) { this.companyId = companyId; }
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getCnpjCpf() { return cnpjCpf; }
    public void setCnpjCpf(String cnpjCpf) { this.cnpjCpf = cnpjCpf; }
    public String getStateRegistration() { return stateRegistration; }
    public void setStateRegistration(String stateRegistration) { this.stateRegistration = stateRegistration; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddressStreet() { return addressStreet; }
    public void setAddressStreet(String addressStreet) { this.addressStreet = addressStreet; }
    public String getAddressNumber() { return addressNumber; }
    public void setAddressNumber(String addressNumber) { this.addressNumber = addressNumber; }
    public String getAddressComplement() { return addressComplement; }
    public void setAddressComplement(String addressComplement) { this.addressComplement = addressComplement; }
    public String getAddressNeighborhood() { return addressNeighborhood; }
    public void setAddressNeighborhood(String addressNeighborhood) { this.addressNeighborhood = addressNeighborhood; }
    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String addressCity) { this.addressCity = addressCity; }
    public String getAddressState() { return addressState; }
    public void setAddressState(String addressState) { this.addressState = addressState; }
    public String getAddressZipCode() { return addressZipCode; }
    public void setAddressZipCode(String addressZipCode) { this.addressZipCode = addressZipCode; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
