package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.comum.UtilStrings;
import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.dto.EstacaoResponseDTO;
import com.br.manager.domain.organizacao.entity.Estacao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EstacaoMapper {

    public Estacao estacaoInputDTOToEstacao(EstacaoInputDTO inputDTO) {
        Estacao estacao = new Estacao();
        estacao.setId(inputDTO.getId());
        estacao.setEmpresaId(inputDTO.getEmpresaId());
        estacao.setNome(inputDTO.getNome());
        estacao.setCnpj(UtilStrings.removeMask(inputDTO.getCnpj()));
        estacao.setInscricaoEstadual(UtilStrings.removeMask(inputDTO.getInscricaoEstadual()));
        estacao.setRua(inputDTO.getRua());
        estacao.setNumeroEndereco(inputDTO.getNumeroEndereco());
        estacao.setComplemento(inputDTO.getComplemento());
        estacao.setBairro(inputDTO.getBairro());
        estacao.setCidade(inputDTO.getCidade());
        estacao.setEstado(inputDTO.getEstado());
        estacao.setCep(UtilStrings.removeMask(inputDTO.getCep()));
        estacao.setFusoHorario(inputDTO.getFusoHorario());
        estacao.setAtivo(inputDTO.getAtivo());
        return estacao;
    }

    public EstacaoResponseDTO estacaoToEstacaoResponseDTO(Estacao estacao) {
        EstacaoResponseDTO dto = new EstacaoResponseDTO();
        dto.setId(estacao.getId());
        dto.setEmpresaId(estacao.getEmpresaId());
        dto.setNome(estacao.getNome());
        dto.setCnpj(estacao.getCnpj());
        dto.setInscricaoEstadual(estacao.getInscricaoEstadual());
        dto.setRua(estacao.getRua());
        dto.setNumeroEndereco(estacao.getNumeroEndereco());
        dto.setComplemento(estacao.getComplemento());
        dto.setBairro(estacao.getBairro());
        dto.setCidade(estacao.getCidade());
        dto.setEstado(estacao.getEstado());
        dto.setCep(estacao.getCep());
        dto.setFusoHorario(estacao.getFusoHorario());
        dto.setAtivo(estacao.getAtivo());
        return dto;
    }

    public List<EstacaoResponseDTO> listEstacaoToListEstacaoResponseDTO(List<Estacao> estacoes) {
        return estacoes.stream().map(this::estacaoToEstacaoResponseDTO).toList();
    }

    public void updateEstacaoFromDto(EstacaoInputDTO dto, Estacao entity) {
        entity.setEmpresaId(dto.getEmpresaId());
        entity.setNome(dto.getNome());
        entity.setCnpj(UtilStrings.removeMask(dto.getCnpj()));
        entity.setInscricaoEstadual(UtilStrings.removeMask(dto.getInscricaoEstadual()));
        entity.setRua(dto.getRua());
        entity.setNumeroEndereco(dto.getNumeroEndereco());
        entity.setComplemento(dto.getComplemento());
        entity.setBairro(dto.getBairro());
        entity.setCidade(dto.getCidade());
        entity.setEstado(dto.getEstado());
        entity.setCep(UtilStrings.removeMask(dto.getCep()));
        entity.setFusoHorario(dto.getFusoHorario());
        entity.setAtivo(dto.getAtivo());
    }
}
