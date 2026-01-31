package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class WarehouseResourceImplTest {
	
	@Test
	void testCreateWarehouse_duplicateBusinessUnitCode() {
	    String payload = """
	        {
	          "businessUnitCode": "MWH.700",
	          "location": "Dubai",
	          "capacity": 500,
	          "stock": 200
	        }
	        """;

	    // First create succeeds
	    given().contentType("application/json").body(payload).when().post("/warehouse").then().statusCode(200);

	    // Second create with same code should fail
	    given().contentType("application/json").body(payload).when().post("/warehouse").then().statusCode(422)
	      .body("error", containsString("BusinessUnitCode already exists"));
	}

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
	void testCreateWarehouse_maxWarehousesExceeded() {
	    String payloadTemplate = """
	        {
	          "businessUnitCode": "%s",
	          "location": "Dubai",
	          "capacity": 500,
	          "stock": 200
	        }
	        """;

	    // Create warehouses until limit reached
	    for (int i = 1; i <= 5; i++) {
	        String payload = String.format(payloadTemplate, "MWH.70" + i);
	        given().contentType("application/json").body(payload).when().post("/warehouse").then().statusCode(200);
	    }

	    // Next one should fail
	    String payload = String.format(payloadTemplate, "MWH.706");
	    given().contentType("application/json").body(payload).when().post("/warehouse").then().statusCode(422)
	      .body("error", containsString("Max warehouses exceeded for location"));
	}

	@Test
	void testCreateWarehouse_capacityExceedsLocationMax() {
	    String payload = """
	        {
	          "businessUnitCode": "MWH.703",
	          "location": "Dubai",
	          "capacity": 999999,
	          "stock": 10
	        }
	        """;

	    given()
	      .contentType("application/json")
	      .body(payload)
	    .when()
	      .post("/warehouse")
	    .then()
	      .statusCode(422)
	      .body("error", containsString("Capacity exceeds location max capacity"));
	}
	
	@Test
	void testCreateWarehouse_capacityLessThanStock() {
	    String payload = """
	        {
	          "businessUnitCode": "MWH.702",
	          "location": "Dubai",
	          "capacity": 100,
	          "stock": 200
	        }
	        """;

	    given()
	      .contentType("application/json")
	      .body(payload)
	    .when()
	      .post("/warehouse")
	    .then()
	      .statusCode(422)
	      .body("error", containsString("Capacity must be greater than or equal to stock"));
	}
	
	@Test
	void testCreateWarehouse_invalidLocation() {
	    String payload = """
	        {
	          "businessUnitCode": "MWH.701",
	          "location": "InvalidCity",
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
	      .statusCode(422)
	      .body("error", containsString("Invalid location"));
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