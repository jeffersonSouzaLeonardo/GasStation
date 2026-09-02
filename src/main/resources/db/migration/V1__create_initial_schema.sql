-- =============================================================================
-- Flyway Migration: V1__create_initial_schema.sql
-- Descrição: Criação de todas as tabelas do sistema de gestão de postos
-- =============================================================================

-- Habilita suporte a UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =============================================================================
-- 1. TIPOS ENUMERADOS (ENUMS)
-- =============================================================================

CREATE TYPE role_type AS ENUM ('ADMIN', 'MANAGER', 'CASHIER', 'ATTENDANT');
CREATE TYPE product_type AS ENUM ('FUEL', 'LUBRICANT', 'SERVICE', 'CONVENIENCE');
CREATE TYPE unit_type AS ENUM ('L', 'UN', 'KG', 'M3');
CREATE TYPE person_type AS ENUM ('PHYSICAL', 'JURIDICAL');
CREATE TYPE reading_type AS ENUM ('OPENING', 'CLOSING', 'TEST', 'CALIBRATION');
CREATE TYPE cash_movement_type AS ENUM ('OPENING', 'SALE', 'WITHDRAWAL', 'CASH_IN', 'REFUND', 'ADJUSTMENT', 'CLOSING');
CREATE TYPE sale_status AS ENUM ('OPEN', 'PAID', 'CANCELLED', 'REFUNDED', 'PENDING_CREDIT');
CREATE TYPE stock_movement_type AS ENUM ('PURCHASE_RECEIPT', 'SALE', 'RETURN', 'TRANSFER_IN', 'TRANSFER_OUT', 'INVENTORY_ADJUSTMENT', 'LOSS', 'TEST', 'OPENING_BALANCE');
CREATE TYPE approval_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

-- =============================================================================
-- 2. ORGANIZAÇÃO E ACESSO
-- =============================================================================

