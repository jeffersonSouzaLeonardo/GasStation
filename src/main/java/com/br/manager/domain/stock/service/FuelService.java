package com.br.manager.domain.stock.service;

import com.br.manager.common.BusinessException;
import com.br.manager.common.NotFoundBusinessException;
import com.br.manager.domain.stock.dto.FuelInputDTO;
import com.br.manager.domain.stock.dto.FuelResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import com.br.manager.domain.stock.repository.FuelRepository;
import com.br.manager.infra.api.stock.mapper.FuelMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

            Fuel fuel = fuelRepository.findByIdAndDeletedIsNull(inputDTO.getId());

            if (fuel == null) {
                throw new NotFoundBusinessException(String.format("ID %s não encontrado", inputDTO.getId()));
            }

            fuelMapper.updateFuelFromDto(inputDTO, fuel);

            Fuel fuelSave = fuelRepository.saveAndFlush(fuel);
            return fuelMapper.fuelEntityToFuelResponseDTO(fuelSave);

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (NotFoundBusinessException notFoundBusinessException){
            throw notFoundBusinessException;
        } catch (Exception e){
            String fuelName = "";
            if( StringUtils.isNotBlank(inputDTO.getName())) {
                fuelName = inputDTO.getName();
            }
            throw new BusinessException("Erro ao salvar combustível " + fuelName, e);
        }
    }


    public List<FuelResponseDTO> findAll(){
        return fuelRepository.findAllByDeletedIsNull()
                .stream()
                .map(fuelMapper::fuelEntityToFuelResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FuelResponseDTO> findByDescription(String description){
        return fuelRepository.findByNameContainingIgnoreCaseAndDeletedIsNull(description)
                .stream()
                .map(fuelMapper::fuelEntityToFuelResponseDTO)
                .collect(Collectors.toList());
    }

    public FuelResponseDTO find(Long id){
        Fuel fuel  = fuelRepository.findByIdAndDeletedIsNull(id);

         if (fuel == null) {
            throw new NotFoundBusinessException(String.format("ID %s não encontrado", id));
        }

         return fuelMapper.fuelEntityToFuelResponseDTO(fuel);
    }

    public void delete(Long id){
        try {

            Fuel fuel = fuelRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("ID %s não encontrado", id)));

            fuel.setDeleted(LocalDateTime.now());
            fuelRepository.saveAndFlush(fuel);

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (NotFoundBusinessException notFoundBusinessException){
            throw notFoundBusinessException;
        } catch (Exception e){
            throw new BusinessException("Erro ao deletar combustível ", e);
        }
    }

    public Fuel getFuelEntityById(Long id){
        Fuel fuel  = fuelRepository.findByIdAndDeletedIsNull(id);

        if (fuel == null) {
            throw new NotFoundBusinessException(String.format("Combustível com ID %s não encontrado", id));
        }

        return fuel;
    }

}
