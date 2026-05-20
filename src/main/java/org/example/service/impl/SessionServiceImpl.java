package org.example.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.entity.Users;
import org.example.repository.UserRepository;
import org.example.service.SessionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserRepository userRepository;

    @Override
    public MongoTemplate getMongoTemplateBySession(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (MongoTemplate) session.getAttribute("MongoTemplate");
    }

    @Override
    public String getRoleBySession(HttpSession session) {
        if (session == null) {
            return null;
        }
        String roleFromSession = (String) session.getAttribute("Role");
        if (roleFromSession != null) {
            return roleFromSession;
        }

        String login = (String) session.getAttribute("Login");
        if (login == null) {
            return null;
        }

        return userRepository.findByUsername(login)
                .map(Users::getRole)
                .orElse(null);
    }
}