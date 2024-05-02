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
    public ResponseEntity<CartItemResponse> addItemToCart(@RequestHeader("X-UserId") Long owner, @Valid @PathVariable Long productId) {
        CartItemResponse cartItemResponse = cartService.addItemToCart(productId, owner);
        return new ResponseEntity<>(cartItemResponse, HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<CartItemResponse>> getAllItemsInCart(@RequestHeader("X-UserId") Long owner) {
        List<CartItemResponse> cartItems = cartService.getAllItemsInCart(owner);
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
