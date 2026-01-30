package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import org.jboss.logging.Logger;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {
	
	private static final Logger LOGGER = Logger.getLogger(CreateWarehouseUseCase.class.getName());

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void create(Warehouse warehouse) {
        LOGGER.info("CreateWarehouseUseCase.create");
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null");
        }

        // Rule 1: BusinessUnitCode must be unique
        LOGGER.info("CreateWarehouseUseCase.create() BusinessUnitCode must be unique");
        Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode());
        if (existing != null) {
            throw new IllegalArgumentException("BusinessUnitCode already exists: " + warehouse.getBusinessUnitCode());
        }

        LOGGER.info("CreateWarehouseUseCase.create() Location must be valid");
        // Rule 2: Location must be valid
        Location location = locationResolver.resolveByIdentifier(warehouse.getLocation());
        if (location == null) {
            throw new IllegalArgumentException("Invalid location: " + warehouse.getLocation());
        }

        LOGGER.info("CreateWarehouseUseCase.create() Capacity must be >= stock");
        // Rule 3: Capacity must be >= stock
        if (warehouse.getCapacity() < warehouse.getStock()) {
            throw new IllegalArgumentException("Capacity must be greater than or equal to stock");
        }

        LOGGER.info("CreateWarehouseUseCase.create() Capacity must not exceed location’s max capacity");
        // Rule 4: Capacity must not exceed location’s max capacity
        if (warehouse.getCapacity() > location.getMaxCapacity()) {
            throw new IllegalArgumentException("Capacity exceeds location max capacity");
        }

        LOGGER.info("CreateWarehouseUseCase.create() Max warehouses per location not exceeded");
        // Rule 5: Max warehouses per location not exceeded
        long count = warehouseStore.getAll().stream()
                .filter(w -> w.getLocation().equalsIgnoreCase(location.getIdentification()))
                .count();
        if (count >= location.getMaxNumberOfWarehouses()) {
            throw new IllegalArgumentException("Max warehouses exceeded for location: " + location.getIdentification());
        }

        // ✅ Always set createdAt before persisting
        warehouse.setCreatedAt(LocalDateTime.now());

        // Persist the warehouse
        warehouseStore.create(warehouse);
    }
}