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

# Get the script's directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
CONFIG_ENV="$PROJECT_ROOT/config/.env"

# Check if master .env exists
if [ ! -f "$CONFIG_ENV" ]; then
    echo -e "${YELLOW}Warning: Master .env file not found at: $CONFIG_ENV${NC}"
    echo "Current directory: $(pwd)"
    echo "Script directory: $SCRIPT_DIR"
    exit 1
fi

# Source the master .env file
source "$CONFIG_ENV"

# Function to create Users service .env
create_users_env() {
    echo -e "${GREEN}Creating Users/.env...${NC}"
    cat > "$PROJECT_ROOT/Users/.env" << EOF
# Users Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${USERS_SERVICE_PORT}

# Database Configuration
DATABASE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${USERS_DB_NAME}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${USERS_DB_PASSWORD}

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
EOF
}

# Function to create Logging service .env
create_logging_env() {
    echo -e "${GREEN}Creating Logging/.env...${NC}"
    cat > "$PROJECT_ROOT/Logging/.env" << EOF
# Logging Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${LOGGING_SERVICE_PORT}

# Database Configuration
DATABASE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${LOGGING_DB_NAME}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${LOGGING_DB_PASSWORD}

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
EOF
}

# Function to create Accounts service .env
create_accounts_env() {
    echo -e "${GREEN}Creating Accounts/.env...${NC}"
    cat > "$PROJECT_ROOT/Accounts/.env" << EOF
# Accounts Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${ACCOUNTS_SERVICE_PORT}

# Database Configuration
DATABASE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${ACCOUNTS_DB_NAME}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${ACCOUNTS_DB_PASSWORD}

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
# Microservice URLs for Accounts service
TRANSACTION_SERVICE_URL=${TRANSACTIONS_SERVICE_URL}
EOF
}

# Function to create Transactions service .env
create_transactions_env() {
    echo -e "${GREEN}Creating Transactions/.env...${NC}"
    cat > "$PROJECT_ROOT/Transactions/.env" << EOF
# Transactions Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${TRANSACTIONS_SERVICE_PORT}

# Database Configuration
DATABASE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${TRANSACTIONS_DB_NAME}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${TRANSACTIONS_DB_PASSWORD}

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
# Microservice URLs for Transactions service
ACCOUNT_SERVICE_URL=${ACCOUNTS_SERVICE_URL}
EOF
}

# Function to create BFF service .env
create_bff_env() {
    echo -e "${GREEN}Creating BFF/.env...${NC}"
    cat > "$PROJECT_ROOT/BFF/.env" << EOF
# BFF Service Environment Configuration
# Generated from master .env file

# Server Configuration
SERVER_PORT=${BFF_SERVICE_PORT}

# Database Configuration (using transactions DB)
DATABASE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${TRANSACTIONS_DB_NAME}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${BFF_DB_PASSWORD}

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}

# Microservice URLs for BFF to communicate with
USER_SERVICE_URL=${USERS_SERVICE_URL}
ACCOUNT_SERVICE_URL=${ACCOUNTS_SERVICE_URL}
TRANSACTION_SERVICE_URL=${TRANSACTIONS_SERVICE_URL}
EOF
}

# Create all .env files
create_users_env
create_logging_env
create_accounts_env
create_transactions_env
create_bff_env

echo -e "${BLUE}✅ All .env files generated successfully!${NC}"
echo ""
echo -e "${YELLOW}Generated files:${NC}"
echo "  - Users/.env"
echo "  - Logging/.env"
echo "  - Accounts/.env"
echo "  - Transactions/.env"
echo "  - BFF/.env"
echo ""
echo -e "${BLUE}Note: These files are automatically used by Docker Compose.${NC}"
