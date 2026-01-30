package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WarehouseTest {

    @Test
    void testWarehouseGettersAndSetters() {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode("MWH.500");
        w.setLocation("Delhi");
        w.setCapacity(300);
        w.setStock(150);

        assertEquals("MWH.500", w.getBusinessUnitCode());
        assertEquals("Delhi", w.getLocation());
        assertEquals(300, w.getCapacity());
        assertEquals(150, w.getStock());
    }
}