package com.inventoryservices.inventoryservices.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryservices.inventoryservices.entity.Inventory;
import com.inventoryservices.inventoryservices.service.InventoryService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/v1")

public class InventoryController {

    @Autowired
    private InventoryService service;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;

    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> isListInventory = service.getAllListInventory();

        return ResponseEntity.status(HttpStatus.CREATED).body(isListInventory);
    }

    @PostMapping("/inventory")
    public ResponseEntity<Inventory> saveOrder(@RequestBody Inventory inventory) {
        Inventory isInventory = (Inventory) service.saveInventory(inventory);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(isInventory);

    }

    private final AtomicInteger retryCount = new AtomicInteger(0);

    @GetMapping("/inventory/{skuCode}")
    // @Retry(name = "retryorder", fallbackMethod = "fallBackMethodRetry")
    @CircuitBreaker(name = "order", fallbackMethod = "fallbackMethod")

    public ResponseEntity<Inventory> getSkuCodeInOrderService(@PathVariable("skuCode") String skuCode) {

        retryCount.incrementAndGet();
        System.out.println("Retry count: " + retryCount.get());

        if (retryCount.get() == 100) {
            System.out.println("Something went error in Retry properties" + retryCount);

        }

        if (skuCode.equals("error")) {
            throw new RuntimeException("Simulated error");
        }

        Inventory skuCodeInventory = service.getSkuCodeInOrderService(skuCode);

        retryCount.set(0);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(skuCodeInventory);
    }

    public ResponseEntity<Inventory> fallbackMethod(String skuCode, Throwable throwable) {
        System.out.println("Fallback triggered due to Circuit Breaker.");
        Inventory fallbackInventory = createFallbackInventory("Fallback Method: Circuit Breaker triggered.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fallbackInventory);
    }

    public ResponseEntity<Inventory> fallBackMethodRetry(String skuCode, Exception exception) {
        System.out.println("Fallback triggered after Retry exhaustion.");
        Inventory retryInventory = createFallbackInventory("Fallback Method: Retry attempts exhausted.");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(retryInventory);
    }

    private Inventory createFallbackInventory(String message) {
        Inventory fallbackInventory = new Inventory();
        fallbackInventory.setCycleTime(0L);
        fallbackInventory.setDemandOFProduct("FallBack Method is calling Something went Error in Services");
        fallbackInventory.setInventoryCarringCost(0L);
        fallbackInventory.setInventoryId(0L);
        fallbackInventory.setInventoryStk(00);
        fallbackInventory.setOrderCost(00L);
        fallbackInventory.setProcurementCost(00L);
        fallbackInventory.setSkuCode("FallBack Method is calling Something went Error in Services ");
        fallbackInventory.setStockOutCost(00L);
        fallbackInventory.setStockOutOfLevel(0);
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(fallbackInventory);
        return fallbackInventory;
    }

    @PostMapping("/{message}")
    public String sendMessage(@PathVariable String message) {
        kafkaTemplate.send(topicName, message);
        return "Message sent: " + message;
    }

}
