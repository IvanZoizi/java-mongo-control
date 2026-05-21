package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.entity.Orders;
import org.example.entity.Product;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map> getOrdersGroupByStatus(HttpSession session);
    List<Map> getTopSellingProducts(HttpSession session, int limit);
    List<Map> getProductStatsByCategory(HttpSession session);
    Map<String, Object> getUserOrdersSummary(HttpSession session, int userId);
    List<Product> findProductsByPriceRange(HttpSession session, double minPrice, double maxPrice);
    List<Orders> findOrdersByDateRange(HttpSession session, String startDate, String endDate);
    List<Map>getMonthlySalesReport(HttpSession session, int year);
    List<Map> getTopCustomers(HttpSession session, int limit);
    List<Orders> findOrdersByStatus(HttpSession session, String status);
    List<Product> findProductsByCategorySortedByPrice(HttpSession session, String category);
}