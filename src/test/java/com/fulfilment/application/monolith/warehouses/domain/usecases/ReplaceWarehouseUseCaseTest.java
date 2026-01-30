package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReplaceWarehouseUseCaseTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Positive Test Case ---
    @Test
    void testReplaceWarehouse_Positive() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("MWH.200");
        existing.setStock(50);
        existing.setCapacity(100);
        existing.setLocation("OldCity");

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setBusinessUnitCode("MWH.200");
        newWarehouse.setStock(50); // must match existing stock
        newWarehouse.setCapacity(120); // >= old stock
        newWarehouse.setLocation("NewCity");

        when(warehouseRepository.findByBusinessUnitCode("MWH.200")).thenReturn(existing);

        // just call, no assignment since method returns void
        replaceWarehouseUseCase.replace(newWarehouse);

        // verify side-effects
        assertEquals("NewCity", existing.getLocation());
        assertEquals(120, existing.getCapacity());
        assertEquals(50, existing.getStock());
        verify(warehouseRepository).update(existing);
    }

    // --- Negative Test Case: Not Found ---
    @Test
    void testReplaceWarehouse_NotFound() {
        when(warehouseRepository.findByBusinessUnitCode("INVALID")).thenReturn(null);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setBusinessUnitCode("INVALID");
        newWarehouse.setStock(10);
        newWarehouse.setCapacity(20);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertEquals("Warehouse not found for code: INVALID", ex.getMessage());
        verify(warehouseRepository, never()).update(any());
    }

    // --- Negative Test Case: Capacity Too Small ---
    @Test
    void testReplaceWarehouse_CapacityTooSmall() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("MWH.201");
        existing.setStock(50);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setBusinessUnitCode("MWH.201");
        newWarehouse.setStock(50);
        newWarehouse.setCapacity(40); // smaller than stock

        when(warehouseRepository.findByBusinessUnitCode("MWH.201")).thenReturn(existing);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertEquals("New capacity must be >= old stock", ex.getMessage());
        verify(warehouseRepository, never()).update(any());
    }

    // --- Negative Test Case: Stock Mismatch ---
    @Test
    void testReplaceWarehouse_StockMismatch() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("MWH.202");
        existing.setStock(50);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setBusinessUnitCode("MWH.202");
        newWarehouse.setStock(40); // mismatch
        newWarehouse.setCapacity(100);

        when(warehouseRepository.findByBusinessUnitCode("MWH.202")).thenReturn(existing);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> replaceWarehouseUseCase.replace(newWarehouse));

        assertEquals("New stock must match old stock", ex.getMessage());
        verify(warehouseRepository, never()).update(any());
    }
}