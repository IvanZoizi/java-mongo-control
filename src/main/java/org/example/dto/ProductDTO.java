package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ProductDTO {
    @NonNull
    private String sku;

    @NonNull
    private String name;

    @NonNull
    private String category;

    @NonNull
    private Double price;

    @NonNull
    private Integer stock;

    @NonNull
    private String description;
}
