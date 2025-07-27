# Ejada Banking System - Complete Project Guide

## 🎯 Project Overview

The Ejada Banking System is a comprehensive microservices-based banking platform built with modern cloud-native architecture. The system provides secure, scalable, and maintainable banking operations through a collection of specialized microservices.

### 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                           Client Layer                          │
├─────────────────────────────────────────────────────────────────┤
│                     React WebUI (Port 3000)                     │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                      API Gateway Layer                          │
├─────────────────────────────────────────────────────────────────┤
│                 WSO2 API Manager (9443/8280/8243)               │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                     Service Layer                               │
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
│                   Infrastructure Layer                          │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐              ┌────────────────────────────────┐│
│  │Apache Kafka │              │        MySQL Database          ││
│  │  (9092)     │              │         (3306)                 ││
│  │             │              │                                ││
│  │KRaft Mode   │              │ • users_service_db             ││
│  │No Zookeeper │              │ • logging_service_db           ││
│  │             │              │ • account_service_db           ││
│  │             │              │ • transaction_service_db       ││
│  └─────────────┘              └────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| Backend Services | Spring Boot | 3.5.3 | Microservices framework |
| Runtime | Java | 21 | Application runtime |
| Frontend | React | 19 | User interface |
| Routing | React Router | 7 | Frontend navigation |
| Database | MySQL | 8.0+ | Data persistence |
| Message Broker | Apache Kafka | Latest (KRaft) | Event streaming |
| API Gateway | WSO2 API Manager | 4.4.0 | API management |
| Containerization | Docker | Latest | Application packaging |
| Orchestration | Docker Compose | 3.8+ | Multi-container management |

## 🏢 Service Architecture

### Frontend Layer
- **React WebUI (Port 3000)**: Modern React-based banking interface with React Router 7 for navigation

### API Gateway Layer
- **WSO2 API Manager**: 
  - Management Console: Port 9443 (HTTPS)
  - HTTP Gateway: Port 8280
  - HTTPS Gateway: Port 8243
  - Features: API routing, authentication, rate limiting, analytics

### Microservices Layer

#### BFF Service (Backend for Frontend) - Port 8084
- **Purpose**: Orchestrates calls between frontend and microservices
- **Responsibilities**: Data aggregation, frontend-specific logic, session management

#### Users Service - Port 8080
- **Database**: users_service_db
- **Responsibilities**: User registration/authentication, profile management, access control

#### Accounts Service - Port 8082
- **Database**: account_service_db
- **Responsibilities**: Account creation/management, balance inquiries, account configurations

#### Transactions Service - Port 8083
- **Database**: transaction_service_db
- **Responsibilities**: Transaction processing, transfer operations, transaction history

#### Logging Service - Port 8081
- **Database**: logging_service_db
- **Responsibilities**: Centralized logging, audit trails, compliance reporting

### Infrastructure Layer

#### Apache Kafka
- **Port**: 9092 (external), 19092 (internal)
- **Mode**: KRaft (no Zookeeper dependency)
- **Purpose**: Event streaming, asynchronous communication, event sourcing

#### MySQL Database
- **Host**: Host machine (host.docker.internal)
- **Port**: 3306
- **Databases**: Separate database per service for data isolation

## 🚀 Quick Start Guide

### Prerequisites

1. **Java Development Kit (JDK) 21**
2. **Node.js 18+ and npm**
3. **Maven 3.6+**
4. **MySQL 8.0+** (running on localhost:3306)
5. **Docker and Docker Compose**
6. **At least 8GB RAM available for Docker**

### Database Setup

1. **Create required databases**:
```sql
CREATE DATABASE IF NOT EXISTS users_service_db;
CREATE DATABASE IF NOT EXISTS logging_service_db;
CREATE DATABASE IF NOT EXISTS account_service_db;
CREATE DATABASE IF NOT EXISTS transaction_service_db;
```

