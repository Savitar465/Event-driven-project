package com.arch.ms_payment.listener;

import com.arch.ms_payment.config.RabbitMQConfig;
import com.arch.ms_payment.dto.PaymentRequestDto;
import com.arch.ms_payment.dto.PaymentResponseDto;
import com.arch.ms_payment.publisher.PaymentMessagePublisher;
import com.arch.ms_payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentMessageListener {

    private final PaymentService paymentService;
    private final PaymentMessagePublisher messagePublisher;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_REQUEST_QUEUE)
    public void handlePaymentRequest(PaymentRequestDto paymentRequest) {
        log.info("Received payment request: {}", paymentRequest.getPaymentId());

        try {
            // Process the payment
            PaymentResponseDto response = paymentService.processPayment(paymentRequest);

            // Send the appropriate response message
            if (response.isSuccess()) {
                log.info("Payment processed successfully: {}", paymentRequest.getPaymentId());
                messagePublisher.publishSuccessMessage(response);
            } else {
                log.warn("Payment processing failed: {}", paymentRequest.getPaymentId());
                messagePublisher.publishFailureMessage(response);
            }

        } catch (Exception e) {
            log.error("Unexpected error processing payment {}: {}",
                    paymentRequest.getPaymentId(), e.getMessage(), e);

            // Create error response
            PaymentResponseDto errorResponse = PaymentResponseDto.builder()
                    .paymentId(paymentRequest.getPaymentId())
                    .userId(paymentRequest.getUserId())
                    .amount(paymentRequest.getAmount())
                    .currency(paymentRequest.getCurrency())
                    .success(false)
                    .message("Unexpected error: " + e.getMessage())
                    .errorCode("SYSTEM_ERROR")
                    .build();

            messagePublisher.publishFailureMessage(errorResponse);
        }
    }
}
