# Troubleshooting Guide

This guide covers common issues you might encounter while setting up and running the Ejada Banking System.

## 🚨 Common Issues and Solutions

### Docker and Container Issues

#### Issue: Docker daemon not running
**Symptoms:**
```bash
Cannot connect to the Docker daemon at unix:///var/run/docker.sock
```

**Solutions:**
```bash
# Windows
# Start Docker Desktop application

# Linux
sudo systemctl start docker
sudo systemctl enable docker

# macOS
# Start Docker Desktop application
```

#### Issue: Port already in use
**Symptoms:**
```bash
Error starting userland proxy: listen tcp 0.0.0.0:8080: bind: address already in use
```

**Solutions:**
```bash
# Find process using the port
# Linux/macOS
lsof -i :8080
sudo kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Or change the port in config/.env
USERS_SERVICE_PORT=8090
```

#### Issue: Docker Compose file not found
**Symptoms:**
```bash
Can't find a suitable configuration file in this directory or any parent
```

**Solutions:**
```bash
# Ensure you're in the correct directory
cd config
docker-compose up -d

# Or specify the file path
docker-compose -f ../config/docker-compose.yml up -d
```

#### Issue: Container keeps restarting
**Symptoms:**
```bash
docker-compose ps shows container status as "Restarting"
```

**Solutions:**
```bash
# Check container logs
docker-compose logs users-service

# Check health check status
docker inspect ejada-users-service | grep Health -A 10

# Common causes:
# 1. Database connection issues
# 2. Missing environment variables
# 3. Application startup errors
```

### Database Issues

#### Issue: MySQL connection refused
**Symptoms:**
```bash
java.sql.SQLException: Connection refused
```

**Solutions:**
```bash
# Check if MySQL is running
# Windows
sc query mysql80

# Linux
systemctl status mysql

# macOS
brew services list | grep mysql

# Verify MySQL is accessible
mysql -u root -p -h localhost

# Check if databases exist
SHOW DATABASES;

# Create missing databases
source Databases_Creation.sql
```

#### Issue: Access denied for user 'root'
**Symptoms:**
```bash
java.sql.SQLException: Access denied for user 'root'@'%'
```

**Solutions:**
```sql
-- Connect to MySQL as root
mysql -u root -p

-- Grant permissions
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;

-- For specific databases
GRANT ALL PRIVILEGES ON users_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON logging_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON account_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON transaction_service_db.* TO 'root'@'%';
FLUSH PRIVILEGES;
```

#### Issue: Database does not exist
**Symptoms:**
```bash
java.sql.SQLSyntaxErrorException: Unknown database 'users_service_db'
```

**Solutions:**
```sql
-- Create the missing databases
CREATE DATABASE IF NOT EXISTS users_service_db;
CREATE DATABASE IF NOT EXISTS logging_service_db;
CREATE DATABASE IF NOT EXISTS account_service_db;
CREATE DATABASE IF NOT EXISTS transaction_service_db;

-- Or run the setup script
source Databases_Creation.sql
```

### Kafka Issues

#### Issue: Kafka broker not available
**Symptoms:**
```bash
org.apache.kafka.common.errors.TimeoutException
```

**Solutions:**
```bash
# Check if Kafka container is running
docker-compose ps kafka

# Check Kafka logs
docker-compose logs kafka

# Restart Kafka service
docker-compose restart kafka

# Verify Kafka is accessible
docker-compose exec kafka kafka-topics --bootstrap-server localhost:19092 --list
```

#### Issue: Kafka health check failing
**Symptoms:**
```bash
Container health check failing for kafka
```

**Solutions:**
```bash
# Check Kafka container logs
docker-compose logs kafka

# Common issues:
# 1. Insufficient memory allocated to Docker
# 2. Kafka still starting up (wait 2-3 minutes)
# 3. Port conflicts

# Manual health check
docker-compose exec kafka bash
kafka-topics --bootstrap-server kafka:19092 --list
```

