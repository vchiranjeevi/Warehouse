package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ProductResourceTest {

	/*
	 * @Test void testGetExistingProduct_TONSTAD() { // TONSTAD seeded with id=1,
	 * stock=10 given() .when() .get("/product/1") .then() .statusCode(200)
	 * .body("name", equalTo("TONSTAD")) .body("stock", equalTo(10)); }
	 */

	@Test
	void testGetExistingProduct_TONSTAD() {
	    String payload = """
	        {
	          "name": "TONSTAD",
	          "description": "Wardrobe",
	          "price": 1200.0,
	          "stock": 10
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

	    given()
	    .when()
	      .get("/product/" + id)
	    .then()
	      .statusCode(200)
	      .body("name", equalTo("TONSTAD"))
	      .body("stock", equalTo(10));
	}
	
    @Test
    void testGetExistingProduct_KALLAX() {
        // KALLAX seeded with id=2, stock=5
        given()
        .when()
          .get("/product/2")
        .then()
          .statusCode(200)
          .body("name", equalTo("KALLAX Updated"))
          .body("stock", equalTo(8));
    }

    @Test
    void testGetExistingProduct_BESTA() {
        // BESTÅ seeded with id=3, stock=3
        given()
        .when()
          .get("/product/3")
        .then()
          .statusCode(200)
          .body("name", equalTo("BESTÅ"))
          .body("stock", equalTo(3));
    }

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

        // Verify GET works
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
        // Update seeded product KALLAX (id=2)
        String newPayload = """
            {
              "name": "KALLAX Updated",
              "description": "Smart Shelf",
              "price": 200.0,
              "stock": 8
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/product/2")
        .then()
          .statusCode(200)
          .body("name", equalTo("KALLAX Updated"))
          .body("description", equalTo("Smart Shelf"))
          .body("price", equalTo(200.0f))
          .body("stock", equalTo(8));
    }

    @Test
    void testDeleteProductEndpoint() {
        // Delete seeded product BESTÅ (id=3)
        given()
        .when()
          .delete("/product/3")
        .then()
          .statusCode(204);

        // Verify GET now fails
        given()
        .when()
          .get("/product/3")
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
          .body("$", isA(java.util.List.class))
          .body("name", hasItems("BESTÅ", "KALLAX Updated")); // seeded names
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
        // Try updating seeded product TONSTAD (id=1) without name
        String newPayload = """
            {
              "description": "Updated TONSTAD",
              "price": 999.0,
              "stock": 12
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/product/1")
        .then()
          .statusCode(422)
          .body("error", containsString("Product Name was not set"));
    }
}