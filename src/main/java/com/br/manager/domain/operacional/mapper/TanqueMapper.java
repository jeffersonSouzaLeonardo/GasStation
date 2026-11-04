package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.TanqueInputDTO;
import com.br.manager.domain.operacional.dto.TanqueResponseDTO;
import com.br.manager.domain.operacional.entity.Tanque;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TanqueMapper {

    public Tanque tanqueInputToTanqueEntity(TanqueInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        Tanque tanque = new Tanque();
        tanque.setId(inputDTO.getId());
        tanque.setCodigo(inputDTO.getCodigo());
        tanque.setProdutoId(inputDTO.getProdutoId());
        tanque.setCapacidadeLitros(inputDTO.getCapacidadeLitros());
        tanque.setEstoqueMortoLitros(inputDTO.getEstoqueMortoLitros());
        tanque.setSaldoContabilLitros(inputDTO.getSaldoContabilLitros());
        tanque.setAtivo(inputDTO.getAtivo() != null ? inputDTO.getAtivo() : true);
        return tanque;
    }

    public TanqueResponseDTO tanqueEntityToTanqueResponseDTO(Tanque tanque) {
        if (tanque == null) {
            return null;
        }

        TanqueResponseDTO responseDTO = new TanqueResponseDTO();
        responseDTO.setId(tanque.getId());
        responseDTO.setCodigo(tanque.getCodigo());
        responseDTO.setProdutoId(tanque.getProdutoId());
        responseDTO.setCapacidadeLitros(tanque.getCapacidadeLitros());
        responseDTO.setEstoqueMortoLitros(tanque.getEstoqueMortoLitros());
        responseDTO.setSaldoContabilLitros(tanque.getSaldoContabilLitros());
        responseDTO.setAtivo(tanque.getAtivo());
        return responseDTO;
    }

    public void updateTanqueFromDto(TanqueInputDTO inputDTO, Tanque entity) {
        if (inputDTO == null || entity == null) {
            return;
        }

        if (inputDTO.getCodigo() != null) { entity.setCodigo(inputDTO.getCodigo()); }
        if (inputDTO.getProdutoId() != null) { entity.setProdutoId(inputDTO.getProdutoId()); }
        if (inputDTO.getCapacidadeLitros() != null) { entity.setCapacidadeLitros(inputDTO.getCapacidadeLitros()); }
        if (inputDTO.getEstoqueMortoLitros() != null) { entity.setEstoqueMortoLitros(inputDTO.getEstoqueMortoLitros()); }
        if (inputDTO.getSaldoContabilLitros() != null) { entity.setSaldoContabilLitros(inputDTO.getSaldoContabilLitros()); }
        if (inputDTO.getAtivo() != null) { entity.setAtivo(inputDTO.getAtivo()); }
    }

    public List<TanqueResponseDTO> listTanqueEntityToListTanqueResponseDTO(List<Tanque> tanques) {
        if (tanques == null) {
            return null;
        }
        return tanques.stream().map(this::tanqueEntityToTanqueResponseDTO).collect(Collectors.toList());
    }
}
