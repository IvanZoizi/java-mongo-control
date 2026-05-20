package org.example.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "org.example.repository",
        mongoTemplateRef = "adminTemplate"   // оставляем для основного репозитория
)
public class MongoTemplatesConfig {

    private final String urlAdmin  = "mongodb://admin:admin123@localhost:27017/mydb?authSource=admin";
    private final String urlEditor = "mongodb://editor:editor123@localhost:27017/mydb?authSource=mydb";
    private final String urlViewer = "mongodb://viewer:viewer123@localhost:27017/mydb?authSource=mydb";

    @Primary
    @Bean("adminTemplate")
    public MongoTemplate adminMongoTemplate() {
        MongoClient client = MongoClients.create(urlAdmin);
        return new MongoTemplate(client, "mydb");
    }

    @Bean("editorTemplate")
    public MongoTemplate editorMongoTemplate() {
        MongoClient client = MongoClients.create(urlEditor);
        return new MongoTemplate(client, "mydb");
    }

    @Bean("viewerTemplate")
    public MongoTemplate viewerMongoTemplate() {
        MongoClient client = MongoClients.create(urlViewer);
        return new MongoTemplate(client, "mydb");
    }
}