2. **Configure user permissions**:
```sql
-- Create dedicated user (recommended)
CREATE USER IF NOT EXISTS 'ejada_user'@'%' IDENTIFIED BY '12345678';
GRANT ALL PRIVILEGES ON users_service_db.* TO 'ejada_user'@'%';
GRANT ALL PRIVILEGES ON logging_service_db.* TO 'ejada_user'@'%';
GRANT ALL PRIVILEGES ON account_service_db.* TO 'ejada_user'@'%';
GRANT ALL PRIVILEGES ON transaction_service_db.* TO 'ejada_user'@'%';
FLUSH PRIVILEGES;
```

3. **Configure MySQL for Docker access**:
   - Set `bind-address = 0.0.0.0` in MySQL configuration
   - Restart MySQL service
   - Ensure port 3306 is accessible through firewall

### Docker Deployment

1. **Navigate to project directory**:
```bash
cd Ejada_project
```

2. **Start all services**:
```bash
cd config
docker-compose up -d
```

3. **Check service status**:
```bash
docker-compose ps
```

4. **View logs**:
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f users-service
```

## 🌐 Service Endpoints

### Application Services
- **WebUI**: http://localhost:3000
- **BFF Service**: http://localhost:8084
- **Users Service**: http://localhost:8080
- **Logging Service**: http://localhost:8081
- **Accounts Service**: http://localhost:8082
- **Transactions Service**: http://localhost:8083

### Infrastructure Services
- **WSO2 API Manager Console**: https://localhost:9443/carbon (admin/admin)
- **WSO2 API Gateway**: http://localhost:8280
- **Kafka**: localhost:9092
- **MySQL**: localhost:3306

## 💻 Development Setup

### Local Development (Individual Services)

1. **Start infrastructure services**:
```bash
cd config
docker-compose up -d kafka wso2-api-manager
```

2. **Run backend services**:
```bash
# Users Service
cd Users
./mvnw spring-boot:run

# Logging Service
cd Logging
./mvnw spring-boot:run

# Accounts Service
cd Accounts
./mvnw spring-boot:run

# Transactions Service
cd Transactions
./mvnw spring-boot:run

# BFF Service
cd BFF
./mvnw spring-boot:run
```

3. **Run frontend**:
```bash
cd WebUI/VBank
npm install
npm run dev
```

### Project Structure

```
Ejada_project/
├── config/                     # Configuration files
│   ├── docker-compose.yml     # Docker orchestration
│   └── .env                   # Master environment variables
├── scripts/                    # Management scripts
│   ├── docker-manage.sh       # Linux/macOS Docker management
│   ├── docker-manage.bat      # Windows Docker management
│   ├── generate-env.sh        # Linux/macOS env generation
│   └── generate-env.bat       # Windows env generation
├── docs/                       # Documentation
├── Users/                      # Users microservice
├── Logging/                    # Logging microservice
├── Accounts/                   # Accounts microservice
├── Transactions/               # Transactions microservice
├── BFF/                        # Backend for Frontend service
├── WebUI/VBank/               # React frontend application
├── SWAGGER_DOCUMENTATION.md   # API specifications
└── Databases_Creation.sql     # Database setup script
```

## 🔄 Communication Patterns

### Synchronous Communication
- **REST APIs**: Between BFF and microservices
- **HTTP calls**: For request-response patterns
- **Circuit breaker**: For fault tolerance

### Asynchronous Communication
- **Kafka Events**: For event-driven architecture
- **Message queues**: For decoupled processing
- **Event sourcing**: For audit and compliance

## 🔒 Security Architecture

### Authentication & Authorization
- **JWT tokens**: Stateless authentication
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

## 📊 Monitoring & Health Checks

All services include comprehensive health checks accessible via:
```bash
# Check specific service health
curl http://localhost:8080/actuator/health  # Users Service
curl http://localhost:8081/actuator/health  # Logging Service
curl http://localhost:8082/actuator/health  # Accounts Service
curl http://localhost:8083/actuator/health  # Transactions Service
curl http://localhost:8084/actuator/health  # BFF Service
```

## 🧪 Testing Guidelines

### Backend Testing
- **Unit Tests**: Service and repository layer testing with Mockito
- **Integration Tests**: Full service testing with TestContainers
- **API Tests**: REST endpoint testing with TestRestTemplate

### Frontend Testing
- **Component Tests**: React component testing with React Testing Library
- **Integration Tests**: Full user flow testing
- **E2E Tests**: Complete system testing

## 🔧 Development Best Practices

### Backend Development
1. **Follow RESTful conventions**
2. **Implement proper error handling with @ControllerAdvice**
3. **Use Spring profiles for environment-specific configuration**
4. **Implement comprehensive health checks**
5. **Follow single responsibility principle**

### Frontend Development
1. **Use TypeScript for type safety**
2. **Create reusable components**
3. **Implement proper API service layer**
4. **Use React.memo for performance optimization**
5. **Follow component composition patterns**

### Code Quality
- **Java**: Google Java Style Guide, CheckStyle
- **TypeScript/React**: Prettier formatting, ESLint
- **Git**: Conventional commit messages, feature branch workflow

## 🐳 Docker Configuration

### Environment Variables
Key customizable environment variables:
- `KAFKA_PORT`: Kafka external port (default: 9092)
- `WSO2_HTTPS_PORT`: WSO2 management console (default: 9443)
- `WSO2_HTTP_PORT`: WSO2 HTTP gateway (default: 8280)
- `WSO2_HTTPS_GATEWAY_PORT`: WSO2 HTTPS gateway (default: 8243)
- Service-specific ports for each microservice

### Volume Management
- **WSO2 Persistence**: API definitions, configurations, logs, database
- **Database**: No MySQL container - uses host MySQL instance
- **Logs**: Centralized logging through Logging service

## 🚨 Troubleshooting

### Common Issues

1. **Port Conflicts**:
```bash
# Find process using port (Windows)
netstat -ano | findstr :8080

