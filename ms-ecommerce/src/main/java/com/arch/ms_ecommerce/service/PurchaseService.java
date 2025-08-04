package com.arch.ms_ecommerce.service;

import com.arch.ms_ecommerce.dto.PurchaseRequest;

public interface PurchaseService {

    void makePurchase(PurchaseRequest purchaseRequest);
}
