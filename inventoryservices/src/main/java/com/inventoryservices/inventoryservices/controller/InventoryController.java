package com.inventoryservices.inventoryservices.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryservices.inventoryservices.entity.Inventory;
import com.inventoryservices.inventoryservices.service.InventoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    @Autowired
    private InventoryService service;

    @PostMapping("/inventory")
    public ResponseEntity<Inventory> saveOrder(@RequestBody Inventory inventory){
        Inventory isInventory =  (Inventory) service.saveInventory(inventory);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(isInventory);
        

    }


    @GetMapping("/inventory/{skuCode}")
    public ResponseEntity<Inventory> getSkuCodeInORderService(@PathVariable("skuCode") String skuCode){

        Inventory skuCodeInventory = service.getSkuCodeInORderService(skuCode);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(skuCodeInventory);



    }
    
}
