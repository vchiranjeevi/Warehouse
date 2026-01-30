package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse; // API bean
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    private WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll().stream()
                .map(this::toWarehouseResponse)
                .toList();
    }

    @Override
    @Transactional
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse =
                new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        domainWarehouse.setBusinessUnitCode(data.getBusinessUnitCode());
        domainWarehouse.setLocation(data.getLocation());
        domainWarehouse.setCapacity(data.getCapacity());
        domainWarehouse.setStock(data.getStock());
        domainWarehouse.setCreatedAt(LocalDateTime.now());

        warehouseRepository.create(domainWarehouse);

        return toWarehouseResponse(domainWarehouse);
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found for id: " + id);
        }
        return toWarehouseResponse(warehouse);
    }

    @Override
    @Transactional
    public void archiveAWarehouseUnitByID(String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found for id: " + id);
        }
        warehouse.setArchivedAt(LocalDateTime.now());
        warehouseRepository.update(warehouse);
    }

    @Override
    @Transactional
    public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse existing =
                warehouseRepository.findByBusinessUnitCode(businessUnitCode);
        if (existing == null) {
            throw new IllegalArgumentException("Warehouse not found for code: " + businessUnitCode);
        }

        if (data.getCapacity() < existing.getStock()) {
            throw new IllegalArgumentException("New capacity must be >= old stock");
        }
        if (!data.getStock().equals(existing.getStock())) {
            throw new IllegalArgumentException("New stock must match old stock");
        }

        existing.setLocation(data.getLocation());
        existing.setCapacity(data.getCapacity());
        existing.setStock(data.getStock());

        warehouseRepository.update(existing);
        return toWarehouseResponse(existing);
    }

    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
        Warehouse response = new Warehouse();
        response.setBusinessUnitCode(warehouse.getBusinessUnitCode());
        response.setLocation(warehouse.getLocation());
        response.setCapacity(warehouse.getCapacity());
        response.setStock(warehouse.getStock());
        return response;
    }
}