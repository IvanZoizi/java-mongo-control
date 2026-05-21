package org.example.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderDTO {
    @NonNull
    private String orderId;
    @NonNull
    private Integer userId;
    @NonNull
    private String productSku;
    @NonNull
    private Integer quantity;
    @NonNull
    private Double totalAmount;
    @NonNull
    private String status;
    @NonNull
    private Date orderDate;
}
