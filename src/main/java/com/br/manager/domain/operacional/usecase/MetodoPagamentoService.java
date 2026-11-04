package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.MetodoPagamentoInputDTO;
import com.br.manager.domain.operacional.dto.MetodoPagamentoResponseDTO;
import com.br.manager.domain.operacional.entity.MetodoPagamento;
import com.br.manager.domain.operacional.mapper.MetodoPagamentoMapper;
import com.br.manager.domain.operacional.repository.MetodoPagamentoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MetodoPagamentoService {
    @Autowired private MetodoPagamentoRepository pagamentoMethodRepository;
    @Autowired private MetodoPagamentoMapper pagamentoMethodMapper;

    public MetodoPagamentoResponseDTO create(MetodoPagamentoInputDTO inputDTO) {
        try {
            MetodoPagamento entity = pagamentoMethodMapper.pagamentoMethodInputDTOToMetodoPagamento(inputDTO);
                        return pagamentoMethodMapper.pagamentoMethodToMetodoPagamentoResponseDTO(pagamentoMethodRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar forma de pagamento", e);
        }
    }

    public MetodoPagamentoResponseDTO update(MetodoPagamentoInputDTO inputDTO) {
        try {
            MetodoPagamento entity = pagamentoMethodRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) { throw new ExcecaoNegocioNaoEncontrado(String.format("Forma de pagamento com ID %s não encontrada", inputDTO.getId())); }
            pagamentoMethodMapper.updateMetodoPagamentoFromDto(inputDTO, entity);
            return pagamentoMethodMapper.pagamentoMethodToMetodoPagamentoResponseDTO(pagamentoMethodRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (ExcecaoNegocioNaoEncontrado e) { throw e; } catch (Exception e) { throw new ExcecaoNegocio("Erro ao atualizar forma de pagamento", e); }
    }

    public List<MetodoPagamentoResponseDTO> findAll() {
        return pagamentoMethodMapper.listMetodoPagamentoToListMetodoPagamentoResponseDTO(pagamentoMethodRepository.findAllByAtivoTrue());
    }

    public List<MetodoPagamentoResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) return findAll();
        return pagamentoMethodMapper.listMetodoPagamentoToListMetodoPagamentoResponseDTO(pagamentoMethodRepository.findByNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public MetodoPagamentoResponseDTO find(UUID id) {
        MetodoPagamento entity = pagamentoMethodRepository.findByIdAndAtivoTrue(id);
        if (entity == null) throw new ExcecaoNegocioNaoEncontrado(String.format("Forma de pagamento com ID %s não encontrada", id));
        return pagamentoMethodMapper.pagamentoMethodToMetodoPagamentoResponseDTO(entity);
    }

    public void delete(UUID id) {
        MetodoPagamento entity = pagamentoMethodRepository.findById(id).orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Forma de pagamento com ID %s não encontrada", id)));
        entity.setAtivo(false);
        pagamentoMethodRepository.saveAndFlush(entity);
    }
}
