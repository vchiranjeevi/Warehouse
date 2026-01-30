package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    @Test
    void testLocationGettersAndSetters() {
        Location loc = new Location("LOC-500", 5, 1000);

        assertEquals("LOC-500", loc.getIdentification());
        assertEquals(5, loc.getMaxNumberOfWarehouses());
        assertEquals(1000, loc.getMaxCapacity());

        loc.setIdentification("LOC-600");
        loc.setMaxNumberOfWarehouses(10);
        loc.setMaxCapacity(2000);

        assertEquals("LOC-600", loc.getIdentification());
        assertEquals(10, loc.getMaxNumberOfWarehouses());
        assertEquals(2000, loc.getMaxCapacity());
    }
}