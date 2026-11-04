package com.br.manager.domain.turno_caixa.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.turno_caixa.dto.TurnoResumoResponseDTO;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.entity.LeituraBico;
import com.br.manager.domain.turno_caixa.entity.FechamentoPagamento;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;
import com.br.manager.domain.turno_caixa.enums.TipoLeituraBicoEnum;
import com.br.manager.domain.turno_caixa.fixture.TurnoFixture;
import com.br.manager.domain.turno_caixa.mapper.MovimentoCaixaMapper;
import com.br.manager.domain.turno_caixa.mapper.FechamentoPagamentoMapper;
import com.br.manager.domain.turno_caixa.mapper.TurnoMapper;
import com.br.manager.domain.turno_caixa.repository.MovimentoCaixaRepository;
import com.br.manager.domain.turno_caixa.repository.BicoLeituraRepository;
import com.br.manager.domain.turno_caixa.repository.FechamentoPagamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoResumoServiceTest {

    private static final BigDecimal FIRST_VENDA = new BigDecimal("100.00");
    private static final BigDecimal SECOND_VENDA = new BigDecimal("50.50");
    private static final BigDecimal SANGRIA = new BigDecimal("30.00");
    private static final BigDecimal TEST_LEITURA = new BigDecimal("2.00");
    private static final BigDecimal CALIBRACAO_LEITURA = new BigDecimal("3.00");
    private static final BigDecimal ABERTURA_LEITURA = new BigDecimal("999.00");
    private static final BigDecimal SHORTAGE = new BigDecimal("-5.00");
    private static final BigDecimal SURPLUS = new BigDecimal("1.25");

    @Mock
    private SuporteValidacaoTurno validacaoSuporte;

    @Mock
    private MovimentoCaixaRepository dinheiroMovimentoRepository;

    @Mock
    private FechamentoPagamentoRepository pagamentoFechamentoRepository;

    @Mock
    private BicoLeituraRepository bicoLeituraRepository;

    @Spy
    private TurnoMapper turnoMapper = new TurnoMapper();

    @Spy
    private MovimentoCaixaMapper dinheiroMovimentoMapper = new MovimentoCaixaMapper();

    @Spy
    private FechamentoPagamentoMapper pagamentoFechamentoMapper = new FechamentoPagamentoMapper();

    @InjectMocks
    private TurnoResumoService turnoResumoService;

    @Test
    void shouldReturnZeroTotalsWhenTurnoHasNoActivity() {
        Turno turno = TurnoFixture.abertoTurno();
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);

        TurnoResumoResponseDTO resumo = turnoResumoService.getTurnoResumo(turno.getId());

        assertThat(resumo.getTurno().getId()).isEqualTo(turno.getId());
        assertThat(resumo.getGrossVendas()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getCalibracaoAndTestAjuste()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getNetVendas()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getFundoAbertura()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
        assertThat(resumo.getFechamentoDinheiro()).isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA);
        assertThat(resumo.getTotalDiferencaValor()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resumo.getMovimentosCaixa()).isEmpty();
        assertThat(resumo.getFechamentosPagamento()).isEmpty();
    }

    @Test
    void shouldSumOnlyVendaMovimentosAsGrossVendas() {
        Turno turno = givenTurnoWithMovimentos();

        TurnoResumoResponseDTO resumo = turnoResumoService.getTurnoResumo(turno.getId());

        assertThat(resumo.getGrossVendas()).isEqualByComparingTo(FIRST_VENDA.add(SECOND_VENDA));
        assertThat(resumo.getMovimentosCaixa()).hasSize(3);
    }

    @Test
    void shouldAddGrossVendasToAberturaDinheiroAsFechamentoDinheiro() {
        Turno turno = givenTurnoWithMovimentos();

        TurnoResumoResponseDTO resumo = turnoResumoService.getTurnoResumo(turno.getId());

        assertThat(resumo.getFechamentoDinheiro())
                .isEqualByComparingTo(TurnoFixture.FUNDO_ABERTURA.add(FIRST_VENDA).add(SECOND_VENDA));
    }

    @Test
    void shouldSubtractTestAndCalibracaoLeiturasFromGrossVendas() {
        Turno turno = givenTurnoWithMovimentos();
        when(bicoLeituraRepository.findByTurnoId(turno.getId())).thenReturn(List.of(
                leitura(TipoLeituraBicoEnum.TESTE, TEST_LEITURA),
                leitura(TipoLeituraBicoEnum.CALIBRACAO, CALIBRACAO_LEITURA),
                leitura(TipoLeituraBicoEnum.ABERTURA, ABERTURA_LEITURA)));

        TurnoResumoResponseDTO resumo = turnoResumoService.getTurnoResumo(turno.getId());

        BigDecimal ajuste = TEST_LEITURA.add(CALIBRACAO_LEITURA);
        assertThat(resumo.getCalibracaoAndTestAjuste()).isEqualByComparingTo(ajuste);
        assertThat(resumo.getNetVendas()).isEqualByComparingTo(FIRST_VENDA.add(SECOND_VENDA).subtract(ajuste));
    }

    @Test
    void shouldSumFechamentoPagamentoDifferences() {
        Turno turno = TurnoFixture.abertoTurno();
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(pagamentoFechamentoRepository.findByTurnoIdOrderByPagamentoMethodId(turno.getId()))
                .thenReturn(List.of(fechamento(turno, SHORTAGE), fechamento(turno, SURPLUS)));

        TurnoResumoResponseDTO resumo = turnoResumoService.getTurnoResumo(turno.getId());

        assertThat(resumo.getTotalDiferencaValor()).isEqualByComparingTo(SHORTAGE.add(SURPLUS));
        assertThat(resumo.getFechamentosPagamento()).hasSize(2);
        assertThat(resumo.getFechamentosPagamento().get(0).getTurnoId()).isEqualTo(turno.getId());
    }

    @Test
    void shouldPropagateNotFoundWhenTurnoIsUnknown() {
        UUID turnoId = UUID.randomUUID();
        when(validacaoSuporte.getObrigatorioTurno(turnoId)).thenThrow(new ExcecaoNegocioNaoEncontrado("turno"));

        assertThatThrownBy(() -> turnoResumoService.getTurnoResumo(turnoId))
                .isInstanceOf(ExcecaoNegocioNaoEncontrado.class);
        verifyNoInteractions(dinheiroMovimentoRepository, pagamentoFechamentoRepository, bicoLeituraRepository);
    }

    private Turno givenTurnoWithMovimentos() {
        Turno turno = TurnoFixture.abertoTurno();
        when(validacaoSuporte.getObrigatorioTurno(turno.getId())).thenReturn(turno);
        when(dinheiroMovimentoRepository.findByTurnoIdOrderByOcorridoEmAsc(turno.getId())).thenReturn(List.of(
                movimento(turno, TipoMovimentoCaixaEnum.VENDA, FIRST_VENDA),
                movimento(turno, TipoMovimentoCaixaEnum.SANGRIA, SANGRIA),
                movimento(turno, TipoMovimentoCaixaEnum.VENDA, SECOND_VENDA)));
        return turno;
    }

    private static MovimentoCaixa movimento(Turno turno, TipoMovimentoCaixaEnum type, BigDecimal valor) {
        MovimentoCaixa movimento = new MovimentoCaixa();
        movimento.setId(UUID.randomUUID());
        movimento.setTurno(turno);
        movimento.setTipoMovimento(type);
        movimento.setValor(valor);
        return movimento;
    }

    private static LeituraBico leitura(TipoLeituraBicoEnum type, BigDecimal medidor) {
        LeituraBico leitura = new LeituraBico();
        leitura.setId(UUID.randomUUID());
        leitura.setTipoLeitura(type);
        leitura.setLeituraMedidor(medidor);
        return leitura;
    }

    private static FechamentoPagamento fechamento(Turno turno, BigDecimal diferenca) {
        FechamentoPagamento fechamento = new FechamentoPagamento();
        fechamento.setId(UUID.randomUUID());
        fechamento.setTurno(turno);
        fechamento.setPagamentoMethodId(UUID.randomUUID());
        fechamento.setValorDiferenca(diferenca);
        return fechamento;
    }
}
