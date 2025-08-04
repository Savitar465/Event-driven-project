package com.arch.ms_ecommerce.service;

import com.arch.ms_ecommerce.dto.PurchaseRequest;
import com.arch.ms_ecommerce.model.Purchase;
import com.arch.ms_ecommerce.repository.PurchaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final RabbitTemplate rabbitTemplate;
    private final PurchaseRepository purchaseRepository;

    public PurchaseServiceImpl(RabbitTemplate rabbitTemplate, PurchaseRepository purchaseRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.purchaseRepository = purchaseRepository;
    }

    @Override
    public void makePurchase(PurchaseRequest purchaseRequest) {
        Purchase purchase = new Purchase();
        purchase.setProduct(purchaseRequest.getProduct());
        purchase.setProductId(purchaseRequest.getProductId());
        purchase.setQuantity(purchaseRequest.getQuantity());
        purchase.setPurchaseDate(LocalDateTime.now());

        purchaseRepository.save(purchase);

        log.info("Purchase successful for product: {}", purchaseRequest.getProductId());

        rabbitTemplate.convertAndSend("purchase_exchange", "purchase_routing_key", "Purchase successful for product: " + purchaseRequest.getProductId());
    }
}
