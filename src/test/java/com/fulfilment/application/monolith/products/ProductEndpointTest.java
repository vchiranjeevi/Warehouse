package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

    @Test
    public void testCrudProduct() {
        final String path = "/product";

        // List all, should have all 3 products initially
        given()
            .when().get(path)
            .then()
            .statusCode(200)
            .body("name", hasItems("TONSTAD", "KALLAX", "BESTÅ"));

        // Delete TONSTAD (assuming ID=1 in seed data)
        given()
            .when().delete(path + "/1")
            .then()
            .statusCode(204);

        // List all, TONSTAD should be missing now
        given()
            .when().get(path)
            .then()
            .statusCode(200)
            .body("name", not(hasItem("TONSTAD")))
            .body("name", hasItems("KALLAX", "BESTÅ"));
    }
}