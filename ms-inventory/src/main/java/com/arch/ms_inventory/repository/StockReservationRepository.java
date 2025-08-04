package com.arch.ms_inventory.repository;

import com.arch.ms_inventory.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    List<StockReservation> findByPurchaseId(String purchaseId);
    List<StockReservation> findByPaymentId(String paymentId);
    List<StockReservation> findByProductId(String productId);
    List<StockReservation> findByStatus(String status);
}
