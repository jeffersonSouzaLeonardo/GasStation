package com.br.manager.domain.shift_cachs.repository;

import com.br.manager.domain.shift_cachs.entity.Shift;
import com.br.manager.domain.shift_cachs.enums.ShiftStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    Optional<Shift> findByIdAndStatus(UUID id, ShiftStatusEnum status);

    boolean existsByStationIdAndCashierUserIdAndStatus(UUID stationId, UUID cashierUserId, ShiftStatusEnum status);
}
