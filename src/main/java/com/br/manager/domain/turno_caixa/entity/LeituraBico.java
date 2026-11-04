package com.br.manager.domain.turno_caixa.entity;

import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leituras_bico")
public class LeituraBico {

    @Id
@Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O turno é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private Turno turno;

    @NotNull(message = "O ID da bomba é obrigatório.")
    @Column(name = "bico_id", nullable = false)
    private UUID bicoId;

    @NotNull(message = "O tipo de leitura é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_leitura", nullable = false, length = 20)
    private TipoLeituraBicoEnum tipoLeitura;

    @NotNull(message = "A leitura do medidor é obrigatória.")
    @DecimalMin(value = "0.000", message = "A leitura do medidor não pode ser negativa.")
    @Column(name = "leitura_medidor", nullable = false, precision = 18, scale = 3)
    private BigDecimal leituraMedidor;

    @NotNull(message = "A data da leitura é obrigatória.")
    @Column(name = "read_em", nullable = false)
    private LocalDateTime lidoEm;

    @NotNull(message = "O usuário registrador é obrigatório.")
    @Column(name = "registrado_by", nullable = false)
    private UUID registradoPor;

    @Column(name = "observacoes", length = 500)
    private String observacoes;
public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public UUID getBicoId() {
        return bicoId;
    }

    public void setBicoId(UUID bicoId) {
        this.bicoId = bicoId;
    }

    public TipoLeituraBicoEnum getTipoLeitura() {
        return tipoLeitura;
    }

    public void setTipoLeitura(TipoLeituraBicoEnum tipoLeitura) {
        this.tipoLeitura = tipoLeitura;
    }

    public BigDecimal getLeituraMedidor() {
        return leituraMedidor;
    }

    public void setLeituraMedidor(BigDecimal leituraMedidor) {
        this.leituraMedidor = leituraMedidor;
    }

    public LocalDateTime getLidoEm() {
        return lidoEm;
    }

    public void setLidoEm(LocalDateTime lidoEm) {
        this.lidoEm = lidoEm;
    }

    public UUID getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(UUID registradoPor) {
        this.registradoPor = registradoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
