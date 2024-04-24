package com.eshop.cartservice.repository;

import com.eshop.cartservice.model.CartItem;
import org.springframework.data.repository.CrudRepository;

public interface CartRepository extends CrudRepository<CartItem, Long> {
}
