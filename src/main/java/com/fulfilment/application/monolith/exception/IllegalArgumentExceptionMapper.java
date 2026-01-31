package com.fulfillment.application.monolith.exception;
import java.util.Map;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException ex) {
        return Response.status(422)
                       .entity(Map.of("error", ex.getMessage()))
                       .build();
    }
}

