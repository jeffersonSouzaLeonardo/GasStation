package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.dto.VeiculoClienteResponseDTO;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import com.br.manager.domain.operacional.mapper.VeiculoClienteMapper;
import com.br.manager.domain.operacional.repository.VeiculoClienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VeiculoClienteService {
    @Autowired private VeiculoClienteRepository clienteVeiculoRepository;
    @Autowired private VeiculoClienteMapper clienteVeiculoMapper;

    public VeiculoClienteResponseDTO create(VeiculoClienteInputDTO inputDTO) {
        try {
            VeiculoCliente entity = clienteVeiculoMapper.clienteVeiculoInputDTOToClienteVeiculo(inputDTO);
            return clienteVeiculoMapper.clienteVeiculoToVeiculoClienteResponseDTO(clienteVeiculoRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar veículo do cliente", e);
        }
    }

    public VeiculoClienteResponseDTO update(VeiculoClienteInputDTO inputDTO) {
        try {
            VeiculoCliente entity = clienteVeiculoRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) { throw new ExcecaoNegocioNaoEncontrado(String.format("Veículo do cliente com ID %s não encontrado", inputDTO.getId())); }
            clienteVeiculoMapper.updateClienteVeiculoFromDto(inputDTO, entity);
            return clienteVeiculoMapper.clienteVeiculoToVeiculoClienteResponseDTO(clienteVeiculoRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (ExcecaoNegocioNaoEncontrado e) { throw e; } catch (Exception e) { throw new ExcecaoNegocio("Erro ao atualizar veículo do cliente", e); }
    }

    public List<VeiculoClienteResponseDTO> findAll() { return clienteVeiculoMapper.listClienteVeiculoToListVeiculoClienteResponseDTO(clienteVeiculoRepository.findAllByAtivoTrue()); }

    public List<VeiculoClienteResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) return findAll();
        return clienteVeiculoMapper.listClienteVeiculoToListVeiculoClienteResponseDTO(clienteVeiculoRepository.findByPlacaContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public VeiculoClienteResponseDTO find(UUID id) {
        VeiculoCliente entity = clienteVeiculoRepository.findByIdAndAtivoTrue(id);
        if (entity == null) throw new ExcecaoNegocioNaoEncontrado(String.format("Veículo do cliente com ID %s não encontrado", id));
        return clienteVeiculoMapper.clienteVeiculoToVeiculoClienteResponseDTO(entity);
    }

    public void delete(UUID id) {
        VeiculoCliente entity = clienteVeiculoRepository.findById(id).orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Veículo do cliente com ID %s não encontrado", id)));
        entity.setAtivo(false);
        clienteVeiculoRepository.saveAndFlush(entity);
    }
}
