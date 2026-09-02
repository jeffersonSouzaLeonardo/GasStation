package com.br.manager.domain.organization.repository;

import com.br.manager.domain.organization.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<Audit, UUID> {
    List<Audit> findAllByOrderByCreatedAtDesc();

    Audit findByIdAndCompanyId(UUID id, UUID companyId);

    @Query("SELECT a FROM Audit a WHERE a.entityName LIKE %:text% ORDER BY a.createdAt DESC")
    List<Audit> findByEntityNameContainingIgnoreCaseAndOrderByCreatedAtDesc(@Param("text") String text);
}
