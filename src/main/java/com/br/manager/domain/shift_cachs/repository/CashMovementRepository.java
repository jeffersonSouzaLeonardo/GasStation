package com.br.manager.domain.shift_cachs.repository;

import com.br.manager.domain.shift_cachs.entity.CashMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CashMovementRepository extends JpaRepository<CashMovement, UUID> {

    List<CashMovement> findByShiftIdOrderByOccurredAtAsc(UUID shiftId);
}
