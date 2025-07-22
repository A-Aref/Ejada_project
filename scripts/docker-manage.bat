@echo off
setlocal enabledelayedexpansion

REM Ejada Banking System Docker Management Script for Windows

set "RED=[91m"
set "GREEN=[92m"
set "YELLOW=[93m"
set "BLUE=[94m"
set "NC=[0m"

REM Get the script's directory and project root
set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%.."
set "CONFIG_ENV=%PROJECT_ROOT%\config\.env"

REM Load environment variables from master .env file
if not exist "%CONFIG_ENV%" (
    echo %RED%[ERROR]%NC% Master .env file not found at: %CONFIG_ENV%
    exit /b 1
)

REM Read master .env file and set variables
for /f "usebackq tokens=1,2 delims==" %%A in ("%CONFIG_ENV%") do (
    if not "%%A"=="" if not "%%A:~0,1%"=="#" (
        set "%%A=%%B"
    )
)

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

cd /d "%PROJECT_ROOT%\config"
docker-compose up -d

call :print_status "Services are starting up. This may take a few minutes..."
call :print_status "You can monitor the progress with: docker-compose logs -f"

echo.
echo %BLUE%Service URLs:%NC%
echo   BFF Service:           http://localhost:!BFF_SERVICE_PORT!
echo   Users Service:         http://localhost:!USERS_SERVICE_PORT!
echo   Logging Service:       http://localhost:!LOGGING_SERVICE_PORT!
echo   Accounts Service:      http://localhost:!ACCOUNTS_SERVICE_PORT!
echo   Transactions Service:  http://localhost:!TRANSACTIONS_SERVICE_PORT!
echo   WSO2 API Manager:      https://localhost:!WSO2_HTTPS_PORT!/carbon (admin/admin)
echo   WSO2 Gateway:          http://localhost:!WSO2_HTTP_PORT!
goto :eof

REM Function to stop all services
:stop_all
call :print_status "Stopping all services..."
cd /d "%PROJECT_ROOT%\config"
docker-compose down
call :print_status "All services stopped."
goto :eof

REM Function to restart all services
:restart_all
call :print_status "Restarting all services..."
cd /d "%PROJECT_ROOT%\config"
docker-compose restart
call :print_status "All services restarted."
goto :eof

REM Function to show service status
:status
call :print_status "Service status:"
cd /d "%PROJECT_ROOT%\config"
docker-compose ps
goto :eof

REM Function to show logs
:logs
cd /d "%PROJECT_ROOT%\config"
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
cd /d "%PROJECT_ROOT%\config"
docker-compose build --no-cache
call :print_status "Build completed."
goto :eof

REM Function to clean up everything
:cleanup
call :print_warning "This will remove all containers, volumes, and images. Are you sure? (y/N)"
set /p response=
if /i "!response!"=="y" (
    call :print_status "Cleaning up..."
    cd /d "%PROJECT_ROOT%\config"
    docker-compose down -v --rmi all
    call :print_status "Cleanup completed."
) else (
    call :print_status "Cleanup cancelled."
)
goto :eof

REM Function to run health checks
:health_check
call :print_status "Running health checks..."

cd /d "%PROJECT_ROOT%\config"
set services=users-service logging-service accounts-service transactions-service bff-service
for %%s in (%services%) do (
    docker-compose ps | findstr "%%s.*Up" >nul
    if !errorlevel! equ 0 (
        set port=
        if "%%s"=="users-service" set port=!USERS_SERVICE_PORT!
        if "%%s"=="logging-service" set port=!LOGGING_SERVICE_PORT!
        if "%%s"=="accounts-service" set port=!ACCOUNTS_SERVICE_PORT!
        if "%%s"=="transactions-service" set port=!TRANSACTIONS_SERVICE_PORT!
        if "%%s"=="bff-service" set port=!BFF_SERVICE_PORT!
        
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
