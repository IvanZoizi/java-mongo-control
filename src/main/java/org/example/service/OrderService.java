package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.dto.OrderDTO;
import org.example.entity.Orders;
import java.util.List;
import java.util.Map;

public interface OrderService {

    List<Orders> findAll(HttpSession session);
    Orders findById(HttpSession session, String id);
    Orders save(HttpSession session, OrderDTO order);
    void delete(HttpSession session, String id);

    List<Orders> findByUserId(HttpSession session, Integer userId);

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