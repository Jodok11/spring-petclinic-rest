package org.springframework.samples.petclinic.e2e.backend;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VetCRUDTests {

    // List to track created vet IDs for cleanup
    private static final List<Integer> createdVetIds = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        // Set the base URI for the API
        RestAssured.baseURI = "http://localhost:9966/petclinic/api";
    }

    @AfterAll
    public static void tearDown() {
        // Cleanup: Delete all vets created during tests
        for (Integer vetId : createdVetIds) {
            given()
                .pathParam("vetId", vetId)
                .when()
                .delete("/vets/{vetId}")
                .then()
                .statusCode(anyOf(is(204), is(404))); // Accept 204 (deleted) or 404 (already deleted)
        }
    }

    // Utility method to track vet IDs for cleanup
    public static void addCreatedVetId(int vetId) {
        createdVetIds.add(vetId);
    }

    /**
     * Happy Path
     */

    /**
     * Test for adding a new vet.
     * Verifies the vet can be created successfully, and its details can be retrieved afterward.
     */
    @Test
    public void testAddNewVet() {
        String vetJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "specialties": [{"id": 1, "name": "radiology"}]
            }
            """;

        // Create a new vet
        Response response = given()
            .header("Content-Type", "application/json")
            .body(vetJson)
            .when()
            .post("/vets")
            .then()
            .statusCode(201) // Created
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .extract()
            .response();

        int vetId = response.path("id");
        addCreatedVetId(vetId); // Track vet for cleanup

        // Validate the created vet
        given()
            .pathParam("vetId", vetId)
            .when()
            .get("/vets/{vetId}")
            .then()
            .statusCode(200) // OK
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"));
    }

    /**
     * Test for deleting a vet.
     * Verifies the vet can be deleted successfully, and ensures the vet no longer exists afterward.
     */
    @Test
    public void testDeleteVet() {
        String vetJson = """
            {
                "firstName": "Jane",
                "lastName": "Smith",
                "specialties": [{"id": 2, "name": "surgery"}]
            }
            """;

        // Create a new vet
        int vetId = given()
            .header("Content-Type", "application/json")
            .body(vetJson)
            .when()
            .post("/vets")
            .then()
            .statusCode(201) // Created
            .extract()
            .path("id");

        // Delete the vet
        given()
            .pathParam("vetId", vetId)
            .when()
            .delete("/vets/{vetId}")
            .then()
            .statusCode(204); // No Content

        // Verify the vet no longer exists
        given()
            .pathParam("vetId", vetId)
            .when()
            .get("/vets/{vetId}")
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Test for updating an existing vet.
     * Verifies the vet's details can be updated successfully.
     */
    @Test
    public void testUpdateVet() {
        String vetJson = """
            {
                "firstName": "Alex",
                "lastName": "Taylor",
                "specialties": [{"id": 1, "name": "radiology"}]
            }
            """;

        // Create a new vet
        Response response = given()
            .header("Content-Type", "application/json")
            .body(vetJson)
            .when()
            .post("/vets")
            .then()
            .statusCode(201) // Created
            .extract()
            .response();

        int vetId = response.path("id");
        addCreatedVetId(vetId); // Track vet for cleanup

        // Update the vet
        String updatedVetJson = """
            {
                "id": %d,
                "firstName": "Alexander",
                "lastName": "Taylor",
                "specialties": [{"id": 2, "name": "surgery"}]
            }
            """.formatted(vetId);

        given()
            .header("Content-Type", "application/json")
            .body(updatedVetJson)
            .when()
            .put("/vets/{vetId}", vetId)
            .then()
            .statusCode(204); // No Content

        // Validate the updated vet
        given()
            .pathParam("vetId", vetId)
            .when()
            .get("/vets/{vetId}")
            .then()
            .statusCode(200) // OK
            .body("firstName", equalTo("Alexander"))
            .body("specialties[0].name", equalTo("surgery"));
    }

    /**
     * Negative Testing && Edge Cases
     */

    /**
     * Test for updating a non-existing vet.
     * Verifies that the API returns a 404 Not Found status.
     */
    @Test
    public void testUpdateNonExistingVet() {
        String updatedVetJson = """
            {
                "id": 9999,
                "firstName": "NonExistentVet",
                "lastName": "DoesNotExist",
                "specialties": [{"id": 1, "name": "radiology"}]
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(updatedVetJson)
            .when()
            .put("/vets/{vetId}", 9999)
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Test for deleting a non-existing vet.
     * Verifies that the API returns a 404 Not Found status.
     */
    @Test
    public void testDeleteNonExistingVet() {
        given()
            .pathParam("vetId", 9999)
            .when()
            .delete("/vets/{vetId}")
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Edge Case:
     * Test for deleting the same vet twice.
     * Verifies that the API returns a 404 Not Found status on the second deletion attempt.
     */
    @Test
    public void testDoubleDeleteVet() {
        String vetJson = """
            {
                "firstName": "Chris",
                "lastName": "Evans",
                "specialties": [{"id": 1, "name": "radiology"}]
            }
            """;

        // Create a new vet
        Response response = given()
            .header("Content-Type", "application/json")
            .body(vetJson)
            .when()
            .post("/vets")
            .then()
            .statusCode(201) // Created
            .extract()
            .response();

        int vetId = response.path("id");
        addCreatedVetId(vetId); // Track vet for cleanup

        // Delete the vet for the first time
        given()
            .pathParam("vetId", vetId)
            .when()
            .delete("/vets/{vetId}")
            .then()
            .statusCode(204); // No Content

        // Attempt to delete the vet a second time
        given()
            .pathParam("vetId", vetId)
            .when()
            .delete("/vets/{vetId}")
            .then()
            .statusCode(404); // Not Found
    }
}
