package com.br.manager.domain.shift_cachs.repository;

import com.br.manager.domain.shift_cachs.entity.PaymentClosing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentClosingRepository extends JpaRepository<PaymentClosing, UUID> {

    List<PaymentClosing> findByShiftIdOrderByPaymentMethodId(UUID shiftId);
}
