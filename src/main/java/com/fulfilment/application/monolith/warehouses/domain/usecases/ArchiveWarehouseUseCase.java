package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import org.jboss.logging.Logger;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {
	private static final Logger LOGGER = Logger.getLogger(ArchiveWarehouseUseCase.class.getName());

    private final WarehouseStore warehouseStore;

    public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    @Override
    public void archive(Warehouse warehouse) {
    	LOGGER.info("ArchiveWarehouseUseCase.archive");
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null");
        }
        // Verify it exists in store
        Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode());
        if (existing == null) {
            throw new IllegalArgumentException("Warehouse not found for code: " + warehouse.getBusinessUnitCode());
        }
        // Business rule: mark warehouse as archived
        warehouse.setArchivedAt(LocalDateTime.now());

        // Persist the change
        warehouseStore.update(warehouse);
    }
}