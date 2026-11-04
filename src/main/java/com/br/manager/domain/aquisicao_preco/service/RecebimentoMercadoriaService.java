package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.operacional.repository.FornecedorRepository;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.RecebimentoMercadoriaResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.RecebimentoMercadoria;
import com.br.manager.domain.aquisicao_preco.entity.PedidoCompra;
import com.br.manager.domain.aquisicao_preco.mapper.RecebimentoMercadoriaMapper;
import com.br.manager.domain.aquisicao_preco.repository.ItemRecebimentoMercadoriaRepository;
import com.br.manager.domain.aquisicao_preco.repository.RecebimentoMercadoriaRepository;
import com.br.manager.domain.aquisicao_preco.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RecebimentoMercadoriaService {

    @Autowired
    private RecebimentoMercadoriaRepository mercadoriaReciboRepository;

    @Autowired
    private RecebimentoMercadoriaMapper mercadoriaReciboMapper;

    @Autowired
    private ItemRecebimentoMercadoriaRepository mercadoriaReciboItemRepository;

    @Autowired
    private ItemRecebimentoMercadoriaService mercadoriaReciboItemService;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private PedidoCompraRepository compraOrderRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public RecebimentoMercadoriaResponseDTO create(RecebimentoMercadoriaInputDTO inputDTO) {
        validateCreate(inputDTO);

        try {
            RecebimentoMercadoria recebimentoMercadoria = mercadoriaReciboMapper.mercadoriaReciboInputDTOToRecebimentoMercadoria(inputDTO);
            recebimentoMercadoria.setId(null);
            recebimentoMercadoria.setStatus(inputDTO.getStatus() != null && !inputDTO.getStatus().isBlank()
                    ? inputDTO.getStatus()
                    : RecebimentoMercadoria.Situacao.DRAFT);
            recebimentoMercadoria.setRecebidoEm(inputDTO.getRecebidoEm() != null ? inputDTO.getRecebidoEm() : LocalDateTime.now());

            Fornecedor fornecedor = fornecedorRepository.findById(inputDTO.getFornecedorId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getFornecedorId())));
            if (!Boolean.TRUE.equals(fornecedor.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getFornecedorId()));
            }
            recebimentoMercadoria.setFornecedor(fornecedor);

            if (inputDTO.getCompraOrderId() != null) {
                PedidoCompra pedidoCompra = compraOrderRepository.findById(inputDTO.getCompraOrderId())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getCompraOrderId())));
                recebimentoMercadoria.setPedidoCompra(pedidoCompra);
            }

            Usuario recebidoPor = usuarioRepository.findById(inputDTO.getRecebidoPor())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getRecebidoPor())));
            if (!Boolean.TRUE.equals(recebidoPor.getAtivo())) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getRecebidoPor()));
            }
            recebimentoMercadoria.setRecebidoPor(recebidoPor);

            RecebimentoMercadoria savedRecebimentoMercadoria = mercadoriaReciboRepository.saveAndFlush(recebimentoMercadoria);
            mercadoriaReciboItemService.saveRecebimentoMercadoriaItens(savedRecebimentoMercadoria.getId(), inputDTO.getItens());
            savedRecebimentoMercadoria.setItens(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(savedRecebimentoMercadoria.getId()));

            return mercadoriaReciboMapper.mercadoriaReciboToRecebimentoMercadoriaResponseDTO(savedRecebimentoMercadoria);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String numeroNota = inputDTO != null && UtilStrings.hasText(inputDTO.getNumeroNota()) ? inputDTO.getNumeroNota() : "";
            throw new ExcecaoNegocio("Erro ao salvar recebimento de mercadorias " + numeroNota, e);
        }
    }

    public RecebimentoMercadoriaResponseDTO update(RecebimentoMercadoriaInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new ExcecaoNegocio("O payload do recebimento de mercadorias é obrigatório.");
        }
        if (inputDTO.getId() == null) {
            throw new ExcecaoNegocio("O ID do recebimento de mercadorias é obrigatório para atualização.");
        }

        try {
            RecebimentoMercadoria recebimentoMercadoria = mercadoriaReciboRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", inputDTO.getId())));

            if (inputDTO.getFornecedorId() != null) {
                Fornecedor fornecedor = fornecedorRepository.findById(inputDTO.getFornecedorId())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getFornecedorId())));
                if (!Boolean.TRUE.equals(fornecedor.getAtivo())) {
                    throw new ExcecaoNegocioNaoEncontrado(String.format("Fornecedor com ID %s não encontrado", inputDTO.getFornecedorId()));
                }
                recebimentoMercadoria.setFornecedor(fornecedor);
            }

            if (inputDTO.getCompraOrderId() != null) {
                PedidoCompra pedidoCompra = compraOrderRepository.findById(inputDTO.getCompraOrderId())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Pedido de compra com ID %s não encontrado", inputDTO.getCompraOrderId())));
                recebimentoMercadoria.setPedidoCompra(pedidoCompra);
            }

            if (inputDTO.getRecebidoPor() != null) {
                Usuario recebidoPor = usuarioRepository.findById(inputDTO.getRecebidoPor())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getRecebidoPor())));
                if (!Boolean.TRUE.equals(recebidoPor.getAtivo())) {
                    throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getRecebidoPor()));
                }
                recebimentoMercadoria.setRecebidoPor(recebidoPor);
            }

            mercadoriaReciboMapper.updateRecebimentoMercadoriaFromDto(inputDTO, recebimentoMercadoria);
            if (inputDTO.getStatus() != null && !inputDTO.getStatus().isBlank()) {
                recebimentoMercadoria.setStatus(inputDTO.getStatus());
            }
            if (inputDTO.getRecebidoEm() != null) {
                recebimentoMercadoria.setRecebidoEm(inputDTO.getRecebidoEm());
            }

            RecebimentoMercadoria savedRecebimentoMercadoria = mercadoriaReciboRepository.saveAndFlush(recebimentoMercadoria);

            if (inputDTO.getItens() != null) {
                mercadoriaReciboItemService.replaceByRecebimentoMercadoria(savedRecebimentoMercadoria.getId(), inputDTO.getItens());
                savedRecebimentoMercadoria.setItens(mercadoriaReciboItemRepository.findByRecebimentoMercadoriaId(savedRecebimentoMercadoria.getId()));
            }

            return mercadoriaReciboMapper.mercadoriaReciboToRecebimentoMercadoriaResponseDTO(savedRecebimentoMercadoria);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String numeroNota = inputDTO != null && UtilStrings.hasText(inputDTO.getNumeroNota()) ? inputDTO.getNumeroNota() : "";
            throw new ExcecaoNegocio("Erro ao atualizar recebimento de mercadorias " + numeroNota, e);
        }
    }

    public List<RecebimentoMercadoriaResponseDTO> findAll() {
        return mercadoriaReciboMapper.listRecebimentoMercadoriaToListRecebimentoMercadoriaResponseDTO(mercadoriaReciboRepository.findAll());
    }

    public List<RecebimentoMercadoriaResponseDTO> findByFornecedor(UUID fornecedorId) {
        return mercadoriaReciboMapper.listRecebimentoMercadoriaToListRecebimentoMercadoriaResponseDTO(mercadoriaReciboRepository.findByFornecedorId(fornecedorId));
    }

    public List<RecebimentoMercadoriaResponseDTO> findByPedidoCompra(UUID compraOrderId) {
        return mercadoriaReciboMapper.listRecebimentoMercadoriaToListRecebimentoMercadoriaResponseDTO(mercadoriaReciboRepository.findByPedidoCompraId(compraOrderId));
    }

    public RecebimentoMercadoriaResponseDTO find(UUID id) {
        RecebimentoMercadoria entity = mercadoriaReciboRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", id)));
        return mercadoriaReciboMapper.mercadoriaReciboToRecebimentoMercadoriaResponseDTO(entity);
    }

    public void delete(UUID id) {
        try {
            RecebimentoMercadoria recebimentoMercadoria = getObrigatorioEntity(id);
            mercadoriaReciboRepository.delete(recebimentoMercadoria);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir recebimento de mercadorias", e);
        }
    }

    private RecebimentoMercadoria getObrigatorioEntity(UUID id) {
        return mercadoriaReciboRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Recebimento de mercadorias com ID %s não encontrado", id)));
    }

    private void validateCreate(RecebimentoMercadoriaInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new ExcecaoNegocio("O payload do recebimento de mercadorias é obrigatório.");
        }
        if (inputDTO.getFornecedorId() == null) {
            throw new ExcecaoNegocio("O ID do fornecedor é obrigatório.");
        }
        if (inputDTO.getRecebidoPor() == null) {
            throw new ExcecaoNegocio("O usuário responsável pelo recebimento é obrigatório.");
        }
        if (inputDTO.getNumeroNota() == null || inputDTO.getNumeroNota().isBlank()) {
            throw new ExcecaoNegocio("O número da nota fiscal é obrigatório.");
        }
        if (inputDTO.getChaveNota() == null || inputDTO.getChaveNota().isBlank()) {
            throw new ExcecaoNegocio("A chave da nota fiscal é obrigatória.");
        }
        if (inputDTO.getItens() == null || inputDTO.getItens().isEmpty()) {
            throw new ExcecaoNegocio("O recebimento de mercadorias deve conter pelo menos um item.");
        }
    }
}
