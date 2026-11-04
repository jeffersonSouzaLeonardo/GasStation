package com.br.manager.domain.aquisicao_preco.entity;

import com.br.manager.domain.organizacao.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tabelas_preco")
public class TabelaPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false)
    private UUID id;


    @NotBlank(message = "O nome é obrigatório.")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull(message = "A data de início de validade é obrigatória.")
    @Column(name = "valido_de", nullable = false)
    private LocalDateTime validoDe;

    @NotNull(message = "A data de fim de validade é obrigatória.")
    @Column(name = "valido_ate", nullable = false)
    private LocalDateTime validoAte;

    @NotBlank(message = "O status é obrigatório.")
    @Column(name = "status", length = 20)
    private String status;

    @NotNull(message = "O usuário de criação é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;

    @OneToMany(mappedBy = "tabelaPreco", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPreco> itensPreco;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDateTime getValidoDe() {
        return validoDe;
    }

    public void setValidoDe(LocalDateTime validoDe) {
        this.validoDe = validoDe;
    }

    public LocalDateTime getValidoAte() {
        return validoAte;
    }

    public void setValidoAte(LocalDateTime validoAte) {
        this.validoAte = validoAte;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }

    public List<ItemPreco> getItensPreco() {
        return itensPreco;
    }

    public void setItensPreco(List<ItemPreco> itensPreco) {
        this.itensPreco = itensPreco;
    }
}
