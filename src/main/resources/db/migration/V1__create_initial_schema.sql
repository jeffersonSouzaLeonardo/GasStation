-- =============================================================================
-- Flyway Migration: V1__create_initial_schema.sql
-- Descrição: Criação de todas as tabelas do sistema de gestão de postos (MySQL)
-- =============================================================================

-- =============================================================================
-- 1. ORGANIZAÇÃO E ACESSO
-- =============================================================================

CREATE TABLE companies (
                           id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                           legal_name VARCHAR(255) NOT NULL,
                           trade_name VARCHAR(255),
                           cnpj VARCHAR(14) NOT NULL UNIQUE,
                           state_registration VARCHAR(30),
                           tax_regime VARCHAR(50),
                           email VARCHAR(255),
                           phone VARCHAR(20),
                           active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE stations (
                          id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                          company_id VARCHAR(36) NOT NULL,
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
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_stations_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE users (
                       id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       last_login_at DATETIME,

    -- Transversais
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_station_roles (
                                    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                    user_id VARCHAR(36) NOT NULL,
                                    company_id VARCHAR(36) NOT NULL,
                                    station_id VARCHAR(36),
                                    role ENUM('ADMIN', 'MANAGER', 'CASHIER', 'ATTENDANT') NOT NULL,
                                    active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Transversais
                                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT fk_usr_user FOREIGN KEY (user_id) REFERENCES users(id),
                                    CONSTRAINT fk_usr_company FOREIGN KEY (company_id) REFERENCES companies(id),
                                    CONSTRAINT fk_usr_station FOREIGN KEY (station_id) REFERENCES stations(id)
);

CREATE TABLE audit_logs (
                            id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                            company_id VARCHAR(36) NOT NULL,
                            user_id VARCHAR(36),
                            entity_name VARCHAR(100) NOT NULL,
                            entity_id VARCHAR(100) NOT NULL,
                            action VARCHAR(50) NOT NULL,
                            before_data JSON,
                            after_data JSON,
                            ip_address VARCHAR(45),
                            created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_company FOREIGN KEY (company_id) REFERENCES companies(id),
                            CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =============================================================================
-- 2. CADASTROS OPERACIONAIS
-- =============================================================================

CREATE TABLE products (
                          id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                          company_id VARCHAR(36) NOT NULL,
                          sku VARCHAR(100) NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          product_type ENUM('FUEL', 'LUBRICANT', 'SERVICE', 'CONVENIENCE') NOT NULL,
                          unit ENUM('L', 'UN', 'KG', 'M3') NOT NULL,
                          anp_code VARCHAR(20),
                          ncm VARCHAR(10),
                          cest VARCHAR(10),
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT uk_product_company_sku UNIQUE (company_id, sku),
                          CONSTRAINT fk_products_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE station_products (
                                  id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                  station_id VARCHAR(36) NOT NULL,
                                  product_id VARCHAR(36) NOT NULL,
                                  min_stock DECIMAL(15, 4) DEFAULT 0,
                                  reorder_point DECIMAL(15, 4) DEFAULT 0,
                                  active BOOLEAN NOT NULL DEFAULT TRUE,

                                  CONSTRAINT uk_station_product UNIQUE (station_id, product_id),
                                  CONSTRAINT fk_sp_station FOREIGN KEY (station_id) REFERENCES stations(id),
                                  CONSTRAINT fk_sp_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE tanks (
                       id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       station_id VARCHAR(36) NOT NULL,
                       code VARCHAR(50) NOT NULL,
                       product_id VARCHAR(36) NOT NULL,
                       capacity_liters DECIMAL(15, 4) NOT NULL,
                       dead_stock_liters DECIMAL(15, 4) DEFAULT 0,
                       current_book_liters DECIMAL(15, 4) NOT NULL DEFAULT 0,
                       active BOOLEAN NOT NULL DEFAULT TRUE,

                       CONSTRAINT uk_tank_station_code UNIQUE (station_id, code),
                       CONSTRAINT fk_tanks_station FOREIGN KEY (station_id) REFERENCES stations(id),
                       CONSTRAINT fk_tanks_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE pumps (
                       id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       station_id VARCHAR(36) NOT NULL,
                       code VARCHAR(50) NOT NULL,
                       location VARCHAR(100),
                       active BOOLEAN NOT NULL DEFAULT TRUE,

                       CONSTRAINT uk_pump_station_code UNIQUE (station_id, code),
                       CONSTRAINT fk_pumps_station FOREIGN KEY (station_id) REFERENCES stations(id)
);

CREATE TABLE nozzles (
                         id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                         pump_id VARCHAR(36) NOT NULL,
                         tank_id VARCHAR(36) NOT NULL,
                         code VARCHAR(50) NOT NULL,
                         product_id VARCHAR(36) NOT NULL,
                         meter_number DECIMAL(15, 4) DEFAULT 0,
                         active BOOLEAN NOT NULL DEFAULT TRUE,

                         CONSTRAINT uk_nozzle_pump_code UNIQUE (pump_id, code),
                         CONSTRAINT fk_nozzles_pump FOREIGN KEY (pump_id) REFERENCES pumps(id),
                         CONSTRAINT fk_nozzles_tank FOREIGN KEY (tank_id) REFERENCES tanks(id),
                         CONSTRAINT fk_nozzles_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE suppliers (
                           id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                           company_id VARCHAR(36) NOT NULL,
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

                           CONSTRAINT fk_suppliers_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE customers (
                           id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                           company_id VARCHAR(36) NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           person_type ENUM('PHYSICAL', 'JURIDICAL') NOT NULL,
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

                           credit_limit DECIMAL(15, 2) DEFAULT 0,
                           active BOOLEAN NOT NULL DEFAULT TRUE,

                           CONSTRAINT fk_customers_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE customer_vehicles (
                                   id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                   customer_id VARCHAR(36) NOT NULL,
                                   plate VARCHAR(10) NOT NULL,
                                   brand VARCHAR(50),
                                   model VARCHAR(50),
                                   fuel_type VARCHAR(30),
                                   active BOOLEAN NOT NULL DEFAULT TRUE,

                                   CONSTRAINT fk_vehicles_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE payment_methods (
                                 id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 company_id VARCHAR(36) NOT NULL,
                                 name VARCHAR(100) NOT NULL,
                                 kind VARCHAR(50) NOT NULL,
                                 requires_reference BOOLEAN NOT NULL DEFAULT FALSE,
                                 settlement_days INT DEFAULT 0,
                                 active BOOLEAN NOT NULL DEFAULT TRUE,

                                 CONSTRAINT fk_pm_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- =============================================================================
-- 3. PREÇO E COMPRA
-- =============================================================================

CREATE TABLE price_tables (
                              id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                              station_id VARCHAR(36) NOT NULL,
                              name VARCHAR(100) NOT NULL,
                              valid_from DATETIME NOT NULL,
                              valid_to DATETIME,
                              status VARCHAR(20) NOT NULL,
                              created_by VARCHAR(36) NOT NULL,

                              CONSTRAINT fk_pt_station FOREIGN KEY (station_id) REFERENCES stations(id),
                              CONSTRAINT fk_pt_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE price_items (
                             id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                             price_table_id VARCHAR(36) NOT NULL,
                             product_id VARCHAR(36) NOT NULL,
                             nozzle_id VARCHAR(36),
                             unit_price DECIMAL(15, 4) NOT NULL,
                             min_price DECIMAL(15, 4),
                             max_discount_percent DECIMAL(5, 2),
                             active BOOLEAN NOT NULL DEFAULT TRUE,

                             CONSTRAINT fk_pi_table FOREIGN KEY (price_table_id) REFERENCES price_tables(id),
                             CONSTRAINT fk_pi_product FOREIGN KEY (product_id) REFERENCES products(id),
                             CONSTRAINT fk_pi_nozzle FOREIGN KEY (nozzle_id) REFERENCES nozzles(id)
);

CREATE TABLE purchase_orders (
                                 id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 station_id VARCHAR(36) NOT NULL,
                                 supplier_id VARCHAR(36) NOT NULL,
                                 number VARCHAR(50),
                                 status VARCHAR(30) NOT NULL,
                                 ordered_at DATETIME,
                                 expected_at DATETIME,
                                 total_amount DECIMAL(15, 2) DEFAULT 0,
                                 created_by VARCHAR(36) NOT NULL,

    -- Transversais
                                 approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
                                 approved_by VARCHAR(36),
                                 approved_at DATETIME,
                                 cancelled_at DATETIME,
                                 cancelled_by VARCHAR(36),
                                 cancellation_reason TEXT,

                                 CONSTRAINT fk_po_station FOREIGN KEY (station_id) REFERENCES stations(id),
                                 CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
                                 CONSTRAINT fk_po_created_by FOREIGN KEY (created_by) REFERENCES users(id),
                                 CONSTRAINT fk_po_approved_by FOREIGN KEY (approved_by) REFERENCES users(id),
                                 CONSTRAINT fk_po_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id)
);

CREATE TABLE purchase_order_items (
                                      id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                      purchase_order_id VARCHAR(36) NOT NULL,
                                      product_id VARCHAR(36) NOT NULL,
                                      quantity DECIMAL(15, 4) NOT NULL,
                                      unit_cost DECIMAL(15, 4) NOT NULL,
                                      total_cost DECIMAL(15, 2) NOT NULL,

                                      CONSTRAINT fk_poi_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
                                      CONSTRAINT fk_poi_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE goods_receipts (
                                id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                station_id VARCHAR(36) NOT NULL,
                                supplier_id VARCHAR(36) NOT NULL,
                                purchase_order_id VARCHAR(36),
                                invoice_number VARCHAR(50),
                                invoice_key VARCHAR(44),
                                received_at DATETIME NOT NULL,
                                status VARCHAR(30) NOT NULL,
                                freight_amount DECIMAL(15, 2) DEFAULT 0,
                                other_costs DECIMAL(15, 2) DEFAULT 0,
                                received_by VARCHAR(36) NOT NULL,

                                CONSTRAINT fk_gr_station FOREIGN KEY (station_id) REFERENCES stations(id),
                                CONSTRAINT fk_gr_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
                                CONSTRAINT fk_gr_po FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id),
                                CONSTRAINT fk_gr_received_by FOREIGN KEY (received_by) REFERENCES users(id)
);

CREATE TABLE goods_receipt_items (
                                     id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                     goods_receipt_id VARCHAR(36) NOT NULL,
                                     product_id VARCHAR(36) NOT NULL,
                                     tank_id VARCHAR(36),
                                     quantity DECIMAL(15, 4) NOT NULL,
                                     unit_cost DECIMAL(15, 4) NOT NULL,
                                     total_cost DECIMAL(15, 2) NOT NULL,

                                     CONSTRAINT fk_gri_receipt FOREIGN KEY (goods_receipt_id) REFERENCES goods_receipts(id),
                                     CONSTRAINT fk_gri_product FOREIGN KEY (product_id) REFERENCES products(id),
                                     CONSTRAINT fk_gri_tank FOREIGN KEY (tank_id) REFERENCES tanks(id)
);

-- =============================================================================
-- 4. TURNO, CAIXA E AFERIÇÃO
-- =============================================================================

CREATE TABLE shifts (
                        id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                        station_id VARCHAR(36) NOT NULL,
                        cashier_user_id VARCHAR(36) NOT NULL,
                        opened_at DATETIME NOT NULL,
                        closed_at DATETIME,
                        opening_cash DECIMAL(15, 2) NOT NULL DEFAULT 0,
                        status VARCHAR(20) NOT NULL,
                        opened_by VARCHAR(36) NOT NULL,
                        closed_by VARCHAR(36),
                        notes TEXT,

                        CONSTRAINT fk_shifts_station FOREIGN KEY (station_id) REFERENCES stations(id),
                        CONSTRAINT fk_shifts_cashier FOREIGN KEY (cashier_user_id) REFERENCES users(id),
                        CONSTRAINT fk_shifts_opened_by FOREIGN KEY (opened_by) REFERENCES users(id),
                        CONSTRAINT fk_shifts_closed_by FOREIGN KEY (closed_by) REFERENCES users(id)
);

CREATE TABLE shift_allocations (
                                   id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                   shift_id VARCHAR(36) NOT NULL,
                                   user_id VARCHAR(36) NOT NULL,
                                   role ENUM('ADMIN', 'MANAGER', 'CASHIER', 'ATTENDANT') NOT NULL,

                                   CONSTRAINT fk_sa_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
                                   CONSTRAINT fk_sa_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE nozzle_readings (
                                 id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 shift_id VARCHAR(36) NOT NULL,
                                 nozzle_id VARCHAR(36) NOT NULL,
                                 reading_type ENUM('OPENING', 'CLOSING', 'TEST', 'CALIBRATION') NOT NULL,
                                 meter_reading DECIMAL(15, 4) NOT NULL,
                                 read_at DATETIME NOT NULL,
                                 recorded_by VARCHAR(36) NOT NULL,
                                 notes TEXT,

                                 CONSTRAINT fk_nr_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
                                 CONSTRAINT fk_nr_nozzle FOREIGN KEY (nozzle_id) REFERENCES nozzles(id),
                                 CONSTRAINT fk_nr_recorded_by FOREIGN KEY (recorded_by) REFERENCES users(id)
);

CREATE TABLE cash_movements (
                                id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                shift_id VARCHAR(36) NOT NULL,
                                movement_type ENUM('OPENING', 'SALE', 'WITHDRAWAL', 'CASH_IN', 'REFUND', 'ADJUSTMENT', 'CLOSING') NOT NULL,
                                payment_method_id VARCHAR(36),
                                amount DECIMAL(15, 2) NOT NULL,
                                occurred_at DATETIME NOT NULL,
                                reference VARCHAR(100),
                                reason TEXT,
                                created_by VARCHAR(36) NOT NULL,

    -- Transversais
                                source_type VARCHAR(50),
                                source_id VARCHAR(100),
                                approval_status ENUM('PENDING', 'APPROVED', 'REJECTED'),
                                approved_by VARCHAR(36),
                                approved_at DATETIME,

                                CONSTRAINT fk_cm_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
                                CONSTRAINT fk_cm_pm FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
                                CONSTRAINT fk_cm_created_by FOREIGN KEY (created_by) REFERENCES users(id),
                                CONSTRAINT fk_cm_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE shift_payment_closings (
                                        id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                        shift_id VARCHAR(36) NOT NULL,
                                        payment_method_id VARCHAR(36) NOT NULL,
                                        expected_amount DECIMAL(15, 2) NOT NULL,
                                        declared_amount DECIMAL(15, 2) NOT NULL,
                                        difference_amount DECIMAL(15, 2) NOT NULL,
                                        evidence_reference VARCHAR(255),

                                        CONSTRAINT fk_spc_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
                                        CONSTRAINT fk_spc_pm FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

-- =============================================================================
-- 5. VENDA E RECEBIMENTO
-- =============================================================================

CREATE TABLE sales (
                       id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                       station_id VARCHAR(36) NOT NULL,
                       shift_id VARCHAR(36) NOT NULL,
                       sale_number VARCHAR(50) NOT NULL,
                       status ENUM('OPEN', 'PAID', 'CANCELLED', 'REFUNDED', 'PENDING_CREDIT') NOT NULL DEFAULT 'OPEN',
                       sold_at DATETIME NOT NULL,
                       customer_id VARCHAR(36),
                       vehicle_id VARCHAR(36),
                       attendant_user_id VARCHAR(36),
                       cashier_user_id VARCHAR(36) NOT NULL,
                       subtotal DECIMAL(15, 2) NOT NULL,
                       discount_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
                       total_amount DECIMAL(15, 2) NOT NULL,
                       fiscal_status VARCHAR(50),
                       notes TEXT,

    -- Transversais
                       approval_status ENUM('PENDING', 'APPROVED', 'REJECTED'),
                       approved_by VARCHAR(36),
                       approved_at DATETIME,
                       cancelled_at DATETIME,
                       cancelled_by VARCHAR(36),
                       cancellation_reason TEXT,

                       CONSTRAINT uk_sale_station_number UNIQUE (station_id, sale_number),
                       CONSTRAINT fk_sales_station FOREIGN KEY (station_id) REFERENCES stations(id),
                       CONSTRAINT fk_sales_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
                       CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
                       CONSTRAINT fk_sales_vehicle FOREIGN KEY (vehicle_id) REFERENCES customer_vehicles(id),
                       CONSTRAINT fk_sales_attendant FOREIGN KEY (attendant_user_id) REFERENCES users(id),
                       CONSTRAINT fk_sales_cashier FOREIGN KEY (cashier_user_id) REFERENCES users(id),
                       CONSTRAINT fk_sales_approved_by FOREIGN KEY (approved_by) REFERENCES users(id),
                       CONSTRAINT fk_sales_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id)
);

CREATE TABLE sale_items (
                            id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                            sale_id VARCHAR(36) NOT NULL,
                            line_number INT NOT NULL,
                            product_id VARCHAR(36) NOT NULL,
                            nozzle_id VARCHAR(36),
                            tank_id VARCHAR(36),
                            quantity DECIMAL(15, 4) NOT NULL,
                            unit_price DECIMAL(15, 4) NOT NULL,
                            discount_amount DECIMAL(15, 2) NOT NULL DEFAULT 0,
                            total_amount DECIMAL(15, 2) NOT NULL,
                            meter_start DECIMAL(15, 4),
                            meter_end DECIMAL(15, 4),

                            CONSTRAINT uk_sale_item_line UNIQUE (sale_id, line_number),
                            CONSTRAINT fk_si_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
                            CONSTRAINT fk_si_product FOREIGN KEY (product_id) REFERENCES products(id),
                            CONSTRAINT fk_si_nozzle FOREIGN KEY (nozzle_id) REFERENCES nozzles(id),
                            CONSTRAINT fk_si_tank FOREIGN KEY (tank_id) REFERENCES tanks(id)
);

CREATE TABLE sale_payments (
                               id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                               sale_id VARCHAR(36) NOT NULL,
                               payment_method_id VARCHAR(36) NOT NULL,
                               amount DECIMAL(15, 2) NOT NULL,
                               status VARCHAR(30) NOT NULL,
                               transaction_reference VARCHAR(100),
                               authorized_at DATETIME,
                               received_at DATETIME,

                               CONSTRAINT fk_spay_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
                               CONSTRAINT fk_spay_pm FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

CREATE TABLE sale_refunds (
                              id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                              sale_id VARCHAR(36) NOT NULL,
                              reason TEXT NOT NULL,
                              requested_by VARCHAR(36) NOT NULL,
                              approved_by VARCHAR(36) NOT NULL,
                              refunded_at DATETIME NOT NULL,
                              notes TEXT,

    -- Transversais
                              approval_status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED',
                              approved_at DATETIME,

                              CONSTRAINT fk_sr_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
                              CONSTRAINT fk_sr_requested_by FOREIGN KEY (requested_by) REFERENCES users(id),
                              CONSTRAINT fk_sr_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

-- =============================================================================
-- 6. ESTOQUE E FINANCEIRO
-- =============================================================================

CREATE TABLE stock_movements (
                                 id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 station_id VARCHAR(36) NOT NULL,
                                 product_id VARCHAR(36) NOT NULL,
                                 tank_id VARCHAR(36),
                                 movement_type ENUM('PURCHASE_RECEIPT', 'SALE', 'RETURN', 'TRANSFER_IN', 'TRANSFER_OUT', 'INVENTORY_ADJUSTMENT', 'LOSS', 'TEST', 'OPENING_BALANCE') NOT NULL,
                                 quantity DECIMAL(15, 4) NOT NULL,
                                 unit_cost DECIMAL(15, 4),
                                 occurred_at DATETIME NOT NULL,
                                 source_type VARCHAR(50) NOT NULL,
                                 source_id VARCHAR(100) NOT NULL,
                                 created_by VARCHAR(36) NOT NULL,
                                 notes TEXT,

                                 CONSTRAINT fk_sm_station FOREIGN KEY (station_id) REFERENCES stations(id),
                                 CONSTRAINT fk_sm_product FOREIGN KEY (product_id) REFERENCES products(id),
                                 CONSTRAINT fk_sm_tank FOREIGN KEY (tank_id) REFERENCES tanks(id),
                                 CONSTRAINT fk_sm_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE tank_measurements (
                                   id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                   tank_id VARCHAR(36) NOT NULL,
                                   measured_at DATETIME NOT NULL,
                                   volume_liters DECIMAL(15, 4) NOT NULL,
                                   water_liters DECIMAL(15, 4),
                                   temperature_celsius DECIMAL(4, 2),
                                   recorded_by VARCHAR(36) NOT NULL,
                                   notes TEXT,

                                   CONSTRAINT fk_tm_tank FOREIGN KEY (tank_id) REFERENCES tanks(id),
                                   CONSTRAINT fk_tm_recorded_by FOREIGN KEY (recorded_by) REFERENCES users(id)
);

CREATE TABLE inventories (
                             id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                             station_id VARCHAR(36) NOT NULL,
                             started_at DATETIME NOT NULL,
                             finished_at DATETIME,
                             status VARCHAR(30) NOT NULL,
                             created_by VARCHAR(36) NOT NULL,
                             approved_by VARCHAR(36),

    -- Transversais
                             approval_status ENUM('PENDING', 'APPROVED', 'REJECTED'),
                             approved_at DATETIME,

                             CONSTRAINT fk_inv_station FOREIGN KEY (station_id) REFERENCES stations(id),
                             CONSTRAINT fk_inv_created_by FOREIGN KEY (created_by) REFERENCES users(id),
                             CONSTRAINT fk_inv_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE inventory_items (
                                 id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                 inventory_id VARCHAR(36) NOT NULL,
                                 product_id VARCHAR(36) NOT NULL,
                                 tank_id VARCHAR(36),
                                 book_quantity DECIMAL(15, 4) NOT NULL,
                                 counted_quantity DECIMAL(15, 4) NOT NULL,
                                 difference_quantity DECIMAL(15, 4) NOT NULL,
                                 adjustment_movement_id VARCHAR(36),

                                 CONSTRAINT fk_ii_inventory FOREIGN KEY (inventory_id) REFERENCES inventories(id),
                                 CONSTRAINT fk_ii_product FOREIGN KEY (product_id) REFERENCES products(id),
                                 CONSTRAINT fk_ii_tank FOREIGN KEY (tank_id) REFERENCES tanks(id),
                                 CONSTRAINT fk_ii_movement FOREIGN KEY (adjustment_movement_id) REFERENCES stock_movements(id)
);

CREATE TABLE accounts_receivable (
                                     id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                     company_id VARCHAR(36) NOT NULL,
                                     station_id VARCHAR(36) NOT NULL,
                                     customer_id VARCHAR(36) NOT NULL,
                                     sale_id VARCHAR(36),
                                     due_date DATE NOT NULL,
                                     original_amount DECIMAL(15, 2) NOT NULL,
                                     open_amount DECIMAL(15, 2) NOT NULL,
                                     status VARCHAR(30) NOT NULL,

    -- Transversais
                                     source_type VARCHAR(50),
                                     source_id VARCHAR(100),
                                     cancelled_at DATETIME,
                                     cancelled_by VARCHAR(36),
                                     cancellation_reason TEXT,

                                     CONSTRAINT fk_ar_company FOREIGN KEY (company_id) REFERENCES companies(id),
                                     CONSTRAINT fk_ar_station FOREIGN KEY (station_id) REFERENCES stations(id),
                                     CONSTRAINT fk_ar_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
                                     CONSTRAINT fk_ar_sale FOREIGN KEY (sale_id) REFERENCES sales(id),
                                     CONSTRAINT fk_ar_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id)
);

CREATE TABLE financial_receipts (
                                    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                    receivable_id VARCHAR(36) NOT NULL,
                                    payment_method_id VARCHAR(36) NOT NULL,
                                    amount DECIMAL(15, 2) NOT NULL,
                                    received_at DATETIME NOT NULL,
                                    reference VARCHAR(100),
                                    received_by VARCHAR(36) NOT NULL,

                                    CONSTRAINT fk_fr_receivable FOREIGN KEY (receivable_id) REFERENCES accounts_receivable(id),
                                    CONSTRAINT fk_fr_pm FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
                                    CONSTRAINT fk_fr_received_by FOREIGN KEY (received_by) REFERENCES users(id)
);

CREATE TABLE accounts_payable (
                                  id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
                                  company_id VARCHAR(36) NOT NULL,
                                  station_id VARCHAR(36) NOT NULL,
                                  supplier_id VARCHAR(36),
                                  goods_receipt_id VARCHAR(36),
                                  description VARCHAR(255) NOT NULL,
                                  due_date DATE NOT NULL,
                                  original_amount DECIMAL(15, 2) NOT NULL,
                                  open_amount DECIMAL(15, 2) NOT NULL,
                                  status VARCHAR(30) NOT NULL,

    -- Transversais
                                  source_type VARCHAR(50),
                                  source_id VARCHAR(100),
                                  cancelled_at DATETIME,
                                  cancelled_by VARCHAR(36),
                                  cancellation_reason TEXT,

                                  CONSTRAINT fk_ap_company FOREIGN KEY (company_id) REFERENCES companies(id),
                                  CONSTRAINT fk_ap_station FOREIGN KEY (station_id) REFERENCES stations(id),
                                  CONSTRAINT fk_ap_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
                                  CONSTRAINT fk_ap_receipt FOREIGN KEY (goods_receipt_id) REFERENCES goods_receipts(id),
                                  CONSTRAINT fk_ap_cancelled_by FOREIGN KEY (cancelled_by) REFERENCES users(id)
);

-- =============================================================================
-- 7. ÍNDICES DE PERFORMANCE
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