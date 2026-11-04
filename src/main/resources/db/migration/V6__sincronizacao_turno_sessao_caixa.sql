-- =============================================================================
-- Flyway Migration: V6__turno_dinheiro_sessao_sincronizacao.sql
-- Descrição: Suporte ao sincronismo de Turnos (turnos) e Sessões de Caixa
-- (sessoes_caixa) com o PDV
-- 1. Colunas de auditoria (atualizado_em/excluido_em) e coluna de estação em
--    turnos, necessárias para a consulta de alterações e para a regra de
--    turno único por posto (mesmo padrão da migration V5)
-- 2. Novos valores no ENUM da tabela sincronizacao + seed das linhas de contcargo
-- 3. Tabela sessoes_caixa (sessões de caixa recebidas do PDV)
-- =============================================================================

-- 1. Ajustes na tabela turnos --------------------------------------------------

-- estacao_id já é exigido pela entity Turno, mas não existia no schema inicial
ALTER TABLE turnos
    ADD COLUMN estacao_id CHAR(36) NULL AFTER id,
    ADD COLUMN nome VARCHAR(120) NULL,
    ADD COLUMN atualizado_em DATETIME NULL,
    ADD COLUMN excluido_em DATETIME NULL;

CREATE INDEX idx_turnos_estacao_situacao ON turnos(estacao_id, status);

-- 2. Contcargo de sincronização -------------------------------------------------

ALTER TABLE sincronizacao
    MODIFY COLUMN nome ENUM('USUARIO', 'EMPRESA', 'ESTACAO', 'BICO', 'PRODUTO', 'BOMBA', 'TANQUE', 'FORNECEDOR', 'TURNO', 'SESSAO_CAIXA') NOT NULL;

INSERT IGNORE INTO sincronizacao (nome) VALUES
    ('TURNO'),
    ('SESSAO_CAIXA');

-- 3. Sessões de caixa recebidas do PDV -----------------------------------------

CREATE TABLE IF NOT EXISTS sessoes_caixa (
    id CHAR(36) PRIMARY KEY,
    turno_id CHAR(36) NOT NULL,
    caixeiro_usuario_id CHAR(36) NOT NULL,
    cargo ENUM('CAIXA', 'FRENTISTA') NOT NULL,
    fundo_abertura DECIMAL(15, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    aberto_em DATETIME NOT NULL,
    fechado_em DATETIME NULL,
    total_dinheiro DECIMAL(15, 2) NULL,
    total_cartao DECIMAL(15, 2) NULL,
    total_pix DECIMAL(15, 2) NULL,
    total_sangria DECIMAL(15, 2) NULL,
    observacoes TEXT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME NULL,
    excluido_em DATETIME NULL,

    CONSTRAINT fk_sessoes_caixa_turno FOREIGN KEY (turno_id) REFERENCES turnos(id)
);

CREATE INDEX idx_sessoes_caixa_turno ON sessoes_caixa(turno_id);
CREATE INDEX idx_sessoes_caixa_caixeiro_situacao ON sessoes_caixa(caixeiro_usuario_id, status);
