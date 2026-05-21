package org.example.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.dto.OrderDTO;
import org.example.entity.Orders;
import org.example.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @GetMapping("/all")
    public List<Orders> getAllOrders(HttpSession session) {
        return orderService.findAll(session);
    }

    @GetMapping("/{id}")
    public Orders getOrderById(@PathVariable String id, HttpSession session) {
        return orderService.findById(session, id);
    }

    @GetMapping("/user/{userId}")
    public List<Orders> getOrdersByUserId(@PathVariable Integer userId, HttpSession session) {
        return orderService.findByUserId(session, userId);
    }

    @GetMapping("/status/{status}")
    public List<Orders> getOrdersByStatus(@PathVariable String status, HttpSession session) {
        return orderService.findByStatus(session, status);
    }

    @GetMapping("/date-range")
    public List<Orders> getOrdersByDateRange(
            @RequestParam String start,
            @RequestParam String end,
            HttpSession session) {
        return orderService.findByDateRange(session, start, end);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<Map<String, Double>> getTotalRevenue(HttpSession session) {
        double revenue = orderService.getTotalRevenue(session);
        return ResponseEntity.ok(Map.of("totalRevenue", revenue));
    }

    @GetMapping("/stats/user/{userId}")
    public Map<String, Object> getUserOrderStats(@PathVariable int userId, HttpSession session) {
        return orderService.getUserOrderStats(session, userId);
    }

    @GetMapping("/stats/by-status")
    public Map<String, Long> getOrderCountByStatus(HttpSession session) {
        return orderService.getOrderCountByStatus(session);
    }

    @PostMapping
    public Orders createOrder(@RequestBody OrderDTO orderDTO, HttpSession session) {
        return orderService.save(session, orderDTO);
    }

    @PutMapping("/{id}")
    public Orders updateOrder(@PathVariable String id, @RequestBody OrderDTO orderDTO, HttpSession session) {
        Orders existing = orderService.findById(session, id);
        if (existing == null) {
            throw new RuntimeException("Order not found");
        }
        return orderService.save(session, orderDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable String id, HttpSession session) {
        orderService.delete(session, id);
        return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
    }
}