package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.comum.UtilStrings;
import com.br.manager.domain.operacional.dto.VeiculoClienteInputDTO;
import com.br.manager.domain.operacional.dto.VeiculoClienteResponseDTO;
import com.br.manager.domain.operacional.entity.VeiculoCliente;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VeiculoClienteMapper {
    public VeiculoCliente clienteVeiculoInputDTOToClienteVeiculo(VeiculoClienteInputDTO inputDTO) {
        VeiculoCliente entity = new VeiculoCliente();
        entity.setId(inputDTO.getId());
        entity.setClienteId(inputDTO.getClienteId());
        entity.setPlaca(UtilStrings.removeMask(inputDTO.getPlaca()));
        entity.setMarca(inputDTO.getMarca());
        entity.setModelo(inputDTO.getModelo());
        entity.setCombustivelType(inputDTO.getCombustivelType());
        entity.setAtivo(inputDTO.getAtivo());
        return entity;
    }

    public VeiculoClienteResponseDTO clienteVeiculoToVeiculoClienteResponseDTO(VeiculoCliente entity) {
        VeiculoClienteResponseDTO dto = new VeiculoClienteResponseDTO();
        dto.setId(entity.getId());
        dto.setClienteId(entity.getClienteId());
        dto.setPlaca(entity.getPlaca());
        dto.setMarca(entity.getMarca());
        dto.setModelo(entity.getModelo());
        dto.setCombustivelType(entity.getCombustivelType());
        dto.setAtivo(entity.getAtivo());
        return dto;
    }

    public List<VeiculoClienteResponseDTO> listClienteVeiculoToListVeiculoClienteResponseDTO(List<VeiculoCliente> entities) {
        return entities.stream().map(this::clienteVeiculoToVeiculoClienteResponseDTO).toList();
    }

    public void updateClienteVeiculoFromDto(VeiculoClienteInputDTO dto, VeiculoCliente entity) {
        entity.setClienteId(dto.getClienteId());
        entity.setPlaca(UtilStrings.removeMask(dto.getPlaca()));
        entity.setMarca(dto.getMarca());
        entity.setModelo(dto.getModelo());
        entity.setCombustivelType(dto.getCombustivelType());
        entity.setAtivo(dto.getAtivo());
    }
}
