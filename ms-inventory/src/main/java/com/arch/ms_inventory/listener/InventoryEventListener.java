package com.arch.ms_inventory.listener;

import com.arch.ms_inventory.event.dto.PaymentEventDto;
import com.arch.ms_inventory.event.dto.PurchaseEventDto;
import com.arch.ms_inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventListener {
    
    private final InventoryService inventoryService;
    
    @RabbitListener(queues = "inventory.payment.queue")
    public void handlePaymentEvent(PaymentEventDto paymentEvent) {
        log.info("Received payment event: {}", paymentEvent);
        
        try {
            switch (paymentEvent.getStatus()) {
                case "COMPLETED":
                    // Payment completed - confirm stock reservation or update inventory
                    inventoryService.confirmStockReservation(paymentEvent.getPaymentId());
                    break;
                case "FAILED":
                case "CANCELLED":
                    // Payment failed - release reserved stock
                    inventoryService.releaseStockReservation(paymentEvent.getPaymentId());
                    break;
                default:
                    log.warn("Unhandled payment status: {}", paymentEvent.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing payment event: {}", paymentEvent, e);
            // You might want to send to a dead letter queue or retry mechanism
        }
    }
    
    @RabbitListener(queues = "inventory.purchase.queue")
    public void handlePurchaseEvent(PurchaseEventDto purchaseEvent) {
        log.info("Received purchase event: {}", purchaseEvent);
        
        try {
            switch (purchaseEvent.getStatus()) {
                case "CREATED":
                    // Reserve stock for the purchase
                    inventoryService.reserveStock(purchaseEvent);
                    break;
                case "CANCELLED":
                    // Release reserved stock
                    inventoryService.releasePurchaseStock(purchaseEvent.getPurchaseId());
                    break;
                default:
                    log.warn("Unhandled purchase status: {}", purchaseEvent.getStatus());
            }
        } catch (Exception e) {
            log.error("Error processing purchase event: {}", purchaseEvent, e);
        }
    }
}
