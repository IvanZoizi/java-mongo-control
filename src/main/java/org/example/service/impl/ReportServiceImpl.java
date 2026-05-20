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
                .count().as("count")                    // Количество заказов
                .sum("totalAmount").as("totalSum")      // Сумма всех заказов
                .avg("totalAmount").as("averageAmount") // Средняя сумма заказа
                .min("totalAmount").as("minAmount")     // Минимальная сумма
                .max("totalAmount").as("maxAmount");    // Максимальная сумма

        SortOperation sortBySum = Aggregation.sort(Sort.Direction.DESC, "totalSum");

        Aggregation aggregation = Aggregation.newAggregation(groupByStatus, sortBySum);

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    /**
     * Отчет 2: Топ продуктов по продажам (с join)
     * Агрегация: $lookup, $unwind, $group, $sort, $limit
     */
    @Override
    public List<Map<String, Object>> getTopSellingProducts(HttpSession session, int limit) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Этап 1: Join с коллекцией products
        LookupOperation lookupProducts = LookupOperation.newLookup()
                .from("products")           // Коллекция для join
                .localField("productSku")   // Поле в orders
                .foreignField("sku")        // Поле в products
                .as("productInfo");         // Имя результирующего массива

        // Этап 2: Разворачиваем массив (превращаем в объект)
        UnwindOperation unwindProducts = Aggregation.unwind("productInfo");

        // Этап 3: Группировка по продукту
        GroupOperation groupByProduct = Aggregation.group("productInfo.sku", "productInfo.name")
                .sum("quantity").as("totalSold")           // Общее количество продаж
                .sum("totalAmount").as("totalRevenue")     // Общая выручка
                .avg("productInfo.price").as("avgPrice")   // Средняя цена продукта
                .first("productInfo.category").as("category"); // Категория

        // Этап 4: Сортировка по количеству продаж (убывание)
        SortOperation sortBySold = Aggregation.sort(Sort.Direction.DESC, "totalSold");

        // Этап 5: Ограничение количества результатов
        LimitOperation limitResults = Aggregation.limit(limit);

        // Этап 6: Проекция для красивых имен полей
        ProjectionOperation project = Aggregation.project()
                .and("_id.sku").as("sku")
                .and("_id.name").as("productName")
                .and("totalSold").as("quantitySold")
                .and("totalRevenue").as("revenue")
                .and("avgPrice").as("averagePrice")
                .and("category").as("category");

        // Собираем все этапы
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

    /**
     * Отчет 3: Статистика по категориям товаров
     * Агрегация: $group, $avg, $sum, $min, $max
     */
    @Override
    public List<Map<String, Object>> getProductStatsByCategory(HttpSession session) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Группировка по категориям с различными агрегатными функциями
        GroupOperation groupByCategory = Aggregation.group("category")
                .count().as("productCount")              // Количество товаров
                .avg("price").as("averagePrice")         // Средняя цена
                .sum("stock").as("totalStock")           // Общий остаток
                .min("price").as("minPrice")             // Минимальная цена
                .max("price").as("maxPrice")             // Максимальная цена
                .sum(ArithmeticOperators.Multiply.valueOf("price").multiplyBy("stock"))
                .as("totalValue");                   // Общая стоимость на складе

        // Сортировка по количеству товаров
        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "productCount");

        Aggregation aggregation = Aggregation.newAggregation(groupByCategory, sortByCount);

        return mongoTemplate.aggregate(aggregation, "products", Map.class).getMappedResults();
    }

    /**
     * Отчет 4: Ежемесячные продажи (с извлечением даты)
     * Агрегация: $project, $month, $year, $group
     */
    @Override
    public List<Map<String, Object>> getMonthlySalesReport(HttpSession session, int year) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Этап 1: Проекция с извлечением месяца и года из даты
        ProjectionOperation extractDateParts = Aggregation.project()
                .and("totalAmount").as("totalAmount")
                .and("orderDate").extractMonth().as("month")
                .and("orderDate").extractYear().as("year")
                .and("status").as("status");

        // Этап 2: Фильтрация по году
        MatchOperation filterByYear = Aggregation.match(
                Criteria.where("year").is(year).and("status").ne("Отменен")
        );

        // Этап 3: Группировка по месяцам
        GroupOperation groupByMonth = Aggregation.group("month")
                .sum("totalAmount").as("monthlyRevenue")
                .count().as("orderCount")
                .avg("totalAmount").as("averageOrderValue");

        // Этап 4: Сортировка по месяцу
        SortOperation sortByMonth = Aggregation.sort(Sort.Direction.ASC, "_id");

        Aggregation aggregation = Aggregation.newAggregation(
                extractDateParts,
                filterByYear,
                groupByMonth,
                sortByMonth
        );

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    /**
     * Отчет 5: Топ покупателей
     * Агрегация: $group по userId, $sum, $sort, $limit
     */
    @Override
    public List<Map<String, Object>> getTopCustomers(HttpSession session, int limit) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Группировка по пользователям
        GroupOperation groupByUser = Aggregation.group("userId")
                .sum("totalAmount").as("totalSpent")
                .count().as("orderCount")
                .avg("totalAmount").as("averageOrder")
                .max("totalAmount").as("maxOrder");

        // Сортировка по общей сумме
        SortOperation sortBySpent = Aggregation.sort(Sort.Direction.DESC, "totalSpent");

        // Лимит результатов
        LimitOperation limitResults = Aggregation.limit(limit);

        Aggregation aggregation = Aggregation.newAggregation(
                groupByUser,
                sortBySpent,
                limitResults
        );

        return mongoTemplate.aggregate(aggregation, "orders", Map.class).getMappedResults();
    }

    // ========== ПРОСТЫЕ ЗАПРОСЫ ==========

    /**
     * Простой запрос 1: Поиск товаров в ценовом диапазоне
     */
    @Override
    public List<Product> findProductsByPriceRange(HttpSession session, double minPrice, double maxPrice) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Создаем простой запрос с условиями
        Query query = new Query();
        query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "price"));

        return mongoTemplate.find(query, Product.class, "products");
    }

    /**
     * Простой запрос 2: Поиск заказов за период
     */
    @Override
    public List<Orders> findOrdersByDateRange(HttpSession session, String startDate, String endDate) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Парсим даты
        Date start = java.sql.Date.valueOf(startDate);
        Date end = java.sql.Date.valueOf(endDate);

        // Простой запрос с диапазоном дат
        Query query = new Query();
        query.addCriteria(Criteria.where("orderDate").gte(start).lte(end));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "orderDate"));

        return mongoTemplate.find(query, Orders.class, "orders");
    }

    @Override
    public Map<String, Object> getUserOrdersSummary(HttpSession session, int userId) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Простой запрос - находим все заказы пользователя
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        List<Orders> userOrders = mongoTemplate.find(query, Orders.class, "orders");

        // Группировка на уровне приложения (простая, без агрегации MongoDB)
        Map<String, Object> summary = new HashMap<>();
        Map<String, List<Orders>> ordersByStatus = new HashMap<>();

        double totalSpent = 0;
        for (Orders order : userOrders) {
            totalSpent += order.getTotalAmount();

            // Группируем по статусу
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
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        Query query = new Query();
        query.addCriteria(Criteria.where("status").is(status));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "orderDate"));

        return mongoTemplate.find(query, Orders.class, "orders");
    }

    @Override
    public List<Product> findProductsByCategorySortedByPrice(HttpSession session, String category) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        Query query = new Query();
        query.addCriteria(Criteria.where("category").is(category));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "price"));

        return mongoTemplate.find(query, Product.class, "products");
    }

    @Override
    public List<Product> fullTextSearch(HttpSession session, String searchText) {
        MongoTemplate mongoTemplate = sessionService.getFromSession(session);

        // Создаем regex для поиска (регистронезависимый)
        Pattern pattern = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);

        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("name").regex(pattern),
                Criteria.where("description").regex(pattern),
                Criteria.where("category").regex(pattern)
        ));

        return mongoTemplate.find(query, Product.class, "products");
    }
}