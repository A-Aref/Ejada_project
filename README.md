# Ejada Banking System

A modern microservices-based banking system built with Spring Boot, React, Apache Kafka, and MySQL. This system provides comprehensive banking functionality including user management, account operations, transaction processing, and audit logging.

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Layer                            │
│                    React WebUI (3000)                          │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                   API Gateway Layer                            │
│              WSO2 API Manager (9443/8280/8243)                 │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                   Microservices Layer                          │
│ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│ │   BFF   │ │  Users  │ │Accounts │ │ Trans.  │ │Logging  │   │
│ │ (8084)  │ │ (8080)  │ │ (8082)  │ │ (8083)  │ │ (8081)  │   │
│ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘   │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────────────┐
│                Infrastructure Layer                            │
│  ┌─────────────────┐              ┌─────────────────────────┐   │
│  │ Apache Kafka    │              │    MySQL Database      │   │
│  │    (9092)       │              │       (3306)           │   │
│  │  KRaft Mode     │              │   Host Machine         │   │
│  └─────────────────┘              └─────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
Ejada_project/
├── 📁 config/                     # 🔧 Configuration & Orchestration
│   ├── docker-compose.yml         # Docker services orchestration
│   └── .env                       # Master environment configuration
├── 📁 scripts/                    # 🛠️ Management & Utility Scripts
│   ├── docker-manage.sh           # Linux/macOS Docker management
│   ├── docker-manage.bat          # Windows Docker management
│   ├── generate-env.sh            # Linux/macOS env generation
│   └── generate-env.bat           # Windows env generation
├── 📁 docs/                       # 📚 Documentation
│   ├── README.md                  # Documentation overview
│   ├── DOCKER_README.md           # Docker setup guide
│   ├── HOST_MYSQL_SETUP.md        # Database setup guide
│   ├── ARCHITECTURE.md            # System architecture
│   ├── DEVELOPMENT.md             # Development guide
│   └── TROUBLESHOOTING.md         # Troubleshooting guide
├── 📁 Users/                      # 👤 Users Microservice
├── 📁 Logging/                    # 📝 Logging Microservice
├── 📁 Accounts/                   # 🏦 Accounts Microservice
├── 📁 Transactions/               # 💰 Transactions Microservice
├── 📁 BFF/                        # 🔀 Backend for Frontend
├── 📁 WebUI/VBank/                # 🌐 React Frontend
├── 📄 API_Documentation.md        # API specifications
└── 📄 Databases_Creation.sql      # Database setup script
```

## 🚀 Quick Start

### Prerequisites
- **Docker & Docker Compose** - For containerization
- **MySQL 8.0+** - Running on host machine (port 3306)
- **Java 21** - For local development (optional)
- **Node.js 18+** - For frontend development (optional)

### 1. Setup Host MySQL Database
```bash
# Create required databases
mysql -u root -p < Databases_Creation.sql

# Or follow the detailed guide
see docs/HOST_MYSQL_SETUP.md
```

### 2. Configure Environment
```bash
# Edit master configuration
nano config/.env

# Generate service-specific configurations
cd scripts
./generate-env.sh          # Linux/macOS
# or
generate-env.bat           # Windows
```

### 3. Start the System
```bash
# Start all services
cd scripts
./docker-manage.sh start   # Linux/macOS
# or
docker-manage.bat start    # Windows

# Check service status
./docker-manage.sh status
```

### 4. Access the System
- **🌐 WebUI**: http://localhost:3000
- **🔧 API Gateway**: http://localhost:8280
- **📊 WSO2 Console**: https://localhost:9443/carbon (admin/admin)
- **🏥 Health Checks**: http://localhost:808{0-4}/actuator/health

## 🛠️ Management Commands

### Using Management Scripts
```bash
cd scripts

# System management
./docker-manage.sh start       # Start all services
./docker-manage.sh stop        # Stop all services
./docker-manage.sh restart     # Restart all services
./docker-manage.sh status      # Show service status
./docker-manage.sh health      # Run health checks
./docker-manage.sh cleanup     # Clean up everything

# Logs and monitoring
./docker-manage.sh logs                    # All service logs
./docker-manage.sh logs users-service      # Specific service logs

# Configuration management
./generate-env.sh              # Regenerate all .env files
```

### Direct Docker Compose
```bash
cd config

