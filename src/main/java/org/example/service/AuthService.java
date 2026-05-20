package org.example.service;

import org.example.dto.AuthDTO;
import org.example.entity.Users;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface AuthService {
    public MongoTemplate login(AuthDTO authDTO);
    public String getRole(String login);
}
