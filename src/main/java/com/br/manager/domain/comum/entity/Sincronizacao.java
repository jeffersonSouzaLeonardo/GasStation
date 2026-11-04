package com.br.manager.domain.comum.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sincronizacao")
public class Sincronizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "O nome da tabela é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "nome", nullable = false)
    private NomeTabelaSincronizacaoEnum nomeTabela;

    @Column(name = "ultima_sincronizacao")
    private LocalDateTime ultimaSincronizacao;

    public LocalDateTime getUltimaSincronizacao() {
        return ultimaSincronizacao;
    }

    public void setUltimaSincronizacao(LocalDateTime ultimaSincronizacao) {
        this.ultimaSincronizacao = ultimaSincronizacao;
    }

    public NomeTabelaSincronizacaoEnum getNomeTabela() {
        return nomeTabela;
    }

}
