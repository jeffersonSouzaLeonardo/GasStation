package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.VinculoUsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.VinculoUsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.VinculoUsuario;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VinculoUsuarioMapper {
    public VinculoUsuario usuarioVinculoInputDTOToVinculoUsuario(VinculoUsuarioInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        VinculoUsuario entity = new VinculoUsuario();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public VinculoUsuarioResponseDTO usuarioVinculoToVinculoUsuarioResponseDTO(VinculoUsuario usuarioVinculo) {
        if (usuarioVinculo == null) {
            return null;
        }
        VinculoUsuarioResponseDTO dto = new VinculoUsuarioResponseDTO();
        BeanUtils.copyProperties(usuarioVinculo, dto);
        return dto;
    }

    public List<VinculoUsuarioResponseDTO> listVinculoUsuarioToListVinculoUsuarioResponseDTO(List<VinculoUsuario> usuarioLinks) {
        if (usuarioLinks == null) {
            return null;
        }
        return usuarioLinks.stream().map(this::usuarioVinculoToVinculoUsuarioResponseDTO).toList();
    }

    public void updateVinculoUsuarioFromDto(VinculoUsuarioInputDTO dto, VinculoUsuario entity) {
        if (dto == null || entity == null) {
            return;
        }
        BeanUtils.copyProperties(dto, entity);
    }
}
