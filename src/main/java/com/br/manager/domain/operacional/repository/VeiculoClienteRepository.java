package com.br.manager.domain.operacional.repository;

import com.br.manager.domain.operacional.entity.VeiculoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VeiculoClienteRepository extends JpaRepository<VeiculoCliente, UUID> {
    @Query("SELECT v FROM VeiculoCliente v WHERE v.ativo = true")
    List<VeiculoCliente> findAllByAtivoTrue();

    @Query("SELECT v FROM VeiculoCliente v WHERE v.id = :id AND v.ativo = true")
    VeiculoCliente findByIdAndAtivoTrue(UUID id);

    @Query("SELECT v FROM VeiculoCliente v WHERE LOWER(v.placa) LIKE LOWER(CONCAT('%', :placa, '%')) AND v.ativo = true")
    List<VeiculoCliente> findByPlacaContainingIgnoreCaseAndAtivoTrue(String placa);

}
