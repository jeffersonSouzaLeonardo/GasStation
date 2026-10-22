package com.br.manager.domain.common.entity;

public enum SynchronizeNameTableEnum {
    USER,
    COMPANY,
    STATION;

    private final String tableName;

    SynchronizeNameTableEnum() {
        this.tableName = name().toLowerCase();
    }

    public String getTableName() {
        return tableName;
    }
}