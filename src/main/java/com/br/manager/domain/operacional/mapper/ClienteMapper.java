package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.ClienteInputDTO;
import com.br.manager.domain.operacional.dto.ClienteResponseDTO;
import com.br.manager.domain.operacional.entity.Cliente;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClienteMapper {
    public Cliente clienteInputDTOToCliente(ClienteInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Cliente entity = new Cliente();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public ClienteResponseDTO clienteToClienteResponseDTO(Cliente entity) {
        if (entity == null) {
            return null;
        }
        ClienteResponseDTO dto = new ClienteResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<ClienteResponseDTO> listClienteToListClienteResponseDTO(List<Cliente> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::clienteToClienteResponseDTO).toList();
    }

    public void updateClienteFromDto(ClienteInputDTO dto, Cliente entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
