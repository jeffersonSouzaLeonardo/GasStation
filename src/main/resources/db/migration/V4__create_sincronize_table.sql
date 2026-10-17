-- =============================================================================
-- Flyway Migration: V4__create_sincronize_table.sql
-- Descrição: Criação da tabela sincronize
-- =============================================================================

CREATE TABLE IF NOT EXISTS sincronize (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name ENUM('USER', 'COMPANY', 'STATION') NOT NULL,
    last_sincronize DATETIME,

    -- Transversais
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE sincronize
  ADD CONSTRAINT sincronize_name_unique UNIQUE (name);
