package com.br.manager.domain.aquisicao_preco.service;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.entity.Usuario;
import com.br.manager.domain.organizacao.repository.UsuarioRepository;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoInputDTO;
import com.br.manager.domain.aquisicao_preco.dto.TabelaPrecoResponseDTO;
import com.br.manager.domain.aquisicao_preco.entity.ItemPreco;
import com.br.manager.domain.aquisicao_preco.entity.TabelaPreco;
import com.br.manager.domain.aquisicao_preco.mapper.ItemPrecoMapper;
import com.br.manager.domain.aquisicao_preco.mapper.TabelaPrecoMapper;
import com.br.manager.domain.aquisicao_preco.repository.TabelaPrecoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
public class TabelaPrecoService {

    @Autowired
    private TabelaPrecoRepository precoTabelaRepository;

    @Autowired
    private TabelaPrecoMapper precoTabelaMapper;

    @Autowired
    ItemPrecoMapper precoItemMapper;

    @Autowired
    ItemPrecoService precoItemService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Transactional
    public TabelaPrecoResponseDTO create(TabelaPrecoInputDTO inputDTO) {

        if (inputDTO.getItensPreco() == null || inputDTO.getItensPreco().isEmpty()) {
            throw new ExcecaoNegocio("A tabela de preços deve conter pelo menos um item");
        }

        try {
            TabelaPreco tabelaPreco = precoTabelaMapper.precoTabelaInputDTOToTabelaPreco(inputDTO);
            tabelaPreco.setId(null);

           Usuario criadoPor = usuarioRepository.findById(inputDTO.getCriadoPor())
                   .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCriadoPor())));
           if (!Boolean.TRUE.equals(criadoPor.getAtivo())) {
               throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCriadoPor()));
           }
           tabelaPreco.setCriadoPor(criadoPor);

           TabelaPreco savedTabelaPreco = precoTabelaRepository.saveAndFlush(tabelaPreco);
           precoItemService.saveItemPrecos(savedTabelaPreco.getId(), inputDTO.getItensPreco());

           TabelaPrecoResponseDTO response = precoTabelaMapper.precoTabelaToTabelaPrecoResponseDTO(savedTabelaPreco);
           response.setItensPreco(precoItemService.findByTabelaPreco(savedTabelaPreco.getId()));
           return response;
        } catch (ConstraintViolationException exception) {
           throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                   .map(v -> v.getMessage())
                   .toList()
                   .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
           throw exception;
        } catch (Exception e) {
           String precoTabelaName = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
           throw new ExcecaoNegocio("Erro ao salvar tabela de preços " + precoTabelaName, e);
        }
    }

    public TabelaPrecoResponseDTO update(TabelaPrecoInputDTO inputDTO) {
        try {
            TabelaPreco tabelaPreco = precoTabelaRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", inputDTO.getId())));


            if (inputDTO.getCriadoPor() != null) {
                Usuario criadoPor = usuarioRepository.findById(inputDTO.getCriadoPor())
                        .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCriadoPor())));
                if (!Boolean.TRUE.equals(criadoPor.getAtivo())) {
                    throw new ExcecaoNegocioNaoEncontrado(String.format("Usuário com ID %s não encontrado", inputDTO.getCriadoPor()));
                }
                tabelaPreco.setCriadoPor(criadoPor);
            }

            precoTabelaMapper.updateTabelaPrecoFromDto(inputDTO, tabelaPreco);
            TabelaPreco savedTabelaPreco = precoTabelaRepository.saveAndFlush(tabelaPreco);

            if (inputDTO.getItensPreco() != null) {
                precoItemService.replaceByTabelaPreco(savedTabelaPreco, inputDTO.getItensPreco());
            }

            TabelaPrecoResponseDTO response = precoTabelaMapper.precoTabelaToTabelaPrecoResponseDTO(savedTabelaPreco);
            response.setItensPreco(precoItemService.findByTabelaPreco(savedTabelaPreco.getId()));
            return response;
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            String precoTabelaName = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar tabela de preços " + precoTabelaName, e);
        }
    }

    public List<TabelaPrecoResponseDTO> findAll() {
        return precoTabelaMapper.listTabelaPrecoToListTabelaPrecoResponseDTO(precoTabelaRepository.findAll());
    }

    public List<TabelaPrecoResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return precoTabelaMapper.listTabelaPrecoToListTabelaPrecoResponseDTO(
                precoTabelaRepository.findByNameContainingIgnoreCase(descricao));
    }

    public List<TabelaPrecoResponseDTO> findByName(String nome) {
        return findByDescricao(nome);
    }

    public TabelaPrecoResponseDTO find(UUID id) {
        TabelaPreco tabelaPreco = precoTabelaRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", id)));
        return precoTabelaMapper.precoTabelaToTabelaPrecoResponseDTO(tabelaPreco);
    }

    public void delete(UUID id) {
        try {
            TabelaPreco tabelaPreco = precoTabelaRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", id)));
            precoTabelaRepository.delete(tabelaPreco);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado exception) {
            throw exception;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir tabela de preços", e);
        }
    }

    public TabelaPreco getTabelaPrecoEntityById(UUID id) {
        return precoTabelaRepository.findById(id)
                .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Tabela de preços com ID %s não encontrada", id)));
    }
}
