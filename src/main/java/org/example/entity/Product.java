package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @Indexed(unique = true)
    private String sku;

    @TextIndexed
    private String name;

    private String category;

    private Double price;

    private Integer stock;

    private String description;
}