package com.br.manager.domain.configuracao.multitenant;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
class FiltroTenant implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // --- CÓDIGO PARA VER TODOS OS HEADERS ---
        System.out.println("====== INICIO DOS HEADERS ======");
        java.util.Collections.list(req.getHeaderNames()).forEach(headerName -> {
            String headerValor = req.getHeader(headerName);
            System.out.println(headerName + ": " + headerValor);
        });
        System.out.println("====== FIM DOS HEADERS ======");
        // ----------------------------------------

        String tenantName = req.getHeader("x-tenant-id");
        ContextoTenant.setCurrentTenant(tenantName);

        try {
            chain.doFilter(request, response);
        } finally {
            ContextoTenant.setCurrentTenant("");
        }
    }
}