package org.example.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.example.dto.ProductDTO;
import org.example.dto.ProductNameDto;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @GetMapping("/all")
    public List<Product> getAllProducts(HttpSession session) {
        return productService.findAll(session);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id, HttpSession session) {
        return productService.findById(session, id);
    }

    @GetMapping("/name")
    public Product getProductByName(@RequestParam String name, HttpSession session) {
        return productService.searchByName(session, name);
    }

    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category, HttpSession session) {
        return productService.findByCategory(session, category);
    }

    @GetMapping("/price-range")
    public List<Product> getProductsByPriceRange(
            @RequestParam double min,
            @RequestParam double max,
            HttpSession session) {
        return productService.findByPriceRange(session, min, max);
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold, HttpSession session) {
        return productService.findLowStockProducts(session, threshold);
    }

    @GetMapping("/stats/category")
    public Map<String, Object> getCategoryStats(HttpSession session) {
        return productService.getCategoryStats(session);
    }

    @GetMapping("/stats/inventory-value")
    public ResponseEntity<Map<String, Double>> getTotalInventoryValue(HttpSession session) {
        double value = productService.getTotalInventoryValue(session);
        return ResponseEntity.ok(Map.of("totalInventoryValue", value));
    }

    @GetMapping("/search")
    public List<Product> fullTextSearch(@RequestParam String text, HttpSession session) {
        return productService.fullTextSearch(session, text);
    }

    @PostMapping
    public Product createProduct(@RequestBody ProductDTO productDTO, HttpSession session) {
        return productService.save(session, productDTO);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO, HttpSession session) {
        Product existing = productService.findById(session, id);
        if (existing == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        return productService.save(session, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable String id, HttpSession session) {
        productService.delete(session, id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deleted successfully");
        response.put("id", id);
        return ResponseEntity.ok(response);
    }
}