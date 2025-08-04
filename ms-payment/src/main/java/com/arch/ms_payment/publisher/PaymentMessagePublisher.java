package com.arch.ms_payment.publisher;

import com.arch.ms_payment.config.RabbitMQConfig;
import com.arch.ms_payment.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishSuccessMessage(PaymentResponseDto response) {
        try {
            log.info("Publishing success message for payment: {}", response.getPaymentId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY,
                    response
            );
            log.info("Success message published for payment: {}", response.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to publish success message for payment {}: {}",
                    response.getPaymentId(), e.getMessage());
        }
    }

    public void publishFailureMessage(PaymentResponseDto response) {
        try {
            log.info("Publishing failure message for payment: {}", response.getPaymentId());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.PAYMENT_EXCHANGE,
                    RabbitMQConfig.PAYMENT_FAILURE_ROUTING_KEY,
                    response
            );
            log.info("Failure message published for payment: {}", response.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to publish failure message for payment {}: {}",
                    response.getPaymentId(), e.getMessage());
        }
    }
}
