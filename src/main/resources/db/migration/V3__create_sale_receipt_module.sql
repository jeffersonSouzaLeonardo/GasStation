CREATE TABLE sale (
    id VARCHAR(36) PRIMARY KEY,
    station_id VARCHAR(36) NOT NULL,
    shift_id VARCHAR(36) NOT NULL,
    sale_number VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    sold_at DATETIME(6) NOT NULL,
    customer_id VARCHAR(36),
    vehicle_id VARCHAR(36),
    attendant_user_id VARCHAR(36),
    cashier_user_id VARCHAR(36) NOT NULL,
    subtotal DECIMAL(19, 4) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
    fiscal_status VARCHAR(40),
    notes VARCHAR(1000),
    CONSTRAINT uk_sale_station_number UNIQUE (station_id, sale_number),
    CONSTRAINT fk_sale_station FOREIGN KEY (station_id) REFERENCES stations(id),
    CONSTRAINT fk_sale_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_sale_vehicle FOREIGN KEY (vehicle_id) REFERENCES customer_vehicles(id),
    CONSTRAINT fk_sale_attendant FOREIGN KEY (attendant_user_id) REFERENCES users(id),
    CONSTRAINT fk_sale_cashier FOREIGN KEY (cashier_user_id) REFERENCES users(id)
);

CREATE TABLE sale_item (
    id VARCHAR(36) PRIMARY KEY,
    sale_id VARCHAR(36) NOT NULL,
    line_number INT NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    nozzle_id VARCHAR(36),
    tank_id VARCHAR(36),
    quantity DECIMAL(19, 4) NOT NULL,
    unit_price DECIMAL(19, 4) NOT NULL,
    discount_amount DECIMAL(19, 4) NOT NULL DEFAULT 0,
    meter_start DECIMAL(19, 4),
    meter_end DECIMAL(19, 4),
    CONSTRAINT uk_sale_item_line UNIQUE (sale_id, line_number),
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sale(id),
    CONSTRAINT fk_sale_item_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_sale_item_nozzle FOREIGN KEY (nozzle_id) REFERENCES nozzles(id),
    CONSTRAINT fk_sale_item_tank FOREIGN KEY (tank_id) REFERENCES tanks(id)
);

CREATE TABLE sale_payment (
    id VARCHAR(36) PRIMARY KEY,
    sale_id VARCHAR(36) NOT NULL,
    payment_method_id VARCHAR(36) NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_reference VARCHAR(120),
    authorized_at DATETIME(6),
    received_at DATETIME(6),
    CONSTRAINT fk_sale_payment_sale FOREIGN KEY (sale_id) REFERENCES sale(id),
    CONSTRAINT fk_sale_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
);

CREATE TABLE sale_refund (
    id VARCHAR(36) PRIMARY KEY,
    sale_id VARCHAR(36) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    requested_by VARCHAR(36) NOT NULL,
    approved_by VARCHAR(36) NOT NULL,
    refunded_at DATETIME(6) NOT NULL,
    notes VARCHAR(1000),
    CONSTRAINT fk_sale_refund_sale FOREIGN KEY (sale_id) REFERENCES sale(id),
    CONSTRAINT fk_sale_refund_requested_by FOREIGN KEY (requested_by) REFERENCES users(id),
    CONSTRAINT fk_sale_refund_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE INDEX idx_sale_station_id ON sale (station_id);
CREATE INDEX idx_sale_shift_id ON sale (shift_id);
CREATE INDEX idx_sale_status ON sale (status);
CREATE INDEX idx_sale_sold_at ON sale (sold_at);
CREATE INDEX idx_sale_payment_sale_id ON sale_payment (sale_id);
CREATE INDEX idx_sale_refund_sale_id ON sale_refund (sale_id);
CREATE INDEX idx_sale_item_sale_id ON sale_item (sale_id);
