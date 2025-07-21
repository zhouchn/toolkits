package com.zch.toolkits.web.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.util.Objects;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {
    private final String[] excludedPaths;

    public RequestLoggingFilter(String... excludedPaths) {
        this.excludedPaths = Objects.requireNonNullElseGet(excludedPaths, () -> new String[0]);
    }

    protected boolean shouldLog(HttpServletRequest request) {
        if (!this.logger.isDebugEnabled()) {
            return false;
        }
        String requestPath = request.getServletPath();
        for (String excludedPath : excludedPaths) {
            if (requestPath.startsWith(excludedPath)) {
                return false;
            }
        }
        return true;
    }

    protected void beforeRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }

    protected void afterRequest(HttpServletRequest request, String message) {
        this.logger.debug(message);
    }
}
