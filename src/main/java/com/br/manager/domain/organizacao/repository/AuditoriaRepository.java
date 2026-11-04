package com.br.manager.domain.organizacao.repository;

import com.br.manager.domain.organizacao.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {
    List<Auditoria> findAllByOrderByCriadoEmDesc();

    @Query("SELECT a FROM Auditoria a WHERE a.entityName LIKE %:text% ORDER BY a.criadoEm DESC")
    List<Auditoria> findByEntityNameContainingIgnoreCaseAndOrderByCriadoEmDesc(@Param("text") String text);
}
