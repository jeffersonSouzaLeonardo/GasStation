package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    List<PaymentMethod> findAllByActiveTrue();

    PaymentMethod findByIdAndActiveTrue(UUID id);

    List<PaymentMethod> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<PaymentMethod> findByCompanyIdAndActiveTrue(UUID companyId);
}
