package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    @Query("SELECT c FROM Cliente c WHERE c.ativo = true")
    List<Cliente> findAllByAtivoTrue();

    @Query("SELECT c FROM Cliente c WHERE c.id = :id AND c.ativo = true")
    Cliente findByIdAndAtivoTrue(UUID id);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND c.ativo = true")
    List<Cliente> findByNameContainingIgnoreCaseAndAtivoTrue(String nome);

}
