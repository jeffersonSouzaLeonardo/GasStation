package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.organizacao.dto.UsuarioInputDTO;
import com.br.manager.domain.organizacao.dto.UsuarioResponseDTO;
import com.br.manager.domain.organizacao.entity.Usuario;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioMapper {
    public Usuario usuarioInputDTOToUsuario(UsuarioInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Usuario entity = new Usuario();
        BeanUtils.copyProperties(inputDTO, entity);
        return entity;
    }

    public UsuarioResponseDTO usuarioToUsuarioResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        BeanUtils.copyProperties(usuario, dto);
        return dto;
    }

    public List<UsuarioResponseDTO> listUsuarioToListUsuarioResponseDTO(List<Usuario> usuarios) {
        if (usuarios == null) {
            return null;
        }
        return usuarios.stream().map(this::usuarioToUsuarioResponseDTO).toList();
    }

    public void updateUsuarioFromDto(UsuarioInputDTO dto, Usuario entity) {
        if (dto == null || entity == null) {
            return;
        }
        // Copy only non-null properties from dto to entity to avoid overwriting timestamps unintentionally
        BeanUtils.copyProperties(dto, entity);
    }
}
