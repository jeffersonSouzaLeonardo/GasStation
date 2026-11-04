package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.turno_caixa.dto.TurnoResumoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.mapper.MovimentoCaixaMapper;
import com.br.manager.domain.turno_caixa.mapper.BicoLeituraMapper;
import com.br.manager.domain.turno_caixa.mapper.FechamentoPagamentoMapper;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.MovimentoCaixaRepository;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.repository.FechamentoPagamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TurnoResumoService {

    @Autowired
    private SuporteValidacaoTurno validacaoSuporte;

    @Autowired
    private MovimentoCaixaRepository dinheiroMovimentoRepository;

    @Autowired
    private FechamentoPagamentoRepository pagamentoFechamentoRepository;

    @Autowired
    private BicoLeituraRepository bicoLeituraRepository;

    @Autowired
    private TurnoMapper turnoMapper;

    @Autowired
    private MovimentoCaixaMapper dinheiroMovimentoMapper;

    @Autowired
    private FechamentoPagamentoMapper pagamentoFechamentoMapper;

    @Transactional
    public TurnoResumoResponseDTO getTurnoResumo(UUID turnoId) {
        Turno turno = validacaoSuporte.getObrigatorioTurno(turnoId);

        List<MovimentoCaixa> movimentos = dinheiroMovimentoRepository.findByTurnoIdOrderByOcorridoEmAsc(turnoId);
        List<FechamentoPagamento> closings = pagamentoFechamentoRepository.findByTurnoIdOrderByPagamentoMethodId(turnoId);
        List<LeituraBico> leituras = bicoLeituraRepository.findByTurnoId(turnoId);

        BigDecimal grossVendas = movimentos.stream()
                .filter(item -> item.getTipoMovimento() == TipoMovimentoCaixaEnum.VENDA)
                .map(MovimentoCaixa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal calibracaoAndTestAjuste = leituras.stream()
                .filter(item -> item.getTipoLeitura() == TipoLeituraBicoEnum.TESTE || item.getTipoLeitura() == TipoLeituraBicoEnum.CALIBRACAO)
                .map(LeituraBico::getLeituraMedidor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netVendas = grossVendas.subtract(calibracaoAndTestAjuste);
        BigDecimal totalDiferencaValor = closings.stream()
                .map(FechamentoPagamento::getValorDiferenca)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TurnoResumoResponseDTO resumo = new TurnoResumoResponseDTO();
        resumo.setTurno(turnoMapper.toResponse(turno));
        resumo.setGrossVendas(grossVendas);
        resumo.setCalibracaoAndTestAjuste(calibracaoAndTestAjuste);
        resumo.setNetVendas(netVendas);
        resumo.setFundoAbertura(turno.getFundoAbertura());
        resumo.setFechamentoDinheiro(turno.getFundoAbertura().add(grossVendas));
        resumo.setTotalDiferencaValor(totalDiferencaValor);
        resumo.setMovimentosCaixa(movimentos.stream().map(dinheiroMovimentoMapper::toResponse).toList());
        resumo.setFechamentosPagamento(closings.stream().map(pagamentoFechamentoMapper::toResponse).toList());
        return resumo;
    }
}
