package com.br.manager.domain.venda_recibo.entity;

import com.br.manager.domain.venda_recibo.enums.SituacaoFiscal;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "venda")
public class Venda {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "O ID de Estacao é obrigatório.")
    @Column(name = "estacao_id", nullable = false)
    private UUID estacaoId;

    @NotNull(message = "O ID de Turno é obrigatório.")
    @Column(name = "turno_id", nullable = false)
    private UUID turnoId;

    @NotNull(message = "Venda numero é obrigatório.")
    @Column(name = "numero_venda", nullable = false, length = 50)
    private String numeroVenda;

    @NotNull(message = "Situacao é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private VendaSituacao status;

    @NotNull(message = "Vendido at é obrigatório.")
    @Column(name = "vendido_em", nullable = false)
    private Instant vendidoEm;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "veiculo_id")
    private UUID veiculoId;

    @Column(name = "frentista_usuario_id")
    private UUID frentistaUsuarioId;

    @NotNull(message = "O ID de Caixeiro usuario é obrigatório.")
    @Column(name = "caixeiro_usuario_id", nullable = false)
    private UUID caixeiroUsuarioId;

    @NotNull(message = "Subtotal é obrigatório.")
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull(message = "Desconto valor é obrigatório.")
    @Column(name = "valor_desconto", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_fiscal", length = 40)
    private SituacaoFiscal situacaoFiscal;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaItem> itens = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PagamentoVenda> pagamentos = new ArrayList<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReembolsoVenda> reembolsos = new ArrayList<>();
public boolean estaBloqueadaFinanceiramente() {
        return status == VendaSituacao.PAGO || status == VendaSituacao.CREDITO_PENDENTE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getEstacaoId() {
        return estacaoId;
    }

    public void setEstacaoId(UUID estacaoId) {
        this.estacaoId = estacaoId;
    }

    public UUID getTurnoId() {
        return turnoId;
    }

    public void setTurnoId(UUID turnoId) {
        this.turnoId = turnoId;
    }

    public String getNumeroVenda() {
        return numeroVenda;
    }

    public void setNumeroVenda(String numeroVenda) {
        this.numeroVenda = numeroVenda;
    }

    public VendaSituacao getStatus() {
        return status;
    }

    public void setStatus(VendaSituacao status) {
        this.status = status;
    }

    public Instant getVendidoEm() {
        return vendidoEm;
    }

    public void setVendidoEm(Instant vendidoEm) {
        this.vendidoEm = vendidoEm;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public void setClienteId(UUID clienteId) {
        this.clienteId = clienteId;
    }

    public UUID getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(UUID veiculoId) {
        this.veiculoId = veiculoId;
    }

    public UUID getFrentistaUsuarioId() {
        return frentistaUsuarioId;
    }

    public void setFrentistaUsuarioId(UUID frentistaUsuarioId) {
        this.frentistaUsuarioId = frentistaUsuarioId;
    }

    public UUID getCaixeiroUsuarioId() {
        return caixeiroUsuarioId;
    }

    public void setCaixeiroUsuarioId(UUID caixeiroUsuarioId) {
        this.caixeiroUsuarioId = caixeiroUsuarioId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public SituacaoFiscal getSituacaoFiscal() {
        return situacaoFiscal;
    }

    public void setSituacaoFiscal(SituacaoFiscal situacaoFiscal) {
        this.situacaoFiscal = situacaoFiscal;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<VendaItem> getItens() {
        return itens;
    }

    public void setItens(List<VendaItem> itens) {
        this.itens.clear();
        if (itens != null) {
            itens.forEach(this::adicionarItem);
        }
    }

    public void adicionarItem(VendaItem item) {
        item.setVenda(this);
        this.itens.add(item);
    }

    public List<PagamentoVenda> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoVenda> pagamentos) {
        this.pagamentos.clear();
        if (pagamentos != null) {
            pagamentos.forEach(this::adicionarPagamento);
        }
    }

    public void adicionarPagamento(PagamentoVenda pagamento) {
        pagamento.setVenda(this);
        this.pagamentos.add(pagamento);
    }

    public List<ReembolsoVenda> getReembolsos() {
        return reembolsos;
    }

    public void setReembolsos(List<ReembolsoVenda> reembolsos) {
        this.reembolsos.clear();
        if (reembolsos != null) {
            reembolsos.forEach(this::adicionarReembolso);
        }
    }

    public void adicionarReembolso(ReembolsoVenda reembolso) {
        reembolso.setVenda(this);
        this.reembolsos.add(reembolso);
    }
}
