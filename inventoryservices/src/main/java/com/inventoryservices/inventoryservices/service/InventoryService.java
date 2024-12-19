package com.inventoryservices.inventoryservices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryservices.inventoryservices.entity.Inventory;
import com.inventoryservices.inventoryservices.helper.EntityConvertTODTO;
import com.inventoryservices.inventoryservices.model.InventoryModel;
import com.inventoryservices.inventoryservices.repositoty.InventoryRepository;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private EntityConvertTODTO entityConvertTODTO;

    @Autowired
    private RestTemplate restTemplate;

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

            return inventoryRepository.save(returnInventory);

        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'saveInventory'" + e.getMessage());
        }

    }

    public Inventory getSkuCodeInORderService(String skuCode) {
        try {

            String apiUrl = "http://localhost:8081/api/v1/order/" + skuCode;

            ResponseEntity<String> aa = restTemplate.getForEntity(apiUrl, String.class);
            System.out.println("--------------" + aa);

            ObjectMapper mapper =new ObjectMapper();
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

                if(isActiveOrder){

                    // call to product service which save to entry 
                
                }
            }


            

            return null;

        } catch (Exception e) {
            throw new UnsupportedOperationException("Unimplemented method 'getSkuCodeInORderService'" + e.getMessage());
        }

    }

}
