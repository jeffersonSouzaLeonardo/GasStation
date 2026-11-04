package com.br.manager.domain.organizacao.repository;

import com.br.manager.domain.organizacao.entity.VinculoUsuario;
import com.br.manager.domain.organizacao.enums.TipoCargoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VinculoUsuarioRepository extends JpaRepository<VinculoUsuario, UUID> {
    @Query("SELECT v FROM VinculoUsuario v WHERE v.ativo = true")
    List<VinculoUsuario> findAllByAtivoTrue();

    @Query("SELECT v FROM VinculoUsuario v WHERE v.id = :id AND v.ativo = true")
    VinculoUsuario findByIdAndAtivoTrue(UUID id);

    @Query("SELECT v FROM VinculoUsuario v WHERE v.cargo = :cargo AND v.ativo = true")
    List<VinculoUsuario> findByCargoAndAtivoTrue(TipoCargoEnum cargo);

    @Query("SELECT ul FROM VinculoUsuario ul WHERE ul.ativo = true AND LOWER(CAST(ul.cargo AS string)) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<VinculoUsuario> findByCargoTextAndAtivoTrue(@Param("text") String text);
}
