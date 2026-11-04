package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.dto.ClienteResponseDTO;
import com.br.manager.domain.operacional.entity.Cliente;
import com.br.manager.domain.operacional.mapper.ClienteMapper;
import com.br.manager.domain.operacional.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClienteService {
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ClienteMapper clienteMapper;

    public ClienteResponseDTO create(ClienteInputDTO inputDTO) {
        try {
            Cliente entity = clienteMapper.clienteInputDTOToCliente(inputDTO);
                        return clienteMapper.clienteToClienteResponseDTO(clienteRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao salvar cliente", e);
        }
    }

    public ClienteResponseDTO update(ClienteInputDTO inputDTO) {
        try {
            Cliente entity = clienteRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (entity == null) { throw new ExcecaoNegocioNaoEncontrado(String.format("Cliente com ID %s não encontrado", inputDTO.getId())); }
            clienteMapper.updateClienteFromDto(inputDTO, entity);
            return clienteMapper.clienteToClienteResponseDTO(clienteRepository.saveAndFlush(entity));
        } catch (ConstraintViolationException e) {
            throw new ExcecaoNegocio(e.getConstraintViolations().stream().map(v -> v.getMessage()).toList().toString());
        } catch (ExcecaoNegocioNaoEncontrado e) { throw e; } catch (Exception e) { throw new ExcecaoNegocio("Erro ao atualizar cliente", e); }
    }

    public List<ClienteResponseDTO> findAll() { return clienteMapper.listClienteToListClienteResponseDTO(clienteRepository.findAllByAtivoTrue()); }

    public List<ClienteResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) return findAll();
        return clienteMapper.listClienteToListClienteResponseDTO(clienteRepository.findByNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public ClienteResponseDTO find(UUID id) {
        Cliente entity = clienteRepository.findByIdAndAtivoTrue(id);
        if (entity == null) throw new ExcecaoNegocioNaoEncontrado(String.format("Cliente com ID %s não encontrado", id));
        return clienteMapper.clienteToClienteResponseDTO(entity);
    }

    public void delete(UUID id) {
        Cliente entity = clienteRepository.findById(id).orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Cliente com ID %s não encontrado", id)));
        entity.setAtivo(false);
        clienteRepository.saveAndFlush(entity);
    }
}
