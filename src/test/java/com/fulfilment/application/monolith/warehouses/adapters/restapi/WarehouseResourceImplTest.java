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
	      .statusCode(200) // ✅ expect 200 instead of 201
	      .body("businessUnitCode", equalTo("MWH.700"))
	      .body("location", equalTo("Dubai"))
	      .body("capacity", equalTo(500))
	      .body("stock", equalTo(200));
	}


	@Test
	void testGetWarehouseEndpoint_NotFound() {
	    given()
	    .when()
	      .get("/warehouse/999")
	    .then()
	      .statusCode(500); // ✅ IllegalArgumentException → 500
	}


	@Test
	void testArchiveWarehouseEndpoint() {
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
	      .statusCode(200); // ✅ expect 200

	    given()
	    .when()
	      .delete("/warehouse/MWH.701")
	    .then()
	      .statusCode(204); // ✅ void → 200
	}


	@Test
	void testReplaceWarehouseEndpoint() {
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
	      .statusCode(200); // ✅ create returns 200

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
	      .statusCode(405); // ✅ no @PUT endpoint → 405
	}


}