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

public class OwnerCRUDTests {

    private static final List<Integer> createdOwnerIds = new ArrayList<>();

    @BeforeAll
    public static void setUp() {
        // Set the base URI for RestAssured
        RestAssured.baseURI = "http://localhost:9966/petclinic/api";
    }

    @AfterAll
    public static void tearDown() {
        // Loop through the list of created owner IDs and delete each on
        for (Integer ownerId : createdOwnerIds) {
            given()
                .pathParam("ownerId", ownerId)
                .when()
                .delete("http://localhost:9966/petclinic/api/owners/{ownerId}")
                .then()
                .statusCode(204); // Verify deletion is successful (No Content)
        }
    }

    /**
     * Utility method to add created owner IDs to the cleanup list.
     */
    public static void addCreatedOwnerId(int ownerId) {
        createdOwnerIds.add(ownerId);
    }

    /**
     * Happy Path
     */

    /**
     * Test for creating an owner with correct fields.
     * Verifies that the API returns a 201 Created response.
     */
    @Test
    public void testAddNewOwner() {
        // Define the owner data
        String ownerJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "address": "123 Main Street",
                "city": "Springfield",
                "telephone": "1234567890"
            }
            """;

        // Send POST request to create a new owner
        Response response = given()
            .header("Content-Type", "application/json")
            .body(ownerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(201) // Verify HTTP status code
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .extract()
            .response();

        // Extract the ID of the newly created owner for further validation
        int ownerId = response.path("id");
        addCreatedOwnerId(ownerId);

        // Validate the owner exists with a GET request
        given()
            .pathParam("ownerId", ownerId)
            .when()
            .get("/owners/{ownerId}")
            .then()
            .statusCode(200) // Verify the owner is retrievable
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .body("address", equalTo("123 Main Street"))
            .body("city", equalTo("Springfield"))
            .body("telephone", equalTo("1234567890"));
    }

    /**
     * Test for deleting an owner.
     * Verifies that the API returns a 204 No Content and afterwards 404 Not Found to verify it is indeed Deleted.
     */
    @Test
    public void testDeleteOwner() {
        //Create a new owner (for testing delete)
        String ownerJson = """
            {
                "firstName": "Mark",
                "lastName": "Taylor",
                "address": "101 Oak Street",
                "city": "Gotham",
                "telephone": "5566778899"
            }
            """;

        int ownerId = given()
            .header("Content-Type", "application/json")
            .body(ownerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

        //Delete the owner
        given()
            .pathParam("ownerId", ownerId)
            .when()
            .delete("/owners/{ownerId}")
            .then()
            .statusCode(204); // No Content

        //Verify the owner no longer exists
        given()
            .pathParam("ownerId", ownerId)
            .when()
            .get("/owners/{ownerId}")
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Test for updating an owner with correct fields.
     * Verifies that the API returns a 200 Ok response.
     */
    @Test
    public void testUpdateOwner() {
        // Define the owner data
        String ownerJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "address": "123 Main Street",
                "city": "Springfield",
                "telephone": "1234567890"
            }
            """;

        // Send POST request to create a new owner
        Response response = given()
            .header("Content-Type", "application/json")
            .body(ownerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(201) // Verify HTTP status code
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Doe"))
            .extract()
            .response();

        // Extract the ID of the newly created owner for further validation
        int ownerId = response.path("id");
        addCreatedOwnerId(ownerId);

        String updatedOwnerJson = """
            {
                "id": %d,
                "firstName": "Jane",
                "lastName": "Doe",
                "address": "456 Elm Street",
                "city": "Shelbyville",
                "telephone": "9876543210"
            }
            """.formatted(ownerId);

        given()
            .header("Content-Type", "application/json")
            .body(updatedOwnerJson)
            .when()
            .put("/owners/{ownerId}", ownerId)
            .then()
            .statusCode(204); // No Content

        given()
            .pathParam("ownerId", ownerId)
            .when()
            .get("/owners/{ownerId}")
            .then()
            .statusCode(200)
            .body("firstName", equalTo("Jane"))
            .body("address", equalTo("456 Elm Street"))
            .body("telephone", equalTo("9876543210"));
    }

