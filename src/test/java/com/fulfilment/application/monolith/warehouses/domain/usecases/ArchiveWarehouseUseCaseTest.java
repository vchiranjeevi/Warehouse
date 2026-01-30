package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArchiveWarehouseUseCaseTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- Positive Test Case ---
    @Test
    void testArchiveWarehouse_Positive() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.001");
        warehouse.setStock(100);

        when(warehouseRepository.findByBusinessUnitCode("MWH.001")).thenReturn(warehouse);

        archiveWarehouseUseCase.archive(warehouse);

        assertNotNull(warehouse.getArchivedAt(), "ArchivedAt should be set");
        verify(warehouseRepository).update(warehouse);
    }

    // --- Negative Test Case ---
    @Test
    void testArchiveWarehouse_NotFound() {
    	Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("INVALID");
        warehouse.setStock(100);
    	
        when(warehouseRepository.findByBusinessUnitCode("INVALID")).thenReturn(null);

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> archiveWarehouseUseCase.archive(warehouse));

        assertEquals("Warehouse not found for code: INVALID", ex.getMessage());
        verify(warehouseRepository, never()).update(any());
    }

    // --- Edge/Error Case ---
    @Test
    void testArchiveWarehouse_AlreadyArchived() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MWH.002");
        warehouse.setArchivedAt(LocalDateTime.now()); // already archived

        when(warehouseRepository.findByBusinessUnitCode("MWH.002")).thenReturn(warehouse);

        archiveWarehouseUseCase.archive(warehouse);

        // ArchivedAt should remain non-null
        assertNotNull(warehouse.getArchivedAt());
        verify(warehouseRepository).update(warehouse);
    }
}