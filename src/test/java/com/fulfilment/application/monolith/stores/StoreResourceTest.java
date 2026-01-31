package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class StoreResourceTest {

    @Test
    void testListStoresEndpoint() {
        given()
        .when()
          .get("/store")
        .then()
          .statusCode(200)
          .body("$", isA(java.util.List.class))
          .body("name", hasItems("BESTÅ", "Electronics Hub", "KALLAX Updated", "TONSTAD")); // seeded stores
    }

    @Test
    void testGetExistingStore_TONSTAD() {
        given()
        .when()
          .get("/store/1")
        .then()
          .statusCode(200)
          .body("name", equalTo("TONSTAD"))
          .body("quantityProductsInStock", equalTo(10));
    }

    @Test
    void testGetExistingStore_KALLAX() {
        given()
        .when()
          .get("/store/2")
        .then()
          .statusCode(200)
          .body("name", equalTo("KALLAX Updated"))
          .body("quantityProductsInStock", equalTo(0));
    }

    @Test
    void testGetExistingStore_BESTA() {
        given()
        .when()
          .get("/store/3")
        .then()
          .statusCode(200)
          .body("name", equalTo("BESTÅ"))
          .body("quantityProductsInStock", equalTo(3));
    }

    @Test
    void testCreateStoreEndpoint() {
        String payload = """
            {
              "name": "Electronics Hub",
              "quantityProductsInStock": 200
            }
            """;

        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(201)
          .body("name", equalTo("Electronics Hub"))
          .body("quantityProductsInStock", equalTo(200))
          .extract().path("id");

        // Verify GET
        given()
        .when()
          .get("/store/" + id)
        .then()
          .statusCode(200)
          .body("name", equalTo("Electronics Hub"))
          .body("quantityProductsInStock", equalTo(200));
    }

    @Test
    void testCreateStoreEndpoint_InvalidId() {
        String payload = """
            {
              "id": 99,
              "name": "Invalid Store"
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(422)
          .body("error", containsString("Id was invalidly set"));
    }

    @Test
    void testGetStoreEndpoint_NotFound() {
        given()
        .when()
          .get("/store/999")
        .then()
          .statusCode(404)
          .body("error", containsString("does not exist"));
    }

    @Test
    void testUpdateStoreEndpoint() {
        // Update seeded store TONSTAD (id=1)
        String newPayload = """
            {
              "name": "TONSTAD Updated",
              "quantityProductsInStock": 20
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/store/1")
        .then()
          .statusCode(200)
          .body("name", equalTo("TONSTAD Updated"))
          .body("quantityProductsInStock", equalTo(20));
    }

    @Test
    void testUpdateStoreEndpoint_MissingName() {
        String payload = """
            {
              "quantityProductsInStock": 100
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .put("/store/1")
        .then()
          .statusCode(422)
          .body("error", containsString("Store Name was not set"));
    }

    @Test
    void testUpdateStoreEndpoint_NotFound() {
        String payload = """
            {
              "name": "Nonexistent",
              "quantityProductsInStock": 10
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .put("/store/999")
        .then()
          .statusCode(404)
          .body("error", containsString("does not exist"));
    }

    @Test
    void testPatchStoreEndpoint() {
        // Patch seeded store KALLAX (id=2)
        String patchPayload = """
            {
              "name": "KALLAX Updated"
            }
            """;

        given()
          .contentType("application/json")
          .body(patchPayload)
        .when()
          .patch("/store/2")
        .then()
          .statusCode(200)
          .body("name", equalTo("KALLAX Updated"));
    }

    @Test
    void testPatchStoreEndpoint_MissingName() {
        String payload = """
            {
              "quantityProductsInStock": 10
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .patch("/store/1")
        .then()
          .statusCode(422)
          .body("error", containsString("Store Name was not set"));
    }

    @Test
    void testPatchStoreEndpoint_NotFound() {
        String payload = """
            {
              "name": "Ghost Store",
              "quantityProductsInStock": 10
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .patch("/store/999")
        .then()
          .statusCode(404)
          .body("error", containsString("does not exist"));
    }

    @Test
    void testDeleteStoreEndpoint() {
        // Delete seeded store BESTÅ (id=3)
        given()
        .when()
          .delete("/store/3")
        .then()
          .statusCode(204);

        // Verify deletion
        given()
        .when()
          .get("/store/3")
        .then()
          .statusCode(404);
    }

    @Test
    void testDeleteStoreEndpoint_NotFound() {
        given()
        .when()
          .delete("/store/999")
        .then()
          .statusCode(404)
          .body("error", containsString("does not exist"));
    }
}