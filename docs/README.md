# Ejada Banking System Documentation

Welcome to the Ejada Banking System documentation. This directory contains all the documentation for setting up, configuring, and running the banking system.

## 📁 Documentation Structure

```
docs/
├── README.md                    # This file - Overview of documentation
├── DOCKER_README.md            # Docker setup and usage guide
├── HOST_MYSQL_SETUP.md         # Host MySQL configuration guide
├── ARCHITECTURE.md             # System architecture documentation
├── API_DOCUMENTATION.md        # API endpoints and usage
├── DEVELOPMENT.md              # Development setup and guidelines
└── TROUBLESHOOTING.md          # Common issues and solutions
```

## 🚀 Quick Start

1. **Prerequisites Setup**: Start with [HOST_MYSQL_SETUP.md](HOST_MYSQL_SETUP.md) to configure your MySQL database
2. **Docker Setup**: Follow [DOCKER_README.md](DOCKER_README.md) for container orchestration
3. **Architecture Overview**: Read [ARCHITECTURE.md](ARCHITECTURE.md) to understand system design
4. **API Reference**: Check [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for endpoint details
5. **Development**: See [DEVELOPMENT.md](DEVELOPMENT.md) for development guidelines

## 🏗️ System Overview

The Ejada Banking System is a microservices-based architecture consisting of:

### Core Services
- **Users Service** (Port 8080) - User management and authentication
- **Logging Service** (Port 8081) - Centralized logging and audit trails
- **Accounts Service** (Port 8082) - Account management and operations
- **Transactions Service** (Port 8083) - Transaction processing and history
- **BFF Service** (Port 8084) - Backend for Frontend orchestration

### Infrastructure
- **Apache Kafka** - Message broker for service communication
- **MySQL Database** - Persistent data storage
- **WSO2 API Manager** - API gateway and management
- **React WebUI** - Frontend application

## 📖 Documentation Index

### Setup Guides
- [Docker Compose Setup](DOCKER_README.md) - Complete Docker environment setup
- [Host MySQL Configuration](HOST_MYSQL_SETUP.md) - Database setup on host machine

### Architecture & Design
- [System Architecture](ARCHITECTURE.md) - Overall system design and component interaction
- [API Documentation](API_DOCUMENTATION.md) - REST API endpoints and schemas

### Development
- [Development Guide](DEVELOPMENT.md) - Local development setup and best practices
- [Troubleshooting](TROUBLESHOOTING.md) - Common issues and their solutions

## 🔗 Related Resources

- **Config Folder**: `../config/` - Contains Docker Compose and environment configurations
- **Scripts Folder**: `../scripts/` - Contains management and utility scripts
- **Source Code**: Individual service directories (`../Users/`, `../Accounts/`, etc.)

## 📝 Contributing

When adding new documentation:

1. Follow the existing structure and naming conventions
2. Include clear examples and code snippets
3. Update this index when adding new documents
4. Use consistent markdown formatting

## 🆘 Getting Help

If you encounter issues:

1. Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common problems
2. Review service logs: `../scripts/docker-manage.sh logs [service-name]`
3. Verify configuration in `../config/` folder
4. Check service health: `../scripts/docker-manage.sh health`

## 📋 Version Information

- **Docker Compose**: v3.8+
- **Java**: 21
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Apache Kafka**: Latest with KRaft mode
- **WSO2 API Manager**: 4.2.0

---

**Note**: Always ensure you have the latest version of Docker and Docker Compose installed before proceeding with the setup.
