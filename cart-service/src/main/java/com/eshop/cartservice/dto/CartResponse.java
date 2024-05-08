package com.eshop.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {

    private List<CartItemResponse> cartItems;
    private Integer currentPage;
    private Integer totalPages;
    private Integer totalItems;
    private BigDecimal totalPrice;

}
