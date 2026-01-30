package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouses")
public class DbWarehouse {

    @Id
    @Column(name = "business_unit_code", nullable = false, unique = true)
    private String businessUnitCode;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    // --- Getters and Setters ---
    public String getBusinessUnitCode() {
        return businessUnitCode;
    }

    public void setBusinessUnitCode(String businessUnitCode) {
        this.businessUnitCode = businessUnitCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    // --- Conversion methods ---
    public Warehouse toWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode(this.businessUnitCode);
        warehouse.setLocation(this.location);
        warehouse.setCapacity(this.capacity);
        warehouse.setStock(this.stock);
        warehouse.setCreatedAt(this.createdAt);
        warehouse.setArchivedAt(this.archivedAt);
        return warehouse;
    }

    public static DbWarehouse fromWarehouse(Warehouse warehouse) {
        DbWarehouse dbWarehouse = new DbWarehouse();
        dbWarehouse.setBusinessUnitCode(warehouse.getBusinessUnitCode());
        dbWarehouse.setLocation(warehouse.getLocation());
        dbWarehouse.setCapacity(warehouse.getCapacity());
        dbWarehouse.setStock(warehouse.getStock());
        dbWarehouse.setCreatedAt(warehouse.getCreatedAt());
        dbWarehouse.setArchivedAt(warehouse.getArchivedAt());
        return dbWarehouse;
    }
}