# Find process using port (Linux/macOS)
lsof -i :8080
```

2. **Database Connection Issues**:
   - Verify MySQL is running and accessible
   - Check credentials in application.properties
   - Ensure databases exist
   - Verify firewall settings for port 3306

3. **Kafka Connection Issues**:
   - Verify Kafka container is running
   - Check bootstrap-servers configuration
   - Review Kafka logs for errors

4. **Docker Issues**:
   - Increase Docker memory allocation to 8GB+
   - Check Docker Desktop settings
   - Restart Docker service if needed

### Service Management Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# Restart specific service
docker-compose restart users-service

# Scale service (if needed)
docker-compose up -d --scale users-service=2

# Reset everything (WARNING: Deletes data)
docker-compose down -v
```

## 📈 Performance & Scalability

### Horizontal Scaling
- **Stateless services**: Easy horizontal scaling
- **Load balancing**: Via API gateway
- **Database sharding**: Future scaling capability

### Optimization Strategies
- **Caching**: In-memory and database-level caching
- **Connection pooling**: Optimized database connections
- **Async processing**: Kafka-based event handling
- **Resource limits**: Configured in Docker Compose

## 🔄 Data Flow Examples

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

## 🎯 Design Principles

1. **Single Responsibility**: Each service has a focused purpose
2. **Loose Coupling**: Services communicate via well-defined APIs
3. **High Cohesion**: Related functionality grouped within services
4. **Fault Tolerance**: Graceful failure handling
5. **Scalability**: Support for horizontal scaling
6. **Security**: Built-in security at every layer
7. **Observability**: Transparent system behavior monitoring

## 📚 Additional Resources

- **API Documentation**: See SWAGGER_DOCUMENTATION.md for detailed API specifications
- **Database Schema**: Check Databases_Creation.sql for complete database structure
- **Management Scripts**: Use scripts in `/scripts` directory for automation
- **Environment Configuration**: Customize settings in `/config/.env`

## 🚀 Deployment Considerations

### Production Deployment
1. **Configuration Management**: Environment-specific configurations
2. **Secret Management**: Secure credential handling
3. **Monitoring**: Comprehensive logging and metrics
4. **Scaling**: Horizontal scaling capabilities
5. **Security**: Production-grade security configurations
6. **Backup**: Database and configuration backup strategies

### Environment Management
- **Development**: Local development with hot reloading
- **Testing**: Automated testing environments
- **Staging**: Production-like environment for testing
- **Production**: Fully secured and monitored deployment

---

**Note**: This guide provides a comprehensive overview of the Ejada Banking System. For specific implementation details, refer to the individual service directories and configuration files. Always ensure proper security measures are in place before deploying to production environments.
