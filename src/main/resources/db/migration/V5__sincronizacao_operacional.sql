-- =============================================================================
-- Flyway Migration: V5__operacional_sincronizacao.sql
-- Descrição: Suporte ao sincronismo das tabelas operacionais com o PDV
-- 1. Colunas de auditoria (criado_em/atualizado_em/excluido_em) nas tabelas
--    operacionais, necessárias para a consulta de alterações desde a última
--    sincronização (mesmo padrão da tabela usuarios)
-- 2. Novos valores no ENUM da tabela sincronizacao + seed das linhas de contcargo
-- 3. Tabela abastecimentos (abastecimentos recebidos do PDV)
-- =============================================================================

-- 1. Auditoria nas tabelas operacionais ----------------------------------------

ALTER TABLE produtos
    ADD COLUMN atualizado_em DATETIME NULL,
    ADD COLUMN excluido_em DATETIME NULL;

ALTER TABLE bombas
    ADD COLUMN criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN atualizado_em DATETIME NULL,
    ADD COLUMN excluido_em DATETIME NULL;

ALTER TABLE tanques
    ADD COLUMN criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN atualizado_em DATETIME NULL,
    ADD COLUMN excluido_em DATETIME NULL;

ALTER TABLE bicos
    ADD COLUMN criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN atualizado_em DATETIME NULL,
    ADD COLUMN excluido_em DATETIME NULL;

ALTER TABLE fornecedores
    ADD COLUMN criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN atualizado_em DATETIME NULL,
    ADD COLUMN excluido_em DATETIME NULL;

-- 2. Contcargo de sincronização -------------------------------------------------

ALTER TABLE sincronizacao
    MODIFY COLUMN nome ENUM('USUARIO', 'EMPRESA', 'ESTACAO', 'BICO', 'PRODUTO', 'BOMBA', 'TANQUE', 'FORNECEDOR') NOT NULL;

INSERT IGNORE INTO sincronizacao (nome) VALUES
    ('BICO'),
    ('PRODUTO'),
    ('BOMBA'),
    ('TANQUE'),
    ('FORNECEDOR');

-- 3. Abastecimentos recebidos do PDV -------------------------------------------

CREATE TABLE IF NOT EXISTS abastecimentos (
    id CHAR(36) PRIMARY KEY,
    data_hora DATETIME NOT NULL,
    tanque_id CHAR(36) NOT NULL,
    bomba_id CHAR(36) NOT NULL,
    volume DECIMAL(15, 4) NOT NULL,
    valor_total DECIMAL(15, 4) NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_abastecimentos_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id),
    CONSTRAINT fk_abastecimentos_bomba FOREIGN KEY (bomba_id) REFERENCES bombas(id)
);

CREATE INDEX idx_abastecimentos_tanque ON abastecimentos(tanque_id);
CREATE INDEX idx_abastecimentos_bomba ON abastecimentos(bomba_id);
