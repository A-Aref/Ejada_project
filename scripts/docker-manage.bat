@echo off
setlocal enabledelayedexpansion

REM Ejada Banking System Docker Management Script for Windows

set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Function to print colored output
:print_status
echo %GREEN%[INFO]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

REM Function to check if Docker is running
:check_docker
docker info >nul 2>&1
if %errorlevel% neq 0 (
    call :print_error "Docker is not running. Please start Docker and try again."
    exit /b 1
)
goto :eof

REM Function to start all services
:start_all
call :print_status "Starting all Ejada Banking System services..."
call :check_docker

cd /d "%~dp0..\config"
docker-compose up -d

call :print_status "Services are starting up. This may take a few minutes..."
call :print_status "You can monitor the progress with: docker-compose logs -f"

echo.
echo %BLUE%Service URLs:%NC%
echo   WebUI:                 http://localhost:3000
echo   BFF Service:           http://localhost:8084
echo   Users Service:         http://localhost:8080
echo   Logging Service:       http://localhost:8081
echo   Accounts Service:      http://localhost:8082
echo   Transactions Service:  http://localhost:8083
echo   WSO2 API Manager:      https://localhost:9443/carbon (admin/admin)
echo   WSO2 Gateway:          http://localhost:8280
goto :eof

REM Function to stop all services
:stop_all
call :print_status "Stopping all services..."
cd /d "%~dp0..\config"
docker-compose down
call :print_status "All services stopped."
goto :eof

REM Function to restart all services
:restart_all
call :print_status "Restarting all services..."
cd /d "%~dp0..\config"
docker-compose restart
call :print_status "All services restarted."
goto :eof

REM Function to show service status
:status
call :print_status "Service status:"
cd /d "%~dp0..\config"
docker-compose ps
goto :eof

REM Function to show logs
:logs
if "%~1"=="" (
    call :print_status "Showing logs for all services..."
    docker-compose logs -f
) else (
    call :print_status "Showing logs for %~1..."
    docker-compose logs -f %~1
)
goto :eof

REM Function to build all images
:build
call :print_status "Building all Docker images..."
docker-compose build --no-cache
call :print_status "Build completed."
goto :eof

REM Function to clean up everything
:cleanup
call :print_warning "This will remove all containers, volumes, and images. Are you sure? (y/N)"
set /p response=
if /i "!response!"=="y" (
    call :print_status "Cleaning up..."
    docker-compose down -v --rmi all
    call :print_status "Cleanup completed."
) else (
    call :print_status "Cleanup cancelled."
)
goto :eof

REM Function to run health checks
:health_check
call :print_status "Running health checks..."

set services=users-service logging-service accounts-service transactions-service bff-service
for %%s in (%services%) do (
    docker-compose ps | findstr "%%s.*Up" >nul
    if !errorlevel! equ 0 (
        set port=
        if "%%s"=="users-service" set port=8080
        if "%%s"=="logging-service" set port=8081
        if "%%s"=="accounts-service" set port=8082
        if "%%s"=="transactions-service" set port=8083
        if "%%s"=="bff-service" set port=8084
        
        curl -f -s http://localhost:!port!/actuator/health >nul 2>&1
        if !errorlevel! equ 0 (
            echo   %GREEN%✓%NC% %%s is healthy
        ) else (
            echo   %RED%✗%NC% %%s is not responding
        )
    ) else (
        echo   %RED%✗%NC% %%s is not running
    )
)
goto :eof

REM Function to show help
:show_help
echo Ejada Banking System Docker Management Script
echo.
echo Usage: %~nx0 [COMMAND]
echo.
echo Commands:
echo   start         Start all services
echo   stop          Stop all services
echo   restart       Restart all services
echo   status        Show service status
echo   logs [SERVICE] Show logs (optionally for specific service)
echo   build         Build all Docker images
echo   health        Run health checks on all services
echo   cleanup       Remove all containers, volumes, and images
echo   help          Show this help message
echo.
echo Examples:
echo   %~nx0 start                    # Start all services
echo   %~nx0 logs users-service       # Show logs for users service
echo   %~nx0 status                   # Check service status
goto :eof

REM Main script logic
if "%~1"=="start" (
    call :start_all
) else if "%~1"=="stop" (
    call :stop_all
) else if "%~1"=="restart" (
    call :restart_all
) else if "%~1"=="status" (
    call :status
) else if "%~1"=="logs" (
    call :logs "%~2"
) else if "%~1"=="build" (
    call :build
) else if "%~1"=="health" (
    call :health_check
) else if "%~1"=="cleanup" (
    call :cleanup
) else if "%~1"=="help" (
    call :show_help
) else if "%~1"=="-h" (
    call :show_help
) else if "%~1"=="--help" (
    call :show_help
) else if "%~1"=="" (
    call :show_help
) else (
    call :print_error "Unknown command: %~1"
    call :show_help
    exit /b 1
)
