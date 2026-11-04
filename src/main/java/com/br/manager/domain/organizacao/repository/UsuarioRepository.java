package com.br.manager.domain.organizacao.repository;

import com.br.manager.domain.organizacao.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query("SELECT u FROM Usuario u WHERE u.ativo = true")
    List<Usuario> findAllByAtivoTrue();

    @Query("SELECT u FROM Usuario u WHERE u.id = :id AND u.ativo = true")
    Usuario findByIdAndAtivoTrue(@Param("id") UUID id);

    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND u.ativo = true")
    List<Usuario> findByNameContainingIgnoreCaseAndAtivoTrue(String nome);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.ativo = true")
    Usuario findByEmailAndAtivoTrue(@Param("email") String email);

    // Busca usuários criados, atualizados ou deletados após a data informada, ou que nunca foram atualizados (atualizadoEm nulo).
    @Query("SELECT u FROM Usuario u WHERE (( u.atualizadoEm > :since OR u.excluidoEm > :since ) OR ( u.atualizadoEm IS NULL AND u.criadoEm > :since ))")
    List<Usuario> findAllChangedSince(@Param("since") LocalDateTime since);
}
