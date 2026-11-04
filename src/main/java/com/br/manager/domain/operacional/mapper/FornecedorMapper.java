package com.br.manager.domain.operacional.mapper;

import com.br.manager.domain.operacional.dto.FornecedorInputDTO;
import com.br.manager.domain.operacional.dto.FornecedorResponseDTO;
import com.br.manager.domain.operacional.entity.Fornecedor;
import com.br.manager.domain.comum.UtilStrings;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FornecedorMapper {
    public Fornecedor fornecedorInputDTOToFornecedor(FornecedorInputDTO inputDTO) {
        Fornecedor entity = new Fornecedor();
        entity.setId(inputDTO.getId());
        entity.setLegalName(inputDTO.getLegalName());
        entity.setCnpjCpf(UtilStrings.removeMask(inputDTO.getCnpjCpf()));
        entity.setInscricaoEstadual(UtilStrings.removeMask(inputDTO.getInscricaoEstadual()));
        entity.setEmail(inputDTO.getEmail());
        entity.setTelefone(UtilStrings.removeMask(inputDTO.getTelefone()));
        entity.setRua(inputDTO.getRua());
        entity.setNumeroEndereco(inputDTO.getNumeroEndereco());
        entity.setComplemento(inputDTO.getComplemento());
        entity.setBairro(inputDTO.getBairro());
        entity.setCidade(inputDTO.getCidade());
        entity.setEstado(inputDTO.getEstado());
        entity.setCep(UtilStrings.removeMask(inputDTO.getCep()));
        entity.setAtivo(inputDTO.getAtivo());
        return entity;
    }

    public FornecedorResponseDTO fornecedorToFornecedorResponseDTO(Fornecedor entity) {
        FornecedorResponseDTO dto = new FornecedorResponseDTO();
        dto.setId(entity.getId());
        dto.setLegalName(entity.getLegalName());
        dto.setCnpjCpf(entity.getCnpjCpf());
        dto.setInscricaoEstadual(entity.getInscricaoEstadual());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        dto.setRua(entity.getRua());
        dto.setNumeroEndereco(entity.getNumeroEndereco());
        dto.setComplemento(entity.getComplemento());
        dto.setBairro(entity.getBairro());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        dto.setCep(entity.getCep());
        dto.setAtivo(entity.getAtivo());
        return dto;
    }

    public List<FornecedorResponseDTO> listFornecedorToListFornecedorResponseDTO(List<Fornecedor> entities) {
        return entities.stream().map(this::fornecedorToFornecedorResponseDTO).toList();
    }

    public void updateFornecedorFromDto(FornecedorInputDTO dto, Fornecedor entity) {
        entity.setLegalName(dto.getLegalName());
        entity.setCnpjCpf(UtilStrings.removeMask(dto.getCnpjCpf()));
        entity.setInscricaoEstadual(UtilStrings.removeMask(dto.getInscricaoEstadual()));
        entity.setEmail(dto.getEmail());
        entity.setTelefone(UtilStrings.removeMask(dto.getTelefone()));
        entity.setRua(dto.getRua());
        entity.setNumeroEndereco(dto.getNumeroEndereco());
        entity.setComplemento(dto.getComplemento());
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setCep(UtilStrings.removeMask(dto.getCep()));
        entity.setAtivo(dto.getAtivo());
    }
}
