package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import java.time.LocalDateTime;
import java.util.List;

import org.jboss.logging.Logger;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse; // API bean

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {
	
	private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class.getName());

    @Inject
    private WarehouseRepository warehouseRepository;
    
    @Inject 
    private CreateWarehouseUseCase createWarehouseUseCase;

    @Inject 
    private ArchiveWarehouseUseCase archiveWarehouseUseCase;
    
    @Inject 
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
    	LOGGER.info("WarehouseResourceImpl.listAllWarehousesUnits");
        return warehouseRepository.getAll().stream()
                .map(this::toWarehouseResponse)
                .toList();
    }

    @Override
    @Transactional
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    	LOGGER.info("WarehouseResourceImpl.createANewWarehouseUnit");
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse =
                new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        LOGGER.info("WarehouseResourceImpl.createANewWarehouseUnit..." + domainWarehouse);
        domainWarehouse.setBusinessUnitCode(data.getBusinessUnitCode());
        domainWarehouse.setLocation(data.getLocation());
        domainWarehouse.setCapacity(data.getCapacity());
        domainWarehouse.setStock(data.getStock());
        domainWarehouse.setCreatedAt(LocalDateTime.now());

        createWarehouseUseCase.create(domainWarehouse);
        //warehouseRepository.create(domainWarehouse);
        return toWarehouseResponse(domainWarehouse);
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {
    	LOGGER.info("WarehouseResourceImpl.getAWarehouseUnitByID");
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode(id);
        LOGGER.info("WarehouseResourceImpl.getAWarehouseUnitByID..." + warehouse);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found for id: " + id);
        }
        return toWarehouseResponse(warehouse);
    }

    @Override
    @Transactional
    public void archiveAWarehouseUnitByID(String id) {
    	LOGGER.info("WarehouseResourceImpl.archiveAWarehouseUnitByID");
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse =
                warehouseRepository.findByBusinessUnitCode(id);
        LOGGER.info("WarehouseResourceImpl.archiveAWarehouseUnitByID...." + warehouse);
        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found for id: " + id);
        }
        warehouse.setArchivedAt(LocalDateTime.now());
        //warehouseRepository.update(warehouse);
        archiveWarehouseUseCase.archive(warehouse);
    }

    @Override
    @Transactional
    public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
    	LOGGER.info("WarehouseResourceImpl.replaceTheCurrentActiveWarehouse");
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse existing =
                warehouseRepository.findByBusinessUnitCode(businessUnitCode);
        if (existing == null) {
        	LOGGER.info("WarehouseResourceImpl.replaceTheCurrentActiveWarehouse  existing..."+ existing);
            throw new IllegalArgumentException("Warehouse not found for code: " + businessUnitCode);
        }

        if (data.getCapacity() < existing.getStock()) {
        	LOGGER.info("WarehouseResourceImpl.replaceTheCurrentActiveWarehouse...");
            throw new IllegalArgumentException("New capacity must be >= old stock");
        }
        if (!data.getStock().equals(existing.getStock())) {
            throw new IllegalArgumentException("New stock must match old stock");
        }

        existing.setLocation(data.getLocation());
        existing.setCapacity(data.getCapacity());
        existing.setStock(data.getStock());
        
        replaceWarehouseUseCase.replace(existing);
        //warehouseRepository.update(existing);
        return toWarehouseResponse(existing);
    }

    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    	LOGGER.info("WarehouseResourceImpl.toWarehouseResponse");
        Warehouse response = new Warehouse();
        response.setBusinessUnitCode(warehouse.getBusinessUnitCode());
        response.setLocation(warehouse.getLocation());
        response.setCapacity(warehouse.getCapacity());
        response.setStock(warehouse.getStock());
        return response;
    }
}