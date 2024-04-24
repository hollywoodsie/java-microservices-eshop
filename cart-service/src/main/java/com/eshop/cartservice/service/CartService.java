package com.eshop.cartservice.service;

import com.eshop.cartservice.dto.CartItemRequest;
import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.model.CartItem;
import com.eshop.cartservice.repository.CartRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
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

    public CartItemResponse addItemToCart(Long productId) {
        // Make HTTP GET request to product service endpoint to retrieve product details
        String productUrl = "http://CATALOG-SERVICE/products/" + productId;
        CartItemResponse productResponse = restTemplate.getForObject(productUrl, CartItemResponse.class);

        // Create a new CartItem with the retrieved product details and quantity

        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .name(productResponse.getName()) //in future from authentication object, maybe as function 2nd param
                .description(productResponse.getDescription())
                .price(productResponse.getPrice())
                .build();

        // Save the cart item to the database
        cartRepository.save(cartItem);
        return convertToDto(cartItem);
    }

    public List<CartItemResponse> getAllItemsInCart() {
        List<CartItem> cartItems = (List<CartItem>) cartRepository.findAll(); //come later here
        return cartItems.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CartItemResponse convertToDto(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .name((cartItem.getName()))
                .userId(cartItem.getUserId())
                .description(cartItem.getDescription())
                .price(cartItem.getPrice())
                .productId(cartItem.getProductId())
                .build();
    }
}




