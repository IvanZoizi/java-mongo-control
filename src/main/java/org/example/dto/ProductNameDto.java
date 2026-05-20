package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class ProductNameDto {
    @NonNull private String name;
}
