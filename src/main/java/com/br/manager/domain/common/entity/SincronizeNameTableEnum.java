package com.br.manager.domain.common.entity;

public enum SincronizeNameTableEnum {
    USER,
    COMPANY,
    STATION;

    private final String tableName;

    SincronizeNameTableEnum() {
        this.tableName = name().toLowerCase();
    }

    public String getTableName() {
        return tableName;
    }
}