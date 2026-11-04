package com.br.manager.domain.aquisicao_preco.entity;

import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos_compra")
public class PedidoCompra {

    public static final class Situacao {
        public static final String DRAFT = "DRAFT";
        public static final String APROVADO = "APROVADO";
        public static final String SENT = "SENT";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELADO = "CANCELADO";
    }

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Fornecedor é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @NotBlank(message = "Compra order numero é obrigatório.")
    @Column(name = "numero", nullable = false, unique = true)
    private String numero;

    @NotBlank(message = "Situacao é obrigatório.")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotNull(message = "Pedido date é obrigatório.")
    @Column(name = "pedido_em", nullable = false)
    private LocalDateTime pedidoEm;

    @NotNull(message = "Esperado date é obrigatório.")
    @Column(name = "esperado_em", nullable = false)
    private LocalDateTime previstoEm;

    @NotNull(message = "Criado by é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por", nullable = false)
    private Usuario criadoPor;

    @OneToMany(mappedBy = "pedidoCompra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PedidoCompraItem> itens = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPedidoEm() {
        return pedidoEm;
    }

    public void setPedidoEm(LocalDateTime pedidoEm) {
        this.pedidoEm = pedidoEm;
    }

    public LocalDateTime getPrevistoEm() {
        return previstoEm;
    }

    public void setPrevistoEm(LocalDateTime previstoEm) {
        this.previstoEm = previstoEm;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }


    public List<PedidoCompraItem> getItens() {
        return itens;
    }

    public void setItens(List<PedidoCompraItem> itens) {
        this.itens.clear();
        if (itens != null) {
            itens.forEach(item -> {
                item.setPedidoCompra(this);
                this.itens.add(item);
            });
        }
    }

    public BigDecimal getValorTotal() {
        return itens.stream()
                .map(PedidoCompraItem::getCustoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