### Application Service Issues

#### Issue: Service health check failing
**Symptoms:**
```bash
Health check for users-service failing
```

**Solutions:**
```bash
# Check service logs
docker-compose logs users-service

# Check if service is responding
curl http://localhost:8080/actuator/health

# Common causes:
# 1. Database connection issues
# 2. Kafka connection issues
# 3. Missing environment variables
# 4. Application startup errors

# Check service dependencies
docker-compose ps mysql kafka
```

#### Issue: Java application won't start
**Symptoms:**
```bash
Spring Boot application failed to start
```

**Solutions:**
```bash
# Check logs for specific errors
docker-compose logs users-service

# Common issues and fixes:
# 1. Database connection
#    - Verify MySQL is running and accessible
#    - Check credentials in .env files

# 2. Port conflicts
#    - Change ports in config/.env
#    - Run scripts/generate-env.sh

# 3. Missing dependencies
#    - Rebuild Docker images: docker-compose build --no-cache

# 4. Memory issues
#    - Increase Docker memory allocation
```

#### Issue: Service can't connect to other services
**Symptoms:**
```bash
Connection refused when calling other services
```

**Solutions:**
```bash
# Check if target service is running
docker-compose ps

# Verify service discovery
docker-compose exec users-service ping accounts-service

# Check service URLs in environment variables
docker-compose exec users-service env | grep SERVICE_URL

# Regenerate environment files
cd scripts
./generate-env.sh
docker-compose restart
```

### Frontend Issues

#### Issue: React app won't start
**Symptoms:**
```bash
npm start fails or shows errors
```

**Solutions:**
```bash
cd WebUI/VBank

# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install

# Check for port conflicts
# Change port in .env file
PORT=3001

# Check Node.js version
node --version  # Should be 18+
```

#### Issue: API calls failing from frontend
**Symptoms:**
```bash
Network errors in browser console
```

**Solutions:**
```bash
# Check if BFF service is running
curl http://localhost:8084/actuator/health

# Verify API URLs in WebUI/.env
cat WebUI/VBank/.env | grep REACT_APP

# Check CORS configuration in BFF service
# Ensure proper CORS headers are set

# Test API manually
curl http://localhost:8084/api/v1/users
```

### WSO2 API Manager Issues

#### Issue: WSO2 API Manager won't start
**Symptoms:**
```bash
WSO2 container keeps restarting
```

**Solutions:**
```bash
# Check logs
docker-compose logs wso2-api-manager

# Increase memory allocation for Docker (minimum 8GB)
# Wait for complete startup (can take 3-5 minutes)

# Check if ports are available
netstat -an | grep 9443
netstat -an | grep 8280
```

#### Issue: Cannot access WSO2 Management Console
**Symptoms:**
```bash
Browser shows connection refused on https://localhost:9443
```

**Solutions:**
```bash
# Check if container is running
docker-compose ps wso2-api-manager

# Wait for complete startup
docker-compose logs -f wso2-api-manager

# Try accessing after health check passes
# Default credentials: admin/admin

# Check firewall settings
# Ensure ports 9443, 8280, 8243 are not blocked
```

### Network and Connectivity Issues

#### Issue: host.docker.internal not resolving
**Symptoms:**
```bash
Name resolution failure for host.docker.internal
```

**Solutions:**
```bash
# Windows/macOS Docker Desktop
# Ensure Docker Desktop is updated

# Linux alternative
# Add to docker-compose.yml:
extra_hosts:
  - "host.docker.internal:host-gateway"

# Or use actual host IP
ip route show default | awk '/default/ {print $3}'
```

#### Issue: Services can't communicate
**Symptoms:**
```bash
Connection refused between containers
```

**Solutions:**
```bash
# Check if all services are on same network
docker network ls
docker network inspect ejada-network

# Verify service names match docker-compose.yml
docker-compose ps

# Test connectivity
docker-compose exec users-service ping mysql
docker-compose exec users-service ping kafka
```

