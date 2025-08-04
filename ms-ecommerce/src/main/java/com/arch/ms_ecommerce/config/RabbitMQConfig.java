package com.arch.ms_ecommerce.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${purchase.exchange.name}")
    private String exchangeName;

    private static final String QUEUE_NAME = "purchase_queue";
    private static final String ROUTING_KEY = "purchase_routing_key";

    @Bean
    public Exchange purchaseExchange() {
        return ExchangeBuilder.directExchange(exchangeName).build();
    }

    @Bean
    public Queue purchaseQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public Binding purchaseBinding(Queue purchaseQueue, Exchange purchaseExchange) {
        return BindingBuilder.bind(purchaseQueue).to(purchaseExchange).with(ROUTING_KEY).noargs();
    }
}
