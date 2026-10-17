package com.br.manager.domain.common;

import java.time.LocalDateTime;

public class SincronizeDTO {
    private String nameTable;
    private LocalDateTime lastSincronize;

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

    public LocalDateTime getLastSincronize() {
        return lastSincronize;
    }

    public void setLastSincronize(LocalDateTime lastSincronize) {
        this.lastSincronize = lastSincronize;
    }
}
