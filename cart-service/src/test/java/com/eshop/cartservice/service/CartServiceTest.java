package com.eshop.cartservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.eshop.cartservice.dto.CartItemResponse;
import com.eshop.cartservice.dto.CartResponse;
import com.eshop.cartservice.model.CartItem;
import com.eshop.cartservice.repository.CartRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CartRepository cartRepository;

    @Spy
    @InjectMocks
    private CartService cartService;

    @Test
    void testAddItemToCart() {

        Long productId = 1L;
        Long userId = 1L;
        CartItemResponse productResponse = new CartItemResponse("Product1", "Description1", new BigDecimal(10));
        when(restTemplate.getForObject(anyString(), eq(CartItemResponse.class))).thenReturn(productResponse);
        when(cartRepository.save(any())).thenReturn(new CartItem());


        CartItemResponse response = cartService.addItemToCart(productId, userId);


        assertNotNull(response);
        assertEquals("Product1", response.getName());
        assertEquals("Description1", response.getDescription());
        assertEquals(new BigDecimal(10), response.getPrice());
    }

    @Test
    void testAddItemToCart_Failure() {

        Long productId = 1L;
        Long userId = 1L;
        when(restTemplate.getForObject(anyString(), eq(CartItemResponse.class))).thenThrow(RestClientException.class);

        CartItemResponse response = null;
        try {
            response = cartService.addItemToCart(productId, userId);
            // If no exception is thrown, the test should fail
            fail("Expected RestClientException but no exception was thrown");
        } catch (RestClientException e) {
            // Exception caught, test passes
        }
        assertNull(response);
    }

    @Test
    void testGetCartForUser() {

        Long userId = 1L;
        Integer page = 0;
        Integer size = 10;


        List<CartItem> cartItems = List.of(
                CartItem.builder().name("Product1").description("Description1").price(new BigDecimal(10)).ownerId(userId).build(),
                CartItem.builder().name("Product2").description("Description2").price(new BigDecimal(20)).ownerId(userId).build()
        );
        Page<CartItem> cartItemsPage = new PageImpl<>(cartItems);
        when(cartRepository.findPaginatedByOwnerId(eq(userId), any(Pageable.class))).thenReturn(cartItemsPage);


        BigDecimal totalPrice = new BigDecimal(30);
        doReturn(totalPrice).when(cartService).calculateTotalPriceForUser(userId);


        CartResponse response = cartService.getCartForUser(userId, page, size);


        assertNotNull(response);
        assertEquals(2, response.getTotalItems());
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getCartItems().size());
        assertEquals(totalPrice, response.getTotalPrice());

    }

    @Test
    void testGetCartForUser_WithNoItems() {

        Long userId = 1L;
        Integer page = 0;
        Integer size = 10;
        Page<CartItem> emptyPage = Page.empty();
        when(cartRepository.findPaginatedByOwnerId(eq(userId), any(Pageable.class))).thenReturn(emptyPage);

        BigDecimal totalPrice = BigDecimal.ZERO;
        doReturn(totalPrice).when(cartService).calculateTotalPriceForUser(userId);


        CartResponse response = cartService.getCartForUser(userId, page, size);


        assertNotNull(response);
        assertEquals(0, response.getTotalItems());
        assertEquals(1, response.getTotalPages());
        assertTrue(response.getCartItems().isEmpty());
        assertEquals(totalPrice, response.getTotalPrice());
    }


}
