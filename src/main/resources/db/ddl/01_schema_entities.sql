-- =============================================================================
-- DDL MySQL consolidado das entities JPA do GasStation
-- Fonte: src/main/java/com/br/manager/domain/**/entity
--
-- Nao e migration Flyway. Rode manualmente no schema desejado.
-- Tabelas do V1 sem entity (movimentos_estoque, medicoes_tanque, inventarios,
-- contas_receber, recebimentos_financeiros, contas_pagar, vendas legada) ficam
-- de fora de proposito.
--
-- MySQL 8+. UUIDs em CHAR(36). Enums JPA (EnumType.STRING) em VARCHAR.
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reembolso_venda;
DROP TABLE IF EXISTS pagamento_venda;
DROP TABLE IF EXISTS item_venda;
DROP TABLE IF EXISTS venda;
DROP TABLE IF EXISTS encerramentos_pagamento;
DROP TABLE IF EXISTS movimentos_caixa;
DROP TABLE IF EXISTS leituras_bico;
DROP TABLE IF EXISTS sessoes_caixa;
DROP TABLE IF EXISTS alocacoes_turno;
DROP TABLE IF EXISTS turnos;
DROP TABLE IF EXISTS abastecimentos;
DROP TABLE IF EXISTS itens_recebimento_mercadorias;
DROP TABLE IF EXISTS recebimentos_mercadorias;
DROP TABLE IF EXISTS itens_pedido_compra;
DROP TABLE IF EXISTS pedidos_compra;
DROP TABLE IF EXISTS itens_preco;
DROP TABLE IF EXISTS tabelas_preco;
DROP TABLE IF EXISTS veiculos_cliente;
DROP TABLE IF EXISTS bicos;
DROP TABLE IF EXISTS estacao_produtos;
DROP TABLE IF EXISTS tanques;
DROP TABLE IF EXISTS bombas;
DROP TABLE IF EXISTS combustivel;
DROP TABLE IF EXISTS logs_auditoria;
DROP TABLE IF EXISTS usuario_estacao_cargos;
DROP TABLE IF EXISTS sincronizacao;
DROP TABLE IF EXISTS metodos_pagamento;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS fornecedores;
DROP TABLE IF EXISTS produtos;
DROP TABLE IF EXISTS estacoes;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS empresas;

SET FOREIGN_KEY_CHECKS = 1;

-- -----------------------------------------------------------------------------
-- Organizacao
-- Empresa, Estacao, Usuario, VinculoUsuario, Auditoria
-- -----------------------------------------------------------------------------

