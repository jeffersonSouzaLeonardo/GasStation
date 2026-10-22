package com.br.manager.domain.organization.repository;

import com.br.manager.domain.organization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByActiveTrue();

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.active = true")
    User findByIdAndActiveTrue(@Param("id") UUID id);

    List<User> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    User findByEmailAndActiveTrue(@Param("email") String email);

    // Busca usuários criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (updatedAt nulo).
    @Query("SELECT u FROM User u WHERE (u.createdAt > :since OR u.updatedAt > :since OR u.deletedAt > :since OR u.updatedAt IS NULL)")
    List<User> findAllChangedSince(@Param("since") LocalDateTime since);
}
