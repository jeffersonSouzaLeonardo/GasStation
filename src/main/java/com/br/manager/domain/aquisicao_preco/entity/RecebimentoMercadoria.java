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
@Table(name = "recebimentos_mercadorias")
public class RecebimentoMercadoria {

    public static final class Situacao {
        public static final String DRAFT = "DRAFT";
        public static final String RECEBIDO = "RECEBIDO";
        public static final String PARTIALLY_RECEBIDO = "PARTIALLY_RECEBIDO";
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_compra_id")
    private PedidoCompra pedidoCompra;

    @NotBlank(message = "Nota numero é obrigatório.")
    @Column(name = "numero_nota", nullable = false)
    private String numeroNota;

    @NotBlank(message = "Nota key é obrigatório.")
    @Column(name = "chave_nota", nullable = false)
    private String chaveNota;

    @NotNull(message = "Recebido date é obrigatório.")
    @Column(name = "recebido_em", nullable = false)
    private LocalDateTime recebidoEm;

    @NotBlank(message = "Situacao é obrigatório.")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @NotNull(message = "Frete valor é obrigatório.")
    @Column(name = "valor_frete", precision = 19, scale = 4, nullable = false)
    private BigDecimal valorFrete;

    @NotNull(message = "Outros custos are obrigatorio.")
    @Column(name = "outros_custos", precision = 19, scale = 4, nullable = false)
    private BigDecimal outrosCustos;

    @NotNull(message = "Recebido by é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebido_por", nullable = false)
    private Usuario recebidoPor;

    @OneToMany(mappedBy = "recebimentoMercadoria", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecebimentoMercadoriaItem> itens = new ArrayList<>();

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

    public PedidoCompra getPedidoCompra() {
        return pedidoCompra;
    }

    public void setPedidoCompra(PedidoCompra pedidoCompra) {
        this.pedidoCompra = pedidoCompra;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getChaveNota() {
        return chaveNota;
    }

    public void setChaveNota(String chaveNota) {
        this.chaveNota = chaveNota;
    }

    public LocalDateTime getRecebidoEm() {
        return recebidoEm;
    }

    public void setRecebidoEm(LocalDateTime recebidoEm) {
        this.recebidoEm = recebidoEm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getValorFrete() {
        return valorFrete;
    }

    public void setValorFrete(BigDecimal valorFrete) {
        this.valorFrete = valorFrete;
    }

    public BigDecimal getOutrosCustos() {
        return outrosCustos;
    }

    public void setOutrosCustos(BigDecimal outrosCustos) {
        this.outrosCustos = outrosCustos;
    }

    public Usuario getRecebidoPor() {
        return recebidoPor;
    }

    public void setRecebidoPor(Usuario recebidoPor) {
        this.recebidoPor = recebidoPor;
    }

    public List<RecebimentoMercadoriaItem> getItens() {
        return itens;
    }

    public void setItens(List<RecebimentoMercadoriaItem> itens) {
        this.itens.clear();
        if (itens != null) {
            itens.forEach(item -> {
                item.setRecebimentoMercadoria(this);
                this.itens.add(item);
            });
        }
    }
}
