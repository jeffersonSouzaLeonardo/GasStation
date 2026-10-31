package com.br.manager.domain.shift_cachs.repository;

import com.br.manager.domain.shift_cachs.entity.CashSession;
import com.br.manager.domain.shift_cachs.enums.CashSessionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CashSessionRepository extends JpaRepository<CashSession, UUID> {

    Optional<CashSession> findByIdAndStatus(UUID id, CashSessionStatusEnum status);

    List<CashSession> findByShift_IdOrderByOpenedAt(UUID shiftId);

    List<CashSession> findByCashierUserIdAndStatus(UUID cashierUserId, CashSessionStatusEnum status);

    boolean existsByCashierUserIdAndStatus(UUID cashierUserId, CashSessionStatusEnum status);
}
