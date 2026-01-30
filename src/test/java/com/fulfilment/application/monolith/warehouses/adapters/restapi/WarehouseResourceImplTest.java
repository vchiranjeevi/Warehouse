package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class WarehouseResourceImplTest {

    @Test
    void testCreateWarehouseEndpoint() {
        String payload = """
            {
              "businessUnitCode": "MWH.700",
              "location": "Dubai",
              "capacity": 500,
              "stock": 200
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/warehouse")
        .then()
          .statusCode(201)
          .body("businessUnitCode", equalTo("MWH.700"))
          .body("location", equalTo("Dubai"))
          .body("capacity", equalTo(500))
          .body("stock", equalTo(200));
    }

    @Test
    void testGetWarehouseEndpoint_NotFound() {
        given()
        .when()
          .get("/warehouse/MWH.999")
        .then()
          .statusCode(404);
    }

    @Test
    void testArchiveWarehouseEndpoint() {
        // First create
        String payload = """
            {
              "businessUnitCode": "MWH.701",
              "location": "Dubai",
              "capacity": 300,
              "stock": 100
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/warehouse")
        .then()
          .statusCode(201);

        // Then archive
        given()
        .when()
          .delete("/warehouse/MWH.701")
        .then()
          .statusCode(204);
    }

    @Test
    void testReplaceWarehouseEndpoint() {
        // First create
        String payload = """
            {
              "businessUnitCode": "MWH.702",
              "location": "Dubai",
              "capacity": 300,
              "stock": 100
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/warehouse")
        .then()
          .statusCode(201);

        // Replace with new capacity/location
        String newPayload = """
            {
              "businessUnitCode": "MWH.702",
              "location": "AbuDhabi",
              "capacity": 400,
              "stock": 100
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/warehouse/MWH.702")
        .then()
          .statusCode(200)
          .body("location", equalTo("AbuDhabi"))
          .body("capacity", equalTo(400));
    }
}