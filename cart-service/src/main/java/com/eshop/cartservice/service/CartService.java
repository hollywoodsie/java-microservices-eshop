package com.eshop.cartservice.service;

import com.eshop.cartservice.dto.CartItemRequest;
import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.exception.NotFoundException;
import com.eshop.cartservice.model.CartItem;
import com.eshop.cartservice.repository.CartRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Base64;
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

    public CartItemResponse addItemToCart(Long productId, String authorizationHeader) {

        String productUrl = "http://CATALOG-SERVICE/products/" + productId;
        CartItemResponse productResponse = restTemplate.getForObject(productUrl, CartItemResponse.class);
        String cartOwner = extractUsernameFromToken(authorizationHeader);

        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .name(productResponse.getName())
                .owner(cartOwner)
                .description(productResponse.getDescription())
                .price(productResponse.getPrice())
                .build();

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
                .owner(cartItem.getOwner())
                .description(cartItem.getDescription())
                .price(cartItem.getPrice())
                .productId(cartItem.getProductId())
                .build();
    }

    public String extractUsernameFromToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes);
            String[] parts = credentials.split(":", 2); // Change 2 to a higher number if username or password might contain ":"
            String username = parts[0];
            return username;
        } else {
            // Handle case where Authorization header is missing or not in expected format
            throw new IllegalArgumentException("Invalid Authorization header");
        }
    }
}




