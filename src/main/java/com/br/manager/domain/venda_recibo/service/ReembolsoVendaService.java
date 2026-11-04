package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaReembolsoResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.PagamentoVenda;
import com.br.manager.domain.venda_recibo.entity.ReembolsoVenda;
import com.br.manager.domain.venda_recibo.enums.SituacaoPagamento;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.mapper.ReembolsoVendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaReembolsoRepository;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import com.br.manager.domain.turno_caixa.entity.MovimentoCaixa;
import com.br.manager.domain.turno_caixa.enums.TipoMovimentoCaixaEnum;
import com.br.manager.domain.turno_caixa.repository.MovimentoCaixaRepository;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReembolsoVendaService {

    @Autowired
    private VendaReembolsoRepository vendaReembolsoRepository;
    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private MovimentoCaixaRepository dinheiroMovimentoRepository;
    @Autowired
    private ReembolsoVendaMapper vendaReembolsoMapper;

    public VendaReembolsoResponseDTO create(VendaReembolsoInputDTO inputDTO) {
        try {
            Venda venda = vendaRepository.findDetailedById(inputDTO.getVendaId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", inputDTO.getVendaId())));
            if (venda.getStatus() != VendaSituacao.PAGO && venda.getStatus() != VendaSituacao.CREDITO_PENDENTE) {
                throw new ExcecaoNegocio("Apenas vendas pagas ou com crédito pendente podem ser estornadas.");
            }

            Usuario requestedBy = usuarioRepository.findById(inputDTO.getRequestedBy())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getRequestedBy())));
            Usuario aprovadoPor = usuarioRepository.findById(inputDTO.getAprovadoPor())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getAprovadoPor())));
            if (!Boolean.TRUE.equals(requestedBy.getAtivo()) || !Boolean.TRUE.equals(aprovadoPor.getAtivo())) {
                throw new ExcecaoNegocio("Os usuários solicitante e aprovador precisam estar ativos.");
            }
            if (requestedBy.getId().equals(aprovadoPor.getId())) {
                throw new ExcecaoNegocio("A aprovação do estorno deve ser feita por um usuário diferente.");
            }

            ReembolsoVenda reembolso = vendaReembolsoMapper.toEntity(inputDTO);
            reembolso.setId(null);
            reembolso.setVenda(venda);
            reembolso.setReembolsadoEm(inputDTO.getReembolsadoEm() != null ? inputDTO.getReembolsadoEm() : Instant.now());
            ReembolsoVenda savedReembolso = vendaReembolsoRepository.saveAndFlush(reembolso);

            for (PagamentoVenda pagamento : venda.getPagamentos()) {
                if (pagamento.getStatus() == SituacaoPagamento.AUTORIZADO || pagamento.getStatus() == SituacaoPagamento.RECEBIDO) {
                    pagamento.setStatus(SituacaoPagamento.REEMBOLSADO);
                }
            }

            venda.setStatus(venda.getPagamentos().isEmpty() ? VendaSituacao.CANCELADO : VendaSituacao.REEMBOLSADO);
            vendaRepository.saveAndFlush(venda);

            turnoRepository.findById(venda.getTurnoId()).ifPresent(turno -> {
                MovimentoCaixa movimento = new MovimentoCaixa();
                movimento.setTurno(turno);
                movimento.setTipoMovimento(TipoMovimentoCaixaEnum.REEMBOLSO);
                movimento.setOcorridoEm(LocalDateTime.now());
                movimento.setReferencia(venda.getId().toString());
                movimento.setMotivo("Reversão de estorno de venda");
                movimento.setCriadoPor(requestedBy.getId());
                movimento.setAprovadoPor(aprovadoPor.getId());
                dinheiroMovimentoRepository.saveAndFlush(movimento);
            });

            return vendaReembolsoMapper.toResponse(savedReembolso);
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao estornar venda", e);
        }
    }

    public List<VendaReembolsoResponseDTO> findByVenda(UUID vendaId) {
        return vendaReembolsoMapper.toResponseList(vendaReembolsoRepository.findByVendaId(vendaId));
    }
}
