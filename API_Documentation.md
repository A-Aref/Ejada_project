# Banking System API Documentation

This document provides comprehensive information about the Swagger/OpenAPI documentation for all microservices in the banking system.

## Microservices Overview

The banking system consists of the following microservices:

1. **Users Service** (Port 8080) - User management and authentication
2. **Accounts Service** (Port 8081) - Bank account management
3. **Transactions Service** (Port 8082) - Financial transactions processing
4. **Logging Service** (Port 8083) - System logging and audit trails
5. **BFF Service** (Port 8084) - Backend for Frontend aggregation layer

## Swagger UI Access

After starting each microservice, you can access the Swagger UI documentation at:

- **Users Service**: http://localhost:8080/swagger-ui.html
- **Accounts Service**: http://localhost:8082/swagger-ui.html
- **Transactions Service**: http://localhost:8083/swagger-ui.html
- **Logging Service**: http://localhost:8081/swagger-ui.html
- **BFF Service**: http://localhost:8084/swagger-ui.html

## OpenAPI Specifications

Each microservice includes a comprehensive OpenAPI 3.0.3 specification (YAML format) located at:

- `Users/src/main/resources/openapi.yml`
- `Accounts/src/main/resources/openapi.yml`
- `Transactions/src/main/resources/openapi.yml`
- `Logging/src/main/resources/openapi.yml`
- `BFF/src/main/resources/openapi.yml`

## API Endpoints Summary

### Users Service API (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users/register` | Register a new user |
| POST | `/users/login` | User authentication |
| GET | `/users/{userId}/profile` | Get user profile |

**Key Features:**
- User registration with validation
- Login authentication
- Profile management
- Input validation for all fields

### Accounts Service API (Port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/accounts` | Create a new bank account |
| GET | `/accounts/{accountId}` | Get account details |
| GET | `/accounts/users/{userId}` | Get all accounts for a user |
| PUT | `/accounts/transfer` | Transfer money between accounts |

**Key Features:**
- Account creation with different types (SAVINGS, CHECKING, BUSINESS)
- Balance management
- Inter-account transfers
- Account status tracking

### Transactions Service API (Port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/transactions/accounts/{accountId}` | Get account transactions |
| GET | `/transactions/accounts/{accountId}/getLatest` | Get latest transaction |
| POST | `/transactions/transfer/initiation` | Initiate a transfer |
| POST | `/transactions/transfer/execution` | Execute a pending transfer |

**Key Features:**
- Two-phase transaction processing (initiate → execute)
- Transaction history retrieval
- Transaction status tracking
- Account validation before transfers

### Logging Service API (Port 8083)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/logs` | Retrieve all system logs |
| GET | `/logs/{id}` | Get specific log entry |

**Key Features:**
- Audit trail management
- Request/Response logging
- Filtering by message type
- Pagination support

### BFF Service API (Port 8084)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/bff/dashboard/{userId}` | Get comprehensive user dashboard |

**Key Features:**
- Aggregated data from multiple services
- Complete user financial overview
- Recent transactions summary
- Account balance totals

## Data Models

### Common Data Types

- **UUID**: Used for all entity identifiers
- **Timestamps**: ISO 8601 format (e.g., "2025-01-21T10:30:00Z")
- **Money**: Double precision for financial amounts
- **Enums**: Defined for account types, transaction status, etc.

### Account Types
- `SAVINGS` - Savings account
- `CHECKING` - Checking account  
- `BUSINESS` - Business account

### Transaction Status
- `PENDING` - Transaction initiated but not executed
- `SUCCESS` - Transaction completed successfully
- `FAILED` - Transaction failed
- `CANCELLED` - Transaction cancelled

### Account Status
- `ACTIVE` - Account is active
- `INACTIVE` - Account is temporarily inactive
- `SUSPENDED` - Account is suspended
- `CLOSED` - Account is closed

## Authentication & Security

Currently, the APIs are designed for development and do not include authentication mechanisms. In a production environment, consider implementing:

- JWT tokens for API authentication
- Role-based access control (RBAC)
- Rate limiting
- Input sanitization and validation
- HTTPS encryption

## Error Handling

All APIs follow consistent error response formats:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error description"
}
```

Common HTTP status codes used:
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `404` - Not Found
- `409` - Conflict
- `500` - Internal Server Error

## Request/Response Examples

### User Registration
```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Account Creation
```bash
curl -X POST http://localhost:8081/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "accountType": "SAVINGS",
    "initialBalance": 1000.00
  }'
```

### Transfer Initiation
```bash
curl -X POST http://localhost:8082/transactions/transfer/initiation \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountId": "660e8400-e29b-41d4-a716-446655440001",
    "toAccountId": "660e8400-e29b-41d4-a716-446655440002",
    "amount": 500.00,
    "description": "Monthly transfer"
  }'
```

## Building and Running

To build and run the services with Swagger documentation:

1. Navigate to each service directory
2. Run Maven build: `mvn clean install`
3. Start the application: `mvn spring-boot:run`
4. Access Swagger UI at the respective port

## Development Dependencies

Each microservice includes the following OpenAPI dependency:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

This dependency provides:
- Automatic OpenAPI specification generation
- Swagger UI integration
- Interactive API documentation
- Schema validation

## Kafka Integration

All services are integrated with Kafka for event-driven communication:
- Request/Response logging for audit trails
- Asynchronous messaging between services
- Event sourcing for transaction history

## Database Schema

The system uses MySQL databases with JPA/Hibernate for data persistence. Each service maintains its own database following the microservices pattern.

## Testing with Swagger UI

1. Open the Swagger UI for any service
2. Expand the desired endpoint
3. Click "Try it out"
4. Fill in the required parameters
5. Click "Execute" to test the API
6. Review the response and status code

The interactive documentation makes it easy to understand and test all API endpoints without writing separate test scripts.

## Production Considerations

When deploying to production:

1. Update server URLs in OpenAPI configurations
2. Implement proper authentication and authorization
3. Add rate limiting and security headers
4. Configure CORS policies appropriately
5. Set up monitoring and logging
6. Use environment-specific configurations
7. Implement circuit breakers for service resilience

This comprehensive API documentation enables developers to understand, test, and integrate with all banking system microservices efficiently.
