package com.br.manager.domain.venda_recibo.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.repository.ClienteRepository;
import com.br.manager.domain.operacional.repository.VeiculoClienteRepository;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.venda_recibo.dto.VendaInputDTO;
import com.br.manager.domain.venda_recibo.dto.VendaResponseDTO;
import com.br.manager.domain.venda_recibo.entity.Venda;
import com.br.manager.domain.venda_recibo.entity.VendaItem;
import com.br.manager.domain.venda_recibo.enums.VendaSituacao;
import com.br.manager.domain.venda_recibo.mapper.ItemVendaMapper;
import com.br.manager.domain.venda_recibo.mapper.VendaMapper;
import com.br.manager.domain.venda_recibo.repository.VendaRepository;
import com.br.manager.domain.turno_caixa.entity.Turno;
import com.br.manager.domain.turno_caixa.enums.TurnoSituacaoEnum;
import com.br.manager.domain.turno_caixa.repository.TurnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private VendaMapper vendaMapper;
    @Autowired
    private ItemVendaService vendaItemService;
    @Autowired
    private ItemVendaMapper vendaItemMapper;
    @Autowired
    private EstacaoRepository estacaoRepository;
    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VeiculoClienteRepository clienteVeiculoRepository;

    public VendaResponseDTO create(VendaInputDTO inputDTO) {
        validateCreate(inputDTO);
        try {
            Venda venda = new Venda();
            fillVenda(venda, inputDTO);
            venda.setId(null);
            venda.setStatus(VendaSituacao.ABERTO);
            venda.setVendidoEm(inputDTO.getVendidoEm() != null ? inputDTO.getVendidoEm() : Instant.now());
            venda.setValorDesconto(inputDTO.getValorDesconto() != null ? inputDTO.getValorDesconto() : BigDecimal.ZERO);

            Venda savedVenda = vendaRepository.saveAndFlush(venda);
            if (inputDTO.getItens() != null && !inputDTO.getItens().isEmpty()) {
                vendaItemService.saveVendaItens(savedVenda.getId(), inputDTO.getItens());
            }

            Venda reloaded = vendaRepository.findDetailedById(savedVenda.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", savedVenda.getId())));
            return vendaMapper.toResponse(vendaRepository.saveAndFlush(reloaded));
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String numeroVenda = inputDTO != null && UtilStrings.hasText(inputDTO.getNumeroVenda()) ? inputDTO.getNumeroVenda() : "";
            throw new ExcecaoNegocio("Erro ao salvar venda " + numeroVenda, e);
        }
    }

    public VendaResponseDTO update(VendaInputDTO inputDTO) {
        try {
            Venda venda = vendaRepository.findDetailedById(inputDTO.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", inputDTO.getId())));
            if (venda.estaBloqueadaFinanceiramente()) {
                throw new ExcecaoNegocio("Vendas pagas ou com crédito pendente não podem ser alteradas.");
            }
            fillVenda(venda, inputDTO);
            return vendaMapper.toResponse(vendaRepository.saveAndFlush(venda));
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String numeroVenda = inputDTO != null && UtilStrings.hasText(inputDTO.getNumeroVenda()) ? inputDTO.getNumeroVenda() : "";
            throw new ExcecaoNegocio("Erro ao salvar venda " + numeroVenda, e);
        }
    }

    public VendaResponseDTO find(UUID id) {
        return vendaMapper.toResponse(vendaRepository.findDetailedById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", id))));
    }

    public List<VendaResponseDTO> findAll() {
        return vendaRepository.findAll().stream().map(venda -> find(venda.getId())).toList();
    }

    public List<VendaResponseDTO> findByEstacao(UUID estacaoId) {
        return vendaRepository.findByEstacaoId(estacaoId).stream().map(venda -> find(venda.getId())).toList();
    }

    public Venda getVendaEntityById(UUID id) {
        return vendaRepository.findDetailedById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Venda com ID %s não encontrada", id)));
    }

    private void fillVenda(Venda venda, VendaInputDTO inputDTO) {
        if (inputDTO.getEstacaoId() != null) {
            Estacao estacao = estacaoRepository.findById(inputDTO.getEstacaoId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", inputDTO.getEstacaoId())));
            if (!Boolean.TRUE.equals(estacao.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", inputDTO.getEstacaoId()));
            }
            venda.setEstacaoId(estacao.getId());
        }

        if (inputDTO.getTurnoId() != null) {
            Turno turno = turnoRepository.findById(inputDTO.getTurnoId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Turno com ID %s não encontrado", inputDTO.getTurnoId())));
            if (turno.getStatus() != TurnoSituacaoEnum.ABERTO) {
                throw new ExcecaoNegocio(String.format("O turno %s não está ativo.", inputDTO.getTurnoId()));
            }
            venda.setTurnoId(turno.getId());
        }

        if (inputDTO.getCaixeiroUsuarioId() != null) {
            Usuario caixeiro = usuarioRepository.findById(inputDTO.getCaixeiroUsuarioId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCaixeiroUsuarioId())));
            if (!Boolean.TRUE.equals(caixeiro.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCaixeiroUsuarioId()));
            }
            venda.setCaixeiroUsuarioId(caixeiro.getId());
        }

        if (inputDTO.getFrentistaUsuarioId() != null) {
            Usuario frentista = usuarioRepository.findById(inputDTO.getFrentistaUsuarioId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getFrentistaUsuarioId())));
            if (!Boolean.TRUE.equals(frentista.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getFrentistaUsuarioId()));
            }
            venda.setFrentistaUsuarioId(frentista.getId());
        }

        if (inputDTO.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(inputDTO.getClienteId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Cliente com ID %s não encontrado", inputDTO.getClienteId())));
            if (!Boolean.TRUE.equals(cliente.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Cliente com ID %s não encontrado", inputDTO.getClienteId()));
            }
            venda.setClienteId(cliente.getId());
        }

        if (inputDTO.getVeiculoId() != null) {
            VeiculoCliente veiculo = clienteVeiculoRepository.findById(inputDTO.getVeiculoId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Veículo com ID %s não encontrado", inputDTO.getVeiculoId())));
            if (!Boolean.TRUE.equals(veiculo.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Veículo com ID %s não encontrado", inputDTO.getVeiculoId()));
            }
            if (venda.getClienteId() != null && !veiculo.getClienteId().equals(venda.getClienteId())) {
                throw new ExcecaoNegocio("O veículo não pertence ao cliente informado.");
            }
            venda.setVeiculoId(veiculo.getId());
        }

        if (inputDTO.getNumeroVenda() != null) {
            venda.setNumeroVenda(inputDTO.getNumeroVenda());
        }
        if (inputDTO.getVendidoEm() != null) {
            venda.setVendidoEm(inputDTO.getVendidoEm());
        }
        if (inputDTO.getValorDesconto() != null) {
            venda.setValorDesconto(inputDTO.getValorDesconto());
        }
        if (inputDTO.getSituacaoFiscal() != null) {
            venda.setSituacaoFiscal(inputDTO.getSituacaoFiscal());
        }
        if (inputDTO.getObservacoes() != null) {
            venda.setObservacoes(inputDTO.getObservacoes());
        }
    }

    private void validateCreate(VendaInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new ExcecaoNegocio("O payload da venda é obrigatório.");
        }
        if (inputDTO.getEstacaoId() == null) {
            throw new ExcecaoNegocio("O ID do posto é obrigatório.");
        }
        if (inputDTO.getTurnoId() == null) {
            throw new ExcecaoNegocio("O ID do turno é obrigatório.");
        }
        if (inputDTO.getCaixeiroUsuarioId() == null) {
            throw new ExcecaoNegocio("O ID do usuário caixa é obrigatório.");
        }
        if (!UtilStrings.hasText(inputDTO.getNumeroVenda())) {
            throw new ExcecaoNegocio("O número da venda é obrigatório.");
        }
        if (vendaRepository.existsByEstacaoIdAndNumeroVenda(inputDTO.getEstacaoId(), inputDTO.getNumeroVenda())) {
            throw new ExcecaoNegocio(String.format("A venda %s já existe para o posto %s.", inputDTO.getNumeroVenda(), inputDTO.getEstacaoId()));
        }
        if (inputDTO.getItens() == null || inputDTO.getItens().isEmpty()) {
            throw new ExcecaoNegocio("A venda deve conter pelo menos um item.");
        }
    }

}
