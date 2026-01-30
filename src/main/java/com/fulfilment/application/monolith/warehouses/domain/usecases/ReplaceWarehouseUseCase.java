package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;

    public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    @Override
    public void replace(Warehouse newWarehouse) {
        if (newWarehouse == null) {
            throw new IllegalArgumentException("New warehouse cannot be null");
        }

        // Find the existing warehouse by businessUnitCode
        Warehouse existing = warehouseStore.findByBusinessUnitCode(newWarehouse.getBusinessUnitCode());
        if (existing == null) {
            throw new IllegalArgumentException("Warehouse not found for code: " + newWarehouse.getBusinessUnitCode());
        }

        // Rule 1: New capacity must accommodate old stock
        if (newWarehouse.getCapacity() < existing.getStock()) {
            throw new IllegalArgumentException("New capacity must be >= old stock");
        }

        // Rule 2: New stock must match old stock
        if (!newWarehouse.getStock().equals(existing.getStock())) {
            throw new IllegalArgumentException("New stock must match old stock");
        }

        // ✅ If rules pass, update warehouse
        warehouseStore.update(newWarehouse);
    }
}