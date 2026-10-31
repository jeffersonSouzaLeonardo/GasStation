package com.br.manager.support.database;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Set;

/**
 * Limpa as tabelas do schema de testes entre os cenarios, preservando
 * o historico do Flyway e as tabelas de seed (dados de referencia das migrations).
 */
@TestComponent
public class DatabaseCleaner {

    private static final Set<String> PRESERVED_TABLES = Set.of("flyway_schema_history", "synchronize");

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clean() {
        List<String> tables = findTablesToClean();
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        try {
            tables.forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE `" + table + "`"));
            jdbcTemplate.update("UPDATE synchronize SET last_synchronize = NULL");
        } finally {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private List<String> findTablesToClean() {
        return jdbcTemplate.queryForList(
                        "SELECT table_name FROM information_schema.tables "
                                + "WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'",
                        String.class)
                .stream()
                .filter(table -> !PRESERVED_TABLES.contains(table.toLowerCase()))
                .toList();
    }
}
