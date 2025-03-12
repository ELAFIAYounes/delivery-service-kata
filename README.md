# Time Slot Management API

A modern Spring Boot application that provides a RESTful API for managing time slots with HATEOAS support. This service allows users to view and reserve time slots for different delivery modes.

## Features

- Time slot management with multiple delivery modes
- HATEOAS-compliant REST API
- SQLite database persistence
- OpenAPI/Swagger documentation
- Comprehensive test coverage

## Technologies

- Java 21
- Spring Boot 3.2.2
- Spring HATEOAS
- SQLite Database
- SpringDoc OpenAPI UI
- Maven

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Quick Start

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd TK
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

Access the interactive API documentation:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## API Endpoints

### Time Slots

- `GET /api/timeslots/delivery-modes`
  - Get all available delivery modes
  - Response: List of delivery modes (DRIVE, DELIVERY, DELIVERY_TODAY, DELIVERY_ASAP)

- `GET /api/timeslots?deliveryMode={mode}`
  - Get available time slots for a specific delivery mode
  - Parameters: 
    - `mode`: Delivery mode (enum)
  - Response: Collection of time slots with HATEOAS links

- `GET /api/timeslots/{id}`
  - Get a specific time slot by ID
  - Parameters:
    - `id`: Time slot ID
  - Response: Time slot details with HATEOAS links

- `PUT /api/timeslots/{id}/reserve`
  - Reserve a specific time slot
  - Parameters:
    - `id`: Time slot ID
  - Response: Updated time slot with reservation status

## Database

The application uses SQLite for data persistence. The database file (`delivery.db`) is automatically created in the project root directory. Initial schema and data are loaded from:
- `schema.sql`: Database schema definition
- `data.sql`: Initial time slot data

## Testing

Run the test suite:
```bash
./mvnw test
```

The project includes:
- Unit tests for service layer
- Integration tests for controllers
- HATEOAS link verification
- Database operation tests

## Project Structure

```
src/
├── main/
│   ├── java/com/delivery/
│   │   ├── domain/
│   │   │   ├── entity/       # Domain entities
│   │   │   ├── model/        # HATEOAS models
│   │   │   ├── mapper/       # Object mappers
│   │   │   └── repository/   # Data repositories
│   │   ├── service/          # Business logic
│   │   └── rest/api/         # REST controllers
│   └── resources/
│       ├── application.yml   # Application configuration
│       ├── schema.sql       # Database schema
│       └── data.sql         # Initial data
└── test/                    # Test classes
```

## Error Handling

The API uses standard HTTP status codes and includes detailed error messages:
- `200 OK`: Successful operation
- `404 Not Found`: Resource not found
- `409 Conflict`: Time slot already reserved
- `400 Bad Request`: Invalid input
- `500 Internal Server Error`: Server-side errors

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

# Order Management Service

A Spring Boot-based order management service implementing Clean Architecture principles.

## Features

- Customer order history viewing
- Refund request submission with evidence
- Clean Architecture implementation
- Comprehensive error handling and logging
- SQLite database integration

## Technical Stack

- Java 21
- Spring Boot 3.2.2
- SQLite Database
- JUnit & Cucumber for testing
- Swagger UI for API documentation

## Project Structure

```
src/
├── main/
│   ├── java/com/ordermanagement/
│   │   ├── domain/          # Entities and business rules
│   │   │   ├── entity/     # Domain entities
│   │   │   └── repository/ # Repository interfaces
│   │   ├── service/        # Use cases implementation
│   │   └── rest/           # Controllers and API definitions
│   └── resources/
│       ├── application.yml # Application configuration
│       ├── schema.sql     # Database schema
│       └── data.sql       # Test data
└── test/
    └── java/
        └── features/      # BDD test scenarios
```

## Getting Started

### Prerequisites

- Java 21 JDK installed
- VS Code with Java extensions
- Git

### Running the Application

1. Clone the repository:
```bash
git clone https://github.com/ELAFIAYounes/delivery-service-kata.git
cd delivery-service-kata
```

2. Run the application using VS Code:
   - Open in VS Code
   - Use Spring Boot Dashboard
   - Click Run on `order-management-service`

The application will start on `http://localhost:8080`

### API Documentation

Access Swagger UI: `http://localhost:8080/swagger-ui.html`

### Available Endpoints

1. Get Customer Order History
```
GET /api/customers/{customerId}/orders
Response:
[
  {
    "id": 1,
    "customerId": "customer123",
    "orderDate": "2024-03-12T10:00:00",
    "status": "CONFIRMED",
    "items": [
      {
        "id": 1,
        "productId": "PROD-001",
        "productName": "Product 1",
        "quantity": 2,
        "price": 29.99
      }
    ]
  }
]
```

2. Submit Refund Request
```
POST /api/orders/items/{orderItemId}/refund
Request Body:
{
  "description": "Product damaged on arrival",
  "evidenceImageUrl": "https://example.com/evidence.jpg"
}
Response: 200 OK
```

## Testing

The project follows TDD/BDD practices with:
- Unit tests using JUnit
- Integration tests with Spring Boot Test
- BDD scenarios using Cucumber

Run tests using VS Code's test explorer or Maven:
```bash
./mvnw test
```

## Clean Architecture

This project strictly follows Clean Architecture principles:

1. **Domain Layer**
   - Contains business entities (`Order`, `OrderItem`, `RefundRequest`)
   - Independent of external frameworks
   - Core business logic and validation rules

2. **Service Layer**
   - Implements use cases (`OrderService`, `RefundService`)
   - Orchestrates domain entities
   - Business rules enforcement

3. **REST Layer**
   - HTTP endpoints in `OrderController`
   - Request/Response handling with DTOs
   - Input validation using Jakarta Validation

## Error Handling

- Comprehensive exception handling with `@ExceptionHandler`
- Proper HTTP status codes (200, 400, 404, 500)
- Detailed error messages in `ErrorResponse`
- Logging at appropriate levels (DEBUG, INFO, ERROR)

## Logging

- Structured logging using SLF4J
- Different log levels for development and production
- Log file rotation with daily rollover
- Performance logging for database operations
