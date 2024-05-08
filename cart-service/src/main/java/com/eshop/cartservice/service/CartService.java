package com.eshop.cartservice.service;

import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.exception.NotFoundException;
import com.eshop.cartservice.model.CartItem;
import com.eshop.cartservice.repository.CartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

public class CartService {


    private final RestTemplate restTemplate;
    private final CartRepository cartRepository;

    @Autowired
    public CartService(RestTemplate restTemplate, CartRepository cartRepository) {
        this.restTemplate = restTemplate;
        this.cartRepository = cartRepository;
    }

    public CartItemResponse addItemToCart(Long productId, Long userId) {

        String productUrl = "http://CATALOG-SERVICE/products/" + productId;
        CartItemResponse productResponse = restTemplate.getForObject(productUrl, CartItemResponse.class);

        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .name(productResponse.getName())
                .ownerId(userId)
                .description(productResponse.getDescription())
                .price(productResponse.getPrice())
                .build();

        cartRepository.save(cartItem);
        return convertToDto(cartItem);
    }

    public List<CartItemResponse> getAllItemsInCart(Long userId) {
        List<CartItem> cartItems = cartRepository.findByOwnerId(userId);
        return cartItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public void deleteItem(Long itemId, Long userId) {
        Optional<CartItem> cartItemOptional = cartRepository.findById(itemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            if (cartItem.getOwnerId().equals(userId)) {
                cartRepository.delete(cartItem);
            } else {
                throw new IllegalArgumentException("The cart item does not belong to the current user");
            }
        } else {
            throw new NotFoundException("Cart item not found with ID: " + itemId);
        }
    }

    public void deleteItemForAllUsers(Long productId) {
        List<CartItem> cartItems = cartRepository.findByProductId(productId);
        cartRepository.deleteAll(cartItems);
    }

    public void deleteAllItemsForUser(Long userId) {
        List<CartItem> cartItems = cartRepository.findByOwnerId(userId);
        cartRepository.deleteAll(cartItems);
    }

    private CartItemResponse convertToDto(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .name((cartItem.getName()))
                .ownerId(cartItem.getOwnerId())
                .description(cartItem.getDescription())
                .price(cartItem.getPrice())
                .productId(cartItem.getProductId())
                .build();
    }
}




