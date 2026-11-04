package com.br.manager.domain.comum.dto;

import java.time.LocalDateTime;

public class SincronizacaoDTO {
    private String nomeTabela;
    private LocalDateTime ultimaSincronizacao;

        public String getNomeTabela() {
            return nomeTabela;
        }

        public void setNomeTabela(String nomeTabela) {
            this.nomeTabela = nomeTabela;
        }

        public LocalDateTime getUltimaSincronizacao() {
            return ultimaSincronizacao;
        }

        public void setUltimaSincronizacao(LocalDateTime ultimaSincronizacao) {
            this.ultimaSincronizacao = ultimaSincronizacao;
        }
}
