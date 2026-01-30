package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class StoreResourceTest {

    @Test
    void testCreateStoreEndpoint() {
        String payload = """
            {
              "storeCode": "STR.100",
              "name": "Electronics Hub",
              "location": "Dubai",
              "capacity": 200
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(201)
          .body("storeCode", equalTo("STR.100"))
          .body("name", equalTo("Electronics Hub"))
          .body("location", equalTo("Dubai"))
          .body("capacity", equalTo(200));
    }

    @Test
    void testGetStoreEndpoint_NotFound() {
        given()
        .when()
          .get("/store/STR.999")
        .then()
          .statusCode(404);
    }

    @Test
    void testUpdateStoreEndpoint() {
        // First create
        String payload = """
            {
              "storeCode": "STR.101",
              "name": "Fashion World",
              "location": "Dubai",
              "capacity": 150
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(201);

        // Update
        String newPayload = """
            {
              "storeCode": "STR.101",
              "name": "Fashion World Updated",
              "location": "AbuDhabi",
              "capacity": 250
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/store/STR.101")
        .then()
          .statusCode(200)
          .body("name", equalTo("Fashion World Updated"))
          .body("location", equalTo("AbuDhabi"))
          .body("capacity", equalTo(250));
    }

    @Test
    void testDeleteStoreEndpoint() {
        // First create
        String payload = """
            {
              "storeCode": "STR.102",
              "name": "Book Haven",
              "location": "Dubai",
              "capacity": 100
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(201);

        // Delete
        given()
        .when()
          .delete("/store/STR.102")
        .then()
          .statusCode(204);
    }
}

