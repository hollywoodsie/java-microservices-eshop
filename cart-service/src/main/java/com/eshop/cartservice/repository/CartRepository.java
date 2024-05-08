package com.eshop.cartservice.repository;

import com.eshop.cartservice.model.CartItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<CartItem, Long> {
    List<CartItem> findByOwnerId(Long userId);
    List<CartItem> findByProductId(Long productId);
}
