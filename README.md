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
