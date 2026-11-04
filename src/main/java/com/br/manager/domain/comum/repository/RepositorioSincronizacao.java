package com.br.manager.domain.comum.repository;

import com.br.manager.domain.comum.entity.Sincronizacao;
import com.br.manager.domain.comum.entity.NomeTabelaSincronizacaoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepositorioSincronizacao extends JpaRepository<Sincronizacao, UUID> {
    @Query("SELECT s FROM Sincronizacao s WHERE s.nomeTabela = :nomeTabela")
    Optional<Sincronizacao> findByNomeTabela(@Param("nomeTabela") NomeTabelaSincronizacaoEnum nomeTabela);

    // Update atômico: evita ler a entidade e segurar lock prolongado na linha sob concorrência.
    @Modifying
    @Query("UPDATE Sincronizacao s SET s.ultimaSincronizacao = :ultimaSincronizacao WHERE s.nomeTabela = :nomeTabela")
    int updateUltimaSincronizacao(@Param("nomeTabela") NomeTabelaSincronizacaoEnum nomeTabela,
                              @Param("ultimaSincronizacao") LocalDateTime ultimaSincronizacao);
}