# Basic operations
docker-compose up -d           # Start all services
docker-compose ps              # Show status
docker-compose logs [service]  # View logs
docker-compose down            # Stop all services

# Individual service management
docker-compose up -d users-service    # Start specific service
docker-compose restart bff-service    # Restart specific service
```

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [📖 Documentation Overview](docs/README.md) | Complete documentation index |
| [🐳 Docker Setup Guide](docs/DOCKER_README.md) | Docker Compose setup and usage |
| [🗄️ MySQL Setup Guide](docs/HOST_MYSQL_SETUP.md) | Host MySQL configuration |
| [🏗️ Architecture Guide](docs/ARCHITECTURE.md) | System design and components |
| [💻 Development Guide](docs/DEVELOPMENT.md) | Local development setup |
| [🔧 Troubleshooting](docs/TROUBLESHOOTING.md) | Common issues and solutions |

## 🎯 Services Overview

### Core Microservices
| Service | Port | Purpose | Database |
|---------|------|---------|----------|
| **Users** | 8080 | User management & authentication | users_service_db |
| **Logging** | 8081 | Centralized logging & audit | logging_service_db |
| **Accounts** | 8082 | Account operations & management | account_service_db |
| **Transactions** | 8083 | Transaction processing & history | transaction_service_db |
| **BFF** | 8084 | Backend for Frontend orchestration | transaction_service_db |

### Infrastructure Components
| Component | Port(s) | Purpose |
|-----------|---------|---------|
| **WebUI** | 3000 | React frontend application |
| **Kafka** | 9092 | Message broker (KRaft mode) |
| **MySQL** | 3306 | Database (host machine) |
| **WSO2 API Manager** | 9443/8280/8243 | API gateway & management |

## 🔧 Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Database**: MySQL 8.0
- **Message Broker**: Apache Kafka (KRaft mode)
- **Containerization**: Docker & Docker Compose

### Frontend
- **Framework**: React 19
- **Routing**: React Router 7
- **Language**: TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS

### Infrastructure
- **API Gateway**: WSO2 API Manager 4.2.0
- **Orchestration**: Docker Compose
- **Development**: Hot reload & live debugging

## 🔐 Security Features

- **Authentication**: JWT-based authentication
- **Authorization**: Role-based access control
- **API Security**: WSO2 API Manager policies
- **Data Security**: Encrypted database connections
- **Network Security**: Docker container isolation

## 📊 Monitoring & Observability

- **Health Checks**: All services expose `/actuator/health`
- **Metrics**: Spring Boot Actuator metrics
- **Logging**: Centralized logging via Logging service
- **Tracing**: Request tracing across services

## 🚀 Development

### Local Development Setup
```bash
# See development guide for detailed instructions
docs/DEVELOPMENT.md

# Quick setup for backend development
cd Users && ./mvnw spring-boot:run

# Quick setup for frontend development
cd WebUI/VBank && npm run dev
```

### Environment Management
```bash
# Master configuration
config/.env

# Service-specific configurations (auto-generated)
Users/.env
Logging/.env
Accounts/.env
Transactions/.env
BFF/.env
WebUI/VBank/.env
```

## 🐛 Troubleshooting

Common issues and solutions are documented in [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md).

Quick diagnostic commands:
```bash
# Check service health
cd scripts && ./docker-manage.sh health

# View service logs
./docker-manage.sh logs [service-name]

# Complete system reset
./docker-manage.sh cleanup
./generate-env.sh
./docker-manage.sh start
```

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Make your changes** following the development guidelines
4. **Run tests**: Ensure all tests pass
5. **Commit changes**: `git commit -m 'Add amazing feature'`
6. **Push to branch**: `git push origin feature/amazing-feature`
7. **Open a Pull Request**

See [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) for detailed development guidelines.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

- **Documentation**: Check the `docs/` folder for comprehensive guides
- **Issues**: Create an issue for bugs or feature requests
- **Troubleshooting**: See [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)

## 🚀 Future Enhancements

- **Kubernetes deployment** configurations
- **Monitoring stack** (Prometheus, Grafana)
- **CI/CD pipelines** (GitHub Actions)
- **API versioning** strategy
- **Performance optimization** and caching
- **Security enhancements** (OAuth2, RBAC)

---

**Built with ❤️ by the Ejada Development Team**
