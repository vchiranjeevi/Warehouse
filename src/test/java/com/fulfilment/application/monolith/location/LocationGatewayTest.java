package com.fulfilment.application.monolith.location;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;

import static org.junit.jupiter.api.Assertions.*;

public class LocationGatewayTest {

    @Test
    public void testWhenResolveExistingLocationShouldReturn() {
        // given
        LocationGateway locationGateway = new LocationGateway();

        // when
        Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

        // then
        assertNotNull(location);
        assertEquals("ZWOLLE-001", location.getIdentification());
        //assertEquals("Zwolle", location.getCity());
    }

    @Test
    public void testWhenResolveNonExistingLocationShouldReturnNull() {
        // given
        LocationGateway locationGateway = new LocationGateway();

        // when
        Location location = locationGateway.resolveByIdentifier("INVALID-999");

        // then
        assertNull(location);
    }
}