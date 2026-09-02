package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.CustomerVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerVehicleRepository extends JpaRepository<CustomerVehicle, UUID> {
    List<CustomerVehicle> findAllByActiveTrue();

    CustomerVehicle findByIdAndActiveTrue(UUID id);

    List<CustomerVehicle> findByPlateContainingIgnoreCaseAndActiveTrue(String plate);

    List<CustomerVehicle> findByCustomerIdAndActiveTrue(UUID customerId);
}
