package com.br.manager.domain.operacional.usecase;

import com.br.manager.domain.comum.exception.ExcecaoNegocio;
import com.br.manager.domain.comum.exception.ExcecaoNegocioNaoEncontrado;
import com.br.manager.domain.operacional.dto.CombustivelInputDTO;
import com.br.manager.domain.operacional.dto.CombustivelResponseDTO;
import com.br.manager.domain.operacional.entity.Combustivel;
import com.br.manager.domain.operacional.repository.CombustivelRepository;
import com.br.manager.domain.operacional.mapper.CombustivelMapper;
import com.br.manager.domain.comum.UtilStrings;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CombustivelService {

    @Autowired
    private CombustivelRepository combustivelRepository;

    @Autowired
    private CombustivelMapper combustivelMapper;

    public CombustivelResponseDTO create(CombustivelInputDTO inputDTO){
        try {
            Combustivel combustivel = combustivelMapper.combustivelInputToCombustivelEntity(inputDTO);
            return combustivelMapper.combustivelEntityToCombustivelResponseDTO(combustivelRepository.saveAndFlush(combustivel));

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validacaoError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new ExcecaoNegocio(validacaoError.toString());
        } catch (Exception e){
            String combustivelName = "";
            if (UtilStrings.isNotVazio(inputDTO.getNome())) {
                combustivelName = inputDTO.getNome();
            }
            throw new ExcecaoNegocio("Erro ao salvar combustível " + combustivelName, e);
        }
    }

    public CombustivelResponseDTO update(CombustivelInputDTO inputDTO){
        try {

            Combustivel combustivel = combustivelRepository.findByIdAndExcluidoIsNull(inputDTO.getId());

            if (combustivel == null) {
                throw new ExcecaoNegocioNaoEncontrado(String.format("Combustível com ID %s não encontrado", inputDTO.getId()));
            }

            combustivelMapper.updateCombustivelFromDto(inputDTO, combustivel);

            Combustivel combustivelSave = combustivelRepository.saveAndFlush(combustivel);
            return combustivelMapper.combustivelEntityToCombustivelResponseDTO(combustivelSave);

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validacaoError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new ExcecaoNegocio(validacaoError.toString());
        } catch (ExcecaoNegocioNaoEncontrado notFoundExcecaoNegocio){
            throw notFoundExcecaoNegocio;
        } catch (Exception e){
            String combustivelName = "";
            if (UtilStrings.isNotVazio(inputDTO.getNome())) {
                combustivelName = inputDTO.getNome();
            }
            throw new ExcecaoNegocio("Erro ao salvar combustível " + combustivelName, e);
        }
    }


    public List<CombustivelResponseDTO> findAll(){
        return combustivelRepository.findAllByExcluidoIsNull()
                .stream()
                .map(combustivelMapper::combustivelEntityToCombustivelResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CombustivelResponseDTO> findByDescricao(String descricao){
        return combustivelRepository.findByNameContainingIgnoreCaseAndExcluidoIsNull(descricao)
                .stream()
                .map(combustivelMapper::combustivelEntityToCombustivelResponseDTO)
                .collect(Collectors.toList());
    }

    public CombustivelResponseDTO find(Long id){
        Combustivel combustivel  = combustivelRepository.findByIdAndExcluidoIsNull(id);

         if (combustivel == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Combustível com ID %s não encontrado", id));
        }

         return combustivelMapper.combustivelEntityToCombustivelResponseDTO(combustivel);
    }

    public void delete(Long id){
        try {

            Combustivel combustivel = combustivelRepository.findById(id)
                    .orElseThrow(() -> new ExcecaoNegocioNaoEncontrado(String.format("Combustível com ID %s não encontrado", id)));

            combustivel.setExcluido(LocalDateTime.now());
            combustivelRepository.saveAndFlush(combustivel);

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validacaoError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new ExcecaoNegocio(validacaoError.toString());
        } catch (ExcecaoNegocioNaoEncontrado notFoundExcecaoNegocio){
            throw notFoundExcecaoNegocio;
        } catch (Exception e){
            throw new ExcecaoNegocio("Erro ao deletar combustível ", e);
        }
    }

    public Combustivel getCombustivelEntityById(Long id){
        Combustivel combustivel  = combustivelRepository.findByIdAndExcluidoIsNull(id);

        if (combustivel == null) {
            throw new ExcecaoNegocioNaoEncontrado(String.format("Combustível com ID %s não encontrado", id));
        }

        return combustivel;
    }

}
