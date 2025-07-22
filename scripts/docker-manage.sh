#!/bin/bash

# Ejada Banking System Docker Management Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get the script's directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
CONFIG_ENV="$PROJECT_ROOT/config/.env"

# Load environment variables from master .env file
if [ -f "$CONFIG_ENV" ]; then
    source "$CONFIG_ENV"
else
    echo -e "${RED}[ERROR]${NC} Master .env file not found at: $CONFIG_ENV"
    exit 1
fi

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to check available memory
check_memory() {
    if command -v docker &> /dev/null; then
        MEMORY=$(docker system info --format '{{.MemTotal}}' 2>/dev/null || echo "0")
        if [ "$MEMORY" -lt 8000000000 ]; then
            print_warning "Docker has less than 8GB of memory allocated. Consider increasing memory allocation for better performance."
        fi
    fi
}

# Function to start all services
start_all() {
    print_status "Starting all Ejada Banking System services..."
    check_docker
    check_memory
    
    cd "$PROJECT_ROOT/config" && docker-compose up -d
    
    print_status "Services are starting up. This may take a few minutes..."
    print_status "You can monitor the progress with: docker-compose logs -f"
    
    echo ""
    echo -e "${BLUE}Service URLs:${NC}"
    echo "  BFF Service:           http://localhost:${BFF_SERVICE_PORT}"
    echo "  Users Service:         http://localhost:${USERS_SERVICE_PORT}"
    echo "  Logging Service:       http://localhost:${LOGGING_SERVICE_PORT}"
    echo "  Accounts Service:      http://localhost:${ACCOUNTS_SERVICE_PORT}"
    echo "  Transactions Service:  http://localhost:${TRANSACTIONS_SERVICE_PORT}"
    echo "  WSO2 API Manager:      https://localhost:${WSO2_HTTPS_PORT}/carbon (admin/admin)"
    echo "  WSO2 Gateway:          http://localhost:${WSO2_HTTP_PORT}"
}

# Function to stop all services
stop_all() {
    print_status "Stopping all services..."
    cd "$PROJECT_ROOT/config" && docker-compose down
    print_status "All services stopped."
}

# Function to restart all services
restart_all() {
    print_status "Restarting all services..."
    cd "$PROJECT_ROOT/config" && docker-compose restart
    print_status "All services restarted."
}

# Function to show service status
status() {
    print_status "Service status:"
    cd "$PROJECT_ROOT/config" && docker-compose ps
}

# Function to show logs
logs() {
    if [ -z "$1" ]; then
        print_status "Showing logs for all services..."
        cd "$PROJECT_ROOT/config" && docker-compose logs -f
    else
        print_status "Showing logs for $1..."
        cd "$PROJECT_ROOT/config" && docker-compose logs -f "$1"
    fi
}

# Function to build all images
build() {
    print_status "Building all Docker images..."
    cd "$PROJECT_ROOT/config" && docker-compose build --no-cache
    print_status "Build completed."
}

# Function to clean up everything
cleanup() {
    print_warning "This will remove all containers, volumes, and images. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_status "Cleaning up..."
        cd "$PROJECT_ROOT/config" && docker-compose down -v --rmi all
        print_status "Cleanup completed."
    else
        print_status "Cleanup cancelled."
    fi
}

# Function to run health checks
health_check() {
    print_status "Running health checks..."
    
    services=("users-service" "logging-service" "accounts-service" "transactions-service" "bff-service")
    
    cd "$PROJECT_ROOT/config"
    for service in "${services[@]}"; do
        if docker-compose ps | grep -q "$service.*Up"; then
            port=""
            case $service in
                "users-service") port="$USERS_SERVICE_PORT" ;;
                "logging-service") port="$LOGGING_SERVICE_PORT" ;;
                "accounts-service") port="$ACCOUNTS_SERVICE_PORT" ;;
                "transactions-service") port="$TRANSACTIONS_SERVICE_PORT" ;;
                "bff-service") port="$BFF_SERVICE_PORT" ;;
            esac
            
            if curl -f -s http://localhost:$port/actuator/health > /dev/null; then
                echo -e "  ${GREEN}✓${NC} $service is healthy"
            else
                echo -e "  ${RED}✗${NC} $service is not responding"
            fi
        else
            echo -e "  ${RED}✗${NC} $service is not running"
        fi
    done
}

# Function to show help
show_help() {
    echo "Ejada Banking System Docker Management Script"
    echo ""
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start         Start all services"
    echo "  stop          Stop all services"
    echo "  restart       Restart all services"
    echo "  status        Show service status"
    echo "  logs [SERVICE] Show logs (optionally for specific service)"
    echo "  build         Build all Docker images"
    echo "  health        Run health checks on all services"
    echo "  cleanup       Remove all containers, volumes, and images"
    echo "  help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start                    # Start all services"
    echo "  $0 logs users-service       # Show logs for users service"
    echo "  $0 status                   # Check service status"
}

# Main script logic
case "${1:-}" in
    start)
        start_all
        ;;
    stop)
        stop_all
        ;;
    restart)
        restart_all
        ;;
    status)
        status
        ;;
    logs)
        logs "$2"
        ;;
    build)
        build
        ;;
    health)
        health_check
        ;;
    cleanup)
        cleanup
        ;;
    help|--help|-h)
        show_help
        ;;
    "")
        show_help
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
