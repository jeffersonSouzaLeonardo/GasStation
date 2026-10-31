package com.br.manager.domain.operational.repository;

import com.br.manager.domain.operational.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, UUID> {
}
