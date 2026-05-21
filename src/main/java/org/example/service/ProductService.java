package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.dto.ProductDTO;
import org.example.entity.Product;
import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> findAll(HttpSession session);
    Product findById(HttpSession session, String id);
    Product save(HttpSession session, ProductDTO product);
    void delete(HttpSession session, String id);
    Product searchByName(HttpSession session, String name);
    List<Product> findByCategory(HttpSession session, String category);
    List<Product> findByPriceRange(HttpSession session, double minPrice, double maxPrice);
    List<Product> findLowStockProducts(HttpSession session, int threshold);
    Map<String, Object> getCategoryStats(HttpSession session);
    double getTotalInventoryValue(HttpSession session);
    List<Product> fullTextSearch(HttpSession session, String searchText);
}