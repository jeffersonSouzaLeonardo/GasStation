package com.br.manager.domain.organizacao.mapper;

import com.br.manager.domain.comum.UtilStrings;
import com.br.manager.domain.organizacao.dto.EmpresaInputDTO;
import com.br.manager.domain.organizacao.dto.EmpresaResponseDTO;
import com.br.manager.domain.organizacao.entity.Empresa;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmpresaMapper {

    public Empresa empresaInputDTOToEmpresa(EmpresaInputDTO inputDTO) {
        Empresa empresa = new Empresa();
        empresa.setId(inputDTO.getId());
        empresa.setLegalName(inputDTO.getLegalName());
        empresa.setTradeName(inputDTO.getTradeName());
        empresa.setCnpj(UtilStrings.removeMask(inputDTO.getCnpj()));
        empresa.setInscricaoEstadual(UtilStrings.removeMask(inputDTO.getInscricaoEstadual()));
        empresa.setTaxRegime(inputDTO.getTaxRegime());
        empresa.setEmail(inputDTO.getEmail());
        empresa.setTelefone(UtilStrings.removeMask(inputDTO.getTelefone()));
        empresa.setAtivo(inputDTO.getAtivo());
        return empresa;
    }

    public EmpresaResponseDTO empresaToEmpresaResponseDTO(Empresa empresa) {
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setId(empresa.getId());
        dto.setLegalName(empresa.getLegalName());
        dto.setTradeName(empresa.getTradeName());
        dto.setCnpj(empresa.getCnpj());
        dto.setInscricaoEstadual(empresa.getInscricaoEstadual());
        dto.setTaxRegime(empresa.getTaxRegime());
        dto.setEmail(empresa.getEmail());
        dto.setTelefone(empresa.getTelefone());
        dto.setAtivo(empresa.getAtivo());
        return dto;
    }

    public List<EmpresaResponseDTO> listEmpresaToListEmpresaResponseDTO(List<Empresa> empresas) {
        return empresas.stream().map(this::empresaToEmpresaResponseDTO).toList();
    }

    public void updateEmpresaFromDto(EmpresaInputDTO dto, Empresa entity) {
        entity.setLegalName(dto.getLegalName());
        entity.setTradeName(dto.getTradeName());
        entity.setCnpj(UtilStrings.removeMask(dto.getCnpj()));
        entity.setInscricaoEstadual(UtilStrings.removeMask(dto.getInscricaoEstadual()));
        entity.setTaxRegime(dto.getTaxRegime());
        entity.setEmail(dto.getEmail());
        entity.setTelefone(UtilStrings.removeMask(dto.getTelefone()));
        entity.setAtivo(dto.getAtivo());
    }
}
