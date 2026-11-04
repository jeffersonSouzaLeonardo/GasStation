-- =============================================================================
-- Flyway Migration: V1__create_initial_schema.sql
-- Descrição: Criação de todas as tabelas do sistema de gestão de postos (MySQL)
-- =============================================================================

-- =============================================================================
-- 1. ORGANIZAÇÃO E ACESSO
-- =============================================================================

CREATE TABLE empresas (
                           id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                           razao_social VARCHAR(255) NOT NULL,
                           nome_fantasia VARCHAR(255),
                           cnpj VARCHAR(14) NOT NULL UNIQUE,
                           inscricao_estadual VARCHAR(30),
                           regime_tributario VARCHAR(50),
                           email VARCHAR(255),
                           telefone VARCHAR(20),
                           ativo BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                           criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           atualizado_em DATETIME ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE estacoes (
                          id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                          empresa_id CHAR(36) NOT NULL,
                          nome VARCHAR(255) NOT NULL,
                          cnpj VARCHAR(14),
                          inscricao_estadual VARCHAR(30),

    -- Endereco Embedded
                          rua VARCHAR(255),
                          numero VARCHAR(20),
                          complemento VARCHAR(100),
                          bairro VARCHAR(100),
                          cidade VARCHAR(100),
                          estado VARCHAR(2),
                          cep VARCHAR(10),

                          fuso_horario VARCHAR(50) NOT NULL DEFAULT 'America/Sao_Paulo',
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                          criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          atualizado_em DATETIME ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_estacoes_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE usuarios (
                       id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       nome VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       hash_senha VARCHAR(255) NOT NULL,
                       telefone VARCHAR(20),
                       ativo BOOLEAN NOT NULL DEFAULT TRUE,
                       ultimo_login_em DATETIME,

    -- Transversais
                       criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       atualizado_em DATETIME ON UPDATE CURRENT_TIMESTAMP,
                       excluido_em DATETIME
);

CREATE TABLE usuario_estacao_cargos (
                                    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                    usuario_id CHAR(36) NOT NULL,
                                    empresa_id CHAR(36) NOT NULL,
                                    cargo ENUM('ADMIN', 'GERENTE', 'CAIXA', 'FRENTISTA') NOT NULL,
                                    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                                    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_usr_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
                                    CONSTRAINT fk_usr_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE logs_auditoria (
                            id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                            empresa_id CHAR(36) NOT NULL,
                            usuario_id CHAR(36),
                            nome_entidade VARCHAR(100) NOT NULL,
                            entidade_id VARCHAR(100) NOT NULL,
                            acao VARCHAR(50) NOT NULL,
                            dados_anteriores JSON,
                            dados_posteriores JSON,
                            endereco_ip VARCHAR(45),
                            criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_auditoria_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
                            CONSTRAINT fk_auditoria_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- =============================================================================
-- 2. CADASTROS OPERACIONAIS
-- =============================================================================

CREATE TABLE produtos (
                          id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                          sku VARCHAR(100) NOT NULL,
                          nome VARCHAR(255) NOT NULL,
                          tipo_produto ENUM('COMBUSTIVEL', 'LUBRIFICANTE', 'SERVICO', 'CONVENIENCIA') NOT NULL,
                          unidade ENUM('L', 'UN', 'KG', 'M3') NOT NULL,
                          anp_codigo VARCHAR(20),
                          ncm VARCHAR(10),
                          cest VARCHAR(10),
                          ativo BOOLEAN NOT NULL DEFAULT TRUE,
                          criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE estacao_produtos (
                                  id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                  produto_id CHAR(36) NOT NULL,
                                  estoque_minimo DECIMAL(15, 4) DEFAULT 0,
                                  ponto_reposicao DECIMAL(15, 4) DEFAULT 0,
                                  ativo BOOLEAN NOT NULL DEFAULT TRUE,

                                  CONSTRAINT fk_sp_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE tanques (
                       id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       codigo VARCHAR(50) NOT NULL,
                       produto_id CHAR(36) NOT NULL,
                       capacidade_litros DECIMAL(15, 4) NOT NULL,
                       estoque_morto_litros DECIMAL(15, 4) DEFAULT 0,
                       saldo_contabil_litros DECIMAL(15, 4) NOT NULL DEFAULT 0,
                       ativo BOOLEAN NOT NULL DEFAULT TRUE,

                       CONSTRAINT fk_tanques_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE bombas (
                       id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       codigo VARCHAR(50) NOT NULL,
                       localizacao VARCHAR(100),
                       ativo BOOLEAN NOT NULL DEFAULT TRUE

);

CREATE TABLE bicos (
                         id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                         bomba_id CHAR(36) NOT NULL,
                         tanque_id CHAR(36) NOT NULL,
                         codigo VARCHAR(50) NOT NULL,
                         produto_id CHAR(36) NOT NULL,
                         numero_medidor DECIMAL(15, 4) DEFAULT 0,
                         ativo BOOLEAN NOT NULL DEFAULT TRUE,

                         CONSTRAINT uk_bico_bomba_codigo UNIQUE (bomba_id, codigo),
                         CONSTRAINT fk_bicos_bomba FOREIGN KEY (bomba_id) REFERENCES bombas(id),
                         CONSTRAINT fk_bicos_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id),
                         CONSTRAINT fk_bicos_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE fornecedores
(
    id                   CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    razao_social           VARCHAR(255) NOT NULL,
    cnpj_cpf             VARCHAR(14),
    inscricao_estadual   VARCHAR(30),
    email                VARCHAR(255),
    telefone                VARCHAR(20),

    -- Endereco Embedded
    rua       VARCHAR(255),
    numero       VARCHAR(20),
    complemento   VARCHAR(100),
    bairro VARCHAR(100),
    cidade         VARCHAR(100),
    estado        VARCHAR(2),
    cep     VARCHAR(10),
    ativo               BOOLEAN      NOT NULL   DEFAULT TRUE
);

CREATE TABLE clientes (
                           id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                           nome VARCHAR(255) NOT NULL,
                           tipo_pessoa ENUM('FISICA', 'JURIDICA') NOT NULL,
                           cnpj_cpf VARCHAR(14),
                           inscricao_estadual VARCHAR(30),
                           email VARCHAR(255),
                           telefone VARCHAR(20),

    -- Endereco Embedded
                           rua VARCHAR(255),
                           numero VARCHAR(20),
                           complemento VARCHAR(100),
                           bairro VARCHAR(100),
                           cidade VARCHAR(100),
                           estado VARCHAR(2),
                           cep VARCHAR(10),

                           limite_credito DECIMAL(15, 2) DEFAULT 0,
                           ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE veiculos_cliente (
                                   id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                   cliente_id CHAR(36) NOT NULL,
                                   placa VARCHAR(10) NOT NULL,
                                   marca VARCHAR(50),
                                   modelo VARCHAR(50),
                                   tipo_combustivel VARCHAR(30),
                                   ativo BOOLEAN NOT NULL DEFAULT TRUE,

                                   CONSTRAINT fk_veiculos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE metodos_pagamento (
                                 id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 nome VARCHAR(100) NOT NULL,
                                 tipo VARCHAR(50) NOT NULL,
                                 exige_referencia BOOLEAN NOT NULL DEFAULT FALSE,
                                 dias_liquidacao INT DEFAULT 0,
                                 ativo BOOLEAN NOT NULL DEFAULT TRUE

);

-- =============================================================================
-- 3. PREÇO E COMPRA
-- =============================================================================

CREATE TABLE tabelas_preco (
                              id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                              nome VARCHAR(100) NOT NULL,
                              valido_de DATETIME NOT NULL,
                              valido_ate DATETIME,
                              status VARCHAR(20) NOT NULL,
                              criado_por CHAR(36) NOT NULL,

                              CONSTRAINT fk_pt_usuario FOREIGN KEY (criado_por) REFERENCES usuarios(id)
);

CREATE TABLE itens_preco (
                             id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                             tabela_preco_id CHAR(36) NOT NULL,
                             produto_id CHAR(36) NOT NULL,
                             bico_id CHAR(36),
                             preco_unitario DECIMAL(15, 4) NOT NULL,
                             preco_minimo DECIMAL(15, 4),
                             desconto_maximo_percentual DECIMAL(5, 2),
                             ativo BOOLEAN NOT NULL DEFAULT TRUE,

                             CONSTRAINT fk_pi_tabela FOREIGN KEY (tabela_preco_id) REFERENCES tabelas_preco(id),
                             CONSTRAINT fk_pi_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
                             CONSTRAINT fk_pi_bico FOREIGN KEY (bico_id) REFERENCES bicos(id)
);

CREATE TABLE pedidos_compra (
                                 id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 fornecedor_id CHAR(36) NOT NULL,
                                 numero VARCHAR(50),
                                 status VARCHAR(30) NOT NULL,
                                 pedido_em DATETIME,
                                 esperado_em DATETIME,
                                 criado_por CHAR(36) NOT NULL,

    -- Transversais
                                 situacao_aprovacao ENUM('PENDENTE', 'APROVADO', 'REJEITADO') DEFAULT 'PENDENTE',
                                 aprovado_por CHAR(36),
                                 aprovado_em DATETIME,
                                 cancelado_em DATETIME,
                                 cancelado_por CHAR(36),
                                 motivo_cancelamento TEXT,

                                 CONSTRAINT fk_po_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id),
                                 CONSTRAINT fk_po_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios(id),
                                 CONSTRAINT fk_po_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios(id),
                                 CONSTRAINT fk_po_cancelado_por FOREIGN KEY (cancelado_por) REFERENCES usuarios(id)
);

CREATE TABLE itens_pedido_compra (
                                      id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                      pedido_compra_id CHAR(36) NOT NULL,
                                      produto_id CHAR(36) NOT NULL,
                                      quantidade DECIMAL(15, 4) NOT NULL,
                                      custo_unitario DECIMAL(15, 4) NOT NULL,

                                      CONSTRAINT fk_poi_order FOREIGN KEY (pedido_compra_id) REFERENCES pedidos_compra(id),
                                      CONSTRAINT fk_poi_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE recebimentos_mercadorias (
                                id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                fornecedor_id CHAR(36) NOT NULL,
                                pedido_compra_id CHAR(36),
                                numero_nota VARCHAR(50),
                                chave_nota VARCHAR(44),
                                recebido_em DATETIME NOT NULL,
                                status VARCHAR(30) NOT NULL,
                                valor_frete DECIMAL(15, 2) DEFAULT 0,
                                outros_custos DECIMAL(15, 2) DEFAULT 0,
                                recebido_por CHAR(36) NOT NULL,

                                CONSTRAINT fk_gr_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id),
                                CONSTRAINT fk_gr_po FOREIGN KEY (pedido_compra_id) REFERENCES pedidos_compra(id),
                                CONSTRAINT fk_gr_recebido_por FOREIGN KEY (recebido_por) REFERENCES usuarios(id)
);

CREATE TABLE itens_recebimento_mercadorias (
                                     id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                     recebimento_mercadoria_id CHAR(36) NOT NULL,
                                     produto_id CHAR(36) NOT NULL,
                                     tanque_id CHAR(36),
                                     quantidade DECIMAL(15, 4) NOT NULL,
                                     custo_unitario DECIMAL(15, 4) NOT NULL,

                                     CONSTRAINT fk_gri_recibo FOREIGN KEY (recebimento_mercadoria_id) REFERENCES recebimentos_mercadorias(id),
                                     CONSTRAINT fk_gri_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
                                     CONSTRAINT fk_gri_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id)
);

-- =============================================================================
-- 4. TURNO, CAIXA E AFERIÇÃO
-- =============================================================================

CREATE TABLE turnos (
                        id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                        caixeiro_usuario_id CHAR(36) NOT NULL,
                        aberto_em DATETIME NOT NULL,
                        fechado_em DATETIME,
                        fundo_abertura DECIMAL(15, 2) NOT NULL DEFAULT 0,
                        status VARCHAR(20) NOT NULL,
                        aberto_por CHAR(36) NOT NULL,
                        fechado_por CHAR(36),
                        observacoes TEXT,

                        CONSTRAINT fk_turnos_caixeiro FOREIGN KEY (caixeiro_usuario_id) REFERENCES usuarios(id),
                        CONSTRAINT fk_turnos_aberto_por FOREIGN KEY (aberto_por) REFERENCES usuarios(id),
                        CONSTRAINT fk_turnos_fechado_por FOREIGN KEY (fechado_por) REFERENCES usuarios(id)
);

CREATE TABLE alocacoes_turno (
                                   id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                   turno_id CHAR(36) NOT NULL,
                                   usuario_id CHAR(36) NOT NULL,
                                   cargo ENUM('ADMIN', 'GERENTE', 'CAIXA', 'FRENTISTA') NOT NULL,

                                   CONSTRAINT fk_sa_turno FOREIGN KEY (turno_id) REFERENCES turnos(id),
                                   CONSTRAINT fk_sa_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE leituras_bico (
                                 id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 turno_id CHAR(36) NOT NULL,
                                 bico_id CHAR(36) NOT NULL,
                                 tipo_leitura ENUM('ABERTURA', 'FECHAMENTO', 'TESTE', 'CALIBRACAO') NOT NULL,
                                 leitura_medidor DECIMAL(15, 4) NOT NULL,
                                 read_em DATETIME NOT NULL,
                                 registrado_by CHAR(36) NOT NULL,
                                 observacoes TEXT,

                                 CONSTRAINT fk_nr_turno FOREIGN KEY (turno_id) REFERENCES turnos(id),
                                 CONSTRAINT fk_nr_bico FOREIGN KEY (bico_id) REFERENCES bicos(id),
                                 CONSTRAINT fk_nr_registrado_by FOREIGN KEY (registrado_by) REFERENCES usuarios(id)
);

CREATE TABLE movimentos_caixa (
                                id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                turno_id CHAR(36) NOT NULL,
                                tipo_movimento ENUM('ABERTURA', 'VENDA', 'SANGRIA', 'SUPRIMENTO', 'REEMBOLSO', 'AJUSTE', 'FECHAMENTO') NOT NULL,
                                metodo_pagamento_id CHAR(36),
                                valor DECIMAL(15, 2) NOT NULL,
                                ocorrido_em DATETIME NOT NULL,
                                referencia VARCHAR(100),
                                motivo TEXT,
                                criado_por CHAR(36) NOT NULL,

    -- Transversais
                                tipo_origem VARCHAR(50),
                                id_origem VARCHAR(100),
                                situacao_aprovacao ENUM('PENDENTE', 'APROVADO', 'REJEITADO'),
                                aprovado_por CHAR(36),
                                aprovado_em DATETIME,

                                CONSTRAINT fk_cm_turno FOREIGN KEY (turno_id) REFERENCES turnos(id),
                                CONSTRAINT fk_cm_pm FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento(id),
                                CONSTRAINT fk_cm_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios(id),
                                CONSTRAINT fk_cm_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios(id)
);

CREATE TABLE encerramentos_pagamento_turno (
                                        id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                        turno_id CHAR(36) NOT NULL,
                                        metodo_pagamento_id CHAR(36) NOT NULL,
                                        valor_esperado DECIMAL(15, 2) NOT NULL,
                                        valor_declarado DECIMAL(15, 2) NOT NULL,
                                        valor_diferenca DECIMAL(15, 2) NOT NULL,
                                        referencia_comprovante VARCHAR(255),

                                        CONSTRAINT fk_spc_turno FOREIGN KEY (turno_id) REFERENCES turnos(id),
                                        CONSTRAINT fk_spc_pm FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento(id)
);

-- =============================================================================
-- 5. VENDA E RECEBIMENTO
-- =============================================================================

CREATE TABLE vendas (
                       id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       turno_id CHAR(36) NOT NULL,
                       numero_venda VARCHAR(50) NOT NULL,
                       status ENUM('ABERTO', 'PAGO', 'CANCELADO', 'REEMBOLSADO', 'CREDITO_PENDENTE') NOT NULL DEFAULT 'ABERTO',
                       vendido_em DATETIME NOT NULL,
                       cliente_id CHAR(36),
                       veiculo_id CHAR(36),
                       frentista_usuario_id CHAR(36),
                       caixeiro_usuario_id CHAR(36) NOT NULL,
                       subtotal DECIMAL(15, 2) NOT NULL,
                       valor_desconto DECIMAL(15, 2) NOT NULL DEFAULT 0,
                       situacao_fiscal VARCHAR(50),
                       observacoes TEXT,

    -- Transversais
                       situacao_aprovacao ENUM('PENDENTE', 'APROVADO', 'REJEITADO'),
                       aprovado_por CHAR(36),
                       aprovado_em DATETIME,
                       cancelado_em DATETIME,
                       cancelado_por CHAR(36),
                       motivo_cancelamento TEXT,

                       CONSTRAINT fk_vendas_turno FOREIGN KEY (turno_id) REFERENCES turnos(id),
                       CONSTRAINT fk_vendas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                       CONSTRAINT fk_vendas_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos_cliente(id),
                       CONSTRAINT fk_vendas_frentista FOREIGN KEY (frentista_usuario_id) REFERENCES usuarios(id),
                       CONSTRAINT fk_vendas_caixeiro FOREIGN KEY (caixeiro_usuario_id) REFERENCES usuarios(id),
                       CONSTRAINT fk_vendas_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios(id),
                       CONSTRAINT fk_vendas_cancelado_por FOREIGN KEY (cancelado_por) REFERENCES usuarios(id)
);

CREATE TABLE itens_venda (
                            id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                            venda_id CHAR(36) NOT NULL,
                            numero_linha INT NOT NULL,
                            produto_id CHAR(36) NOT NULL,
                            bico_id CHAR(36),
                            tanque_id CHAR(36),
                            quantidade DECIMAL(15, 4) NOT NULL,
                            preco_unitario DECIMAL(15, 4) NOT NULL,
                            valor_desconto DECIMAL(15, 2) NOT NULL DEFAULT 0,
                            medidor_inicial DECIMAL(15, 4),
                            medidor_final DECIMAL(15, 4),

                            CONSTRAINT uk_venda_item_linha UNIQUE (venda_id, numero_linha),
                            CONSTRAINT fk_si_venda FOREIGN KEY (venda_id) REFERENCES vendas(id),
                            CONSTRAINT fk_si_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
                            CONSTRAINT fk_si_bico FOREIGN KEY (bico_id) REFERENCES bicos(id),
                            CONSTRAINT fk_si_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id)
);

CREATE TABLE pagamentos_venda (
                               id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                               venda_id CHAR(36) NOT NULL,
                               metodo_pagamento_id CHAR(36) NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               referencia_transacao VARCHAR(100),
                               autorizado_em DATETIME,
                               recebido_em DATETIME,

                               CONSTRAINT fk_spay_venda FOREIGN KEY (venda_id) REFERENCES vendas(id),
                               CONSTRAINT fk_spay_pm FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento(id)
);

CREATE TABLE reembolsos_venda (
                              id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                              venda_id CHAR(36) NOT NULL,
                              motivo TEXT NOT NULL,
                              solicitado_por CHAR(36) NOT NULL,
                              aprovado_por CHAR(36) NOT NULL,
                              reembolsado_em DATETIME NOT NULL,
                              observacoes TEXT,

    -- Transversais
                              situacao_aprovacao ENUM('PENDENTE', 'APROVADO', 'REJEITADO') DEFAULT 'APROVADO',
                              aprovado_em DATETIME,

                              CONSTRAINT fk_sr_venda FOREIGN KEY (venda_id) REFERENCES vendas(id),
                              CONSTRAINT fk_sr_solicitado_por FOREIGN KEY (solicitado_por) REFERENCES usuarios(id),
                              CONSTRAINT fk_sr_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios(id)
);

-- =============================================================================
-- 6. ESTOQUE E FINANCEIRO
-- =============================================================================

CREATE TABLE movimentos_estoque (
                                 id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 produto_id CHAR(36) NOT NULL,
                                 tanque_id CHAR(36),
                                 tipo_movimento ENUM('RECEBIMENTO_COMPRA', 'VENDA', 'DEVOLUCAO', 'TRANSFERENCIA_ENTRADA', 'TRANSFERENCIA_SAIDA', 'AJUSTE_INVENTARIO', 'PERDA', 'TESTE', 'SALDO_INICIAL') NOT NULL,
                                 quantidade DECIMAL(15, 4) NOT NULL,
                                 custo_unitario DECIMAL(15, 4),
                                 ocorrido_em DATETIME NOT NULL,
                                 tipo_origem VARCHAR(50) NOT NULL,
                                 id_origem VARCHAR(100) NOT NULL,
                                 criado_por CHAR(36) NOT NULL,
                                 observacoes TEXT,

                                 CONSTRAINT fk_sm_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
                                 CONSTRAINT fk_sm_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id),
                                 CONSTRAINT fk_sm_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios(id)
);

CREATE TABLE medicoes_tanque (
                                   id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                   tanque_id CHAR(36) NOT NULL,
                                   medido_em DATETIME NOT NULL,
                                   volume_litros DECIMAL(15, 4) NOT NULL,
                                   agua_litros DECIMAL(15, 4),
                                   temperatura_celsius DECIMAL(4, 2),
                                   registrado_by CHAR(36) NOT NULL,
                                   observacoes TEXT,

                                   CONSTRAINT fk_tm_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id),
                                   CONSTRAINT fk_tm_registrado_by FOREIGN KEY (registrado_by) REFERENCES usuarios(id)
);

CREATE TABLE inventarios (
                             id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                             iniciado_em DATETIME NOT NULL,
                             finalizado_em DATETIME,
                             status VARCHAR(30) NOT NULL,
                             criado_por CHAR(36) NOT NULL,
                             aprovado_por CHAR(36),

    -- Transversais
                             situacao_aprovacao ENUM('PENDENTE', 'APROVADO', 'REJEITADO'),
                             aprovado_em DATETIME,

                             CONSTRAINT fk_inv_criado_por FOREIGN KEY (criado_por) REFERENCES usuarios(id),
                             CONSTRAINT fk_inv_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios(id)
);

CREATE TABLE itens_inventario (
                                 id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 inventario_id CHAR(36) NOT NULL,
                                 produto_id CHAR(36) NOT NULL,
                                 tanque_id CHAR(36),
                                 quantidade_contabil DECIMAL(15, 4) NOT NULL,
                                 quantidade_contada DECIMAL(15, 4) NOT NULL,
                                 quantidade_diferenca DECIMAL(15, 4) NOT NULL,
                                 movimento_ajuste_id CHAR(36),

                                 CONSTRAINT fk_ii_inventario FOREIGN KEY (inventario_id) REFERENCES inventarios(id),
                                 CONSTRAINT fk_ii_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
                                 CONSTRAINT fk_ii_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id),
                                 CONSTRAINT fk_ii_movimento FOREIGN KEY (movimento_ajuste_id) REFERENCES movimentos_estoque(id)
);

CREATE TABLE contas_receber (
                                     id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                     cliente_id CHAR(36) NOT NULL,
                                     venda_id CHAR(36),
                                     data_vencimento DATE NOT NULL,
                                     valor_original DECIMAL(15, 2) NOT NULL,
                                     valor_aberto DECIMAL(15, 2) NOT NULL,
                                     status VARCHAR(30) NOT NULL,

    -- Transversais
                                     tipo_origem VARCHAR(50),
                                     id_origem VARCHAR(100),
                                     cancelado_em DATETIME,
                                     cancelado_por CHAR(36),
                                     motivo_cancelamento TEXT,

                                     CONSTRAINT fk_ar_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                                     CONSTRAINT fk_ar_venda FOREIGN KEY (venda_id) REFERENCES vendas(id),
                                     CONSTRAINT fk_ar_cancelado_por FOREIGN KEY (cancelado_por) REFERENCES usuarios(id)
);

CREATE TABLE recebimentos_financeiros (
                                    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                    conta_receber_id CHAR(36) NOT NULL,
                                    metodo_pagamento_id CHAR(36) NOT NULL,
                                    valor DECIMAL(15, 2) NOT NULL,
                                    recebido_em DATETIME NOT NULL,
                                    referencia VARCHAR(100),
                                    recebido_por CHAR(36) NOT NULL,

                                    CONSTRAINT fk_fr_receber FOREIGN KEY (conta_receber_id) REFERENCES contas_receber(id),
                                    CONSTRAINT fk_fr_pm FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento(id),
                                    CONSTRAINT fk_fr_recebido_por FOREIGN KEY (recebido_por) REFERENCES usuarios(id)
);

CREATE TABLE contas_pagar (
                                  id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                  fornecedor_id CHAR(36),
                                  recebimento_mercadoria_id CHAR(36),
                                  descricao VARCHAR(255) NOT NULL,
                                  data_vencimento DATE NOT NULL,
                                  valor_original DECIMAL(15, 2) NOT NULL,
                                  valor_aberto DECIMAL(15, 2) NOT NULL,
                                  status VARCHAR(30) NOT NULL,

    -- Transversais
                                  tipo_origem VARCHAR(50),
                                  id_origem VARCHAR(100),
                                  cancelado_em DATETIME,
                                  cancelado_por CHAR(36),
                                  motivo_cancelamento TEXT,
                                  criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_ap_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id),
                                  CONSTRAINT fk_ap_recibo FOREIGN KEY (recebimento_mercadoria_id) REFERENCES recebimentos_mercadorias(id),
                                  CONSTRAINT fk_ap_cancelado_por FOREIGN KEY (cancelado_por) REFERENCES usuarios(id)
);

-- =============================================================================
-- 7. ÍNDICES DE PERFORMANCE
-- =============================================================================

CREATE INDEX idx_bicos_bomba ON bicos(bomba_id);
CREATE INDEX idx_itens_venda_venda ON itens_venda(venda_id);
CREATE INDEX idx_contas_receber_cliente ON contas_receber(cliente_id);
CREATE INDEX idx_contas_pagar_fornecedor ON contas_pagar(fornecedor_id);