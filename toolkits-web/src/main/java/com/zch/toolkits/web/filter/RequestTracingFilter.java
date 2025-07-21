package com.zch.toolkits.web.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class RequestTracingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String traceId = servletRequest.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = servletRequest.getHeader("X-Request-Id");
        }
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
            ((HttpServletResponse) response).addHeader("X-Trace-Id", traceId);
        }
        try {
            MDC.put("TRACE_ID", traceId);
            chain.doFilter(request, response);
        } finally {
            MDC.remove("TRACE_ID");
        }
    }
}
