package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProductResourceTest {

    @Test
    void testCreateProductEndpoint() {
        String payload = """
            {
              "name": "Laptop",
              "description": "Gaming Laptop",
              "price": 1500.0,
              "stock": 10
            }
            """;

        // Create product and extract numeric id
        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/product")
        .then()
          .statusCode(201)
          .body("name", equalTo("Laptop"))
          .body("description", equalTo("Gaming Laptop"))
          .body("price", equalTo(1500.0f))
          .body("stock", equalTo(10))
          .extract().path("id");

        // Verify GET works with numeric id
        given()
        .when()
          .get("/product/" + id)
        .then()
          .statusCode(200)
          .body("name", equalTo("Laptop"))
          .body("description", equalTo("Gaming Laptop"))
          .body("price", equalTo(1500.0f))
          .body("stock", equalTo(10));
    }

    @Test
    void testGetProductEndpoint_NotFound() {
        given()
        .when()
          .get("/product/99999")
        .then()
          .statusCode(404)
          .body("error", containsString("does not exist"));
    }

    @Test
    void testUpdateProductEndpoint() {
        // First create
        String payload = """
            {
              "name": "Phone",
              "description": "Smartphone",
              "price": 800.0,
              "stock": 20
            }
            """;

        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/product")
        .then()
          .statusCode(201)
          .extract().path("id");

        // Update
        String newPayload = """
            {
              "name": "Phone Updated",
              "description": "Smartphone Updated",
              "price": 900.0,
              "stock": 25
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/product/" + id)
        .then()
          .statusCode(200)
          .body("name", equalTo("Phone Updated"))
          .body("description", equalTo("Smartphone Updated"))
          .body("price", equalTo(900.0f))
          .body("stock", equalTo(25));
    }

    @Test
    void testDeleteProductEndpoint() {
        // First create
        String payload = """
            {
              "name": "Tablet",
              "description": "Android Tablet",
              "price": 400.0,
              "stock": 15
            }
            """;

        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/product")
        .then()
          .statusCode(201)
          .extract().path("id");

        // Delete
        given()
        .when()
          .delete("/product/" + id)
        .then()
          .statusCode(204);

        // Verify GET now fails
        given()
        .when()
          .get("/product/" + id)
        .then()
          .statusCode(404);
    }

    @Test
    void testListProductsEndpoint() {
        given()
        .when()
          .get("/product")
        .then()
          .statusCode(200)
          .body("$", isA(java.util.List.class));
    }

    @Test
    void testCreateProductWithIdShouldFail() {
        String payload = """
            {
              "id": 123,
              "name": "Invalid Product",
              "description": "Should fail",
              "price": 100.0,
              "stock": 5
            }
            """;

        given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/product")
        .then()
          .statusCode(422)
          .body("error", containsString("Id was invalidly set"));
    }

    @Test
    void testUpdateProductWithoutNameShouldFail() {
        // First create
        String payload = """
            {
              "name": "Camera",
              "description": "DSLR Camera",
              "price": 1200.0,
              "stock": 5
            }
            """;

        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/product")
        .then()
          .statusCode(201)
          .extract().path("id");

        // Update without name
        String newPayload = """
            {
              "description": "Updated DSLR Camera",
              "price": 1300.0,
              "stock": 6
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/product/" + id)
        .then()
          .statusCode(422)
          .body("error", containsString("Product Name was not set"));
    }
}

