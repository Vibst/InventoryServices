package com.inventoryservices.inventoryservices.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderPlacedEvents {
    private String orderNumber;
    
}
