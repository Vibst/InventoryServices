package com.inventoryservices.inventoryservices.entity;

import java.util.* ;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "t_inventory")
@Entity
@Getter
@Setter
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
