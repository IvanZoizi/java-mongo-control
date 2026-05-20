package org.example.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.example.dto.ProductDTO;
import org.example.dto.ProductNameDto;
import org.example.entity.Product;
import org.example.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Product getAllProducts(@PathParam("id") String id, HttpSession session) {
        return productService.findById(session, id);
    }

    @GetMapping("/name")
    public Product getProductByName(@RequestBody ProductNameDto productNameDto, HttpSession session) {
        return productService.searchByName(session, productNameDto.getName());
    }

    @PostMapping
    public Product createProduct(@RequestBody ProductDTO productDTO, HttpSession session) {
        return productService.save(session, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathParam("id") String id, HttpSession session) {
        productService.delete(session, id);
        return ResponseEntity.ok("Product deleted");
    }
}
