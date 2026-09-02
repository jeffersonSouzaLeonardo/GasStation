package com.br.manager.domain.operational.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.dto.TankInputDTO;
import com.br.manager.domain.operational.dto.TankResponseDTO;
import com.br.manager.domain.operational.entity.Fuel;
import com.br.manager.domain.operational.entity.Tank;
import com.br.manager.domain.operational.repository.TankRepository;
import com.br.manager.domain.operational.mapper.TankMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TankService {

    @Autowired
    private TankRepository tankRepository;

    @Autowired
    private TankMapper tankMapper;

    @Autowired
    private FuelService fuelService;

    public TankResponseDTO create(TankInputDTO inputDTO) {
        try {
            Fuel fuel = fuelService.getFuelEntityById(inputDTO.getFuel());

            Tank tank = tankMapper.tankInputToTankEntity(inputDTO);
            tank.setFuel(fuel);
            return tankMapper.tankEntityToTankResponseDTO(tankRepository.saveAndFlush(tank));

        } catch (ConstraintViolationException constraintViolationException) {
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (BusinessException businessException) {
            throw businessException;
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
            Tank tank = tankRepository.findByIdAndDeletedIsNull(inputDTO.getId());

            if (tank == null) {
                throw new NotFoundBusinessException(String.format("Tanque ID: %s não encontrado.", inputDTO.getId()));
            }

            tankMapper.updateTankFromDto(inputDTO, tank);
            Fuel fuel = fuelService.getFuelEntityById(inputDTO.getFuel());
            tank.setFuel(fuel);
            Tank tankSave = tankRepository.saveAndFlush(tank);
            return tankMapper.tankEntityToTankResponseDTO(tankSave);
        } catch (ConstraintViolationException constraintViolationException) {
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (NotFoundBusinessException notFoundBusinessException) {
            throw notFoundBusinessException;
        } catch (Exception e) {
            String fuelName = "";
            if (StringUtils.isNotBlank(inputDTO.getIdentity())) {
                fuelName = inputDTO.getIdentity();
            }
            throw new BusinessException("Erro ao salvar tanque " + fuelName, e);
        }
    }

    public List<TankResponseDTO> findAll() {
        return tankRepository.findAllByDeletedIsNull()
                .stream()
                .map(tankMapper::tankEntityToTankResponseDTO)
                .collect(Collectors.toList());
    }

    public TankResponseDTO find(Long id) {
        Tank tank = tankRepository.findByIdAndDeletedIsNull(id);

        if (tank == null) {
            throw new NotFoundBusinessException(String.format("Tanque com ID:%s não localizado.", id));
        }

        return tankMapper.tankEntityToTankResponseDTO(tank);
    }

    public void delete(Long id) {
        try {
            Tank tank = tankRepository.findById(id).orElseThrow(() -> new NotFoundBusinessException(String.format("Tanque ID:%s não localizado para exclusão.", id)));
            tank.setDeleted(LocalDateTime.now());
            tankRepository.saveAndFlush(tank);
        } catch (ConstraintViolationException constraintViolationException){
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (NotFoundBusinessException notFoundBusinessException){
            throw notFoundBusinessException;
        } catch (Exception e){
            throw new BusinessException("Erro ao deletar tanque ", e);
        }
    }

}
