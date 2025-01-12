package org.springframework.samples.petclinic.e2e.frontend;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // To specify test execution order
public class FrontendTests {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:4200";

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "..\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    @AfterEach
    public void sleep() {
        if (driver != null) {
            driver.quit();
        }
        try {
            // to sleep 2.5 seconds
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            // recommended because catching InterruptedException clears interrupt flag
            Thread.currentThread().interrupt();
            return;
        }
    }

    @Test
    @Order(1)
    public void testAddOwner() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Add Owner"
        WebElement addOwnerButton = driver.findElement(By.linkText("ADD NEW"));
        addOwnerButton.click();

        // Fill out the owner form
        driver.findElement(By.id("firstName")).sendKeys("Jane");
        driver.findElement(By.id("lastName")).sendKeys("Doe");
        driver.findElement(By.id("address")).sendKeys("456 Elm Street");
        driver.findElement(By.id("city")).sendKeys("Metropolis");
        driver.findElement(By.id("telephone")).sendKeys("9876543210");

        // Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Wait for the Owners page to reload and validate the new owner
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Jane Doe")));
        Assertions.assertTrue(driver.getPageSource().contains("Jane Doe"), "Owner should be added successfully");
    }

    @Test
    @Order(2)
    public void testViewOwnerDetails() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink2 = driver.findElement(By.linkText("OWNERS"));
        ownersLink2.click();

        // Click "Add Owner"
        WebElement addOwnerButton = driver.findElement(By.linkText("ADD NEW"));
        addOwnerButton.click();

        // Fill out the owner form
        driver.findElement(By.id("firstName")).sendKeys("Joane");
        driver.findElement(By.id("lastName")).sendKeys("Doe");
        driver.findElement(By.id("address")).sendKeys("789 Elm Street");
        driver.findElement(By.id("city")).sendKeys("Petropolis");
        driver.findElement(By.id("telephone")).sendKeys("1276543210");

        // Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Wait for the Owners page to reload and validate the new owner
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Joane Doe")));
        Assertions.assertTrue(driver.getPageSource().contains("Joane Doe"), "Owner should be added successfully");

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Search" to view all Owners
        WebElement searchOwnerButton = driver.findElement(By.linkText("SEARCH"));
        searchOwnerButton.click();

        // Click on the owner's name to view details
        WebElement ownerDetailsLink = driver.findElement(By.linkText("Joane Doe"));
        ownerDetailsLink.click();

        // Validate owner details are displayed
        WebElement ownerDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Edit Owner']")));
        Assertions.assertTrue(ownerDetails.getText().contains("Joane Doe"), "Owner details should be displayed");
        Assertions.assertTrue(ownerDetails.getText().contains("789 Elm Street"), "Owner address should be correct");
    }

    @Test
    @Order(3)
    public void testUpdateOwner() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Search" to view all Owners
        WebElement addOwnerButton = driver.findElement(By.linkText("SEARCH"));
        addOwnerButton.click();

        // Click on the owner's name to view details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Jane Doe")));
        WebElement ownerDetailsLink = driver.findElement(By.linkText("Jane Doe"));
        ownerDetailsLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));

        // Click on "Edit" for the owner
        WebElement editOwnerLink = driver.findElement(By.xpath("//button[text()='Edit Owner']"));
        editOwnerLink.click();

        // Update the telephone number
        WebElement telephoneField = driver.findElement(By.id("telephone"));
        telephoneField.clear();
        telephoneField.sendKeys("1112223333");

        // Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Validate the updated information
        WebElement ownerDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));
        Assertions.assertTrue(ownerDetails.getText().contains("1112223333"), "Owner telephone should be updated");
    }

    @Test
    @Order(4)
    public void testAddPet() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Search" to view all Owners
        WebElement searchOwnerButton = driver.findElement(By.linkText("SEARCH"));
        searchOwnerButton.click();

        // Click on the owner's name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Jane Doe")));
        WebElement ownerDetailsLink = driver.findElement(By.linkText("Jane Doe"));
        ownerDetailsLink.click();

        // Click "Add New Pet"
        WebElement addPetButton = driver.findElement(By.xpath("//button[text()='Add New Pet']"));
        addPetButton.click();

        // Fill out the pet form
        driver.findElement(By.id("name")).sendKeys("Buddy");
        driver.findElement(By.name("birthDate")).sendKeys("2023-01-01");
        driver.findElement(By.id("type")).sendKeys("Dog");
        driver.findElement(By.tagName("body")).click();

        // Save the Data
        WebElement submitButton = driver.findElement(By.xpath("//button[text()='Save Pet']"));
        submitButton.click();

        // Validate the pet is listed under the owner
        WebElement petsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-pet-list")));
        Assertions.assertTrue(petsTable.getText().contains("Buddy"), "Pet should be added successfully");
    }

    @Test
    @Order(5)
    public void testViewPetDetails() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Search" to view all Owners
        WebElement searchOwnerButton = driver.findElement(By.linkText("SEARCH"));
        searchOwnerButton.click();

        // Click on the owner's name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Jane Doe")));
        WebElement ownerDetailsLink = driver.findElement(By.linkText("Jane Doe"));
        ownerDetailsLink.click();

        // Validate pet details
        WebElement petDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-pet-list")));
        Assertions.assertTrue(petDetails.getText().contains("Buddy"), "Pet details should be visible");
        Assertions.assertTrue(petDetails.getText().contains("dog"), "Pet type should be visible");
    }

    @Test
    @Order(6)
    public void testUpdatePetDetails() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Search" to view all Owners
        WebElement searchOwnerButton = driver.findElement(By.linkText("SEARCH"));
        searchOwnerButton.click();

        // Click on the owner's name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Jane Doe")));
        WebElement ownerDetailsLink = driver.findElement(By.linkText("Jane Doe"));
        ownerDetailsLink.click();

        // Validate pet details
        WebElement petDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-pet-list")));
        Assertions.assertTrue(petDetails.getText().contains("Buddy"), "Pet details should be visible");
        Assertions.assertTrue(petDetails.getText().contains("dog"), "Pet type should be visible");

        // Click "Edit Pet"
        WebElement addPetButton = driver.findElement(By.xpath("//button[text()='Edit Pet']"));
        addPetButton.click();

        // Update Info
        // Fill out the pet form
        driver.findElement(By.id("name")).clear();
        driver.findElement(By.id("name")).sendKeys("Buddy v2");
        driver.findElement(By.name("birthDate")).sendKeys("2024-01-01");
        driver.findElement(By.id("type")).sendKeys("cat");
        driver.findElement(By.tagName("body")).click();

        // Save the Data
        WebElement submitButton = driver.findElement(By.xpath("//button[text()='Update Pet']"));
        submitButton.click();

        // Validate the pet is listed under the owner
        WebElement petsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-pet-list")));
        Assertions.assertTrue(petsTable.getText().contains("Buddy v2"), "Pet should be updated successfully");

        // Delete the pet
        WebElement deletePetButton = driver.findElement(By.xpath("//button[text()='Delete Pet']"));
        deletePetButton.click();
    }

    @Test
    @Order(7)
    public void testDeletePet() {
        driver.get(BASE_URL);

        // Navigate to the "Owners" page
        WebElement ownersLink = driver.findElement(By.linkText("OWNERS"));
        ownersLink.click();

        // Click "Search" to view all Owners
        WebElement searchOwnerButton = driver.findElement(By.linkText("SEARCH"));
        searchOwnerButton.click();

        // Click on the owner's name
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Jane Doe")));
        WebElement ownerDetailsLink = driver.findElement(By.linkText("Jane Doe"));
        ownerDetailsLink.click();

        // Click "Add New Pet"
        WebElement addPetButton = driver.findElement(By.xpath("//button[text()='Add New Pet']"));
        addPetButton.click();

        // Fill out the pet form
        driver.findElement(By.id("name")).sendKeys("Buddy Junior");
        driver.findElement(By.name("birthDate")).sendKeys("2023-01-01");
        driver.findElement(By.id("type")).sendKeys("Dog");
        driver.findElement(By.tagName("body")).click();

        // Save the Data
        WebElement submitButton = driver.findElement(By.xpath("//button[text()='Save Pet']"));
        submitButton.click();

        // Validate the pet is listed under the owner
        WebElement petsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-pet-list")));
        Assertions.assertTrue(petsTable.getText().contains("Buddy Junior"), "Pet should be added successfully");

        // Delete the pet
        WebElement deletePetButton = driver.findElement(By.xpath("//button[text()='Delete Pet']"));
        deletePetButton.click();

        // Validate the pet is no longer listed under the owner
        WebElement petsTableNew = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-pet-list")));
        Assertions.assertFalse(petsTableNew.getText().contains("Buddy Junior"), "Pet should be deleted successfully");
    }

    @Test
    @Order(8)
    public void testAddVet() {
        driver.get(BASE_URL);

        // Navigate to the "Veterinarians" page
        WebElement vetsLink = driver.findElement(By.linkText("VETERINARIANS"));
        vetsLink.click();

        // Click "Add New"
        WebElement addVetButton = driver.findElement(By.linkText("ADD NEW"));
        addVetButton.click();

        // Fill out the vet form
        driver.findElement(By.id("firstName")).sendKeys("John");
        driver.findElement(By.id("lastName")).sendKeys("Smith");
        driver.findElement(By.id("specialties")).sendKeys("Radiology");

        // Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Validate the vet is listed
        WebElement vetsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));
        Assertions.assertTrue(vetsTable.getText().contains("John Smith"), "Vet should be added successfully");
    }

    @Test
    @Order(9)
    public void testEditVetDetails() {
        driver.get(BASE_URL);
        String vetName = "John Smith";

        // Navigate to the "Veterinarians" page
        WebElement vetsLink = driver.findElement(By.linkText("VETERINARIANS"));
        vetsLink.click();

        // Click "Search" to view all Vets
        WebElement searchVetButton = driver.findElement(By.linkText("ALL"));
        searchVetButton.click();

        // Wait for the table to be present
        WebElement tableRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td[contains(text(), '" + vetName + "')]]")));

        // Find the cell containing the "Edit Vet" button within the row
        WebElement buttonCell = tableRow.findElement(By.xpath(".//td[contains(., 'Edit Vet')]"));

        // Find the "Edit Vet" button within the cell
        WebElement editButton = buttonCell.findElement(By.xpath(".//button[text()='Edit Vet']"));

        // Click the "Edit Vet" button
        editButton.click();

        // Update the vet's specialty
        WebElement specialtyField = driver.findElement(By.xpath("//div[contains(@class, 'mat-mdc-select-trigger')]"));
        specialtyField.click();
        specialtyField.findElement(By.xpath("//mat-option[contains(., 'surgery')]//mat-pseudo-checkbox")).click();
        specialtyField.findElement(By.xpath("//mat-option[contains(., 'radiology')]//mat-pseudo-checkbox")).click();
        driver.findElement(By.tagName("body")).click();

        // Save the Data
        WebElement submitButton = driver.findElement(By.xpath("//button[text()='Save Vet']"));
        submitButton.click();

        // Validate the updated specialty
        WebElement vetDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));
        Assertions.assertTrue(vetDetails.getText().contains("surgery"), "Vet specialty should be updated");
    }

    @Test
    @Order(10)
    public void testDeleteVet() {
        String vetName = "Max Muster";
        driver.get(BASE_URL);

        // Navigate to the "Veterinarians" page
        WebElement vetsLink = driver.findElement(By.linkText("VETERINARIANS"));
        vetsLink.click();

        // Click "Add New"
        WebElement addVetButton = driver.findElement(By.linkText("ADD NEW"));
        addVetButton.click();

        // Fill out the vet form
        driver.findElement(By.id("firstName")).sendKeys("Max");
        driver.findElement(By.id("lastName")).sendKeys("Muster");
        driver.findElement(By.id("specialties")).sendKeys("Radiology");

        // Submit the form
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        submitButton.click();

        // Validate the vet is listed
        WebElement vetsTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));
        Assertions.assertTrue(vetsTable.getText().contains("Max Muster"), "Vet should be added successfully");

        // Delete the Vet
        // Wait for the table to be present
        WebElement tableRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[td[contains(text(), '" + vetName + "')]]")));

        // Find the cell containing the "Delete Vet" button within the row
        WebElement buttonCell = tableRow.findElement(By.xpath(".//td[contains(., 'Delete Vet')]"));

        // Find the "Delete Vet" button within the cell
        WebElement deleteButton = buttonCell.findElement(By.xpath(".//button[text()='Delete Vet']"));

        // Click the "Delete Vet" button
        deleteButton.click();

        // Validate the Vet is not listed anymore
        WebElement vetDetails = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("table-striped")));
        try {
            // to sleep 1 second
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        Assertions.assertFalse(vetDetails.getText().contains("Max Muster"), "Vet should not be visible anymore");
    }
}
