package com.br.manager.domain.organization.repository;

import com.br.manager.domain.organization.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StationRepository extends JpaRepository<Station, UUID> {
    List<Station> findAllByActiveTrue();

    Station findByIdAndActiveTrue(UUID id);

    List<Station> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    List<Station> findByCompanyIdAndActiveTrue(UUID companyId);
}
