package com.inventoryservices.inventoryservices.service;

import java.io.IOException;
import java.security.Guard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryservices.inventoryservices.entity.Inventory;
import com.inventoryservices.inventoryservices.helper.EntityConvertTODTO;
import com.inventoryservices.inventoryservices.model.InventoryModel;
import com.inventoryservices.inventoryservices.repositoty.InventoryRepository;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EntityConvertTODTO entityConvertTODTO;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

   


    public Object saveInventory(Inventory inventory) {
        try {
            InventoryModel inventoryDTO = new InventoryModel();
            inventoryDTO.setCycleTime(inventory.getCycleTime());
            inventoryDTO.setDemandOFProduct(inventory.getDemandOFProduct());
            inventoryDTO.setInventoryCarringCost(inventory.getInventoryCarringCost());
            inventoryDTO.setInventoryId(inventory.getInventoryId());
            inventoryDTO.setInventoryStk(inventory.getInventoryStk());
            inventoryDTO.setOrderCost(inventory.getOrderCost());
            inventoryDTO.setProcurementCost(inventory.getProcurementCost());
            inventoryDTO.setStockOutCost(inventory.getStockOutCost());
            inventoryDTO.setStockOutOfLevel(inventory.getStockOutOfLevel());
            Inventory returnInventory = entityConvertTODTO.convertToEntity(inventoryDTO);

            kafkaTemplate.send("sendNotification", "Suceessfully Save Inenventory");

            return inventoryRepository.save(returnInventory);

        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'saveInventory'" + e.getMessage());
        }

    }

    public Inventory getSkuCodeInOrderService(String skuCode) {
        try {

            String apiUrl = "http://ORDER-SERVICE/api/v1/order/" + skuCode;

            ResponseEntity<String> aa = restTemplate.getForEntity(apiUrl, String.class);
           log.info("--------------" + aa);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aa.getBody());

            String productName = rootNode.path("productName").asText();
            String orderName = rootNode.path("orderName").asText();
            String skuCodes = rootNode.path("skuCode").asText();

           log.info("Product Name: " + productName);
           log.info("Order Name: " + orderName);
           log.info("SKU Code: " + skuCodes);

            // Array of order items
            JsonNode orderItems = rootNode.path("orderItems");
            for (JsonNode item : orderItems) {
                JsonNode orderItemsList = item.path("orderItems");
                String orderItemsUser = item.path("orderItemsUser").asText();
                boolean isActiveOrder = item.path("isActiveOrder").asBoolean();

               log.info("Order Items User: " + orderItemsUser);
               log.info("Is Active Order: " + isActiveOrder);
               log.info("Order Items: ");
                for (JsonNode orderItem : orderItemsList) {
                   log.info("  - " + orderItem.asText());
                }
                // kafkaTemplate.send("sendNotification", "CR001" + " - " + " " + orderItemsUser);
                log.info("The value is Active Product is {}:",isActiveOrder);

                if (isActiveOrder) {

                    
                    CollectorRegistry registry = new CollectorRegistry(); 

                    Gauge gauge = Gauge.build()
                        .name("my_custom_metric") // Provide a valid name (no spaces)
                        .help("My Metric Description")
                        .labelNames("status", "os") // Correct: all label names in one call
                        .register(registry);

                        Gauge analytics_product_name = Gauge.build().name("product_name").help("Account Product").register(registry);
                        analytics_product_name.set(1);
                        

                    Gauge AnalyticsOrderName = Gauge.build().name("orderName").help("orderName").register(registry);
                    AnalyticsOrderName.set(2);

                    Gauge AnalyticsSkuCode =Gauge.build().name("sku_code").help("SKU Code Metric").register(registry);
                    AnalyticsSkuCode.set(3);

                    // gauge.labels("User Items").set(4);
                    // gauge.labels("Active Order").set(5);

                    PushGateway pushGateway = new PushGateway("192.168.8.85:9091"); 

                     pushGateway.pushAdd(registry, "realDataAnalytics"); 
                    

                    
                    

                   

                }
            }
            System.out.println("Metric pushed successfully!");

            return null;

        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getSkuCodeInORderService'" + e.getMessage());
        }

    }

    public List<Inventory> getAllListInventory() {
        try {

            return inventoryRepository.findAll();

        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getAllListInventory'" + e.getMessage());

        }
    }

}
