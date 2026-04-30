package com.br.manager.domain.stock.repository;

import com.br.manager.domain.stock.entity.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FuelRepository extends JpaRepository<Fuel, Long> {
    List<Fuel> findByNameContainingIgnoreCaseAndDeletedIsNull(String name);

    Fuel findByIdAndDeletedIsNull(Long id);

    List<Fuel> findAllByDeletedIsNull();
}
