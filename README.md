# Price Service

REST service that resolves the applicable final price for a product, brand and application date. If several price lists
overlap, the entry with the highest numeric priority wins.

This repository implements the Inditex backend technical exercise using API First and hexagonal architecture,
with unit tests, persistence integration tests and REST endpoint acceptance tests.

## Requirements

To build and run the application locally:

- JDK 25

Maven does not need to be installed because the project includes Maven Wrapper.

For containerized execution:

- Docker

H2 runs in memory and is initialized automatically through Flyway migrations.

## Quick start

### Linux and macOS

Compile the application and execute all tests:

```bash
./mvnw clean verify
```

Run locally:

```bash
./mvnw spring-boot:run
```

Build and run the executable JAR:

```bash
./mvnw clean package
java -jar target/pricing-service-1.0.0.jar
```

### Windows

Compile the application and execute all tests:

```powershell
.\mvnw.cmd clean verify
```

Run locally:

```powershell
.\mvnw.cmd spring-boot:run
```

Build and run the executable JAR:

```powershell
.\mvnw.cmd clean package
java -jar target/pricing-service-1.0.0.jar
```

The service listens on port `8080`.

### IntelliJ IDEA

Set both the Project SDK and the Maven Runner JRE to JDK 25. The Maven Wrapper selects the Maven version,
but Maven still needs a compatible JDK. You can check the JDK used from the terminal with `./mvnw -version`
(or `.\mvnw.cmd -version` on Windows).

After importing the project, if IntelliJ does not recognize the generated OpenAPI classes,
open the Maven tool window and select **Generate Sources and Update Folders**.

## Tests

The `clean verify` command above runs unit tests, persistence integration tests and REST endpoint acceptance tests.
The acceptance tests check the following required scenarios for product `35455` and brand `1` (ZARA),
including the identifiers, validity dates, price list, final price and currency in each response:

| Application date    | Price list | Price (EUR) |
|---------------------|------------|-------------|
| 2020-06-14 10:00:00 | 1          | 35.50       |
| 2020-06-14 16:00:00 | 2          | 25.45       |
| 2020-06-14 21:00:00 | 1          | 35.50       |
| 2020-06-15 10:00:00 | 3          | 30.50       |
| 2020-06-16 21:00:00 | 4          | 38.95       |

Additional tests cover inclusive date boundaries, invalid or missing parameters, queries without an applicable price,
and highest-priority selection regardless of candidate order.

## Profiles

The `dev` profile is active by default, so local and Docker execution require no additional environment variables.
Spring-based tests explicitly activate the `test` profile and use a separate in-memory H2 configuration.

## Docker

Build the Docker image from the project root:

```bash
docker build -t pricing-service:1.0.0 .
```

Run the container:

```bash
docker run --name pricing-service -p 8080:8080 pricing-service:1.0.0
```

The API will be available at:

http://localhost:8080

Stop and remove the container with:

```bash
docker stop pricing-service
docker rm pricing-service
```

## Architecture

The service follows hexagonal architecture, separating application use cases, domain models,
and REST and persistence adapters.

## Database

The application uses an in-memory H2 database. Data is discarded when the application stops and reloaded on startup.

Flyway manages the schema and loads the exercise's initial records through versioned
[database migrations](src/main/resources/db/migration).

The H2 console is enabled in the `dev` profile at <http://localhost:8080/h2-console>.
Use the following credentials:

| Field     | Value                   |
|-----------|-------------------------|
| JDBC URL  | `jdbc:h2:mem:pricingdb`  |
| User name | `inditex`               |
| Password  | `inditex`               |

## API First - OpenAPI

The API contract is defined in [`prices-api.yml`](src/main/resources/static/openapi/prices-api.yml).
Java interfaces and models are generated automatically during the Maven build.

### Get an applicable price

```http
GET /api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1
```

`applicationDate` must use the format `yyyy-MM-dd'T'HH:mm:ss`, without a time zone or UTC offset.
Both the start and end dates of each price's validity period are inclusive.

Try the request from a terminal:

```bash
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"
```

Successful response (`200 OK`):

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

Error responses:

- `400 Bad Request`: missing, malformed or invalid parameters.
- `404 Not Found`: no price applies to the supplied criteria.

### Main URLs

| Resource              | URL                                            |
|-----------------------|------------------------------------------------|
| Price endpoint        | <http://localhost:8080/api/v1/prices>          |
| Swagger UI            | <http://localhost:8080/swagger-ui.html>        |
| OpenAPI JSON            | <http://localhost:8080/v3/api-docs>        |
| OpenAPI YAML contract            | <http://localhost:8080/openapi/prices-api.yml>        |
| H2 console            | <http://localhost:8080/h2-console>             |
| Actuator health       | <http://localhost:8080/actuator/health>        |

## API collection - Postman

The repository includes a Postman collection
[`postman/Inditex-price-service.postman_collection.json`](postman/Inditex-price-service.postman_collection.json)
containing a test suite that covers the same REST endpoint scenarios as the Java acceptance tests,
with assertions on the responses. All requests and their assertions can be executed together using Collection Runner,
or individually.

1. Start the service and import the collection into Postman.
2. Check the collection variable `baseUrl`, which defaults to `http://localhost:8080`.
3. Open Collection Runner and run the entire collection to execute all scenarios and view their results,
   or send individual requests to check a specific scenario.
