package com.br.manager.domain.shift_cachs.service;

import com.br.manager.domain.common.exception.BusinessException;
import com.br.manager.domain.common.exception.NotFoundBusinessException;
import com.br.manager.domain.operational.entity.Nozzle;
import com.br.manager.domain.operational.entity.Pump;
import com.br.manager.domain.operational.repository.NozzleRepository;
import com.br.manager.domain.operational.repository.PumpRepository;
import com.br.manager.domain.organization.entity.Station;
import com.br.manager.domain.organization.entity.User;
import com.br.manager.domain.organization.repository.StationRepository;
import com.br.manager.domain.organization.repository.UserRepository;
import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import com.br.manager.domain.shift_cachs.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ShiftValidationSupport {

    private final ShiftRepository shiftRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final PumpRepository pumpRepository;
    private final NozzleRepository nozzleRepository;

    public ShiftValidationSupport(
            ShiftRepository shiftRepository,
            StationRepository stationRepository,
            UserRepository userRepository,
            PumpRepository pumpRepository,
            NozzleRepository nozzleRepository) {
        this.shiftRepository = shiftRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.pumpRepository = pumpRepository;
        this.nozzleRepository = nozzleRepository;
    }

    public Shift getRequiredShift(UUID shiftId) {
        return shiftRepository.findById(shiftId)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Turno com ID %s não encontrado", shiftId)));
    }

    public void ensureShiftIsOpen(Shift shift) {
        if (shift.getStatus() == ShiftStatusEnum.CLOSED) {
            throw new BusinessException(String.format("O turno %s está fechado e não pode ser alterado.", shift.getId()));
        }
    }

    public Station getRequiredStation(UUID stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Posto com ID %s não encontrado", stationId)));
        if (!Boolean.TRUE.equals(station.getActive())) {
            throw new NotFoundBusinessException(String.format("Posto com ID %s não encontrado", stationId));
        }
        return station;
    }

    public User getRequiredUser(UUID userId, String fieldName) {
        String field = fieldName == null || fieldName.isBlank() ? "usuário" : fieldName;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado no campo %s", userId, field)));
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new NotFoundBusinessException(String.format("Usuário com ID %s não encontrado no campo %s", userId, field));
        }
        return user;
    }

    public void validateNozzleBelongsToShiftStation(UUID stationId, UUID nozzleId) {
        List<Nozzle> stationNozzles = findNozzlesByStationId(stationId);
        boolean exists = stationNozzles.stream().anyMatch(item -> item.getId().equals(nozzleId));
        if (!exists) {
            throw new BusinessException(String.format("A bomba %s não pertence ao posto %s.", nozzleId, stationId));
        }
    }

    public List<Nozzle> findNozzlesByStationId(UUID stationId) {
        List<Pump> pumps = pumpRepository.findAllByActiveTrue();
        if (pumps.isEmpty()) {
            return List.of();
        }

        Set<UUID> seenNozzles = new HashSet<>();
        List<Nozzle> stationNozzles = new ArrayList<>();

        for (Pump pump : pumps) {
            List<Nozzle> nozzles = nozzleRepository.findByPumpIdAndActiveTrue(pump.getId());
            for (Nozzle nozzle : nozzles) {
                if (seenNozzles.add(nozzle.getId())) {
                    stationNozzles.add(nozzle);
                }
            }
        }

        return stationNozzles;
    }
}
