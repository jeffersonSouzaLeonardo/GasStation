package com.br.manager.domain.price_acquisition.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.price_acquisition.dto.PriceTableInputDTO;
import com.br.manager.domain.price_acquisition.dto.PriceTableResponseDTO;
import com.br.manager.domain.price_acquisition.entity.PriceItem;
import com.br.manager.domain.price_acquisition.entity.PriceTable;
import com.br.manager.domain.price_acquisition.mapper.PriceItemMapper;
import com.br.manager.domain.price_acquisition.mapper.PriceTableMapper;
import com.br.manager.domain.price_acquisition.repository.PriceTableRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class PriceTableService {

    @Autowired
    private PriceTableRepository priceTableRepository;

    @Autowired
    private PriceTableMapper priceTableMapper;

    @Autowired
    PriceItemMapper priceItemMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StationRepository stationRepository;

    @Transactional
    public PriceTableResponseDTO create(PriceTableInputDTO inputDTO) {

        if (inputDTO.getPriceItems() == null || inputDTO.getPriceItems().isEmpty()) {
            throw new BusinessException("Price table must have at least one price item");
        }

        try {
            PriceTable priceTable = priceTableMapper.priceTableInputDTOToPriceTable(inputDTO);
            priceTable.setId(null);
            priceTable.setPriceItems(priceItemMapper.listPriceItemInputDTOToListPriceItem(inputDTO.getPriceItems()));
            for (PriceItem item : priceTable.getPriceItems()) {
                item.setPriceTable(priceTable);
            }

           User createdBy = userRepository.findById(inputDTO.getCreatedBy())
                   .orElseThrow(() -> new NotFoundBusinessException(String.format("User with ID %s not found", inputDTO.getCreatedBy())));
           if (!Boolean.TRUE.equals(createdBy.getActive())) {
               throw new NotFoundBusinessException(String.format("User with ID %s not found", inputDTO.getCreatedBy()));
           }
           priceTable.setCreatedBy(createdBy);

            Station station = stationRepository.findById(inputDTO.getStationId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Station with ID %s not found", inputDTO.getStationId())));
              if (!Boolean.TRUE.equals(station.getActive())) {
                  throw new NotFoundBusinessException(String.format("Station with ID %s not found", inputDTO.getStationId()));
              }
            priceTable.setStation(station);
           PriceTable savedPriceTable = priceTableRepository.saveAndFlush(priceTable);
           return priceTableMapper.priceTableToPriceTableResponseDTO(savedPriceTable);
        } catch (ConstraintViolationException exception) {
           throw new BusinessException(exception.getConstraintViolations().stream()
                   .map(v -> v.getMessage())
                   .toList()
                   .toString());
        } catch (NotFoundBusinessException exception) {
           throw exception;
        } catch (Exception e) {
           String priceTableName = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
           throw new BusinessException("Error while saving price Table " + priceTableName, e);
        }
    }

    public PriceTableResponseDTO update(PriceTableInputDTO inputDTO) {
        try {
            PriceTable priceTable = priceTableRepository.findById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price Table with ID %s not found", inputDTO.getId())));
            priceTableMapper.updatePriceTableFromDto(inputDTO, priceTable);
            return priceTableMapper.priceTableToPriceTableResponseDTO(priceTableRepository.saveAndFlush(priceTable));
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String priceTableName = inputDTO != null && StringUtils.hasText(inputDTO.getName()) ? inputDTO.getName() : "";
            throw new BusinessException("Error while saving price Table " + priceTableName, e);
        }
    }

    public List<PriceTableResponseDTO> findAll() {
        return priceTableMapper.listPriceTableToListPriceTableResponseDTO(priceTableRepository.findAll());
    }

    public List<PriceTableResponseDTO> findByDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return findAll();
        }
        return priceTableMapper.listPriceTableToListPriceTableResponseDTO(
                priceTableRepository.findByNameContainingIgnoreCase(description));
    }

    public List<PriceTableResponseDTO> findByName(String name) {
        return findByDescription(name);
    }

    public PriceTableResponseDTO find(UUID id) {
        PriceTable priceTable = priceTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", id)));
        return priceTableMapper.priceTableToPriceTableResponseDTO(priceTable);
    }

    public void delete(UUID id) {
        try {
            PriceTable priceTable = priceTableRepository.findById(id)
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", id)));
            priceTableRepository.delete(priceTable);
        } catch (ConstraintViolationException exception) {
            throw new BusinessException(exception.getConstraintViolations().stream()
                    .map(v -> v.getMessage())
                    .toList()
                    .toString());
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            throw new BusinessException("Error while deleting price table", e);
        }
    }

    public PriceTable getPriceTableEntityById(UUID id) {
        return priceTableRepository.findById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Price table with ID %s not found", id)));
    }
}
