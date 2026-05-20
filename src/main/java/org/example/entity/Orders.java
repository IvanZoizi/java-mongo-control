package org.example.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "orders")  // Было "collation", исправлено на "collection"
@Data
@NoArgsConstructor
public class Orders {

    @Id
    private String id;

    private String orderId;

    @Indexed
    private Integer userId;

    private String productSku;

    private Integer quantity;

    private Double totalAmount;

    @Indexed
    private String status;

    @Indexed
    private Date orderDate;
}