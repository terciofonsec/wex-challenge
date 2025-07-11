# Purchase Transaction API

This project implements a Spring Boot application designed to manage purchase transactions and convert their amounts to various currencies using exchange rates from the Treasury Reporting Rates of Exchange API. The application adheres to Domain-Driven Design (DDD), Test-Driven Development (TDD), Clean Code, SOLID principles, KISS, and DRY.

## Requirements Implemented

* **Requirement #1: Store a Purchase Transaction**

  * Accepts and persists a purchase transaction with a description (max 50 characters), a valid transaction date, and a positive purchase amount in United States dollars, rounded to the nearest cent.

  * Assigns a unique identifier upon storage.

* **Requirement #2: Retrieve a Purchase Transaction in a Specified Country’s Currency**

  * Retrieves stored purchase transactions and converts their original USD amount to a specified target currency.

  * Uses exchange rates from the Treasury Reporting Rates of Exchange API.

  * Currency conversion rate must be less than or equal to the purchase date from within the last 6 months.

  * If no suitable rate is found, an error is returned.

  * Converted amount is rounded to two decimal places.

## Technologies Used

* **Language:** Java 17

* **Framework:** Spring Boot 3.2.7

* **Build Tool:** Gradle

* **Database:** H2 Database (in-memory)

* **External API Integration:** Spring Cloud OpenFeign

* **Data Persistence:** Spring Data JPA

* **Validation:** Jakarta Validation

* **Boilerplate Reduction:** Lombok

* **API Documentation:** SpringDoc OpenAPI (Swagger UI)

* **Monitoring:** Spring Boot Actuator

* **Containerization:** Docker, Docker Compose

* **Testing:** JUnit 5, Mockito

## Architecture Overview

The application follows a Clean/Hexagonal Architecture, separating concerns into distinct layers:

* **Domain Layer:** Contains core business logic, entities, and value objects (`Purchase`, `PurchaseId`, `Description`, `TransactionDate`, `PurchaseAmount`, `CurrencyAmount`, `CurrencyConversionService`). It is independent of external frameworks.

* **Application Layer:** Defines use cases (interactors) and ports (interfaces) for input (commands/queries) and output (repositories, external providers). (`CreatePurchaseUseCase`, `RetrieveConvertedPurchaseUseCase`, `PurchaseRepository`, `ExchangeRateProvider`).

* **Infrastructure Layer:** Implements the output ports using external technologies.

  * **Persistence Adapters:** Uses Spring Data JPA with H2 for database interaction.

  * **External API Adapters:** Uses Feign Client to interact with the Treasury API.

  * **Web Adapters:** REST controllers expose API endpoints.

* **Configuration Layer:** Spring configurations for dependency injection and external tool setup.

## How to Run the Application

### 1. Via IDE (IntelliJ IDEA, Eclipse, etc.)

1. **Clone the repository:**

   ```bash
   git clone <your-repo-url>
   cd purchase-transaction
   ```

2. **Import the project:** Open your IDE and import the project as a Gradle project.

3. **Build the project:** Let your IDE download dependencies and build the project. You might need to refresh Gradle dependencies.

4. **Run the application:** Locate the `PurchaseTransactionApplication.java` file (usually in `src/main/java/com/example/purchasetransaction`) and run its `main` method.

5. The application will start on `http://localhost:8080`.

### 2. Via Docker Compose

Ensure you have Docker and Docker Compose installed on your system.

1. **Clone the repository:**

   ```bash
   git clone <your-repo-url>
   cd purchase-transaction
   ```

2. **Build and run the Docker containers:**

   ```bash
   docker compose up --build
   ```

   This command will build the Docker image (if not already built or if changes are detected) and start the application container.

3. The application will be accessible at `http://localhost:8080`.

4. To stop and remove the containers:

   ```bash
   docker compose down
   ```

## API Documentation (OpenAPI / Swagger UI)

Once the application is running (either via IDE or Docker), you can access the API documentation:

1. **Open your web browser.**

2. **Navigate to the Swagger UI:**

   ```
   http://localhost:8080/swagger-ui.html
   ```

   Here you can see all available API endpoints, their expected inputs, and example responses. You can also try out the API calls directly from this interface.

3. **Access the OpenAPI JSON specification:**

   ```
   http://localhost:8080/v3/api-docs
   ```

   This URL provides the raw OpenAPI specification in JSON format, which can be used by other tools.

## How to Use Postman Collection

The project includes a Postman collection to easily test the API endpoints.

1. **Open Postman.**

2. **Import the Collection:**

   * Click on the `Import` button (usually in the top left or in the sidebar).

   * Select the `Raw Text` tab.

   * **Copy the entire content of the `postman-collection` Canvas** (the JSON provided in this conversation).

   * Paste the JSON into the `Raw Text` area.

   * Click `Continue`, then `Import`.

3. **Run API Calls:**

   * In your Postman sidebar, you will now see a collection named "Purchase Transaction API".

   * Expand the collection to see the requests.

   * **For "Create New Purchase":** Send the request. Copy the `id` from the response.

   * **For "Retrieve Converted Purchase":** Paste the copied `id` into the `purchase_id` collection variable (or directly into the URL `http://localhost:8080/purchases/{{purchase_id}}/converted/EUR`). Change `EUR` to other currency codes (e.g., `CAD`, `JPY`) as needed.

   * Test the Actuator and Swagger UI endpoints as well.

Enjoy exploring the application!
