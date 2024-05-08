package com.eshop.cartservice.messaging;
import org.springframework.amqp.core.*;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.product.exchange.name}")
    private String productExchangeName;

    @Value("${rabbitmq.user.exchange.name}")
    private String userExchangeName;

    @Value("${rabbitmq.product.routing.key}")
    private String productRoutingKey;

    @Value("${rabbitmq.user.routing.key}")
    private String userRoutingKey;

    @Value("${rabbitmq.product.queue.name}")
    private String productQueueName;

    @Value("${rabbitmq.user.queue.name}")
    private String userQueueName;


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange productExchange() {
        return new DirectExchange(productExchangeName);
    }
    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(userExchangeName);
    }

    @Bean
    public Queue productQueue() {
        return new Queue(productQueueName, true);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(userQueueName, true);
    }

    @Bean
    public Binding bindingProduct(Queue productQueue, DirectExchange productExchange) {
        return BindingBuilder.bind(productQueue).to(productExchange).with(productRoutingKey);
    }

    @Bean
    public Binding bindingUser(Queue userQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(userRoutingKey);
    }
}
