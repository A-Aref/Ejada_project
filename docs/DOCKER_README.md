# Ejada Banking System - Docker Compose Setup

This Docker Compose configuration sets up the complete Ejada banking system with all microservices, infrastructure components, and the web UI.

## Architecture Overview

The system consists of the following components:

### Infrastructure Services
- **Apache Kafka** - Message broker for microservices communication
- **MySQL Database** - Shared database for all microservices
- **WSO2 API Manager** - API gateway and management

### Microservices
- **Users Service** (Port 8080) - User management and authentication
- **Logging Service** (Port 8081) - Centralized logging and audit trails
- **Accounts Service** (Port 8082) - Account management and operations
- **Transactions Service** (Port 8083) - Transaction processing and history
- **BFF Service** (Port 8084) - Backend for Frontend, orchestrates calls to other services

### Frontend
- **WebUI** (Port 3000) - React-based web application

## Prerequisites

- Docker and Docker Compose installed
- At least 8GB RAM available for Docker
- Ports 3000, 8080-8084, 8243, 8280, 9092, 9443, and 3306 available

## Quick Start

1. **Clone the repository and navigate to the project directory:**
   ```bash
   cd Ejada_project
   ```

2. **Start all services:**
   ```bash
   docker-compose up -d
   ```

3. **Check service status:**
   ```bash
   docker-compose ps
   ```

4. **View logs for a specific service:**
   ```bash
   docker-compose logs -f [service-name]
   # Example: docker-compose logs -f users-service
   ```

## Service Endpoints

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
- **MySQL**: localhost:3306 (root/12345678)

## Health Checks

All services include health checks. You can monitor their status using:

```bash
# Check all services
docker-compose ps

# Check specific service health
docker-compose exec users-service curl http://localhost:8080/actuator/health
```

## Database Setup

The MySQL container will automatically create the required databases using the `Databases_Creation.sql` script. The following databases will be created:
- `users_service_db`
- `logging_service_db`
- `account_service_db`
- `transaction_service_db`

## Kafka Topics

Kafka topics will be automatically created by the services as needed. Common topics include:
- User events
- Account operations
- Transaction processing
- Audit logs

## WSO2 API Manager Configuration

1. Access the management console at https://localhost:9443/carbon
2. Default credentials: admin/admin
3. Configure APIs to proxy requests to your microservices
4. Set up security policies and rate limiting as needed

## Development Mode

For development, you can run services individually:

```bash
# Start only infrastructure services
docker-compose up -d zookeeper kafka mysql wso2-api-manager

# Start a specific microservice
docker-compose up -d users-service
```

## Monitoring and Debugging

### View real-time logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f users-service
```

### Access service containers
```bash
# Access a running container
docker-compose exec users-service /bin/bash
```

### Check network connectivity
```bash
# Test connectivity between services
docker-compose exec users-service ping mysql
docker-compose exec users-service ping kafka
```

## Stopping the System

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: This will delete all data)
docker-compose down -v
```

## Scaling Services

You can scale specific services if needed:

```bash
# Scale a service to multiple instances
docker-compose up -d --scale users-service=2
```

## Troubleshooting

### Common Issues

1. **Port Conflicts**: Ensure all required ports are available
2. **Memory Issues**: Increase Docker memory allocation to at least 8GB
3. **Startup Order**: Services have health checks and dependencies configured

### Checking Service Dependencies
```bash
# Check if MySQL is ready
docker-compose exec mysql mysqladmin ping -h localhost -u root -p12345678

# Check if Kafka is ready
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### Reset Everything
```bash
# Stop all services and remove volumes
docker-compose down -v

# Remove all images (optional)
docker-compose down --rmi all

# Rebuild and start
docker-compose build --no-cache
docker-compose up -d
```

## Environment Variables

Key environment variables that can be customized:

- `MYSQL_ROOT_PASSWORD`: MySQL root password (default: 12345678)
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka connection string
- `SPRING_PROFILES_ACTIVE`: Spring Boot profile (set to 'docker')

## Security Notes

- Default passwords are used for demonstration purposes
- In production, use proper secrets management
- Configure proper network security and firewalls
- Use HTTPS for all external endpoints
- Regularly update base images for security patches

## Performance Tuning

For production deployments:

1. Adjust JVM memory settings in Dockerfiles
2. Configure Kafka partitions and replication factors
3. Optimize MySQL configuration
4. Use proper resource limits in docker-compose.yml
5. Consider using Docker Swarm or Kubernetes for orchestration
