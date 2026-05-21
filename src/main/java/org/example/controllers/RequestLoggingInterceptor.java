package org.example.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.SessionService;
import org.slf4j.MDC;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@AllArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("Start MiddleWare");

        String method = request.getMethod();
        String path = request.getRequestURI();

        if (path.equals("/auth/login") || path.equals("/auth/logout") || path.equals("/login123123")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication is required\"}");
            return false;
        }

        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        if (mongoTemplate == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication is required\"}");
            return false;
        }

        String role = (String) session.getAttribute("Role");

        if (!checkAccess(method, role)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Insufficient permissions to perform the operation\"}");
            return false;
        }

        return true;
    }

    private boolean checkAccess(String method, String role) {
        if (method.equals("GET") || method.equals("HEAD")) {
            return true;
        }

        if (method.equals("PUT") || method.equals("PATCH")) {
            return role.equals("admin") || role.equals("editor");
        } else if (role.equals("admin")) {
            return true;
        }

        return false;
    }
}