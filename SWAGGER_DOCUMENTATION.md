# API Documentation with OpenAPI/Swagger UI

This document provides information about accessing the API documentation for all microservices in the Ejada Banking System.

## Overview

All microservices in this project are now equipped with comprehensive API documentation using OpenAPI 3.0 and Swagger UI. Each service provides interactive documentation that allows you to:

- View all available endpoints
- See request/response schemas with examples
- Test API endpoints directly from the browser
- Download OpenAPI specifications as YAML files
- View OpenAPI specifications directly in the browser

## Downloading OpenAPI Specifications

Each microservice now provides endpoints to download or view their OpenAPI specification:

- **Download**: Use the `/api-spec/download` endpoint to download the YAML file
- **View**: Use the `/api-spec/view` endpoint to view the YAML content in your browser
- **JSON Format**: Use the standard `/api-docs` endpoint for JSON format

The downloaded YAML files can be imported into:
- Postman for API testing
- Insomnia for API testing
- OpenAPI generators for client SDK generation
- Other API documentation tools

## Service Endpoints

### 1. Users Service
- **Port**: 8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **Download YAML**: http://localhost:8080/api-spec/download
- **View YAML**: http://localhost:8080/api-spec/view
- **Description**: User authentication and profile management

#### Available Endpoints:
- `POST /users/register` - Register a new user
- `POST /users/login` - User authentication
- `GET /users/{userId}/profile` - Get user profile information
- `GET /api-spec/download` - Download OpenAPI specification as YAML file
- `GET /api-spec/view` - View OpenAPI specification in browser

### 2. Logging Service
- **Port**: 8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/api-docs
- **Download YAML**: http://localhost:8081/api-spec/download
- **View YAML**: http://localhost:8081/api-spec/view
- **Description**: System logging and audit trail

#### Available Endpoints:
- `GET /logs/` - Retrieve all system logs
- `GET /api-spec/download` - Download OpenAPI specification as YAML file
- `GET /api-spec/view` - View OpenAPI specification in browser

### 3. Accounts Service
- **Port**: 8082
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/api-docs
- **Download YAML**: http://localhost:8082/api-spec/download
- **View YAML**: http://localhost:8082/api-spec/view
- **Description**: Bank account management

#### Available Endpoints:
- `POST /accounts/` - Create a new account
- `GET /accounts/{accountId}` - Get account details
- `GET /accounts/users/{userId}` - Get all accounts for a user
- `PUT /accounts/transfer` - Transfer money between accounts
- `GET /api-spec/download` - Download OpenAPI specification as YAML file
- `GET /api-spec/view` - View OpenAPI specification in browser

### 4. Transactions Service
- **Port**: 8083
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8083/api-docs
- **Download YAML**: http://localhost:8083/api-spec/download
- **View YAML**: http://localhost:8083/api-spec/view
- **Description**: Transaction processing and history

#### Available Endpoints:
- `GET /transactions/accounts/{accountId}` - Get transactions for an account
- `POST /transactions/transfer/initiation` - Initiate a transfer transaction
- `POST /transactions/transfer/execution` - Execute a transfer transaction
- `GET /transactions/accounts/{accountId}/getLatest` - Get latest transaction for an account
- `GET /api-spec/download` - Download OpenAPI specification as YAML file
- `GET /api-spec/view` - View OpenAPI specification in browser

### 5. BFF (Backend for Frontend)
- **Port**: 8084
- **Swagger UI**: http://localhost:8084/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8084/api-docs
- **Download YAML**: http://localhost:8084/api-spec/download
- **View YAML**: http://localhost:8084/api-spec/view
- **Description**: Aggregated API for frontend applications

#### Available Endpoints:
- `GET /bff/dashboard/{userId}` - Get user dashboard with aggregated data
- `GET /api-spec/download` - Download OpenAPI specification as YAML file
- `GET /api-spec/view` - View OpenAPI specification in browser

## Using the Swagger UI

1. **Start the services**: Make sure all microservices are running
2. **Access Swagger UI**: Navigate to the respective Swagger UI URL for each service
3. **Explore endpoints**: Click on any endpoint to see detailed information
4. **Try it out**: Use the "Try it out" button to test endpoints directly
5. **View examples**: Each endpoint includes request/response examples

## Request/Response Examples

Each endpoint in the Swagger UI includes comprehensive examples showing:
- Expected request body structure
- Required and optional parameters
- Possible response formats for different HTTP status codes
- Error response formats

## API Features Documented

- **Authentication**: Login and registration endpoints
- **Account Management**: Account creation, retrieval, and balance operations
- **Transaction Processing**: Two-phase transaction initiation and execution
- **Data Aggregation**: Dashboard endpoint combining multiple service data
- **System Monitoring**: Centralized logging and audit capabilities

## Configuration

The OpenAPI documentation is configured with:
- Custom titles and descriptions for each service
- Server information for local development
- Organized endpoint grouping by functionality
- Comprehensive error handling documentation
- Try-it-out functionality enabled

## Development Notes

- All endpoints include proper HTTP status codes documentation
- Request parameters are validated and documented
- Response schemas include example values
- Error responses are consistently formatted across all services
- OpenAPI 3.0 specification is used for maximum compatibility

Access any service's Swagger UI to explore the complete API documentation and test the endpoints interactively.
