package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public List<Warehouse> getAll() {
        return this.listAll().stream()
                .map(DbWarehouse::toWarehouse)
                .toList();
    }

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
        DbWarehouse dbWarehouse = DbWarehouse.fromWarehouse(warehouse);
        this.persist(dbWarehouse);
    }

    @Override
    @Transactional
    public void update(Warehouse warehouse) {
        DbWarehouse existing = this.find("businessUnitCode", warehouse.getBusinessUnitCode())
                .firstResult();
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
        DbWarehouse existing = this.find("businessUnitCode", warehouse.getBusinessUnitCode())
                .firstResult();
        if (existing != null) {
            this.delete(existing);
        }
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        DbWarehouse dbWarehouse = this.find("businessUnitCode", buCode).firstResult();
        return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
    }
}