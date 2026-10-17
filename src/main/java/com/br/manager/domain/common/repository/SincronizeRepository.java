package com.br.manager.domain.common.repository;

import com.br.manager.domain.common.entity.Sincronize;
import com.br.manager.domain.common.entity.SincronizeNameTableEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SincronizeRepository extends JpaRepository<Sincronize, UUID> {
    Optional<Sincronize> findByNameTable(SincronizeNameTableEnum nameTable);
}
