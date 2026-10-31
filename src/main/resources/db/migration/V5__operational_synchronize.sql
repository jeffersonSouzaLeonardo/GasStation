-- =============================================================================
-- Flyway Migration: V5__operational_synchronize.sql
-- Descrição: Suporte ao sincronismo das tabelas operacionais com o PDV
-- 1. Colunas de auditoria (created_at/updated_at/deleted_at) nas tabelas
--    operacionais, necessárias para a consulta de alterações desde a última
--    sincronização (mesmo padrão da tabela users)
-- 2. Novos valores no ENUM da tabela synchronize + seed das linhas de controle
-- 3. Tabela supplies (abastecimentos recebidos do PDV)
-- =============================================================================

-- 1. Auditoria nas tabelas operacionais ----------------------------------------

ALTER TABLE products
    ADD COLUMN updated_at DATETIME NULL,
    ADD COLUMN deleted_at DATETIME NULL;

ALTER TABLE pumps
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NULL,
    ADD COLUMN deleted_at DATETIME NULL;

ALTER TABLE tanks
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NULL,
    ADD COLUMN deleted_at DATETIME NULL;

ALTER TABLE nozzles
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NULL,
    ADD COLUMN deleted_at DATETIME NULL;

ALTER TABLE suppliers
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NULL,
    ADD COLUMN deleted_at DATETIME NULL;

-- 2. Controle de sincronização -------------------------------------------------

ALTER TABLE synchronize
    MODIFY COLUMN name ENUM('USER', 'COMPANY', 'STATION', 'NOZZLE', 'PRODUCT', 'PUMP', 'TANK', 'SUPPLIER') NOT NULL;

INSERT IGNORE INTO synchronize (name) VALUES
    ('NOZZLE'),
    ('PRODUCT'),
    ('PUMP'),
    ('TANK'),
    ('SUPPLIER');

-- 3. Abastecimentos recebidos do PDV -------------------------------------------

CREATE TABLE IF NOT EXISTS supplies (
    id CHAR(36) PRIMARY KEY,
    data_time DATETIME NOT NULL,
    tank_id CHAR(36) NOT NULL,
    pump_id CHAR(36) NOT NULL,
    volume DECIMAL(15, 4) NOT NULL,
    total_value DECIMAL(15, 4) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_supplies_tank FOREIGN KEY (tank_id) REFERENCES tanks(id),
    CONSTRAINT fk_supplies_pump FOREIGN KEY (pump_id) REFERENCES pumps(id)
);

CREATE INDEX idx_supplies_tank ON supplies(tank_id);
CREATE INDEX idx_supplies_pump ON supplies(pump_id);
