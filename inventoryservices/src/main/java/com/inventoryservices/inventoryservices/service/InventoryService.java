package com.inventoryservices.inventoryservices.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryservices.inventoryservices.entity.Inventory;
import com.inventoryservices.inventoryservices.events.OrderPlacedEvents;
import com.inventoryservices.inventoryservices.helper.EntityConvertTODTO;
import com.inventoryservices.inventoryservices.model.InventoryModel;
import com.inventoryservices.inventoryservices.repositoty.InventoryRepository;

import jakarta.annotation.PostConstruct;

@Service
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
            System.out.println("--------------" + aa);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aa.getBody());

            String productName = rootNode.path("productName").asText();
            String orderName = rootNode.path("orderName").asText();
            String skuCodes = rootNode.path("skuCode").asText();

            System.out.println("Product Name: " + productName);
            System.out.println("Order Name: " + orderName);
            System.out.println("SKU Code: " + skuCodes);

            // Array of order items
            JsonNode orderItems = rootNode.path("orderItems");
            for (JsonNode item : orderItems) {
                JsonNode orderItemsList = item.path("orderItems");
                String orderItemsUser = item.path("orderItemsUser").asText();
                boolean isActiveOrder = item.path("isActiveOrder").asBoolean();

                System.out.println("Order Items User: " + orderItemsUser);
                System.out.println("Is Active Order: " + isActiveOrder);
                System.out.println("Order Items: ");
                for (JsonNode orderItem : orderItemsList) {
                    System.out.println("  - " + orderItem.asText());
                }
                kafkaTemplate.send("sendNotification", "CR001"+ " - " + " " +orderItemsUser);

                if (isActiveOrder) {

                    // call to product service which save to entry && logics of email notification Services 

                }
            }

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