CREATE TABLE empresas (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    razao_social VARCHAR(255) NOT NULL,
    nome_fantasia VARCHAR(255) NULL,
    cnpj VARCHAR(14) NOT NULL,
    inscricao_estadual VARCHAR(30) NULL,
    regime_tributario VARCHAR(50) NULL,
    email VARCHAR(255) NULL,
    telefone VARCHAR(20) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_empresas_cnpj UNIQUE (cnpj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE estacoes (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    empresa_id CHAR(36) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NULL,
    inscricao_estadual VARCHAR(30) NULL,
    rua VARCHAR(255) NULL,
    numero VARCHAR(255) NULL,
    complemento VARCHAR(255) NULL,
    bairro VARCHAR(255) NULL,
    cidade VARCHAR(255) NULL,
    estado VARCHAR(2) NULL,
    cep VARCHAR(10) NULL,
    fusoHorario VARCHAR(255) NOT NULL DEFAULT 'America/Sao_Paulo',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_estacoes_empresa FOREIGN KEY (empresa_id) REFERENCES empresas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuarios (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    hash_senha VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_login_em DATETIME(6) NULL,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL,
    CONSTRAINT uk_usuarios_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE usuario_estacao_cargos (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36) NOT NULL,
    empresa_id CHAR(36) NOT NULL,
    estacao_id CHAR(36) NULL,
    cargo VARCHAR(30) NOT NULL COMMENT 'ADMIN, GERENTE, CAIXA, FRENTISTA',
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_usr_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_usr_empresa FOREIGN KEY (empresa_id) REFERENCES empresas (id),
    CONSTRAINT fk_usr_estacao FOREIGN KEY (estacao_id) REFERENCES estacoes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE logs_auditoria (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36) NULL,
    nome_entidade VARCHAR(100) NOT NULL,
    entidade_id VARCHAR(100) NOT NULL,
    acao VARCHAR(50) NOT NULL,
    dados_anteriores JSON NULL,
    dados_posteriores JSON NULL,
    endereco_ip VARCHAR(45) NULL,
    criado_em DATETIME(6) NOT NULL,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Comum
-- Sincronizacao (NomeTabelaSincronizacaoEnum)
-- -----------------------------------------------------------------------------

CREATE TABLE sincronizacao (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    nome VARCHAR(30) NOT NULL COMMENT 'USUARIO, EMPRESA, ESTACAO, BICO, PRODUTO, BOMBA, TANQUE, FORNECEDOR, TURNO, SESSAO_CAIXA',
    ultima_sincronizacao DATETIME(6) NULL,
    CONSTRAINT uk_sincronizacao_nome UNIQUE (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Operacional
-- -----------------------------------------------------------------------------

CREATE TABLE produtos (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    sku VARCHAR(100) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    tipo_produto VARCHAR(30) NOT NULL COMMENT 'COMBUSTIVEL, LUBRIFICANTE, SERVICO, CONVENIENCIA',
    unidade VARCHAR(10) NOT NULL COMMENT 'L, UN, KG, M3',
    anp_codigo VARCHAR(20) NULL,
    ncm VARCHAR(10) NULL,
    cest VARCHAR(10) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE combustivel (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    unidade VARCHAR(20) NOT NULL COMMENT 'LITROS, METROS',
    status VARCHAR(20) NOT NULL COMMENT 'ATIVO, INATIVO',
    id_anp VARCHAR(255) NULL,
    excluido DATETIME(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE estacao_produtos (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    produto_id CHAR(36) NOT NULL,
    estoque_minimo DECIMAL(15, 4) NULL DEFAULT 0,
    ponto_reposicao DECIMAL(15, 4) NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_sp_produto FOREIGN KEY (produto_id) REFERENCES produtos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE tanques (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    codigo VARCHAR(50) NOT NULL,
    produto_id CHAR(36) NOT NULL,
    capacidade_litros DECIMAL(15, 4) NOT NULL,
    estoque_morto_litros DECIMAL(15, 4) NULL DEFAULT 0,
    saldo_contabil_litros DECIMAL(15, 4) NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL,
    CONSTRAINT fk_tanques_produto FOREIGN KEY (produto_id) REFERENCES produtos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bombas (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    codigo VARCHAR(50) NOT NULL,
    localizacao VARCHAR(100) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bicos (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    bomba_id CHAR(36) NOT NULL,
    tanque_id CHAR(36) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    produto_id CHAR(36) NOT NULL,
    numero_medidor DECIMAL(15, 4) NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL,
    CONSTRAINT uk_bico_bomba_codigo UNIQUE (bomba_id, codigo),
    CONSTRAINT fk_bicos_bomba FOREIGN KEY (bomba_id) REFERENCES bombas (id),
    CONSTRAINT fk_bicos_tanque FOREIGN KEY (tanque_id) REFERENCES tanques (id),
    CONSTRAINT fk_bicos_produto FOREIGN KEY (produto_id) REFERENCES produtos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE fornecedores (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    razao_social VARCHAR(255) NOT NULL,
    cnpj_cpf VARCHAR(14) NULL,
    inscricao_estadual VARCHAR(30) NULL,
    email VARCHAR(255) NULL,
    telefone VARCHAR(20) NULL,
    rua VARCHAR(255) NULL,
    numero VARCHAR(20) NULL,
    complemento VARCHAR(100) NULL,
    bairro VARCHAR(100) NULL,
    cidade VARCHAR(100) NULL,
    estado VARCHAR(2) NULL,
    cep VARCHAR(10) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE clientes (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    nome VARCHAR(255) NOT NULL,
    tipo_pessoa VARCHAR(20) NOT NULL COMMENT 'FISICA, JURIDICA',
    cnpj_cpf VARCHAR(14) NULL,
    inscricao_estadual VARCHAR(30) NULL,
    email VARCHAR(255) NULL,
    telefone VARCHAR(20) NULL,
    rua VARCHAR(255) NULL,
    numero VARCHAR(20) NULL,
    complemento VARCHAR(100) NULL,
    bairro VARCHAR(100) NULL,
    cidade VARCHAR(100) NULL,
    estado VARCHAR(2) NULL,
    cep VARCHAR(10) NULL,
    limite_credito DECIMAL(15, 2) NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE veiculos_cliente (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    cliente_id CHAR(36) NOT NULL,
    placa VARCHAR(10) NOT NULL,
    marca VARCHAR(50) NULL,
    modelo VARCHAR(50) NULL,
    tipo_combustivel VARCHAR(30) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_veiculos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE metodos_pagamento (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL COMMENT 'DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX, CONVENIO, TRANSFERENCIA_BANCARIA, CHEQUE, OUTRO',
    exige_referencia BOOLEAN NOT NULL DEFAULT FALSE,
    dias_liquidacao INT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE abastecimentos (
    id CHAR(36) NOT NULL PRIMARY KEY,
    data_hora DATETIME(6) NOT NULL,
    tanque_id CHAR(36) NOT NULL,
    bomba_id CHAR(36) NOT NULL,
    volume DECIMAL(15, 4) NOT NULL,
    valor_total DECIMAL(15, 4) NOT NULL,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_abastecimentos_tanque FOREIGN KEY (tanque_id) REFERENCES tanques (id),
    CONSTRAINT fk_abastecimentos_bomba FOREIGN KEY (bomba_id) REFERENCES bombas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Aquisicao e preco
-- -----------------------------------------------------------------------------

CREATE TABLE tabelas_preco (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    nome VARCHAR(255) NOT NULL,
    valido_de DATETIME(6) NOT NULL,
    valido_ate DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL,
    criado_por CHAR(36) NOT NULL,
    CONSTRAINT fk_pt_usuario FOREIGN KEY (criado_por) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE itens_preco (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    tabela_preco_id CHAR(36) NOT NULL,
    produto_id CHAR(36) NOT NULL,
    bico_id CHAR(36) NULL,
    preco_unitario DECIMAL(10, 4) NOT NULL,
    preco_minimo DECIMAL(10, 4) NULL,
    desconto_maximo_percentual DECIMAL(5, 2) NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_pi_tabela FOREIGN KEY (tabela_preco_id) REFERENCES tabelas_preco (id),
    CONSTRAINT fk_pi_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT fk_pi_bico FOREIGN KEY (bico_id) REFERENCES bicos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pedidos_compra (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    fornecedor_id CHAR(36) NOT NULL,
    numero VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    pedido_em DATETIME(6) NOT NULL,
    esperado_em DATETIME(6) NOT NULL,
    criado_por CHAR(36) NOT NULL,
    CONSTRAINT uk_pedidos_compra_numero UNIQUE (numero),
    CONSTRAINT fk_po_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores (id),
    CONSTRAINT fk_po_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE itens_pedido_compra (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    pedido_compra_id CHAR(36) NOT NULL,
    produto_id CHAR(36) NOT NULL,
    quantidade INT NOT NULL,
    custo_unitario DECIMAL(19, 4) NOT NULL,
    CONSTRAINT fk_poi_order FOREIGN KEY (pedido_compra_id) REFERENCES pedidos_compra (id),
    CONSTRAINT fk_poi_produto FOREIGN KEY (produto_id) REFERENCES produtos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE recebimentos_mercadorias (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    fornecedor_id CHAR(36) NOT NULL,
    pedido_compra_id CHAR(36) NULL,
    numero_nota VARCHAR(255) NOT NULL,
    chave_nota VARCHAR(255) NOT NULL,
    recebido_em DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL,
    valor_frete DECIMAL(19, 4) NOT NULL,
    outros_custos DECIMAL(19, 4) NOT NULL,
    recebido_por CHAR(36) NOT NULL,
    CONSTRAINT fk_gr_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores (id),
    CONSTRAINT fk_gr_po FOREIGN KEY (pedido_compra_id) REFERENCES pedidos_compra (id),
    CONSTRAINT fk_gr_recebido_por FOREIGN KEY (recebido_por) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE itens_recebimento_mercadorias (
    id CHAR(36) NOT NULL PRIMARY KEY DEFAULT (UUID()),
    recebimento_mercadoria_id CHAR(36) NOT NULL,
    produto_id CHAR(36) NOT NULL,
    tanque_id CHAR(36) NULL,
    quantidade DECIMAL(19, 4) NOT NULL,
    custo_unitario DECIMAL(19, 4) NOT NULL,
    CONSTRAINT fk_gri_recibo FOREIGN KEY (recebimento_mercadoria_id) REFERENCES recebimentos_mercadorias (id),
    CONSTRAINT fk_gri_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT fk_gri_tanque FOREIGN KEY (tanque_id) REFERENCES tanques (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Turno e caixa
-- -----------------------------------------------------------------------------

CREATE TABLE turnos (
    id CHAR(36) NOT NULL PRIMARY KEY,
    estacao_id CHAR(36) NOT NULL,
    caixeiro_usuario_id CHAR(36) NOT NULL,
    aberto_em DATETIME(6) NOT NULL,
    fechado_em DATETIME(6) NULL,
    fundo_abertura DECIMAL(18, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL COMMENT 'ABERTO, FECHADO',
    aberto_por CHAR(36) NOT NULL,
    fechado_por CHAR(36) NULL,
    nome VARCHAR(120) NULL,
    observacoes VARCHAR(500) NULL,
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL,
    CONSTRAINT fk_turnos_estacao FOREIGN KEY (estacao_id) REFERENCES estacoes (id),
    CONSTRAINT fk_turnos_caixeiro FOREIGN KEY (caixeiro_usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_turnos_aberto_por FOREIGN KEY (aberto_por) REFERENCES usuarios (id),
    CONSTRAINT fk_turnos_fechado_por FOREIGN KEY (fechado_por) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE alocacoes_turno (
    id CHAR(36) NOT NULL PRIMARY KEY,
    turno_id CHAR(36) NOT NULL,
    usuario_id CHAR(36) NOT NULL,
    cargo VARCHAR(30) NOT NULL COMMENT 'CAIXA, FRENTISTA, SUPERVISOR, ESTOQUISTA',
    CONSTRAINT uk_turno_alocacao_turno_usuario UNIQUE (turno_id, usuario_id),
    CONSTRAINT fk_sa_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
    CONSTRAINT fk_sa_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sessoes_caixa (
    id CHAR(36) NOT NULL PRIMARY KEY,
    turno_id CHAR(36) NOT NULL,
    caixeiro_usuario_id CHAR(36) NOT NULL,
    cargo VARCHAR(30) NOT NULL COMMENT 'CAIXA, FRENTISTA',
    fundo_abertura DECIMAL(15, 2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL COMMENT 'ABERTO, FECHADO',
    aberto_em DATETIME(6) NOT NULL,
    fechado_em DATETIME(6) NULL,
    total_dinheiro DECIMAL(15, 2) NULL,
    total_cartao DECIMAL(15, 2) NULL,
    total_pix DECIMAL(15, 2) NULL,
    total_sangria DECIMAL(15, 2) NULL,
    observacoes VARCHAR(255) NULL,
    criado_em DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em DATETIME(6) NULL,
    excluido_em DATETIME(6) NULL,
    CONSTRAINT fk_sessoes_caixa_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
    CONSTRAINT fk_sessoes_caixa_caixeiro FOREIGN KEY (caixeiro_usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE leituras_bico (
    id CHAR(36) NOT NULL PRIMARY KEY,
    turno_id CHAR(36) NOT NULL,
    bico_id CHAR(36) NOT NULL,
    tipo_leitura VARCHAR(20) NOT NULL COMMENT 'ABERTURA, FECHAMENTO, TESTE, CALIBRACAO',
    leitura_medidor DECIMAL(18, 3) NOT NULL,
    read_em DATETIME(6) NOT NULL,
    registrado_by CHAR(36) NOT NULL,
    observacoes VARCHAR(500) NULL,
    CONSTRAINT fk_nr_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
    CONSTRAINT fk_nr_bico FOREIGN KEY (bico_id) REFERENCES bicos (id),
    CONSTRAINT fk_nr_registrado_by FOREIGN KEY (registrado_by) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movimentos_caixa (
    id CHAR(36) NOT NULL PRIMARY KEY,
    turno_id CHAR(36) NOT NULL,
    tipo_movimento VARCHAR(25) NOT NULL COMMENT 'ABERTURA, VENDA, SANGRIA, SUPRIMENTO, REEMBOLSO, AJUSTE, FECHAMENTO',
    metodo_pagamento_id CHAR(36) NULL,
    valor DECIMAL(18, 2) NOT NULL,
    ocorrido_em DATETIME(6) NOT NULL,
    referencia VARCHAR(100) NULL,
    motivo VARCHAR(255) NULL,
    criado_por CHAR(36) NOT NULL,
    aprovado_por CHAR(36) NULL,
    CONSTRAINT fk_cm_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
    CONSTRAINT fk_cm_pm FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento (id),
    CONSTRAINT fk_cm_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios (id),
    CONSTRAINT fk_cm_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE encerramentos_pagamento (
    id CHAR(36) NOT NULL PRIMARY KEY,
    turno_id CHAR(36) NOT NULL,
    metodo_pagamento_id CHAR(36) NOT NULL,
    valor_esperado DECIMAL(18, 2) NOT NULL,
    valor_declarado DECIMAL(18, 2) NOT NULL,
    valor_diferenca DECIMAL(18, 2) NOT NULL,
    referencia_comprovante VARCHAR(255) NULL,
    CONSTRAINT fk_spc_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
    CONSTRAINT fk_spc_pm FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- Venda e recibo
-- -----------------------------------------------------------------------------

CREATE TABLE venda (
    id CHAR(36) NOT NULL PRIMARY KEY,
    estacao_id CHAR(36) NOT NULL,
    turno_id CHAR(36) NOT NULL,
    numero_venda VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL COMMENT 'ABERTO, PAGO, CANCELADO, REEMBOLSADO, CREDITO_PENDENTE',
    vendido_em DATETIME(6) NOT NULL,
    cliente_id CHAR(36) NULL,
    veiculo_id CHAR(36) NULL,
    frentista_usuario_id CHAR(36) NULL,
    caixeiro_usuario_id CHAR(36) NOT NULL,
    subtotal DECIMAL(19, 4) NOT NULL DEFAULT 0,
    valor_desconto DECIMAL(19, 4) NOT NULL DEFAULT 0,
    situacao_fiscal VARCHAR(40) NULL COMMENT 'EMISSAO_PENDENTE, EMITIDO, REJEITADO, CANCELADO, EMITIDO_EM_CONTINGENCIA',
    observacoes VARCHAR(1000) NULL,
    CONSTRAINT uk_venda_estacao_numero UNIQUE (estacao_id, numero_venda),
    CONSTRAINT fk_venda_estacao FOREIGN KEY (estacao_id) REFERENCES estacoes (id),
    CONSTRAINT fk_venda_turno FOREIGN KEY (turno_id) REFERENCES turnos (id),
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT fk_venda_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos_cliente (id),
    CONSTRAINT fk_venda_frentista FOREIGN KEY (frentista_usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_venda_caixeiro FOREIGN KEY (caixeiro_usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE item_venda (
    id CHAR(36) NOT NULL PRIMARY KEY,
    venda_id CHAR(36) NOT NULL,
    numero_linha INT NOT NULL,
    produto_id CHAR(36) NOT NULL,
    bico_id CHAR(36) NULL,
    tanque_id CHAR(36) NULL,
    quantidade DECIMAL(19, 4) NOT NULL,
    preco_unitario DECIMAL(19, 4) NOT NULL,
    valor_desconto DECIMAL(19, 4) NOT NULL DEFAULT 0,
    medidor_inicial DECIMAL(19, 4) NULL,
    medidor_final DECIMAL(19, 4) NULL,
    CONSTRAINT uk_venda_item_linha UNIQUE (venda_id, numero_linha),
    CONSTRAINT fk_venda_item_venda FOREIGN KEY (venda_id) REFERENCES venda (id),
    CONSTRAINT fk_venda_item_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT fk_venda_item_bico FOREIGN KEY (bico_id) REFERENCES bicos (id),
    CONSTRAINT fk_venda_item_tanque FOREIGN KEY (tanque_id) REFERENCES tanques (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE pagamento_venda (
    id CHAR(36) NOT NULL PRIMARY KEY,
    venda_id CHAR(36) NOT NULL,
    metodo_pagamento_id CHAR(36) NOT NULL,
    valor DECIMAL(19, 4) NOT NULL,
    status VARCHAR(20) NOT NULL COMMENT 'PENDENTE, AUTORIZADO, RECEBIDO, REJEITADO, REEMBOLSADO',
    referencia_transacao VARCHAR(120) NULL,
    autorizado_em DATETIME(6) NULL,
    recebido_em DATETIME(6) NULL,
    CONSTRAINT fk_venda_pagamento_venda FOREIGN KEY (venda_id) REFERENCES venda (id),
    CONSTRAINT fk_venda_pagamento_method FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reembolso_venda (
    id CHAR(36) NOT NULL PRIMARY KEY,
    venda_id CHAR(36) NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    solicitado_por CHAR(36) NOT NULL,
    aprovado_por CHAR(36) NOT NULL,
    reembolsado_em DATETIME(6) NOT NULL,
    observacoes VARCHAR(1000) NULL,
    CONSTRAINT fk_venda_reembolso_venda FOREIGN KEY (venda_id) REFERENCES venda (id),
    CONSTRAINT fk_venda_reembolso_solicitado_por FOREIGN KEY (solicitado_por) REFERENCES usuarios (id),
    CONSTRAINT fk_venda_reembolso_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_estacoes_empresa ON estacoes (empresa_id);
CREATE INDEX idx_bicos_bomba ON bicos (bomba_id);
CREATE INDEX idx_abastecimentos_tanque ON abastecimentos (tanque_id);
CREATE INDEX idx_abastecimentos_bomba ON abastecimentos (bomba_id);
CREATE INDEX idx_turnos_estacao_situacao ON turnos (estacao_id, status);
CREATE INDEX idx_sessoes_caixa_turno ON sessoes_caixa (turno_id);
CREATE INDEX idx_sessoes_caixa_caixeiro_situacao ON sessoes_caixa (caixeiro_usuario_id, status);
CREATE INDEX idx_venda_estacao_id ON venda (estacao_id);
CREATE INDEX idx_venda_turno_id ON venda (turno_id);
CREATE INDEX idx_venda_situacao ON venda (status);
CREATE INDEX idx_venda_vendido_em ON venda (vendido_em);
CREATE INDEX idx_venda_item_venda_id ON item_venda (venda_id);
CREATE INDEX idx_venda_pagamento_venda_id ON pagamento_venda (venda_id);
CREATE INDEX idx_venda_reembolso_venda_id ON reembolso_venda (venda_id);
