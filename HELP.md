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

        Supported Currencies and How to Extend Them
        The application relies on a specific mapping between ISO currency codes (e.g., "BRL", "EUR") and the country names used by the Treasury Reporting Rates of Exchange API (e.g., "Brazil", "Euro Zone"). This mapping is essential for correctly filtering exchange rates from the external API.

        Currently Supported Currencies:

        The following ISO currency codes are explicitly mapped in the TreasuryExchangeRateAdapter.java file:

        USD: United States - Dollar

        EUR: Euro Zone - Euro

        BRL: Brazil - Real

        CAD: Canada - Dollar

        JPY: Japan - Yen

        GBP: United Kingdom - Pound

        AUD: Australia - Dollar

        MXN: Mexico - Peso

        CHF: Switzerland - Franc

        CNY: China - Yuan (Example)

        How to Add Support for New Currencies:

        If you need to convert a purchase to a currency not listed above, you must manually add its mapping to the ISO_CODE_TO_TREASURY_COUNTRY_MAP in the src/main/java/com/example/purchasetransaction/infrastructure/adapter/external/TreasuryExchangeRateAdapter.java file.

        Identify the ISO Code: Determine the standard 3-letter ISO code for the currency (e.g., "SEK" for Swedish Krona).

        Find the Treasury API Country Name: Consult the Treasury Reporting Rates of Exchange API documentation or explore its data directly (e.g., https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange) to find the exact "Country" name associated with that currency in the API's dataset. For example, for Swedish Krona, it might be "Sweden".

        Update the Map: Add an entry to the ISO_CODE_TO_TREASURY_COUNTRY_MAP in TreasuryExchangeRateAdapter.java:

        private static final Map<String, String> ISO_CODE_TO_TREASURY_COUNTRY_MAP = Map.of(
        // ... existing mappings
        "SEK", "Sweden" // Example for Swedish Krona
        );

        Rebuild and Redeploy: After modifying the map, you must rebuild and redeploy the application for the changes to take effect.

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

* **Code Coverage:** JaCoCo

* **Code Quality Analysis:** SonarQube

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
   git clone https://github.com/terciofonsec/wex-challenge.git
   cd to-your-project-location

   ```

2. **Import the project:** Open your IDE and import the project as a Gradle project.

3. **Build the project:** Let your IDE download dependencies and build the project. You might need to refresh Gradle dependencies.

4. **Run the application:** Locate the `PurchaseTransactionApplication.java` file (in `com/wex/challenge`) and run its `main` method.

5. The application will start on `http://localhost:8080`.

### 2. Via Docker Compose

Ensure you have Docker and Docker Compose installed on your system.

1. **Clone the repository:**

    The same above

2. **Build and run the Docker containers:**

   ```bash
   docker compose up --build

   ```

   This command will build the Docker image (if not already built or if changes are detected) and start the application container.

3. The application will be accessible at `http://localhost:8080`.

4. To stop and remove the containers:

   ```
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

## Test Coverage (JaCoCo)

To generate the code coverage report using JaCoCo:

1. **Run the JaCoCo report task:**

   ```
   ./gradlew jacocoTestReport

   ```

2. **View the report:** Open the `index.html` file located in `build/jacocoHtml/index.html` in your web browser.

You can also run the coverage verification task to ensure a minimum coverage threshold is met:

```
./gradlew jacocoTestCoverageVerification

```

## Code Quality Analysis (SonarQube)

To perform code quality analysis with SonarQube:

1. **Start the SonarQube Server locally (if not already running):**
   The easiest way is using Docker:

   ```
   docker run -d --name sonarqube -p 9000:9000 -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true sonarqube:latest

   ```

   * SonarQube UI: `http://localhost:9000`

   * Credentials admin/admin.

2. **Run the SonarQube analysis task:**

   ```
   ./gradlew sonar

   ```

3. **View the analysis results:**

   * After the `sonar` task completes successfully, go to `http://localhost:9000` in your browser.

   * You should see your project listed on the dashboard with its code quality metrics, *code smells*, bugs, and coverage.


 BIG NOTE:
    For challenge propose I did not create any branches to commit and opened PRs, I did it directly to main to facilitate and speed up the development. I hope you forgive me.

