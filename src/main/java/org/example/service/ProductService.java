package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.dto.ProductDTO;
import org.example.entity.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {

    // Существующие методы
    List<Product> findAll(HttpSession session);
    Product findById(HttpSession session, String id);
    Product save(HttpSession session, ProductDTO product);
    void delete(HttpSession session, String id);
    Product searchByName(HttpSession session, String name);

    // ===== ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ОТЧЕТОВ =====

    /**
     * Поиск товаров по категории
     */
    List<Product> findByCategory(HttpSession session, String category);

    /**
     * Поиск товаров в ценовом диапазоне
     */
    List<Product> findByPriceRange(HttpSession session, double minPrice, double maxPrice);

    /**
     * Поиск товаров с низким остатком (для отчетов)
     */
    List<Product> findLowStockProducts(HttpSession session, int threshold);

    /**
     * Получить статистику по категориям (группировка)
     */
    Map<String, Object> getCategoryStats(HttpSession session);

    /**
     * Получить среднюю цену по категориям (агрегация)
     */
    List<Map<String, Object>> getAveragePriceByCategory(HttpSession session);

    /**
     * Получить общую стоимость всех товаров на складе
     */
    double getTotalInventoryValue(HttpSession session);

    /**
     * Поиск товаров по тексту (полнотекстовый поиск)
     */
    List<Product> fullTextSearch(HttpSession session, String searchText);
}