CREATE TABLE companies (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           legal_name VARCHAR(255) NOT NULL,
                           trade_name VARCHAR(255),
                           cnpj VARCHAR(14) NOT NULL UNIQUE,
                           state_registration VARCHAR(30),
                           tax_regime VARCHAR(50),
                           email VARCHAR(255),
                           phone VARCHAR(20),
                           active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                           external_id VARCHAR(100),
                           version BIGINT NOT NULL DEFAULT 0,
                           created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE stations (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          company_id UUID NOT NULL REFERENCES companies(id),
                          name VARCHAR(255) NOT NULL,
                          cnpj VARCHAR(14),
                          state_registration VARCHAR(30),

    -- Address Embedded
                          address_street VARCHAR(255),
                          address_number VARCHAR(20),
                          address_complement VARCHAR(100),
                          address_neighborhood VARCHAR(100),
                          address_city VARCHAR(100),
                          address_state VARCHAR(2),
                          address_zip_code VARCHAR(10),

                          timezone VARCHAR(50) NOT NULL DEFAULT 'America/Sao_Paulo',
                          active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                          external_id VARCHAR(100),
                          version BIGINT NOT NULL DEFAULT 0,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       last_login_at TIMESTAMP WITH TIME ZONE,

    -- Transversais
                       external_id VARCHAR(100),
                       version BIGINT NOT NULL DEFAULT 0,
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE user_station_roles (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    user_id UUID NOT NULL REFERENCES users(id),
                                    company_id UUID NOT NULL REFERENCES companies(id),
                                    station_id UUID REFERENCES stations(id),
                                    role role_type NOT NULL,
                                    active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                                    version BIGINT NOT NULL DEFAULT 0,
                                    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            company_id UUID NOT NULL REFERENCES companies(id),
                            user_id UUID REFERENCES users(id),
                            entity_name VARCHAR(100) NOT NULL,
                            entity_id VARCHAR(100) NOT NULL,
                            action VARCHAR(50) NOT NULL,
                            before_data JSONB,
                            after_data JSONB,
                            ip_address VARCHAR(45),
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- 3. CADASTROS OPERACIONAIS
-- =============================================================================

CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          company_id UUID NOT NULL REFERENCES companies(id),
                          sku VARCHAR(100) NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          product_type product_type NOT NULL,
                          unit unit_type NOT NULL,
                          anp_code VARCHAR(20),
                          ncm VARCHAR(10),
                          cest VARCHAR(10),
                          active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                          external_id VARCHAR(100),
                          version BIGINT NOT NULL DEFAULT 0,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT uk_product_company_sku UNIQUE (company_id, sku)
);

CREATE TABLE station_products (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  station_id UUID NOT NULL REFERENCES stations(id),
                                  product_id UUID NOT NULL REFERENCES products(id),
                                  min_stock NUMERIC(15, 4) DEFAULT 0,
                                  reorder_point NUMERIC(15, 4) DEFAULT 0,
                                  active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                                  version BIGINT NOT NULL DEFAULT 0,

                                  CONSTRAINT uk_station_product UNIQUE (station_id, product_id)
);

CREATE TABLE tanks (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       station_id UUID NOT NULL REFERENCES stations(id),
                       code VARCHAR(50) NOT NULL,
                       product_id UUID NOT NULL REFERENCES products(id),
                       capacity_liters NUMERIC(15, 4) NOT NULL,
                       dead_stock_liters NUMERIC(15, 4) DEFAULT 0,
                       current_book_liters NUMERIC(15, 4) NOT NULL DEFAULT 0,
                       active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                       external_id VARCHAR(100),
                       version BIGINT NOT NULL DEFAULT 0,

                       CONSTRAINT uk_tank_station_code UNIQUE (station_id, code)
);

CREATE TABLE pumps (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       station_id UUID NOT NULL REFERENCES stations(id),
                       code VARCHAR(50) NOT NULL,
                       location VARCHAR(100),
                       active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                       version BIGINT NOT NULL DEFAULT 0,

                       CONSTRAINT uk_pump_station_code UNIQUE (station_id, code)
);

CREATE TABLE nozzles (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         pump_id UUID NOT NULL REFERENCES pumps(id),
                         tank_id UUID NOT NULL REFERENCES tanks(id),
                         code VARCHAR(50) NOT NULL,
                         product_id UUID NOT NULL REFERENCES products(id),
                         meter_number NUMERIC(15, 4) DEFAULT 0,
                         active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                         version BIGINT NOT NULL DEFAULT 0,

                         CONSTRAINT uk_nozzle_pump_code UNIQUE (pump_id, code)
);

CREATE TABLE suppliers (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           company_id UUID NOT NULL REFERENCES companies(id),
                           legal_name VARCHAR(255) NOT NULL,
                           cnpj_cpf VARCHAR(14),
                           state_registration VARCHAR(30),
                           email VARCHAR(255),
                           phone VARCHAR(20),

    -- Address Embedded
                           address_street VARCHAR(255),
                           address_number VARCHAR(20),
                           address_complement VARCHAR(100),
                           address_neighborhood VARCHAR(100),
                           address_city VARCHAR(100),
                           address_state VARCHAR(2),
                           address_zip_code VARCHAR(10),

                           active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                           external_id VARCHAR(100),
                           version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE customers (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           company_id UUID NOT NULL REFERENCES companies(id),
                           name VARCHAR(255) NOT NULL,
                           person_type person_type NOT NULL,
                           cnpj_cpf VARCHAR(14),
                           state_registration VARCHAR(30),
                           email VARCHAR(255),
                           phone VARCHAR(20),

    -- Address Embedded
                           address_street VARCHAR(255),
                           address_number VARCHAR(20),
                           address_complement VARCHAR(100),
                           address_neighborhood VARCHAR(100),
                           address_city VARCHAR(100),
                           address_state VARCHAR(2),
                           address_zip_code VARCHAR(10),

                           credit_limit NUMERIC(15, 2) DEFAULT 0,
                           active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                           external_id VARCHAR(100),
                           version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE customer_vehicles (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   customer_id UUID NOT NULL REFERENCES customers(id),
                                   plate VARCHAR(10) NOT NULL,
                                   brand VARCHAR(50),
                                   model VARCHAR(50),
                                   fuel_type VARCHAR(30),
                                   active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                                   version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE payment_methods (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 company_id UUID NOT NULL REFERENCES companies(id),
                                 name VARCHAR(100) NOT NULL,
                                 kind VARCHAR(50) NOT NULL, -- MONEY, CREDIT_CARD, DEBIT_CARD, PIX, CONVENIO, etc.
                                 requires_reference BOOLEAN NOT NULL DEFAULT FALSE,
                                 settlement_days INT DEFAULT 0,
                                 active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                                 external_id VARCHAR(100),
                                 version BIGINT NOT NULL DEFAULT 0
);

-- =============================================================================
-- 4. PREÇO E COMPRA
-- =============================================================================

CREATE TABLE price_tables (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              station_id UUID NOT NULL REFERENCES stations(id),
                              name VARCHAR(100) NOT NULL,
                              valid_from TIMESTAMP WITH TIME ZONE NOT NULL,
                              valid_to TIMESTAMP WITH TIME ZONE,
                              status VARCHAR(20) NOT NULL, -- ACTIVE, INACTIVE, SCHEDULED
                              created_by UUID NOT NULL REFERENCES users(id),

    -- Transversais
                              version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE price_items (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             price_table_id UUID NOT NULL REFERENCES price_tables(id),
                             product_id UUID NOT NULL REFERENCES products(id),
                             nozzle_id UUID REFERENCES nozzles(id),
                             unit_price NUMERIC(15, 4) NOT NULL,
                             min_price NUMERIC(15, 4),
                             max_discount_percent NUMERIC(5, 2),

    -- Transversais
                             version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE purchase_orders (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 station_id UUID NOT NULL REFERENCES stations(id),
                                 supplier_id UUID NOT NULL REFERENCES suppliers(id),
                                 number VARCHAR(50),
                                 status VARCHAR(30) NOT NULL, -- DRAFT, APPROVED, RECEIVED, CANCELLED
                                 ordered_at TIMESTAMP WITH TIME ZONE,
                                 expected_at TIMESTAMP WITH TIME ZONE,
                                 total_amount NUMERIC(15, 2) DEFAULT 0,
                                 created_by UUID NOT NULL REFERENCES users(id),

    -- Transversais
                                 approval_status approval_status DEFAULT 'PENDING',
                                 approved_by UUID REFERENCES users(id),
                                 approved_at TIMESTAMP WITH TIME ZONE,
                                 cancelled_at TIMESTAMP WITH TIME ZONE,
                                 cancelled_by UUID REFERENCES users(id),
                                 cancellation_reason TEXT,
                                 external_id VARCHAR(100),
                                 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE purchase_order_items (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
                                      product_id UUID NOT NULL REFERENCES products(id),
                                      quantity NUMERIC(15, 4) NOT NULL,
                                      unit_cost NUMERIC(15, 4) NOT NULL,
                                      total_cost NUMERIC(15, 2) NOT NULL,

    -- Transversais
                                      version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE goods_receipts (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                station_id UUID NOT NULL REFERENCES stations(id),
                                supplier_id UUID NOT NULL REFERENCES suppliers(id),
                                purchase_order_id UUID REFERENCES purchase_orders(id),
                                invoice_number VARCHAR(50),
                                invoice_key VARCHAR(44),
                                received_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                status VARCHAR(30) NOT NULL,
                                freight_amount NUMERIC(15, 2) DEFAULT 0,
                                other_costs NUMERIC(15, 2) DEFAULT 0,
                                received_by UUID NOT NULL REFERENCES users(id),

    -- Transversais
                                external_id VARCHAR(100),
                                version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE goods_receipt_items (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     goods_receipt_id UUID NOT NULL REFERENCES goods_receipts(id),
                                     product_id UUID NOT NULL REFERENCES products(id),
                                     tank_id UUID REFERENCES tanks(id),
                                     quantity NUMERIC(15, 4) NOT NULL,
                                     unit_cost NUMERIC(15, 4) NOT NULL,
                                     total_cost NUMERIC(15, 2) NOT NULL,

    -- Transversais
                                     version BIGINT NOT NULL DEFAULT 0
);

-- =============================================================================
-- 5. TURNO, CAIXA E AFERIÇÃO
-- =============================================================================

CREATE TABLE shifts (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        station_id UUID NOT NULL REFERENCES stations(id),
                        cashier_user_id UUID NOT NULL REFERENCES users(id),
                        opened_at TIMESTAMP WITH TIME ZONE NOT NULL,
                        closed_at TIMESTAMP WITH TIME ZONE,
                        opening_cash NUMERIC(15, 2) NOT NULL DEFAULT 0,
                        status VARCHAR(20) NOT NULL, -- OPEN, CLOSED
                        opened_by UUID NOT NULL REFERENCES users(id),
                        closed_by UUID REFERENCES users(id),
                        notes TEXT,

    -- Transversais
                        version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE shift_allocations (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   shift_id UUID NOT NULL REFERENCES shifts(id),
                                   user_id UUID NOT NULL REFERENCES users(id),
                                   role role_type NOT NULL
);

CREATE TABLE nozzle_readings (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 shift_id UUID NOT NULL REFERENCES shifts(id),
                                 nozzle_id UUID NOT NULL REFERENCES nozzles(id),
                                 reading_type reading_type NOT NULL,
                                 meter_reading NUMERIC(15, 4) NOT NULL,
                                 read_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 recorded_by UUID NOT NULL REFERENCES users(id),
                                 notes TEXT,

    -- Transversais
                                 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE cash_movements (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                shift_id UUID NOT NULL REFERENCES shifts(id),
                                movement_type cash_movement_type NOT NULL,
                                payment_method_id UUID REFERENCES payment_methods(id),
                                amount NUMERIC(15, 2) NOT NULL,
                                occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                reference VARCHAR(100),
                                reason TEXT,
                                created_by UUID NOT NULL REFERENCES users(id),

    -- Transversais
                                source_type VARCHAR(50),
                                source_id VARCHAR(100),
                                approval_status approval_status,
                                approved_by UUID REFERENCES users(id),
                                approved_at TIMESTAMP WITH TIME ZONE,
                                version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE shift_payment_closings (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        shift_id UUID NOT NULL REFERENCES shifts(id),
                                        payment_method_id UUID NOT NULL REFERENCES payment_methods(id),
                                        expected_amount NUMERIC(15, 2) NOT NULL,
                                        declared_amount NUMERIC(15, 2) NOT NULL,
                                        difference_amount NUMERIC(15, 2) NOT NULL,
                                        evidence_reference VARCHAR(255),

    -- Transversais
                                        version BIGINT NOT NULL DEFAULT 0
);

-- =============================================================================
-- 6. VENDA E RECEBIMENTO
-- =============================================================================

CREATE TABLE sales (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       station_id UUID NOT NULL REFERENCES stations(id),
                       shift_id UUID NOT NULL REFERENCES shifts(id),
                       sale_number VARCHAR(50) NOT NULL,
                       status sale_status NOT NULL DEFAULT 'OPEN',
                       sold_at TIMESTAMP WITH TIME ZONE NOT NULL,
                       customer_id UUID REFERENCES customers(id),
                       vehicle_id UUID REFERENCES customer_vehicles(id),
                       attendant_user_id UUID REFERENCES users(id),
                       cashier_user_id UUID NOT NULL REFERENCES users(id),
                       subtotal NUMERIC(15, 2) NOT NULL,
                       discount_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
                       total_amount NUMERIC(15, 2) NOT NULL,
                       fiscal_status VARCHAR(50),
                       notes TEXT,

    -- Transversais
                       approval_status approval_status,
                       approved_by UUID REFERENCES users(id),
                       approved_at TIMESTAMP WITH TIME ZONE,
                       cancelled_at TIMESTAMP WITH TIME ZONE,
                       cancelled_by UUID REFERENCES users(id),
                       cancellation_reason TEXT,
                       external_id VARCHAR(100),
                       version BIGINT NOT NULL DEFAULT 0,

                       CONSTRAINT uk_sale_station_number UNIQUE (station_id, sale_number)
);

CREATE TABLE sale_items (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            sale_id UUID NOT NULL REFERENCES sales(id),
                            line_number INT NOT NULL,
                            product_id UUID NOT NULL REFERENCES products(id),
                            nozzle_id UUID REFERENCES nozzles(id),
                            tank_id UUID REFERENCES tanks(id),
                            quantity NUMERIC(15, 4) NOT NULL,
                            unit_price NUMERIC(15, 4) NOT NULL,
                            discount_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
                            total_amount NUMERIC(15, 2) NOT NULL,
                            meter_start NUMERIC(15, 4),
                            meter_end NUMERIC(15, 4),

    -- Transversais
                            version BIGINT NOT NULL DEFAULT 0,

                            CONSTRAINT uk_sale_item_line UNIQUE (sale_id, line_number)
);

CREATE TABLE sale_payments (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               sale_id UUID NOT NULL REFERENCES sales(id),
                               payment_method_id UUID NOT NULL REFERENCES payment_methods(id),
                               amount NUMERIC(15, 2) NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               transaction_reference VARCHAR(100),
                               authorized_at TIMESTAMP WITH TIME ZONE,
                               received_at TIMESTAMP WITH TIME ZONE,

    -- Transversais
                               version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE sale_refunds (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              sale_id UUID NOT NULL REFERENCES sales(id),
                              reason TEXT NOT NULL,
                              requested_by UUID NOT NULL REFERENCES users(id),
                              approved_by UUID NOT NULL REFERENCES users(id),
                              refunded_at TIMESTAMP WITH TIME ZONE NOT NULL,
                              notes TEXT,

    -- Transversais
                              approval_status approval_status DEFAULT 'APPROVED',
                              approved_at TIMESTAMP WITH TIME ZONE,
                              version BIGINT NOT NULL DEFAULT 0
);

-- =============================================================================
-- 7. ESTOQUE E FINANCEIRO
-- =============================================================================

CREATE TABLE stock_movements (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 station_id UUID NOT NULL REFERENCES stations(id),
                                 product_id UUID NOT NULL REFERENCES products(id),
                                 tank_id UUID REFERENCES tanks(id),
                                 movement_type stock_movement_type NOT NULL,
                                 quantity NUMERIC(15, 4) NOT NULL,
                                 unit_cost NUMERIC(15, 4),
                                 occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                 source_type VARCHAR(50) NOT NULL,
                                 source_id VARCHAR(100) NOT NULL,
                                 created_by UUID NOT NULL REFERENCES users(id),
                                 notes TEXT,

    -- Transversais
                                 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE tank_measurements (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   tank_id UUID NOT NULL REFERENCES tanks(id),
                                   measured_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                   volume_liters NUMERIC(15, 4) NOT NULL,
                                   water_liters NUMERIC(15, 4),
                                   temperature_celsius NUMERIC(4, 2),
                                   recorded_by UUID NOT NULL REFERENCES users(id),
                                   notes TEXT,

    -- Transversais
                                   version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE inventories (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             station_id UUID NOT NULL REFERENCES stations(id),
                             started_at TIMESTAMP WITH TIME ZONE NOT NULL,
                             finished_at TIMESTAMP WITH TIME ZONE,
                             status VARCHAR(30) NOT NULL,
                             created_by UUID NOT NULL REFERENCES users(id),
                             approved_by UUID REFERENCES users(id),

    -- Transversais
                             approval_status approval_status,
                             approved_at TIMESTAMP WITH TIME ZONE,
                             version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE inventory_items (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 inventory_id UUID NOT NULL REFERENCES inventories(id),
                                 product_id UUID NOT NULL REFERENCES products(id),
                                 tank_id UUID REFERENCES tanks(id),
                                 book_quantity NUMERIC(15, 4) NOT NULL,
                                 counted_quantity NUMERIC(15, 4) NOT NULL,
                                 difference_quantity NUMERIC(15, 4) NOT NULL,
                                 adjustment_movement_id UUID REFERENCES stock_movements(id),

    -- Transversais
                                 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE accounts_receivable (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     company_id UUID NOT NULL REFERENCES companies(id),
                                     station_id UUID NOT NULL REFERENCES stations(id),
                                     customer_id UUID NOT NULL REFERENCES customers(id),
                                     sale_id UUID REFERENCES sales(id),
                                     due_date DATE NOT NULL,
                                     original_amount NUMERIC(15, 2) NOT NULL,
                                     open_amount NUMERIC(15, 2) NOT NULL,
                                     status VARCHAR(30) NOT NULL, -- OPEN, PARTIALLY_PAID, PAID, CANCELLED

    -- Transversais
                                     source_type VARCHAR(50),
                                     source_id VARCHAR(100),
                                     cancelled_at TIMESTAMP WITH TIME ZONE,
                                     cancelled_by UUID REFERENCES users(id),
                                     cancellation_reason TEXT,
                                     external_id VARCHAR(100),
                                     version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE financial_receipts (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    receivable_id UUID NOT NULL REFERENCES accounts_receivable(id),
                                    payment_method_id UUID NOT NULL REFERENCES payment_methods(id),
                                    amount NUMERIC(15, 2) NOT NULL,
                                    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                    reference VARCHAR(100),
                                    received_by UUID NOT NULL REFERENCES users(id),

    -- Transversais
                                    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE accounts_payable (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  company_id UUID NOT NULL REFERENCES companies(id),
                                  station_id UUID NOT NULL REFERENCES stations(id),
                                  supplier_id UUID REFERENCES suppliers(id),
                                  goods_receipt_id UUID REFERENCES goods_receipts(id),
                                  description VARCHAR(255) NOT NULL,
                                  due_date DATE NOT NULL,
                                  original_amount NUMERIC(15, 2) NOT NULL,
                                  open_amount NUMERIC(15, 2) NOT NULL,
                                  status VARCHAR(30) NOT NULL, -- OPEN, PARTIALLY_PAID, PAID, CANCELLED

    -- Transversais
                                  source_type VARCHAR(50),
                                  source_id VARCHAR(100),
                                  cancelled_at TIMESTAMP WITH TIME ZONE,
                                  cancelled_by UUID REFERENCES users(id),
                                  cancellation_reason TEXT,
                                  external_id VARCHAR(100),
                                  version BIGINT NOT NULL DEFAULT 0
);

-- =============================================================================
-- 8. ÍNDICES DE PERFORMANCE (FOREIGN KEYS E SEARCHES)
-- =============================================================================

CREATE INDEX idx_stations_company ON stations(company_id);
CREATE INDEX idx_user_station_roles_user ON user_station_roles(user_id);
CREATE INDEX idx_products_company ON products(company_id);
CREATE INDEX idx_tanks_station ON tanks(station_id);
CREATE INDEX idx_nozzles_pump ON nozzles(pump_id);
CREATE INDEX idx_sales_station_shift ON sales(station_id, shift_id);
CREATE INDEX idx_sale_items_sale ON sale_items(sale_id);
CREATE INDEX idx_stock_movements_station_product ON stock_movements(station_id, product_id);
CREATE INDEX idx_accounts_receivable_customer ON accounts_receivable(customer_id);
CREATE INDEX idx_accounts_payable_supplier ON accounts_payable(supplier_id);