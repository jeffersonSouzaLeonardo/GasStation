package com.br.manager.infra.multitenant;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Profile({"test"})
@Component
@Order(1)
class TenantTestFilter implements Filter {

    @Value("${tenant.database.test}")
    private String databaseTest;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.setCurrentTenant(databaseTest);
        }
    }
}