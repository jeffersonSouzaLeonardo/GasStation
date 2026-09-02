package com.br.manager.domain.organization.repository;

import com.br.manager.domain.organization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByActiveTrue();

    User findByIdAndActiveTrue(UUID id);

    List<User> findByNameContainingIgnoreCaseAndActiveTrue(String name);

    User findByEmailAndActiveTrue(String email);
}
