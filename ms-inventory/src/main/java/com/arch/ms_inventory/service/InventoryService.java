package com.arch.ms_inventory.service;

import com.arch.ms_inventory.event.dto.PurchaseEventDto;
import com.arch.ms_inventory.model.Inventory;
import com.arch.ms_inventory.model.StockReservation;
import com.arch.ms_inventory.repository.InventoryRepository;
import com.arch.ms_inventory.repository.StockReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final StockReservationRepository stockReservationRepository;
    
    @Transactional
    public void reserveStock(PurchaseEventDto purchaseEvent) {
        log.info("Reserving stock for purchase: {}", purchaseEvent.getPurchaseId());
        
        for (PurchaseEventDto.PurchaseItemDto item : purchaseEvent.getItems()) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            
            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductId());
            }
            
            // Reserve stock
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
            
            // Create reservation record
            StockReservation reservation = StockReservation.builder()
                    .purchaseId(purchaseEvent.getPurchaseId())
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .status("RESERVED")
                    .createdAt(LocalDateTime.now())
                    .build();
            stockReservationRepository.save(reservation);
        }
    }
    
    @Transactional
    public void confirmStockReservation(String paymentId) {
        log.info("Confirming stock reservation for payment: {}", paymentId);
        
        List<StockReservation> reservations = stockReservationRepository.findByPaymentId(paymentId);
        
        for (StockReservation reservation : reservations) {
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));
            
            // Move from reserved to sold
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
            inventory.setSoldQuantity(inventory.getSoldQuantity() + reservation.getQuantity());
            inventoryRepository.save(inventory);
            
            // Update reservation status
            reservation.setStatus("CONFIRMED");
            reservation.setUpdatedAt(LocalDateTime.now());
            stockReservationRepository.save(reservation);
        }
    }
    
    @Transactional
    public void releaseStockReservation(String paymentId) {
        log.info("Releasing stock reservation for payment: {}", paymentId);
        
        List<StockReservation> reservations = stockReservationRepository.findByPaymentId(paymentId);
        
        for (StockReservation reservation : reservations) {
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));
            
            // Return reserved stock to available
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
            inventoryRepository.save(inventory);
            
            // Update reservation status
            reservation.setStatus("RELEASED");
            reservation.setUpdatedAt(LocalDateTime.now());
            stockReservationRepository.save(reservation);
        }
    }
    
    @Transactional
    public void releasePurchaseStock(String purchaseId) {
        log.info("Releasing stock for cancelled purchase: {}", purchaseId);
        
        List<StockReservation> reservations = stockReservationRepository.findByPurchaseId(purchaseId);
        
        for (StockReservation reservation : reservations) {
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));
            
            // Return reserved stock to available
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
            inventoryRepository.save(inventory);
            
            // Update reservation status
            reservation.setStatus("CANCELLED");
            reservation.setUpdatedAt(LocalDateTime.now());
            stockReservationRepository.save(reservation);
        }
    }
}
