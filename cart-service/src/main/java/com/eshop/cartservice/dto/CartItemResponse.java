package com.eshop.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private Long id;

    private String name;

    private String description;

    private Long ownerId;

    private Long productId;

    private BigDecimal price;
}