package com.eshop.cartservice.controller;

import com.eshop.cartservice.dto.CartItemRequest;
import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;


    @PostMapping("/add/{productId}")
    public ResponseEntity<CartItemResponse> addItemToCart(@RequestHeader("Authorization") String authorizationHeader, @Valid @PathVariable Long productId) {
        CartItemResponse cartItemResponse = cartService.addItemToCart(productId, authorizationHeader);
        return new ResponseEntity<>(cartItemResponse, HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getAllItemsInCart() {
        List<CartItemResponse> cartItems = cartService.getAllItemsInCart();
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }
}
