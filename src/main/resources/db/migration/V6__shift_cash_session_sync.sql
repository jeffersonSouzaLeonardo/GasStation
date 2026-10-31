-- =============================================================================
-- Flyway Migration: V6__shift_cash_session_sync.sql
-- Descrição: Suporte ao sincronismo de Turnos (shifts) e Sessões de Caixa
-- (cash_sessions) com o PDV
-- 1. Colunas de auditoria (updated_at/deleted_at) e coluna de estação em
--    shifts, necessárias para a consulta de alterações e para a regra de
--    turno único por posto (mesmo padrão da migration V5)
-- 2. Novos valores no ENUM da tabela synchronize + seed das linhas de controle
-- 3. Tabela cash_sessions (sessões de caixa recebidas do PDV)
-- =============================================================================

-- 1. Ajustes na tabela shifts --------------------------------------------------

-- station_id já é exigido pela entity Shift, mas não existia no schema inicial
ALTER TABLE shifts
    ADD COLUMN station_id CHAR(36) NULL AFTER id,
    ADD COLUMN name VARCHAR(120) NULL,
    ADD COLUMN updated_at DATETIME NULL,
    ADD COLUMN deleted_at DATETIME NULL;

CREATE INDEX idx_shifts_station_status ON shifts(station_id, status);

-- 2. Controle de sincronização -------------------------------------------------

ALTER TABLE synchronize
    MODIFY COLUMN name ENUM('USER', 'COMPANY', 'STATION', 'NOZZLE', 'PRODUCT', 'PUMP', 'TANK', 'SUPPLIER', 'SHIFT', 'CASH_SESSION') NOT NULL;

INSERT IGNORE INTO synchronize (name) VALUES
    ('SHIFT'),
    ('CASH_SESSION');

-- 3. Sessões de caixa recebidas do PDV -----------------------------------------

CREATE TABLE IF NOT EXISTS cash_sessions (
    id CHAR(36) PRIMARY KEY,
    shift_id CHAR(36) NOT NULL,
    cashier_user_id CHAR(36) NOT NULL,
    role ENUM('CASHIER', 'PUMP_ATTENDANT') NOT NULL,
    opening_cash DECIMAL(15, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    opened_at DATETIME NOT NULL,
    closed_at DATETIME NULL,
    total_cash DECIMAL(15, 2) NULL,
    total_card DECIMAL(15, 2) NULL,
    total_pix DECIMAL(15, 2) NULL,
    total_withdrawal DECIMAL(15, 2) NULL,
    notes TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL,
    deleted_at DATETIME NULL,

    CONSTRAINT fk_cash_sessions_shift FOREIGN KEY (shift_id) REFERENCES shifts(id)
);

CREATE INDEX idx_cash_sessions_shift ON cash_sessions(shift_id);
CREATE INDEX idx_cash_sessions_cashier_status ON cash_sessions(cashier_user_id, status);
