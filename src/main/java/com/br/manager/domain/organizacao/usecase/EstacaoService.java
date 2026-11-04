package com.br.manager.domain.organizacao.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.organizacao.dto.EstacaoInputDTO;
import com.br.manager.domain.organizacao.dto.EstacaoResponseDTO;
import com.br.manager.domain.organizacao.entity.Estacao;
import com.br.manager.domain.organizacao.mapper.EstacaoMapper;
import com.br.manager.domain.organizacao.repository.EstacaoRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.br.manager.domain.comum.UtilStrings;

import java.util.List;
import java.util.UUID;

@Service
public class EstacaoService {

    @Autowired
    private EstacaoRepository estacaoRepository;

    @Autowired
    private EstacaoMapper estacaoMapper;

    public EstacaoResponseDTO create(EstacaoInputDTO inputDTO) {
        try {
            Estacao estacao = estacaoMapper.estacaoInputDTOToEstacao(inputDTO);
            if (!UtilStrings.hasText(estacao.getFusoHorario())) {
                estacao.setFusoHorario("America/Sao_Paulo");
            }
            return estacaoMapper.estacaoToEstacaoResponseDTO(estacaoRepository.saveAndFlush(estacao));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String nome = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar posto " + nome, e);
        }
    }

    public EstacaoResponseDTO update(EstacaoInputDTO inputDTO) {
        try {
            Estacao estacao = estacaoRepository.findByIdAndAtivoTrue(inputDTO.getId());
            if (estacao == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", inputDTO.getId()));
            }
            estacaoMapper.updateEstacaoFromDto(inputDTO, estacao);
            return estacaoMapper.estacaoToEstacaoResponseDTO(estacaoRepository.saveAndFlush(estacao));
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            String nome = inputDTO != null && UtilStrings.hasText(inputDTO.getNome()) ? inputDTO.getNome() : "";
            throw new ExcecaoNegocio("Erro ao salvar posto " + nome, e);
        }
    }

    public List<EstacaoResponseDTO> findAll() {
        return estacaoMapper.listEstacaoToListEstacaoResponseDTO(estacaoRepository.findAllByAtivoTrue());
    }

    public List<EstacaoResponseDTO> findByDescricao(String descricao) {
        if (!UtilStrings.hasText(descricao)) {
            return findAll();
        }
        return estacaoMapper.listEstacaoToListEstacaoResponseDTO(
                estacaoRepository.findByNameContainingIgnoreCaseAndAtivoTrue(descricao));
    }

    public List<EstacaoResponseDTO> findByName(String nome) {
        return findByDescricao(nome);
    }

    public EstacaoResponseDTO find(UUID id) {
        Estacao estacao = estacaoRepository.findByIdAndAtivoTrue(id);
        if (estacao == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", id));
        }
        return estacaoMapper.estacaoToEstacaoResponseDTO(estacao);
    }

    public void delete(UUID id) {
        try {
            Estacao estacao = estacaoRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", id)));
            estacao.setAtivo(false);
            estacaoRepository.saveAndFlush(estacao);
        } catch (ConstraintViolationException exception) {
            throw new ExcecaoNegocio(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (ExcecaoNegocioNaoEncontrado e) {
            throw e;
        } catch (Exception e) {
            throw new ExcecaoNegocio("Erro ao excluir posto", e);
        }
    }

    public Estacao getEstacaoEntityById(UUID id) {
        Estacao estacao = estacaoRepository.findByIdAndAtivoTrue(id);
        if (estacao == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Posto com ID %s não encontrado", id));
        }
        return estacao;
    }
}
