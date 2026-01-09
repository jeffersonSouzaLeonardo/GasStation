package com.br.manager.domain.stock.service;

import com.br.manager.common.BusinessException;
import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import com.br.manager.domain.stock.repository.FuelRepository;
import com.br.manager.infra.api.stock.mapper.FuelMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FuelService {

    @Autowired
    private FuelRepository fuelRepository;

    @Autowired
    private FuelMapper fuelMapper;

    public FuelResponseDTO create(FuelInputDTO inputDTO){
        try {
            Fuel fuel = fuelMapper.fuelInputToFuelEntity(inputDTO);
            return fuelMapper.fuelEntityToFuelResponseDTO(fuelRepository.saveAndFlush(fuel));

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (Exception e){
            String fuelName = "";
            if( StringUtils.isNotBlank(inputDTO.getName())) {
                fuelName = inputDTO.getName();
            }
            throw new BusinessException("Erro ao salvar combustível " + fuelName, e);
        }
    }

    public FuelResponseDTO update(FuelInputDTO inputDTO){
        try {

            Fuel fuel = fuelRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new BusinessException(String.format("ID %s não encontrado", inputDTO.getId())));

            fuelMapper.updateFuelFromDto(inputDTO, fuel);

            Fuel fuelSave = fuelRepository.saveAndFlush(fuel);
            return fuelMapper.fuelEntityToFuelResponseDTO(fuelSave);

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (Exception e){
            String fuelName = "";
            if( StringUtils.isNotBlank(inputDTO.getName())) {
                fuelName = inputDTO.getName();
            }
            throw new BusinessException("Erro ao salvar combustível " + fuelName, e);
        }
    }


    public List<FuelResponseDTO> findAll(){
        return fuelRepository.findAll()
                .stream()
                .map(fuelMapper::fuelEntityToFuelResponseDTO)
                .collect(Collectors.toList());
    }

    public FuelResponseDTO find(Long id){
        return fuelRepository.findById(id)
                .map(fuelMapper::fuelEntityToFuelResponseDTO)
                .orElseThrow(()-> new BusinessException(String.format("Combustível não encontrado com o ID: %s", id)));
    }

    public void delete(Long id){
        try {
            fuelRepository.findById(id)
                    .ifPresentOrElse(fuelRepository::delete,
                            ()-> {throw new BusinessException("Combustível ID:%s não encontrado para exclusão");});

        } catch (Exception e ){
            throw new BusinessException(String.format("Erro ao excluir o combustível ID: %s", id));
        }
    }

}
