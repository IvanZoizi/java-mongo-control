package org.example.service;

import jakarta.servlet.http.HttpSession;
import org.example.dto.ProductDTO;
import org.example.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll(HttpSession httpSession);
    Product findById(HttpSession httpSession, String id);
    Product save(HttpSession httpSession, ProductDTO product);
    void delete(HttpSession httpSession, String id);
    Product searchByName(HttpSession httpSession, String name);
}
