-- =============================================================================
-- Flyway Migration: V4__create_sincronizacao_tabela.sql
-- Descrição: Criação da tabela sincronizacao
-- =============================================================================

CREATE TABLE IF NOT EXISTS sincronizacao (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    nome ENUM('USUARIO', 'EMPRESA', 'ESTACAO') NOT NULL,
    ultima_sincronizacao DATETIME,

    -- Transversais
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE sincronizacao
  ADD CONSTRAINT sincronizacao_nome_unique UNIQUE (nome);
