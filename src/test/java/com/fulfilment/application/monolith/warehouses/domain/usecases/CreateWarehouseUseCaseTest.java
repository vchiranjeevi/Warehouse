package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateWarehouseUseCaseTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private CreateWarehouseUseCase createWarehouseUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Positive Test Case ---
    @Test
    void testCreateWarehouse_Positive() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.100");
        warehouse.setLocation("Bangalore");
        warehouse.setCapacity(200);
        warehouse.setStock(100);

        doNothing().when(warehouseRepository).create(any(Warehouse.class));

        // just call, no assignment
        createWarehouseUseCase.create(warehouse);

        assertNotNull(warehouse.getCreatedAt(), "CreatedAt should be set");
        verify(warehouseRepository).create(warehouse);
    }

    // --- Negative Test Case ---
    @Test
    void testCreateWarehouse_CapacityLessThanStock_ShouldThrowException() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.101");
        warehouse.setLocation("Chennai");
        warehouse.setCapacity(50);
        warehouse.setStock(100);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> createWarehouseUseCase.create(warehouse));

        assertEquals("Capacity must be >= stock", ex.getMessage());
        verify(warehouseRepository, never()).create(any());
    }

    // --- Edge/Error Case ---
    @Test
    void testCreateWarehouse_ZeroCapacityAndZeroStock() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.102");
        warehouse.setLocation("Hyderabad");
        warehouse.setCapacity(0);
        warehouse.setStock(0);

        doNothing().when(warehouseRepository).create(any(Warehouse.class));

        // just call, no assignment since method returns void
        createWarehouseUseCase.create(warehouse);

        // verify side-effects
        assertEquals(0, warehouse.getCapacity());
        assertEquals(0, warehouse.getStock());
        assertNotNull(warehouse.getCreatedAt(), "CreatedAt should be set");
        verify(warehouseRepository).create(warehouse);
    }
}