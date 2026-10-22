package com.br.manager.domain.common.repository;

import com.br.manager.domain.common.entity.Synchronize;
import com.br.manager.domain.common.entity.SynchronizeNameTableEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SynchronizeRepository extends JpaRepository<Synchronize, UUID> {
    Optional<Synchronize> findByNameTable(SynchronizeNameTableEnum nameTable);

    // Update atômico: evita ler a entidade e segurar lock prolongado na linha sob concorrência.
    @Modifying
    @Query("UPDATE Synchronize s SET s.lastSynchronize = :lastSynchronize WHERE s.nameTable = :nameTable")
    int updateLastSynchronize(@Param("nameTable") SynchronizeNameTableEnum nameTable,
                              @Param("lastSynchronize") LocalDateTime lastSynchronize);
}
