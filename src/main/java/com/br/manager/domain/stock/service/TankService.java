package com.br.manager.domain.stock.service;

import com.br.manager.common.BusinessException;
import com.br.manager.domain.stock.dto.TankInputDTO;
import com.br.manager.domain.stock.dto.TankResponseDTO;
import com.br.manager.domain.stock.entity.Tank;
import com.br.manager.domain.stock.repository.TankRepository;
import com.br.manager.infra.api.stock.mapper.TankMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TankService {

    @Autowired
    private TankRepository tankRepository;

    @Autowired
    private TankMapper tankMapper;

    public TankResponseDTO save(TankInputDTO inputDTO){
        try {
            Tank tank = tankMapper.tankInputToTankEntity(inputDTO);
            return tankMapper.tankEntityToTankResponseDTO(tankRepository.saveAndFlush(tank));

        } catch (ConstraintViolationException constraintViolationException){
            List<String> validationError = constraintViolationException.getConstraintViolations().stream()
                    .map(v -> v.getMessage().toString())
                    .collect(Collectors.toList());
            throw new BusinessException(validationError.toString());
        } catch (Exception e){
            String name = "";
            if( StringUtils.isNotBlank(inputDTO.getIdentity())) {
                name = inputDTO.getIdentity();
            }
            throw new BusinessException("Erro ao salvar tanque " + name, e);
        }
    }

    public List<TankResponseDTO> findAll(){
        try {

            return tankMapper.listTankEntityToListTankResponseDTO(tankRepository.findAll());

        } catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

    public TankResponseDTO find(Long id){
        try {
            return tankMapper.tankEntityToTankResponseDTO(tankRepository.findById(id).get());
        } catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

    public void delete(Long id){
        try {
            tankRepository.deleteById(id);
        } catch (Exception e ){
            throw new BusinessException(e.getMessage());
        }
    }

}
