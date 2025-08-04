package com.arch.ms_inventory.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventoryRabbitMQConfig {
    
    public static final String INVENTORY_PAYMENT_QUEUE = "inventory.payment.queue";
    public static final String INVENTORY_PURCHASE_QUEUE = "inventory.purchase.queue";
    public static final String PAYMENT_EXCHANGE = "payment_exchange";
    public static final String PURCHASE_EXCHANGE = "purchase_exchange";
    
    @Bean
    public Queue inventoryPaymentQueue() {
        return new Queue(INVENTORY_PAYMENT_QUEUE, true);
    }
    
    @Bean
    public Queue inventoryPurchaseQueue() {
        return new Queue(INVENTORY_PURCHASE_QUEUE, true);
    }
    
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }
    
    @Bean
    public TopicExchange purchaseExchange() {
        return new TopicExchange(PURCHASE_EXCHANGE);
    }
    
    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(inventoryPaymentQueue())
                .to(paymentExchange())
                .with("payment.*");
    }
    
    @Bean
    public Binding purchaseBinding() {
        return BindingBuilder.bind(inventoryPurchaseQueue())
                .to(purchaseExchange())
                .with("purchase.*");
    }
    
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
