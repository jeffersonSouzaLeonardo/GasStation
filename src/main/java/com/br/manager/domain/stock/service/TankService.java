package com.br.manager.domain.stock.service;

import com.br.manager.common.BusinessException;
import com.br.manager.domain.stock.dto.TankInputDTO;
import com.br.manager.domain.stock.dto.TankResponseDTO;
import com.br.manager.domain.stock.entity.Fuel;
import com.br.manager.domain.stock.entity.Tank;
import com.br.manager.domain.stock.repository.FuelRepository;
import com.br.manager.domain.stock.repository.TankRepository;
import com.br.manager.infra.api.stock.mapper.TankMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.ToDoubleBiFunction;
import java.util.stream.Collectors;

@Service
public class TankService {

    @Autowired
    private TankRepository tankRepository;

    @Autowired
    private TankMapper tankMapper;

    @Autowired
    private FuelRepository fuelRepository;

    public TankResponseDTO create(TankInputDTO inputDTO) {
        try {
            Fuel fuel = fuelRepository.findById(inputDTO.getFuel()).orElseThrow(()-> new BusinessException("Combustivel obrigatório."));

            if( fuel == null){
                new BusinessException("Combustivel não localizado!");
            }

            Tank tank = tankMapper.tankInputToTankEntity(inputDTO);
            tank.setFuel(fuel);
            return tankMapper.tankEntityToTankResponseDTO(tankRepository.saveAndFlush(tank));

        } catch (ConstraintViolationException constraintViolationException) {
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (Exception e) {
            String name = "";
            if (StringUtils.isNotBlank(inputDTO.getIdentity())) {
                name = inputDTO.getIdentity();
            }
            throw new BusinessException("Erro ao salvar tanque " + name, e);
        }
    }

    public TankResponseDTO update(TankInputDTO inputDTO) {
        try {
            Tank tank = tankRepository.findById(inputDTO.getId()).orElseThrow(() -> new BusinessException(String.format("Tanque ID: %s não encontrado.", inputDTO.getId())));
            tankMapper.tankInputToTankEntity(inputDTO);

            Tank tankSave = tankRepository.saveAndFlush(tank);
            return tankMapper.tankEntityToTankResponseDTO(tankSave);
        } catch (ConstraintViolationException constraintViolationException) {
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (Exception e) {
            String fuelName = "";
            if (StringUtils.isNotBlank(inputDTO.getIdentity())) {
                fuelName = inputDTO.getIdentity();
            }
            throw new BusinessException("Erro ao salvar tanque " + fuelName, e);
        }
    }

    public List<TankResponseDTO> findAll() {
        return tankRepository.findAll().stream().map(tankMapper::tankEntityToTankResponseDTO).collect(Collectors.toList());
    }

    public TankResponseDTO find(Long id) {
        return tankRepository.findById(id).map(tankMapper::tankEntityToTankResponseDTO).orElseThrow(()-> new BusinessException(String.format("Tanque com ID:%s não localizado.", id)));
    }

    public void delete(Long id) {
        tankRepository.findById(id).ifPresentOrElse(tankRepository::delete, ()-> new BusinessException(String.format("Tanque ID:%s não localizado para exclusão.")));
    }

}
