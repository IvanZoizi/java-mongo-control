package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.entity.Orders;
import org.example.entity.Product;

import java.util.List;
import java.util.Map;

public interface ReportService {

    // ===== ОТЧЕТЫ С АГРЕГАЦИОННЫМИ ФУНКЦИЯМИ =====

    /**
     * Отчет 1 (агрегация): Статистика заказов по статусам
     * Группировка заказов по статусу с подсчетом количества и суммы
     * Использует: $group, $sum, $count
     */
    List<Map> getOrdersGroupByStatus(HttpSession session);

    /**
     * Отчет 2 (агрегация + join): Топ продуктов по продажам
     * Объединение коллекций orders и products, группировка по продуктам
     * Использует: $lookup, $unwind, $group, $sort, $limit
     */
    List<Map<String, Object>> getTopSellingProducts(HttpSession session, int limit);

    /**
     * Отчет 3 (агрегация): Статистика по категориям товаров
     * Группировка товаров по категориям: средняя цена, общий остаток, количество
     * Использует: $group, $avg, $sum, $count
     */
    List<Map<String, Object>> getProductStatsByCategory(HttpSession session);

    // ===== ПРОСТЫЕ ЗАПРОСЫ С ГРУППИРОВКОЙ И ПОИСКОМ =====

    /**
     * Простой запрос 1: Поиск заказов пользователя с группировкой по статусу
     * Простая фильтрация + группировка на уровне приложения
     */
    Map<String, Object> getUserOrdersSummary(HttpSession session, int userId);

    /**
     * Простой запрос 2: Поиск товаров в ценовом диапазоне
     * Простой поиск с фильтрацией
     */
    List<Product> findProductsByPriceRange(HttpSession session, double minPrice, double maxPrice);

    /**
     * Простой запрос 3: Поиск заказов за период
     * Простой поиск с фильтрацией по датам
     */
    List<Orders> findOrdersByDateRange(HttpSession session, String startDate, String endDate);

    // ===== ДОПОЛНИТЕЛЬНЫЕ ОТЧЕТЫ ДЛЯ ВЫПОЛНЕНИЯ ЗАДАНИЯ =====

    /**
     * Отчет 4: Ежемесячные продажи (агрегация с группировкой по месяцам)
     * Использует: $project с $month, $year, затем $group
     */
    List<Map<String, Object>> getMonthlySalesReport(HttpSession session, int year);

    /**
     * Отчет 5: Топ покупателей по сумме заказов (агрегация)
     * Использует: $group по userId, $sum, $sort, $limit
     */
    List<Map<String, Object>> getTopCustomers(HttpSession session, int limit);

    /**
     * Простой запрос 4: Поиск заказов по статусу с сортировкой
     */
    List<Orders> findOrdersByStatus(HttpSession session, String status);

    /**
     * Простой запрос 5: Поиск товаров по категории с сортировкой по цене
     */
    List<Product> findProductsByCategorySortedByPrice(HttpSession session, String category);
}