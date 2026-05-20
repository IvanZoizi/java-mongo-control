package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface SessionService {
    MongoTemplate getMongoTemplateBySession(HttpSession session);
    String getRoleBySession(HttpSession session);
}
