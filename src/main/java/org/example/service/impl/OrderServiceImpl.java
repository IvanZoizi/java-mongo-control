package org.example.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.OrderDTO;
import org.example.entity.Orders;
import org.example.service.OrderService;
import org.example.service.SessionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private SessionService sessionService;

    @Override
    public List<Orders> findAll(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.findAll(Orders.class);
    }

    @Override
    public Orders findById(HttpSession session, String id) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.findById(id, Orders.class);
    }

    @Override
    public Orders save(HttpSession session, OrderDTO orderDTO) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        Orders order = new Orders();
        order.setOrderDate(orderDTO.getOrderDate());
        order.setOrderId(orderDTO.getOrderId());
        order.setProductSku(orderDTO.getProductSku());
        order.setQuantity(orderDTO.getQuantity());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order.setUserId(orderDTO.getUserId());
        order.setStatus(orderDTO.getStatus());
        return mongoTemplate.save(order);
    }

    @Override
    public void delete(HttpSession session, String id) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        mongoTemplate.remove(
                Query.query(Criteria.where("id").is(id)),
                Orders.class);
    }

    @Override
    public List<Orders> findByUserId(HttpSession session, Integer userId) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId)),
                Orders.class);
    }

    @Override
    public List<Orders> findByStatus(HttpSession session, String status) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        return mongoTemplate.find(
                Query.query(Criteria.where("status").is(status)),
                Orders.class);
    }

    @Override
    public List<Orders> findByDateRange(HttpSession session, String startDate, String endDate) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return mongoTemplate.find(
                    Query.query(Criteria.where("orderDate").gte(start).lte(end)),
                    Orders.class);
        } catch (Exception e) {
            log.error("Error parsing dates", e);
            return List.of();
        }
    }

    @Override
    public double getTotalRevenue(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        List<Orders> orders = mongoTemplate.findAll(Orders.class);
        return orders.stream().mapToDouble(Orders::getTotalAmount).sum();
    }

    @Override
    public Map<String, Object> getUserOrderStats(HttpSession session, int userId) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        List<Orders> userOrders = mongoTemplate.find(
                Query.query(Criteria.where("userId").is(userId)),
                Orders.class);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("totalOrders", userOrders.size());
        stats.put("totalSpent", userOrders.stream().mapToDouble(Orders::getTotalAmount).sum());
        stats.put("averageOrderValue", userOrders.stream()
                .mapToDouble(Orders::getTotalAmount)
                .average()
                .orElse(0));

        Map<String, Long> ordersByStatus = new HashMap<>();
        for (Orders order : userOrders) {
            ordersByStatus.merge(order.getStatus(), 1L, Long::sum);
        }
        stats.put("ordersByStatus", ordersByStatus);

        return stats;
    }

    @Override
    public Map<String, Long> getOrderCountByStatus(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getMongoTemplateBySession(session);
        List<Orders> allOrders = mongoTemplate.findAll(Orders.class);
        Map<String, Long> countByStatus = new HashMap<>();
        for (Orders order : allOrders) {
            countByStatus.merge(order.getStatus(), 1L, Long::sum);
        }
        return countByStatus;
    }
}