package com.eshop.cartservice.service;

import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.dto.CartResponse;
import com.eshop.cartservice.exception.NotFoundException;
import com.eshop.cartservice.model.CartItem;
import com.eshop.cartservice.repository.CartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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

    public CartItemResponse addItemToCart(Long productId, Long userId, Integer quantity) {


        CartItem existingCartItem = cartRepository.findByProductIdAndOwnerId(productId, userId);

        if (existingCartItem != null) {

            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cartRepository.save(existingCartItem);
            return convertToCartItemResponse(existingCartItem);
        } else {

            String productUrl = "http://CATALOG-SERVICE/products/" + productId;
            CartItemResponse productResponse = restTemplate.getForObject(productUrl, CartItemResponse.class);

            CartItem cartItem = CartItem.builder()
                    .productId(productId)
                    .name(productResponse.getName())
                    .ownerId(userId)
                    .description(productResponse.getDescription())
                    .price(productResponse.getPrice())
                    .quantity(quantity)
                    .build();

            cartRepository.save(cartItem);
            return convertToCartItemResponse(cartItem);
        }
    }

    public CartResponse getCartForUser(Long userId, Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<CartItem> cartItemsPage = cartRepository.findPaginatedByOwnerId(userId, pageable);

        List<CartItemResponse> cartItemDTOs = cartItemsPage.stream()
                .map(this::convertToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalPrice = calculateTotalPriceForUser(userId);
        Integer totalPages = cartItemsPage.getTotalPages();
        Integer currentPage = cartItemsPage.getNumber();
        Integer totalItems = (int)cartItemsPage.getTotalElements();

        return new CartResponse(cartItemDTOs, currentPage, totalPages, totalItems, totalPrice);
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

    public BigDecimal calculateTotalPriceForUser(Long userId) {
        List<CartItem> cartItems = cartRepository.findByOwnerId(userId);

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            BigDecimal itemTotalPrice = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotalPrice);
        }

        return totalPrice;
    }

    private CartItemResponse convertToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .name((cartItem.getName()))
                .description(cartItem.getDescription())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .build();
    }
}




