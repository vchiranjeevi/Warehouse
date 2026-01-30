package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    public void create(Warehouse warehouse) {
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse cannot be null");
        }

        // Rule 1: BusinessUnitCode must be unique
        Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode());
        if (existing != null) {
            throw new IllegalArgumentException("BusinessUnitCode already exists: " + warehouse.getBusinessUnitCode());
        }

        // Rule 2: Location must be valid
        Location location = locationResolver.resolveByIdentifier(warehouse.getLocation());
        if (location == null) {
            throw new IllegalArgumentException("Invalid location: " + warehouse.getLocation());
        }

        // Rule 3: Capacity must be >= stock
        if (warehouse.getCapacity() < warehouse.getStock()) {
            throw new IllegalArgumentException("Capacity must be greater than or equal to stock");
        }

        // Rule 4: Capacity must not exceed location’s max capacity
        if (warehouse.getCapacity() > location.getMaxCapacity()) {
            throw new IllegalArgumentException("Capacity exceeds location max capacity");
        }

        // Rule 5: Max warehouses per location not exceeded
        long count = warehouseStore.getAll().stream()
                .filter(w -> w.getLocation().equalsIgnoreCase(location.getIdentification()))
                .count();
        if (count >= location.getMaxNumberOfWarehouses()) {
            throw new IllegalArgumentException("Max warehouses exceeded for location: " + location.getIdentification());
        }

        // ✅ If all rules pass, persist the warehouse
        warehouseStore.create(warehouse);
    }
}