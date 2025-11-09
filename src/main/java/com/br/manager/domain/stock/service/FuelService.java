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

    public FuelResponseDTO save(FuelInputDTO inputDTO){
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

    public List<FuelResponseDTO> findAll(){
        try {

            return fuelMapper.listFuelEntityToListFuelResponseDTO(fuelRepository.findAll());

        } catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

    public FuelResponseDTO find(Long id){
        try {
            return fuelMapper.fuelEntityToFuelResponseDTO(fuelRepository.findById(id).get());
        } catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

    public void delete(Long id){
        try {
            fuelRepository.deleteById(id);
        } catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

}
