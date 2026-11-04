package com.br.manager.domain.comum.service;

import com.br.manager.domain.comum.dto.SincronizacaoDTO;
import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import com.br.manager.domain.comum.repository.RepositorioSincronizacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.br.manager.domain.comum.UtilStrings;

@Service
public class ServicoSincronizacao {

    @Autowired
    private RepositorioSincronizacao sincronizacaoRepository;

    /**
     * Consulta a entidade Sincronize pelo nome da tabela (como String).
     * Aceita valores que correspondam ao nome do enum SincronizeNameTabelaEnum (case-insensitive).
     * Lance ExcecaoNegocioNaoEncontrado se não encontrada ou argumento inválido.
     */
    @Transactional(readOnly = true)
    public Sincronizacao findByTabelaName(String tabelaName) {
        if (!UtilStrings.hasText(tabelaName)) {
            throw new ExcecaoNegocioNaoEncontrado("Nome da tabela não informado");
        }

        NomeTabelaSincronizacaoEnum enumValor;
        try {
            enumValor = NomeTabelaSincronizacaoEnum.valueOf(tabelaName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Nome da tabela inválido: %s", tabelaName));
        }

        var sincronizacao = sincronizacaoRepository.findByNomeTabela(enumValor)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Sincronize para tabela %s não encontrada", tabelaName)));
        return sincronizacao;
    }

    @Transactional
    public Sincronizacao update(SincronizacaoDTO sincronizacaoDTO) {
        if (sincronizacaoDTO == null) {
            throw new ExcecaoNegocioNaoEncontrado("SincronizacaoDTO não informado");
        }

        String tabelaName = sincronizacaoDTO.getNomeTabela();
        if (!UtilStrings.hasText(tabelaName)) {
            throw new ExcecaoNegocioNaoEncontrado("Nome da tabela não informado");
        }

        NomeTabelaSincronizacaoEnum enumValor;
        try {
            enumValor = NomeTabelaSincronizacaoEnum.valueOf(tabelaName.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Nome da tabela inválido: %s", tabelaName));
        }

        Sincronizacao sincronizacao = sincronizacaoRepository.findByNomeTabela(enumValor)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Sincronize para tabela %s não encontrada", tabelaName)));

        java.time.LocalDateTime ultimaSincronizacao = sincronizacaoDTO.getUltimaSincronizacao() != null
                ? sincronizacaoDTO.getUltimaSincronizacao()
                : java.time.LocalDateTime.now();
        sincronizacao.setUltimaSincronizacao(ultimaSincronizacao);
        sincronizacaoRepository.updateUltimaSincronizacao(enumValor, ultimaSincronizacao);

        return sincronizacao;
    }
}
