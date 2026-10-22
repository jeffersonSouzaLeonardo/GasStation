-- =============================================================================
-- Flyway Migration: V4__create_synchronize_table.sql
-- Descrição: Criação da tabela synchronize
-- =============================================================================

CREATE TABLE IF NOT EXISTS synchronize (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name ENUM('USER', 'COMPANY', 'STATION') NOT NULL,
    last_synchronize DATETIME,

    -- Transversais
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE synchronize
  ADD CONSTRAINT synchronize_name_unique UNIQUE (name);
