package com.eshop.cartservice.controller;


import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.dto.CartResponse;
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
    public ResponseEntity<CartItemResponse> addItemToCart(@RequestHeader("X-UserId") Long owner, @Valid @PathVariable Long productId, @RequestParam(defaultValue = "1") Integer quantity) {
        CartItemResponse cartItemResponse = cartService.addItemToCart(productId, owner, quantity);
        return new ResponseEntity<>(cartItemResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getAllItemsInCart(@RequestHeader("X-UserId") Long owner, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer size) {
        CartResponse cartItems = cartService.getCartForUser(owner, page, size);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteItemFromCart(@RequestHeader("X-UserId") Long owner, @Valid @PathVariable Long productId) {
        cartService.deleteItem(productId,owner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/empty")
    public ResponseEntity<Void> emptyCartForUser(@RequestHeader("X-UserId") Long owner) {
        cartService.deleteAllItemsForUser(owner);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