## 🔧 Diagnostic Commands

### System Health Check
```bash
# Run comprehensive health check
cd scripts
./docker-manage.sh health

# Check all service status
./docker-manage.sh status

# View all logs
./docker-manage.sh logs
```

### Manual Service Verification
```bash
# Test database connectivity
mysql -h localhost -u root -p -e "SHOW DATABASES;"

# Test Kafka
docker-compose exec kafka kafka-topics --bootstrap-server kafka:19092 --list

# Test service endpoints
curl http://localhost:8080/actuator/health  # Users
curl http://localhost:8081/actuator/health  # Logging
curl http://localhost:8082/actuator/health  # Accounts
curl http://localhost:8083/actuator/health  # Transactions
curl http://localhost:8084/actuator/health  # BFF

# Test WebUI
curl http://localhost:3000
```

### Container Inspection
```bash
# Inspect container details
docker inspect ejada-users-service

# Check container resources
docker stats

# Execute commands in container
docker-compose exec users-service bash
```

## 🔄 Recovery Procedures

### Complete System Reset
```bash
# Stop everything
cd scripts
./docker-manage.sh stop

# Clean up containers and volumes
./docker-manage.sh cleanup

# Regenerate configuration
./generate-env.sh

# Start fresh
./docker-manage.sh start
```

### Individual Service Reset
```bash
cd config

# Stop specific service
docker-compose stop users-service

# Remove container
docker-compose rm users-service

# Rebuild and restart
docker-compose build users-service
docker-compose up -d users-service
```

### Database Reset
```bash
# Backup important data first!
mysqldump -u root -p users_service_db > backup.sql

# Drop and recreate databases
mysql -u root -p -e "DROP DATABASE users_service_db;"
mysql -u root -p < Databases_Creation.sql

# Restart services to recreate tables
docker-compose restart users-service
```

## 📊 Monitoring and Logs

### Log Locations
```bash
# Docker logs
docker-compose logs [service-name]

# Application logs (inside containers)
docker-compose exec users-service tail -f /var/log/application.log

# MySQL logs
docker-compose exec mysql tail -f /var/log/mysql/error.log
```

### Performance Monitoring
```bash
# Resource usage
docker stats

# Service metrics
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/prometheus

# Database performance
mysql -u root -p -e "SHOW PROCESSLIST;"
mysql -u root -p -e "SHOW ENGINE INNODB STATUS;"
```

## 🆘 Getting Help

### Log Collection for Support
```bash
# Collect all logs
mkdir support-logs
docker-compose logs > support-logs/docker-compose.log
docker-compose ps > support-logs/service-status.txt
docker stats --no-stream > support-logs/resource-usage.txt

# System information
docker version > support-logs/docker-version.txt
docker-compose version > support-logs/compose-version.txt
```

### Useful Commands Summary
```bash
# Quick diagnostics
docker-compose ps                    # Service status
docker-compose logs [service]        # Service logs
docker network ls                    # Network list
docker volume ls                     # Volume list
docker system df                     # Disk usage
docker system prune                  # Clean unused resources

# Service management
docker-compose up -d [service]       # Start service
docker-compose restart [service]     # Restart service
docker-compose stop [service]        # Stop service
docker-compose rm [service]          # Remove service container
```

## 🔍 Debug Mode

### Enable Debug Logging
```bash
# For Spring Boot services, add to .env:
LOGGING_LEVEL_COM_EJADA=DEBUG
SPRING_JPA_SHOW_SQL=true

# For detailed startup logs:
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=DEBUG
```

### Verbose Docker Compose
```bash
# Run with verbose output
docker-compose --verbose up -d

# Build with detailed output
docker-compose build --progress=plain --no-cache
```

Remember to check the specific error messages in logs as they often provide the exact cause and solution for issues.
