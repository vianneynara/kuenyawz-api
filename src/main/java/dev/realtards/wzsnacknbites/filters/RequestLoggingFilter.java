package dev.realtards.wzsnacknbites.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * This class is used to log incoming and outgoing request and responses.
 * This is VERY helpful for debugging our code.
 */
@Order(1)
@Slf4j
public class RequestLoggingFilter implements Filter {
    
    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // get request details
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();
        String remoteAddr = httpRequest.getRemoteAddr();
        
        // format the log message
        String logMessage = String.format("REQ: %s %s%s from %s",
            method,
            uri,
            queryString != null ? "?" + queryString : "",
            remoteAddr
        );
        
        // log before processing
        log.debug(logMessage);
        
        // calculate request processing time
        long startTime = System.currentTimeMillis();
        
        try {
            // continue with the request
            chain.doFilter(request, response);
        } finally {
            // log after processing
            long duration = System.currentTimeMillis() - startTime;
            log.debug("RES: {} {} - {} ms (Status: {})",
                method, uri, duration, httpResponse.getStatus());
        }
    }
}