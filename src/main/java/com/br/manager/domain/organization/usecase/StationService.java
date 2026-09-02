package com.br.manager.domain.organization.usecase;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.dto.StationInputDTO;
import com.br.manager.domain.organization.dto.StationResponseDTO;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.mapper.StationMapper;
import com.br.manager.domain.organization.repository.StationRepository;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationMapper stationMapper;

    public StationResponseDTO create(StationInputDTO inputDTO) {
        try {
            Station station = stationMapper.stationInputDTOToStation(inputDTO);
            if (station.getId() == null) {
                station.setId(UUID.randomUUID());
            }
            if (!StringUtils.hasText(station.getTimezone())) {
                station.setTimezone("America/Sao_Paulo");
            }
            return stationMapper.stationToStationResponseDTO(stationRepository.saveAndFlush(station));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (Exception e) {
            String name = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Error while saving station " + name, e);
        }
    }

    public StationResponseDTO update(StationInputDTO inputDTO) {
        try {
            Station station = stationRepository.findByIdAndActiveTrue(inputDTO.getId());
            if (station == null) {
                throw new NotFoundBusinessException(String.format("Station with ID %s not found", inputDTO.getId()));
            }
            stationMapper.updateStationFromDto(inputDTO, station);
            return stationMapper.stationToStationResponseDTO(stationRepository.saveAndFlush(station));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            String name = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Error while saving station " + name, e);
        }
    }

    public List<StationResponseDTO> findAll() {
        return stationMapper.listStationToListStationResponseDTO(stationRepository.findAllByActiveTrue());
    }

    public List<StationResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return stationMapper.listStationToListStationResponseDTO(
                stationRepository.findByNameContainingIgnoreCaseAndActiveTrue(description));
    }

    public List<StationResponseDTO> findByName(String name) {
        return findByDescription(name);
    }

    public StationResponseDTO find(UUID id) {
        Station station = stationRepository.findByIdAndActiveTrue(id);
        if (station == null) {
            throw new NotFoundBusinessException(String.format("Station with ID %s not found", id));
        }
        return stationMapper.stationToStationResponseDTO(station);
    }

    public void delete(UUID id) {
        try {
            Station station = stationRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Station with ID %s not found", id)));
            station.setActive(false);
            stationRepository.saveAndFlush(station);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting station", e);
        }
    }

    public Station getStationEntityById(UUID id) {
        Station station = stationRepository.findByIdAndActiveTrue(id);
        if (station == null) {
            throw new NotFoundBusinessException(String.format("Station with ID %s not found", id));
        }
        return station;
    }
}
