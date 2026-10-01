package com.br.manager.domain.sale_receipt.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Customer;
import com.br.manager.domain.operational.entity.CustomerVehicle;
import com.br.manager.domain.operational.repository.CustomerRepository;
import com.br.manager.domain.operational.repository.CustomerVehicleRepository;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.sale_receipt.dto.SaleInputDTO;
import com.br.manager.domain.sale_receipt.dto.SaleResponseDTO;
import com.br.manager.domain.sale_receipt.entity.Sale;
import com.br.manager.domain.sale_receipt.entity.SaleItem;
import com.br.manager.domain.sale_receipt.enums.SaleStatus;
import com.br.manager.domain.sale_receipt.mapper.SaleItemMapper;
import com.br.manager.domain.sale_receipt.mapper.SaleMapper;
import com.br.manager.domain.sale_receipt.repository.SaleRepository;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private SaleMapper saleMapper;
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private SaleItemMapper saleItemMapper;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerVehicleRepository customerVehicleRepository;

    public SaleResponseDTO create(SaleInputDTO inputDTO) {
        validateCreate(inputDTO);
        try {
            Sale sale = new Sale();
            fillSale(sale, inputDTO);
            sale.setId(null);
            sale.setStatus(SaleStatus.OPEN);
            sale.setSoldAt(inputDTO.getSoldAt() != null ? inputDTO.getSoldAt() : Instant.now());
            sale.setDiscountAmount(inputDTO.getDiscountAmount() != null ? inputDTO.getDiscountAmount() : BigDecimal.ZERO);

            Sale savedSale = saleRepository.saveAndFlush(sale);
            if (inputDTO.getItems() != null && !inputDTO.getItems().isEmpty()) {
                saleItemService.saveSaleItems(savedSale.getId(), inputDTO.getItems());
            }

            Sale reloaded = saleRepository.findDetailedById(savedSale.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", savedSale.getId())));
            return saleMapper.toResponse(saleRepository.saveAndFlush(reloaded));
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String saleNumber = inputDTO != null && StringUtils.hasText(inputDTO.getSaleNumber()) ? inputDTO.getSaleNumber() : "";
            throw new BusinessException("Erro ao salvar venda " + saleNumber, e);
        }
    }

    public SaleResponseDTO update(SaleInputDTO inputDTO) {
        try {
            Sale sale = saleRepository.findDetailedById(inputDTO.getId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", inputDTO.getId())));
            if (sale.isFinanciallyLocked()) {
                throw new BusinessException("Vendas pagas ou com crédito pendente não podem ser alteradas.");
            }
            fillSale(sale, inputDTO);
            return saleMapper.toResponse(saleRepository.saveAndFlush(sale));
        } catch (NotFoundBusinessException exception) {
            throw exception;
        } catch (Exception e) {
            String saleNumber = inputDTO != null && StringUtils.hasText(inputDTO.getSaleNumber()) ? inputDTO.getSaleNumber() : "";
            throw new BusinessException("Erro ao salvar venda " + saleNumber, e);
        }
    }

    public SaleResponseDTO find(UUID id) {
        return saleMapper.toResponse(saleRepository.findDetailedById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", id))));
    }

    public List<SaleResponseDTO> findAll() {
        return saleRepository.findAll().stream().map(sale -> find(sale.getId())).toList();
    }

    public List<SaleResponseDTO> findByStation(UUID stationId) {
        return saleRepository.findByStationId(stationId).stream().map(sale -> find(sale.getId())).toList();
    }

    public Sale getSaleEntityById(UUID id) {
        return saleRepository.findDetailedById(id)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Venda com ID %s não encontrada", id)));
    }

    private void fillSale(Sale sale, SaleInputDTO inputDTO) {
        if (inputDTO.getStationId() != null) {
            Station station = stationRepository.findById(inputDTO.getStationId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Posto com ID %s não encontrado", inputDTO.getStationId())));
            if (!Boolean.TRUE.equals(station.getActive())) {
                throw new NotFoundBusinessException(String.format("Posto com ID %s não encontrado", inputDTO.getStationId()));
            }
            sale.setStationId(station.getId());
        }

        if (inputDTO.getShiftId() != null) {
            Shift shift = shiftRepository.findById(inputDTO.getShiftId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Turno com ID %s não encontrado", inputDTO.getShiftId())));
            if (shift.getStatus() != ShiftStatusEnum.OPEN) {
                throw new BusinessException(String.format("O turno %s não está ativo.", inputDTO.getShiftId()));
            }
            sale.setShiftId(shift.getId());
        }

        if (inputDTO.getCashierUserId() != null) {
            User cashier = userRepository.findById(inputDTO.getCashierUserId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getCashierUserId())));
            if (!Boolean.TRUE.equals(cashier.getActive())) {
                throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getCashierUserId()));
            }
            sale.setCashierUserId(cashier.getId());
        }

        if (inputDTO.getAttendantUserId() != null) {
            User attendant = userRepository.findById(inputDTO.getAttendantUserId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getAttendantUserId())));
            if (!Boolean.TRUE.equals(attendant.getActive())) {
                throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado", inputDTO.getAttendantUserId()));
            }
            sale.setAttendantUserId(attendant.getId());
        }

        if (inputDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(inputDTO.getCustomerId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Cliente com ID %s não encontrado", inputDTO.getCustomerId())));
            if (!Boolean.TRUE.equals(customer.getActive())) {
                throw new NotFoundBusinessException(String.format("Cliente com ID %s não encontrado", inputDTO.getCustomerId()));
            }
            sale.setCustomerId(customer.getId());
        }

        if (inputDTO.getVehicleId() != null) {
            CustomerVehicle vehicle = customerVehicleRepository.findById(inputDTO.getVehicleId())
                    .orElseThrow(() -> new NotFoundBusinessException(String.format("Veículo com ID %s não encontrado", inputDTO.getVehicleId())));
            if (!Boolean.TRUE.equals(vehicle.getActive())) {
                throw new NotFoundBusinessException(String.format("Veículo com ID %s não encontrado", inputDTO.getVehicleId()));
            }
            if (sale.getCustomerId() != null && !vehicle.getCustomerId().equals(sale.getCustomerId())) {
                throw new BusinessException("O veículo não pertence ao cliente informado.");
            }
            sale.setVehicleId(vehicle.getId());
        }

        if (inputDTO.getSaleNumber() != null) {
            sale.setSaleNumber(inputDTO.getSaleNumber());
        }
        if (inputDTO.getSoldAt() != null) {
            sale.setSoldAt(inputDTO.getSoldAt());
        }
        if (inputDTO.getDiscountAmount() != null) {
            sale.setDiscountAmount(inputDTO.getDiscountAmount());
        }
        if (inputDTO.getFiscalStatus() != null) {
            sale.setFiscalStatus(inputDTO.getFiscalStatus());
        }
        if (inputDTO.getNotes() != null) {
            sale.setNotes(inputDTO.getNotes());
        }
    }

    private void validateCreate(SaleInputDTO inputDTO) {
        if (inputDTO == null) {
            throw new BusinessException("O payload da venda é obrigatório.");
        }
        if (inputDTO.getStationId() == null) {
            throw new BusinessException("O ID do posto é obrigatório.");
        }
        if (inputDTO.getShiftId() == null) {
            throw new BusinessException("O ID do turno é obrigatório.");
        }
        if (inputDTO.getCashierUserId() == null) {
            throw new BusinessException("O ID do usuário caixa é obrigatório.");
        }
        if (!StringUtils.hasText(inputDTO.getSaleNumber())) {
            throw new BusinessException("O número da venda é obrigatório.");
        }
        if (saleRepository.existsByStationIdAndSaleNumber(inputDTO.getStationId(), inputDTO.getSaleNumber())) {
            throw new BusinessException(String.format("A venda %s já existe para o posto %s.", inputDTO.getSaleNumber(), inputDTO.getStationId()));
        }
        if (inputDTO.getItems() == null || inputDTO.getItems().isEmpty()) {
            throw new BusinessException("A venda deve conter pelo menos um item.");
        }
    }

}
