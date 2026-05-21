package org.example.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.entity.Orders;
import org.example.entity.Product;
import org.example.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/orders/status")
    public List<Map> getOrdersGroupByStatus(HttpSession session) {
        return reportService.getOrdersGroupByStatus(session);
    }

    @GetMapping("/products/top/{limit}")
    public List<Map> getTopSellingProducts(@PathVariable("limit") Integer limit, HttpSession session) {
        return reportService.getTopSellingProducts(session, limit);
    }

    @GetMapping("/product/category")
    public List<Map> getProductStatsByCategory(HttpSession session) {
        return reportService.getProductStatsByCategory(session);
    }

    @GetMapping("/orders/monthly")
    public List<Map> getMonthlySalesReport(
            @RequestParam(defaultValue = "2024") Integer year,
            HttpSession session) {
        return reportService.getMonthlySalesReport(session, year);
    }

    @GetMapping("/customers/top/{limit}")
    public List<Map> getTopCustomers(@PathVariable("limit") Integer limit, HttpSession session) {
        return reportService.getTopCustomers(session, limit);
    }

    @GetMapping("/products/price-range")
    public List<Product> findProductsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max,
            HttpSession session) {
        return reportService.findProductsByPriceRange(session, min, max);
    }

    @GetMapping("/orders/date-range")
    public List<Orders> findOrdersByDateRange(
            @RequestParam String start,
            @RequestParam String end,
            HttpSession session) {
        return reportService.findOrdersByDateRange(session, start, end);
    }

    @GetMapping("/user-orders/{userId}")
    public Map<String, Object> getUserOrdersSummary(
            @PathVariable Integer userId,
            HttpSession session) {
        return reportService.getUserOrdersSummary(session, userId);
    }

    @GetMapping("/orders/status/{status}")
    public List<Orders> findOrdersByStatus(
            @PathVariable String status,
            HttpSession session) {
        return reportService.findOrdersByStatus(session, status);
    }

    @GetMapping("/products/category/{category}")
    public List<Product> findProductsByCategorySortedByPrice(
            @PathVariable String category,
            HttpSession session) {
        return reportService.findProductsByCategorySortedByPrice(session, category);
    }
}