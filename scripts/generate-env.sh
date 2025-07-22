#!/bin/bash

# Script to generate individual .env files from master .env
# This script reads the master .env file and creates service-specific .env files

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Generating individual .env files from master .env...${NC}"

# Check if master .env exists
if [ ! -f "../config/.env" ]; then
    echo -e "${YELLOW}Warning: Master .env file not found in config folder!${NC}"
    exit 1
fi

# Source the master .env file
source ../config/.env

# Function to create Users service .env
create_users_env() {
    echo -e "${GREEN}Creating Users/.env...${NC}"
    cat > ../Users/.env << EOF
# Users Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${USERS_SERVICE_PORT}
SPRING_APPLICATION_NAME=users

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${USERS_DB_NAME}
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=${USERS_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false

# External Service URLs
LOGGING_SERVICE_URL=${LOGGING_SERVICE_URL}
ACCOUNTS_SERVICE_URL=${ACCOUNTS_SERVICE_URL}
TRANSACTIONS_SERVICE_URL=${TRANSACTIONS_SERVICE_URL}
BFF_SERVICE_URL=${BFF_SERVICE_URL}

# Actuator Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always

# Spring Profile
SPRING_PROFILES_ACTIVE=docker
EOF
}

# Function to create Logging service .env
create_logging_env() {
    echo -e "${GREEN}Creating Logging/.env...${NC}"
    cat > ../Logging/.env << EOF
# Logging Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${LOGGING_SERVICE_PORT}
SPRING_APPLICATION_NAME=logging

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${LOGGING_DB_NAME}
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=${LOGGING_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Kafka Configuration (Consumer)
SPRING_KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER=org.apache.kafka.common.serialization.StringDeserializer
SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER=org.springframework.kafka.support.serializer.JsonDeserializer
SPRING_KAFKA_CONSUMER_PROPERTIES_SPRING_JSON_TRUSTED_PACKAGES=*
SPRING_KAFKA_CONSUMER_PROPERTIES_SPRING_JSON_USE_TYPE_HEADERS=false
SPRING_KAFKA_CONSUMER_PROPERTIES_SPRING_JSON_VALUE_DEFAULT_TYPE=java.util.HashMap
SPRING_KAFKA_CONSUMER_GROUP_ID=logging-group

# External Service URLs
USERS_SERVICE_URL=${USERS_SERVICE_URL}
ACCOUNTS_SERVICE_URL=${ACCOUNTS_SERVICE_URL}
TRANSACTIONS_SERVICE_URL=${TRANSACTIONS_SERVICE_URL}
BFF_SERVICE_URL=${BFF_SERVICE_URL}

# Actuator Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always

# Spring Profile
SPRING_PROFILES_ACTIVE=docker
EOF
}

# Function to create Accounts service .env
create_accounts_env() {
    echo -e "${GREEN}Creating Accounts/.env...${NC}"
    cat > ../Accounts/.env << EOF
# Accounts Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${ACCOUNTS_SERVICE_PORT}
SPRING_APPLICATION_NAME=accounts

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${ACCOUNTS_DB_NAME}
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=${ACCOUNTS_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false

# External Service URLs
USERS_SERVICE_URL=${USERS_SERVICE_URL}
LOGGING_SERVICE_URL=${LOGGING_SERVICE_URL}
TRANSACTIONS_SERVICE_URL=${TRANSACTIONS_SERVICE_URL}
BFF_SERVICE_URL=${BFF_SERVICE_URL}

# Actuator Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always

# Spring Profile
SPRING_PROFILES_ACTIVE=docker
EOF
}

# Function to create Transactions service .env
create_transactions_env() {
    echo -e "${GREEN}Creating Transactions/.env...${NC}"
    cat > ../Transactions/.env << EOF
# Transactions Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${TRANSACTIONS_SERVICE_PORT}
SPRING_APPLICATION_NAME=transactions

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${TRANSACTIONS_DB_NAME}
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=${TRANSACTIONS_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false

# External Service URLs
USERS_SERVICE_URL=${USERS_SERVICE_URL}
LOGGING_SERVICE_URL=${LOGGING_SERVICE_URL}
ACCOUNTS_SERVICE_URL=${ACCOUNTS_SERVICE_URL}
BFF_SERVICE_URL=${BFF_SERVICE_URL}

# Actuator Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always

# Spring Profile
SPRING_PROFILES_ACTIVE=docker
EOF
}

# Function to create BFF service .env
create_bff_env() {
    echo -e "${GREEN}Creating BFF/.env...${NC}"
    cat > ../BFF/.env << EOF
# BFF Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${BFF_SERVICE_PORT}
SPRING_APPLICATION_NAME=bff

# Database Configuration (using transactions DB)
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${TRANSACTIONS_DB_NAME}
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=${BFF_DB_PASSWORD}
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false

# Microservice URLs for BFF to communicate with
SERVICES_USER_BASE_URL=${USERS_SERVICE_URL}
SERVICES_ACCOUNT_BASE_URL=${ACCOUNTS_SERVICE_URL}
SERVICES_TRANSACTION_BASE_URL=${TRANSACTIONS_SERVICE_URL}
SERVICES_LOGGING_BASE_URL=${LOGGING_SERVICE_URL}

# Actuator Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always

# Spring Profile
SPRING_PROFILES_ACTIVE=docker
EOF
}

# Function to create WebUI .env
create_webui_env() {
    echo -e "${GREEN}Creating WebUI/VBank/.env...${NC}"
    cat > ../WebUI/VBank/.env << EOF
# WebUI Environment Configuration
# Generated from master .env file

# Server Configuration
PORT=${WEBUI_PORT}
NODE_ENV=production

# API Endpoints
REACT_APP_BFF_BASE_URL=http://localhost:${BFF_SERVICE_PORT}
REACT_APP_API_MANAGER_URL=http://localhost:${WSO2_HTTP_PORT}

# Service URLs (for direct access if needed)
REACT_APP_USERS_SERVICE_URL=http://localhost:${USERS_SERVICE_PORT}
REACT_APP_LOGGING_SERVICE_URL=http://localhost:${LOGGING_SERVICE_PORT}
REACT_APP_ACCOUNTS_SERVICE_URL=http://localhost:${ACCOUNTS_SERVICE_PORT}
REACT_APP_TRANSACTIONS_SERVICE_URL=http://localhost:${TRANSACTIONS_SERVICE_PORT}

# WSO2 API Manager URLs
REACT_APP_WSO2_GATEWAY_URL=http://localhost:${WSO2_HTTP_PORT}
REACT_APP_WSO2_HTTPS_GATEWAY_URL=https://localhost:${WSO2_HTTPS_GATEWAY_PORT}
REACT_APP_WSO2_CONSOLE_URL=https://localhost:${WSO2_HTTPS_PORT}

# Build Configuration
GENERATE_SOURCEMAP=false
BUILD_PATH=./build
EOF
}

# Create all .env files
create_users_env
create_logging_env
create_accounts_env
create_transactions_env
create_bff_env
create_webui_env

echo -e "${BLUE}✅ All .env files generated successfully!${NC}"
echo ""
echo -e "${YELLOW}Generated files:${NC}"
echo "  - Users/.env"
echo "  - Logging/.env"
echo "  - Accounts/.env"
echo "  - Transactions/.env"
echo "  - BFF/.env"
echo "  - WebUI/VBank/.env"
echo ""
echo -e "${BLUE}Note: These files are automatically used by Docker Compose.${NC}"
