package com.arch.ms_ecommerce.api;

import com.arch.ms_ecommerce.dto.PurchaseRequest;
import com.arch.ms_ecommerce.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<String> makePurchase(@RequestBody PurchaseRequest purchaseRequest) {
        purchaseService.makePurchase(purchaseRequest);
        return ResponseEntity.ok("Purchase successful");
    }
}
