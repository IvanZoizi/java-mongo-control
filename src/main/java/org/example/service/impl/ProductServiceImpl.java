package org.example.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.dto.ProductDTO;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.example.service.SessionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private SessionService sessionService;

    @Override
    public List<Product> findAll(HttpSession httpSession) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(httpSession);
        return mongoTemplate.findAll(Product.class);
    }

    @Override
    public Product findById(HttpSession httpSession, String id) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(httpSession);
        return mongoTemplate.findById(id, Product.class);
    }

    @Override
    public Product save(HttpSession httpSession, ProductDTO productDto) {
        Product product = new Product();
        product.setSku(productDto.getSku());
        product.setName(productDto.getName());
        product.setCategory(productDto.getCategory());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setStock(productDto.getStock());

        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(httpSession);
        return mongoTemplate.save(product);
    }

    @Override
    public void delete(HttpSession httpSession, String id) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(httpSession);
        mongoTemplate.remove(
                Query.query(
                        Criteria.where("id")
                                .is(id)
                ),
                Product.class
        );
    }

    @Override
    public Product searchByName(HttpSession httpSession, String name) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(httpSession);
        Product products = mongoTemplate.findOne(
            Query.query(
                    Criteria.where("name").is(name)
            ),
                Product.class
        );
        return products;
    }
}
