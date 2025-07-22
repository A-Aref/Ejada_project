# System Architecture

## Overview

The Ejada Banking System follows a microservices architecture pattern, providing scalability, maintainability, and fault tolerance. The system is designed with clear separation of concerns and follows modern cloud-native principles.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                           Client Layer                          │
├─────────────────────────────────────────────────────────────────┤
│                     React WebUI (Port 3000)                    │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                      API Gateway Layer                         │
├─────────────────────────────────────────────────────────────────┤
│                 WSO2 API Manager (9443/8280/8243)              │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                     Service Layer                              │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌──────────┐│
│  │BFF Service  │  │Users Service│  │Accounts Svc │  │Trans Svc ││
│  │  (8084)     │  │   (8080)    │  │   (8082)    │  │ (8083)   ││
│  └─────────────┘  └─────────────┘  └─────────────┘  └──────────┘│
│  ┌─────────────┐                                                │
│  │Logging Svc  │                                                │
│  │  (8081)     │                                                │
│  └─────────────┘                                                │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                   Infrastructure Layer                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐              ┌─────────────────────────────────┐│
│  │Apache Kafka │              │        MySQL Database          ││
│  │  (9092)     │              │         (3306)                 ││
│  │             │              │                                 ││
│  │KRaft Mode   │              │ • users_service_db              ││
│  │No Zookeeper │              │ • logging_service_db            ││
│  │             │              │ • account_service_db            ││
│  │             │              │ • transaction_service_db        ││
│  └─────────────┘              └─────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

## Service Details

### Frontend Layer

#### React WebUI (Port 3000)
- **Technology**: React 19 with React Router 7
- **Purpose**: User interface for banking operations
- **Features**:
  - Account management interface
  - Transaction history views
  - User authentication flows
  - Real-time balance updates

### API Gateway Layer

#### WSO2 API Manager
- **Management Console**: Port 9443 (HTTPS)
- **HTTP Gateway**: Port 8280
- **HTTPS Gateway**: Port 8243
- **Features**:
  - API routing and load balancing
  - Authentication and authorization
  - Rate limiting and throttling
  - API analytics and monitoring
  - Security policies enforcement

### Service Layer

#### BFF Service (Backend for Frontend) - Port 8084
- **Technology**: Spring Boot 3.5.3, Java 21
- **Purpose**: Orchestrates calls between frontend and microservices
- **Responsibilities**:
  - Aggregates data from multiple services
  - Handles frontend-specific logic
  - Provides simplified APIs for UI
  - Manages session state

#### Users Service - Port 8080
- **Technology**: Spring Boot 3.5.3, Java 21
- **Database**: users_service_db
- **Responsibilities**:
  - User registration and authentication
  - Profile management
  - User preferences
  - Access control

#### Accounts Service - Port 8082
- **Technology**: Spring Boot 3.5.3, Java 21
- **Database**: account_service_db
- **Responsibilities**:
  - Account creation and management
  - Balance inquiries
  - Account status management
  - Account types and configurations

#### Transactions Service - Port 8083
- **Technology**: Spring Boot 3.5.3, Java 21
- **Database**: transaction_service_db
- **Responsibilities**:
  - Transaction processing
  - Transfer operations
  - Transaction history
  - Transaction validation

#### Logging Service - Port 8081
- **Technology**: Spring Boot 3.5.3, Java 21
- **Database**: logging_service_db
- **Responsibilities**:
  - Centralized logging
  - Audit trail management
  - Event sourcing
  - Compliance reporting

### Infrastructure Layer

#### Apache Kafka
- **Version**: Latest with KRaft mode
- **Port**: 9092 (external), 19092 (internal)
- **Features**:
  - Event streaming between services
  - Asynchronous communication
  - Event sourcing support
  - No Zookeeper dependency (KRaft mode)

#### MySQL Database
- **Version**: 8.0
- **Host**: Host machine (host.docker.internal)
- **Port**: 3306
- **Databases**:
  - `users_service_db`
  - `logging_service_db`
  - `account_service_db`
  - `transaction_service_db`

## Communication Patterns

### Synchronous Communication
- **REST APIs**: Between BFF and microservices
- **HTTP calls**: For request-response patterns
- **Circuit breaker**: For fault tolerance

### Asynchronous Communication
- **Kafka Events**: For event-driven architecture
- **Message queues**: For decoupled processing
- **Event sourcing**: For audit and compliance

## Security Architecture

### Authentication & Authorization
- **JWT tokens**: For stateless authentication
- **OAuth 2.0**: Via WSO2 API Manager
- **Role-based access**: Service-level permissions

### Data Security
- **Database encryption**: At rest and in transit
- **API security**: TLS/SSL for all communications
- **Secret management**: Environment-based configuration

### Network Security
- **Docker networking**: Isolated service communication
- **Firewall rules**: Restricted port access
- **HTTPS enforcement**: For external communications

## Scalability & Performance

### Horizontal Scaling
- **Stateless services**: Easy to scale horizontally
- **Load balancing**: Via API gateway
- **Database sharding**: Potential for future scaling

### Caching Strategy
- **In-memory caching**: At service level
- **Database caching**: Query result caching
- **CDN**: For static assets (WebUI)

### Monitoring & Observability
- **Health checks**: All services expose health endpoints
- **Metrics collection**: Via Spring Actuator
- **Distributed tracing**: For request tracking
- **Centralized logging**: Via Logging service

## Deployment Architecture

### Containerization
- **Docker containers**: Each service in its own container
- **Docker Compose**: Local development orchestration
- **Multi-stage builds**: Optimized container images

### Environment Management
- **Environment variables**: Configuration management
- **Service discovery**: Via Docker networking
- **Configuration externalization**: Via .env files

## Data Flow Examples

### User Registration Flow
1. WebUI → BFF Service → Users Service
2. Users Service → Kafka (UserCreated event)
3. Logging Service ← Kafka (logs the event)
4. Response flows back through the chain

### Transaction Processing Flow
1. WebUI → BFF Service → Transactions Service
2. Transactions Service → Accounts Service (balance check)
3. Transactions Service → Kafka (TransactionProcessed event)
4. Logging Service ← Kafka (audit logging)
5. Response with transaction status

## Technology Stack Summary

| Component | Technology | Version |
|-----------|------------|---------|
| Backend Services | Spring Boot | 3.5.3 |
| Runtime | Java | 21 |
| Frontend | React | 19 |
| Routing | React Router | 7 |
| Database | MySQL | 8.0 |
| Message Broker | Apache Kafka | Latest (KRaft) |
| API Gateway | WSO2 API Manager | 4.2.0 |
| Containerization | Docker | Latest |
| Orchestration | Docker Compose | 3.8+ |

## Design Principles

1. **Single Responsibility**: Each service has a clear, focused purpose
2. **Loose Coupling**: Services are independent and communicate via well-defined APIs
3. **High Cohesion**: Related functionality is grouped within services
4. **Fault Tolerance**: Services handle failures gracefully
5. **Scalability**: Architecture supports horizontal scaling
6. **Security**: Security is built-in at every layer
7. **Observability**: System behavior is transparent and monitorable
