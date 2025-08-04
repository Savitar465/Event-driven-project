package com.arch.ms_inventory.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEventDto {
    private String purchaseId;
    private String userId;
    private List<PurchaseItemDto> items;
    private String status;
    private LocalDateTime createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseItemDto {
        private String productId;
        private Integer quantity;
        private String productName;
    }
}
