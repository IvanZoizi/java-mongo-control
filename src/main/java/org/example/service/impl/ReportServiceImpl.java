package org.example.service.impl;

import lombok.AllArgsConstructor;
import org.example.entity.Orders;
import org.example.entity.Product;
import org.example.service.ReportService;
import org.example.service.SessionService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

    private SessionService sessionService;

    @Override
    public List<Map> getOrdersGroupByStatus(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        GroupOperation groupByStatus = Aggregation.group("status")
                .count().as("count")
                .sum("totalAmount").as("totalSum")
                .avg("totalAmount").as("averageAmount")
                .min("totalAmount").as("minAmount")
                .max("totalAmount").as("maxAmount");

        SortOperation sortBySum = Aggregation.sort(Sort.Direction.DESC, "totalSum");

        Aggregation aggregation = Aggregation.newAggregation(groupByStatus, sortBySum);

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    @Override
    public List<Map> getTopSellingProducts(HttpSession session, int limit) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        LookupOperation lookupProducts = LookupOperation.newLookup()
                .from("products")
                .localField("productSku")
                .foreignField("sku")
                .as("productInfo");

        UnwindOperation unwindProducts = Aggregation.unwind("productInfo");

        GroupOperation groupByProduct = Aggregation.group("productInfo.sku", "productInfo.name")
                .sum("quantity").as("totalSold")
                .sum("totalAmount").as("totalRevenue")
                .avg("productInfo.price").as("avgPrice")
                .first("productInfo.category").as("category");

        SortOperation sortBySold = Aggregation.sort(Sort.Direction.DESC, "totalSold");

        LimitOperation limitResults = Aggregation.limit(limit);

        ProjectionOperation project = Aggregation.project()
                .and("_id.sku").as("sku")
                .and("_id.name").as("productName")
                .and("totalSold").as("quantitySold")
                .and("totalRevenue").as("revenue")
                .and("avgPrice").as("averagePrice")
                .and("category").as("category");

        Aggregation aggregation = Aggregation.newAggregation(
                lookupProducts,
                unwindProducts,
                groupByProduct,
                sortBySold,
                limitResults,
                project
        );

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    @Override
    public List<Map> getProductStatsByCategory(HttpSession session) {
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

        return mongoTemplate.aggregate(aggregation, "products", Map.class).getMappedResults();
    }

    @Override
    public List<Map> getMonthlySalesReport(HttpSession session, int year) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        ProjectionOperation extractDateParts = Aggregation.project()
                .and("totalAmount").as("totalAmount")
                .and("orderDate").extractMonth().as("month")
                .and("orderDate").extractYear().as("year")
                .and("status").as("status");

        MatchOperation filterByYear = Aggregation.match(
                Criteria.where("year").is(year).and("status").ne("Отменен")
        );

        GroupOperation groupByMonth = Aggregation.group("month")
                .sum("totalAmount").as("monthlyRevenue")
                .count().as("orderCount")
                .avg("totalAmount").as("averageOrderValue");

        SortOperation sortByMonth = Aggregation.sort(Sort.Direction.ASC, "_id");

        Aggregation aggregation = Aggregation.newAggregation(
                extractDateParts,
                filterByYear,
                groupByMonth,
                sortByMonth
        );

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    @Override
    public List<Map>getTopCustomers(HttpSession session, int limit) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        GroupOperation groupByUser = Aggregation.group("userId")
                .sum("totalAmount").as("totalSpent")
                .count().as("orderCount")
                .avg("totalAmount").as("averageOrder")
                .max("totalAmount").as("maxOrder");

        SortOperation sortBySpent = Aggregation.sort(Sort.Direction.DESC, "totalSpent");

        LimitOperation limitResults = Aggregation.limit(limit);

        Aggregation aggregation = Aggregation.newAggregation(
                groupByUser,
                sortBySpent,
                limitResults
        );

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    @Override
    public List<Product> findProductsByPriceRange(HttpSession session, double minPrice, double maxPrice) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        Query query = new Query();
        query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "price"));

        return mongoTemplate.find(query, Product.class, "products");
    }

    @Override
    public List<Orders> findOrdersByDateRange(HttpSession session, String startDate, String endDate) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        Query query = new Query();
        query.addCriteria(Criteria.where("orderDate").gte(start).lte(end));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "orderDate"));

        return mongoTemplate.find(query, Orders.class, "orders");
    }

    @Override
    public Map<String, Object> getUserOrdersSummary(HttpSession session, int userId) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        List<Orders> userOrders = mongoTemplate.find(query, Orders.class, "orders");

        Map<String, Object> summary = new HashMap<>();
        Map<String, List<Orders>> ordersByStatus = new HashMap<>();

        double totalSpent = 0;
        for (Orders order : userOrders) {
            totalSpent += order.getTotalAmount();

            ordersByStatus.computeIfAbsent(order.getStatus(), k -> new ArrayList<>()).add(order);
        }

        summary.put("userId", userId);
        summary.put("totalOrders", userOrders.size());
        summary.put("totalSpent", totalSpent);
        summary.put("averageOrderValue", userOrders.isEmpty() ? 0 : totalSpent / userOrders.size());
        summary.put("ordersByStatus", ordersByStatus);
        summary.put("orders", userOrders);

        return summary;
    }

    @Override
    public List<Orders> findOrdersByStatus(HttpSession session, String status) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(status));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "orderDate"));

        return mongoTemplate.find(query, Orders.class, "orders");
    }

    @Override
    public List<Product> findProductsByCategorySortedByPrice(HttpSession session, String category) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);

        Query query = new Query();
        query.addCriteria(Criteria.where("category").is(category));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "price"));

        return mongoTemplate.find(query, Product.class, "products");
    }

}