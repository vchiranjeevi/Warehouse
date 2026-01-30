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
	          "name": "Electronics Hub",
	          "location": "Dubai",
	          "capacity": 200
	        }
	        """;

	    // Create store and extract numeric id
	    Integer id = given()
	      .contentType("application/json")
	      .body(payload)
	    .when()
	      .post("/store")
	    .then()
	      .statusCode(201)
	      .extract().path("id");

	    // Verify GET works with numeric id
	    given()
	    .when()
	      .get("/store/" + id)
	    .then()
	      .statusCode(200)
	      .body("name", equalTo("Electronics Hub"))
	      .body("location", equalTo("Dubai"))
	      .body("capacity", equalTo(200));
	}
	
    @Test
    void testGetStoreEndpoint_NotFound() {
        given()
        .when()
          .get("/store/999")
        .then()
          .statusCode(404);
    }

    @Test
    void testUpdateStoreEndpoint() {
        // First create
        String payload = """
            {
              "name": "Fashion World",
              "location": "Dubai",
              "capacity": 150
            }
            """;

        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(201)
          .extract().path("id");

        // Update using numeric id
        String newPayload = """
            {
              "name": "Fashion World Updated",
              "location": "AbuDhabi",
              "capacity": 250
            }
            """;

        given()
          .contentType("application/json")
          .body(newPayload)
        .when()
          .put("/store/" + id)
        .then()
          .statusCode(200)
          .body("name", equalTo("Fashion World Updated"))
          .body("location", equalTo("AbuDhabi"))
          .body("capacity", equalTo(250));
    }


    @Test
    void testDeleteStoreEndpoint() {
        String payload = """
            {
              "name": "Book Haven",
              "location": "Dubai",
              "capacity": 100
            }
            """;

        Integer id = given()
          .contentType("application/json")
          .body(payload)
        .when()
          .post("/store")
        .then()
          .statusCode(201)
          .extract().path("id");

        // Delete using numeric id
        given()
        .when()
          .delete("/store/" + id)
        .then()
          .statusCode(204);
    }

}

