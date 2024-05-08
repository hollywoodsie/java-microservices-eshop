package com.eshop.cartservice.messaging;

import com.eshop.cartservice.service.CartService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    private final CartService cartService;

    @Autowired
    public RabbitMQListener(CartService cartService) {
        this.cartService = cartService;
    }

    @RabbitListener(queues = {"${rabbitmq.product.queue.name}"} )
    public void receiveProductDeletedMessage(ProductDeletedEvent event) {
        cartService.deleteItemForAllUsers(event.getProductId());
    }

    @RabbitListener(queues = {"${rabbitmq.user.queue.name}"})
    public void receiveUserDeletedMessage(UserDeletedEvent event) {
        cartService.deleteAllItemsForUser(event.getUserId());
    }

}