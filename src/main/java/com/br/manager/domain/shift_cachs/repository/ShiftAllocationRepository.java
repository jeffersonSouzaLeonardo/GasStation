package com.br.manager.domain.shift_cachs.repository;

import com.br.manager.domain.shift_cachs.entity.ShiftAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftAllocationRepository extends JpaRepository<ShiftAllocation, UUID> {

    List<ShiftAllocation> findByShiftId(UUID shiftId);

    Optional<ShiftAllocation> findByShiftIdAndUserId(UUID shiftId, UUID userId);
}
