package com.br.manager.domain.common.entity;

public enum SincronizeNameTableEnum {
    USER("users"),
    COMPANY("companies"),
    STATION("stations");

    private final String tableName;

    SincronizeNameTableEnum(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}