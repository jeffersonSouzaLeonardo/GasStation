package com.br.manager.domain.operacional.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abastecimento registrado no PDV e enviado para a retaguarda.
 * O identificador é gerado pelo PDV e replicado aqui (sem geração automática).
 */
@Entity
@Table(name = "abastecimentos")
public class Abastecimento {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "A data/hora do abastecimento é obrigatória.")
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @NotNull(message = "O ID do tanque é obrigatório.")
    @Column(name = "tanque_id", nullable = false)
    private UUID tanqueId;

    @NotNull(message = "O ID da bomba é obrigatório.")
    @Column(name = "bomba_id", nullable = false)
    private UUID bombaId;

    @NotNull(message = "O volume é obrigatório.")
    @Column(name = "volume", nullable = false, precision = 15, scale = 4)
    private BigDecimal volume;

    @NotNull(message = "O valor total é obrigatório.")
    @Column(name = "valor_total", nullable = false, precision = 15, scale = 4)
    private BigDecimal valorTotal;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public UUID getTanqueId() { return tanqueId; }
    public void setTanqueId(UUID tanqueId) { this.tanqueId = tanqueId; }
    public UUID getBombaId() { return bombaId; }
    public void setBombaId(UUID bombaId) { this.bombaId = bombaId; }
    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
