package com.br.manager.domain.aquisicao_preco.mapper;

import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.aquisicao_preco.dto.ItemPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TabelaPrecoMapper {

    public TabelaPreco precoTabelaInputDTOToTabelaPreco(TabelaPrecoInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        TabelaPreco entity = new TabelaPreco();
        entity.setId(inputDTO.getId());
        entity.setNome(inputDTO.getNome());
        entity.setValidoDe(inputDTO.getValidoDe());
        entity.setValidoAte(inputDTO.getValidoAte());
        entity.setStatus(inputDTO.getStatus());

        if (inputDTO.getCriadoPor() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(inputDTO.getCriadoPor());
            entity.setCriadoPor(usuario);
        }


        return entity;
    }

    public TabelaPrecoResponseDTO precoTabelaToTabelaPrecoResponseDTO(TabelaPreco tabelaPreco) {
        if (tabelaPreco == null) {
            return null;
        }

        TabelaPrecoResponseDTO dto = new TabelaPrecoResponseDTO();
        dto.setId(tabelaPreco.getId());
        dto.setNome(tabelaPreco.getNome());
        dto.setValidoDe(tabelaPreco.getValidoDe() != null ? tabelaPreco.getValidoDe().toString() : null);
        dto.setValidoAte(tabelaPreco.getValidoAte() != null ? tabelaPreco.getValidoAte().toString() : null);
        dto.setStatus(tabelaPreco.getStatus());
        dto.setCriadoPor(tabelaPreco.getCriadoPor() != null ? tabelaPreco.getCriadoPor().getId() : null);

        if (tabelaPreco.getItensPreco() != null) {
            dto.setItensPreco(tabelaPreco.getItensPreco().stream()
                    .filter(Objects::nonNull)
                    .map(this::toItemPrecoResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<TabelaPrecoResponseDTO> listTabelaPrecoToListTabelaPrecoResponseDTO(List<TabelaPreco> tabelasPreco) {
        if (tabelasPreco == null) {
            return null;
        }

        return tabelasPreco.stream()
                .filter(Objects::nonNull)
                .map(this::precoTabelaToTabelaPrecoResponseDTO)
                .collect(Collectors.toList());
    }

    public void updateTabelaPrecoFromDto(TabelaPrecoInputDTO dto, TabelaPreco entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getNome() != null) {
            entity.setNome(dto.getNome());
        }
        if (dto.getValidoDe() != null) {
            entity.setValidoDe(dto.getValidoDe());
        }
        if (dto.getValidoAte() != null) {
            entity.setValidoAte(dto.getValidoAte());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
    }

    private ItemPrecoResponseDTO toItemPrecoResponseDTO(ItemPreco itemPreco) {
        if (itemPreco == null) {
            return null;
        }

        ItemPrecoResponseDTO dto = new ItemPrecoResponseDTO();
        dto.setId(itemPreco.getId());
        dto.setPrecoTabelaId(itemPreco.getTabelaPreco() != null ? itemPreco.getTabelaPreco().getId() : null);
        dto.setProdutoId(itemPreco.getProdutoId());
        dto.setBicoId(itemPreco.getBicoId());
        dto.setPrecoUnitario(itemPreco.getPrecoUnitario());
        dto.setPrecoMinimo(itemPreco.getPrecoMinimo());
        dto.setDescontoMaximoPercentual(itemPreco.getDescontoMaximoPercentual());
        dto.setAtivo(itemPreco.getAtivo());
        return dto;
    }
}
