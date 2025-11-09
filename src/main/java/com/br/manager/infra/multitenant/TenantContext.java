package com.br.manager.infra.multitenant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TenantContext {
    private static final Logger logger = LogManager.getLogger(TenantContext.class);

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void setCurrentTenant(String tenant) {
        logger.info("####### DataBase setado: " + tenant );
        CURRENT_TENANT.set(tenant);
    }
}