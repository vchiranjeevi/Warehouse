package com.fulfilment.application.monolith.warehouses.adapters.database;

import java.util.List;

import org.jboss.logging.Logger;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {
	private static final Logger LOGGER = Logger.getLogger(WarehouseRepository.class.getName());
    @Override
    public List<Warehouse> getAll() {
    	LOGGER.info("WarehouseRepository.getAll");
        return this.listAll().stream()
                .map(DbWarehouse::toWarehouse)
                .toList();
    }

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
    	LOGGER.info("WarehouseRepository.create");
        DbWarehouse dbWarehouse = DbWarehouse.fromWarehouse(warehouse);
        this.persist(dbWarehouse);
    }

    @Override
    @Transactional
    public void update(Warehouse warehouse) {
    	LOGGER.info("WarehouseRepository.update");
        DbWarehouse existing = this.find("businessUnitCode", warehouse.getBusinessUnitCode())
                .firstResult();
        LOGGER.info("WarehouseRepository.update existing.." + existing);
        if (existing != null) {
            existing.setLocation(warehouse.getLocation());
            existing.setCapacity(warehouse.getCapacity());
            existing.setStock(warehouse.getStock());
            existing.setArchivedAt(warehouse.getArchivedAt());
            this.persist(existing);
        }
    }

    @Override
    @Transactional
    public void remove(Warehouse warehouse) {
    	LOGGER.info("WarehouseRepository.remove");
        DbWarehouse existing = this.find("businessUnitCode", warehouse.getBusinessUnitCode())
                .firstResult();
        LOGGER.info("WarehouseRepository.remove...." + existing);
        if (existing != null) {
            this.delete(existing);
        }
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
    	LOGGER.info("WarehouseRepository.findByBusinessUnitCode");
        DbWarehouse dbWarehouse = this.find("businessUnitCode", buCode).firstResult();
        LOGGER.info("WarehouseRepository.findByBusinessUnitCode..." + dbWarehouse);
        return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
    }
}