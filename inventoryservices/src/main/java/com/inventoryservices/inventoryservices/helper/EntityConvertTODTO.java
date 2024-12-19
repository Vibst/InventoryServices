package com.inventoryservices.inventoryservices.helper;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.inventoryservices.inventoryservices.entity.Inventory;
import com.inventoryservices.inventoryservices.model.InventoryModel;

@Component
public class EntityConvertTODTO {

    public Inventory convertToEntity(InventoryModel inventoryDTO) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(inventoryDTO, inventory);
        return inventory;

    }

}
