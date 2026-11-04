CREATE TABLE venda (
    id VARCHAR(36) PRIMARY KEY,
    estacao_id VARCHAR(36) NOT NULL,
    turno_id VARCHAR(36) NOT NULL,
    numero_venda VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    vendido_em DATETIME(6) NOT NULL,
    cliente_id VARCHAR(36),
    veiculo_id VARCHAR(36),
    frentista_usuario_id VARCHAR(36),
    caixeiro_usuario_id VARCHAR(36) NOT NULL,
    subtotal DECIMAL(19, 4) NOT NULL DEFAULT 0,
    valor_desconto DECIMAL(19, 4) NOT NULL DEFAULT 0,
    situacao_fiscal VARCHAR(40),
    observacoes VARCHAR(1000),
    CONSTRAINT uk_venda_estacao_numero UNIQUE (estacao_id, numero_venda),
    CONSTRAINT fk_venda_estacao FOREIGN KEY (estacao_id) REFERENCES estacoes(id),
    CONSTRAINT fk_venda_turno FOREIGN KEY (turno_id) REFERENCES turnos(id),
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_venda_veiculo FOREIGN KEY (veiculo_id) REFERENCES veiculos_cliente(id),
    CONSTRAINT fk_venda_frentista FOREIGN KEY (frentista_usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_venda_caixeiro FOREIGN KEY (caixeiro_usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE item_venda (
    id VARCHAR(36) PRIMARY KEY,
    venda_id VARCHAR(36) NOT NULL,
    numero_linha INT NOT NULL,
    produto_id VARCHAR(36) NOT NULL,
    bico_id VARCHAR(36),
    tanque_id VARCHAR(36),
    quantidade DECIMAL(19, 4) NOT NULL,
    preco_unitario DECIMAL(19, 4) NOT NULL,
    valor_desconto DECIMAL(19, 4) NOT NULL DEFAULT 0,
    medidor_inicial DECIMAL(19, 4),
    medidor_final DECIMAL(19, 4),
    CONSTRAINT uk_venda_item_linha UNIQUE (venda_id, numero_linha),
    CONSTRAINT fk_venda_item_venda FOREIGN KEY (venda_id) REFERENCES venda(id),
    CONSTRAINT fk_venda_item_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    CONSTRAINT fk_venda_item_bico FOREIGN KEY (bico_id) REFERENCES bicos(id),
    CONSTRAINT fk_venda_item_tanque FOREIGN KEY (tanque_id) REFERENCES tanques(id)
);

CREATE TABLE pagamento_venda (
    id VARCHAR(36) PRIMARY KEY,
    venda_id VARCHAR(36) NOT NULL,
    metodo_pagamento_id VARCHAR(36) NOT NULL,
    valor DECIMAL(19, 4) NOT NULL,
    status VARCHAR(20) NOT NULL,
    referencia_transacao VARCHAR(120),
    autorizado_em DATETIME(6),
    recebido_em DATETIME(6),
    CONSTRAINT fk_venda_pagamento_venda FOREIGN KEY (venda_id) REFERENCES venda(id),
    CONSTRAINT fk_venda_pagamento_method FOREIGN KEY (metodo_pagamento_id) REFERENCES metodos_pagamento(id)
);

CREATE TABLE reembolso_venda (
    id VARCHAR(36) PRIMARY KEY,
    venda_id VARCHAR(36) NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    solicitado_por VARCHAR(36) NOT NULL,
    aprovado_por VARCHAR(36) NOT NULL,
    reembolsado_em DATETIME(6) NOT NULL,
    observacoes VARCHAR(1000),
    CONSTRAINT fk_venda_reembolso_venda FOREIGN KEY (venda_id) REFERENCES venda(id),
    CONSTRAINT fk_venda_reembolso_solicitado_por FOREIGN KEY (solicitado_por) REFERENCES usuarios(id),
    CONSTRAINT fk_venda_reembolso_aprovado_por FOREIGN KEY (aprovado_por) REFERENCES usuarios(id)
);

CREATE INDEX idx_venda_estacao_id ON venda (estacao_id);
CREATE INDEX idx_venda_turno_id ON venda (turno_id);
CREATE INDEX idx_venda_situacao ON venda (status);
CREATE INDEX idx_venda_vendido_em ON venda (vendido_em);
CREATE INDEX idx_venda_pagamento_venda_id ON pagamento_venda (venda_id);
CREATE INDEX idx_venda_reembolso_venda_id ON reembolso_venda (venda_id);
CREATE INDEX idx_venda_item_venda_id ON item_venda (venda_id);
