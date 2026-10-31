package com.br.manager.domain.common.entity;

public enum SynchronizeNameTableEnum {
    USER,
    COMPANY,
    STATION,
    NOZZLE,
    PRODUCT,
    PUMP,
    TANK,
    SUPPLIER,
    SHIFT,
    CASH_SESSION;

    private final String tableName;

    SynchronizeNameTableEnum() {
        this.tableName = name().toLowerCase();
    }

    public String getTableName() {
        return tableName;
    }
}