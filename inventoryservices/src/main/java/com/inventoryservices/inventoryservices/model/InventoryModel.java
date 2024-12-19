package com.inventoryservices.inventoryservices.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryModel {

    public Long inventoryId;
    public String demandOFProduct;
    public Long procurementCost;
    public Long cycleTime;
    public Long orderCost;
    public Long inventoryCarringCost;
    public Integer inventoryStk;
    public Integer stockOutOfLevel;
    public Long stockOutCost;
    public String skuCode;
}
