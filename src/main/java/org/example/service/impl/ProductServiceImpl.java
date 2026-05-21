package org.example.service.impl;

import com.mongodb.client.model.Aggregates;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.dto.ProductDTO;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.example.service.SessionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Query.query(Criteria.where("id").is(id)),
                Product.class
        );
    }

    @Override
    public Product searchByName(HttpSession httpSession, String name) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(httpSession);
        return mongoTemplate.findOne(
                Query.query(Criteria.where("name").is(name)),
                Product.class
        );
    }

    @Override
    public List<Product> findByCategory(HttpSession session, String category) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.find(
                Query.query(Criteria.where("category").is(category)),
                Product.class
        );
    }

    @Override
    public List<Product> findByPriceRange(HttpSession session, double minPrice, double maxPrice) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.find(
                Query.query(Criteria.where("price").gte(minPrice).lte(maxPrice)),
                Product.class
        );
    }

    @Override
    public List<Product> findLowStockProducts(HttpSession session, int threshold) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.find(
                Query.query(Criteria.where("stock").lt(threshold)),
                Product.class
        );
    }

    @Override
    public Map<String, Object> getCategoryStats(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        GroupOperation groupByCategory = Aggregation.group("category")
                .count().as("productCount")
                .avg("price").as("averagePrice")
                .sum("stock").as("totalStock")
                .min("price").as("minPrice")
                .max("price").as("maxPrice")
                .sum(ArithmeticOperators.Multiply.valueOf("price").multiplyBy("stock"))
                .as("totalValue");

        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "productCount");

        Aggregation aggregation = Aggregation.newAggregation(groupByCategory, sortByCount);

        List<Map> results = mongoTemplate.aggregate(aggregation, "products", Map.class).getMappedResults();

        Map<String, Object> stats = new HashMap<>();
        stats.put("categories", results);
        stats.put("totalCategories", results.size());

        return stats;
    }

    @Override
    public double getTotalInventoryValue(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        List<Product> products = mongoTemplate.findAll(Product.class);
        return products.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
    }

    @Override
    public List<Product> fullTextSearch(HttpSession session, String searchText) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        TextCriteria textCriteria = TextCriteria.forDefaultLanguage()
                .matchingAny(searchText.split(" "));

        TextQuery textQuery = TextQuery.queryText(textCriteria)
                .sortByScore()
                .includeScore();

        return mongoTemplate.find(textQuery, Product.class);
    }
}