    /**
     * Test for creating an owner with correct fields and getting it.
     * Verifies that the API returns a 200 Ok response, and verifies the fields of the reponse.
     */
    @Test
    public void testReadOwner() {
        // Define the owner data
        String ownerJson = """
            {
                "firstName": "Alice",
                "lastName": "Smith",
                "address": "789 Pine Street",
                "city": "Metropolis",
                "telephone": "1122334455"
            }
            """;

        // Send POST request to create a new owner
        Response response = given()
            .header("Content-Type", "application/json")
            .body(ownerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(201) // Verify HTTP status code
            .body("firstName", equalTo("Alice"))
            .body("lastName", equalTo("Smith"))
            .extract()
            .response();

        // Extract the ID of the newly created owner for further validation
        int ownerId = response.path("id");
        addCreatedOwnerId(ownerId);

        given()
            .pathParam("ownerId", ownerId)
            .when()
            .get("/owners/{ownerId}")
            .then()
            .statusCode(200)
            .body("firstName", equalTo("Alice"))
            .body("lastName", equalTo("Smith"))
            .body("address", equalTo("789 Pine Street"))
            .body("city", equalTo("Metropolis"))
            .body("telephone", equalTo("1122334455"));
    }

    /**
     * Negative Testing
     */

    /**
     * Test for creating an owner with missing required fields.
     * Verifies that the API returns a 400 Bad Request response.
     */
    @Test
    public void testCreateOwnerWithMissingFields() {
        // JSON payload with missing required fields
        String incompleteOwnerJson = """
            {
                "firstName": "",
                "lastName": "",
                "address": "123 Main Street",
                "city": "Springfield",
                "telephone": "1234567890"
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(incompleteOwnerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(400); // Bad Request
    }

    /**
     * Test for creating an owner with invalid telephone number.
     * Verifies that the API returns a 400 Bad Request response.
     */
    @Test
    public void testCreateOwnerWithInvalidTelephone() {
        // JSON payload with an invalid telephone number
        String invalidPhoneJson = """
            {
                "firstName": "John",
                "lastName": "Doe",
                "address": "123 Main Street",
                "city": "Springfield",
                "telephone": "abc123"
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(invalidPhoneJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(400); // Bad Request
    }

    /**
     * Test for updating a non-existing owner.
     * Verifies that the API returns a 404 Not Found response.
     */
    @Test
    public void testUpdateNonExistentOwner() {
        String updatedOwnerJson = """
            {
                "id": 9999,
                "firstName": "Jane",
                "lastName": "Doe",
                "address": "456 Elm Street",
                "city": "Shelbyville",
                "telephone": "9876543210"
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(updatedOwnerJson)
            .when()
            .put("/owners/{ownerId}", 9999)
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Test for getting a non-existing owner.
     * Verifies that the API returns a 404 Not Found response.
     */
    @Test
    public void testRetrieveNonExistentOwner() {
        given()
            .pathParam("ownerId", 9999)
            .when()
            .get("/owners/{ownerId}")
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Test for deleting a non-existing owner.
     * Verifies that the API returns a 404 Not Found response.
     */
    @Test
    public void testDeleteNonExistentOwner() {
        given()
            .pathParam("ownerId", 9999)
            .when()
            .delete("/owners/{ownerId}")
            .then()
            .statusCode(404); // Not Found
    }

    /**
     * Edge Cases
     */

    /**
     * Test for creating an owner with an invalid name.
     * Verifies that the API returns a 400 Bad Request response.
     */
    @Test
    public void testCreateOwnerWithIllegalNameFails() {
        String ownerJson = """
            {
                "firstName": "Muad'dib",
                "lastName": "Doe",
                "address": "123 Main Street",
                "city": "Springfield",
                "telephone": "123"
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(ownerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(400); // Bad Request
    }

    /**
     * Test for creating an owner with an invalid name.
     * Verifies that the API returns a 400 Bad Request response.
     */
    @Test
    public void testCreateOwnerSQLInjectionNameFails() {
        // Define the owner data
        String ownerJson = """
            {
                "firstName": "Robert');DROP TABLE Owners;--",
                "lastName": "Doe",
                "address": "123 Main Street",
                "city": "Springfield",
                "telephone": "1234567890"
            }
            """;

        given()
            .header("Content-Type", "application/json")
            .body(ownerJson)
            .when()
            .post("/owners")
            .then()
            .statusCode(400); // Bad Request
    }
}
