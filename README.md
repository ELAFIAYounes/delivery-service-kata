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
