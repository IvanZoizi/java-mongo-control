package org.example.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthDTO;
import org.example.entity.Users;
import org.example.mapper.Mapper;
import org.example.repository.UserRepository;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.task.SimpleAsyncTaskExecutorBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private Mapper mapper;
    private UserRepository userRepository;

    private final Map<String, MongoTemplate> mongoTemplates;

    public AuthServiceImpl(
            Mapper mapper,
            UserRepository userRepository,
            @Qualifier("adminTemplate")
            MongoTemplate adminMongoTemplate,
            @Qualifier("editorTemplate")
            MongoTemplate editorMongoTemplate,
            @Qualifier("viewerTemplate")
            MongoTemplate viewerMongoTemplate
    ) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.mongoTemplates = Map.of(
                "ADMIN", adminMongoTemplate,
                "EDITOR", editorMongoTemplate,
                "VIEWER", viewerMongoTemplate
        );
    }

    private MongoTemplate setMongoTemplateByUser(Users user) {
        return this.mongoTemplates.get(user.getRole());
    }

    @Override
    public MongoTemplate login(AuthDTO authDTO) {
        Users user = userRepository.findByUsername(authDTO.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Login is not find"));
        log.info("Find user " + user);
        if (!user.getPassword().equals(authDTO.getPassword())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        return setMongoTemplateByUser(user);
    }

    @Override
    public String getRole(String login) {
        Users user = userRepository.findByUsername(login)
                .orElseThrow(() -> new IllegalArgumentException("Login is not find"));
        return user.getRole();
    }
}