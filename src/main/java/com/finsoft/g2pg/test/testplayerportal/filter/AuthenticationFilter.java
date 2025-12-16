package com.finsoft.g2pg.test.testplayerportal.filter;

import com.finsoft.g2pg.test.testplayerportal.domain.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class AuthenticationFilter implements Filter {

    private static final String LOGIN_PAGE = "/login";
    private static final String INDEX_PAGE = "/";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String contextPath = httpRequest.getContextPath();
        String requestURI = httpRequest.getRequestURI();
        String path = requestURI.substring(contextPath.length());
        
        if (path.isEmpty()) {
            path = "/";
        }

        if (path.equals("/") ||
                path.equals("/login") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/error")) {
            chain.doFilter(request, response);
            return;
        }

        if (httpRequest.isRequestedSessionIdValid() && User.existsInSession(session)) {
            chain.doFilter(request, response);
        } else {
            if (isSessionInvalid(httpRequest)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + INDEX_PAGE);
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + LOGIN_PAGE);
            }
        }
    }

    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        return (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();
    }
}

