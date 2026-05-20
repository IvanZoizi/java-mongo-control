package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.entity.Orders;
import java.util.List;
import java.util.Map;

public interface OrderService {

    // Существующие методы (из вашего предыдущего задания)
    List<Orders> findAll(HttpSession session);
    Orders findById(HttpSession session, String id);
    Orders save(HttpSession session, Orders order);
    void delete(HttpSession session, String id);

    // ===== ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ОТЧЕТОВ =====

    /**
     * Поиск заказов по пользователю
     */
    List<Orders> findByUserId(HttpSession session, int userId);

    /**
     * Поиск заказов по статусу
     */
    List<Orders> findByStatus(HttpSession session, String status);

    /**
     * Поиск заказов за период
     */
    List<Orders> findByDateRange(HttpSession session, String startDate, String endDate);

    /**
     * Получить общую сумму всех заказов (агрегация)
     */
    double getTotalRevenue(HttpSession session);

    /**
     * Получить статистику по заказам пользователя
     */
    Map<String, Object> getUserOrderStats(HttpSession session, int userId);

    /**
     * Получить количество заказов по статусам (простая группировка)
     */
    Map<String, Long> getOrderCountByStatus(HttpSession session);
}