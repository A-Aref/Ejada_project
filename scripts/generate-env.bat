@echo off
setlocal enabledelayedexpansion

REM Script to generate individual .env files from master .env
REM This script reads the master .env file and creates service-specific .env files

echo Generating individual .env files from master .env...

REM Get the script's directory
set "SCRIPT_DIR=%~dp0"
set "PROJECT_ROOT=%SCRIPT_DIR%.."
set "CONFIG_ENV=%PROJECT_ROOT%\config\.env"

REM Check if master .env exists
if not exist "%CONFIG_ENV%" (
    echo Warning: Master .env file not found at: %CONFIG_ENV%
    echo Current directory: %CD%
    echo Script directory: %SCRIPT_DIR%
    pause
    exit /b 1
)

REM Read master .env file and set variables
for /f "usebackq tokens=1,2 delims==" %%A in ("%CONFIG_ENV%") do (
    if not "%%A"=="" if not "%%A:~0,1%"=="#" (
        set "%%A=%%B"
    )
)

echo Creating Users/.env...
(
echo # Users Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!USERS_SERVICE_PORT!
echo.
echo # Database Configuration
echo DATABASE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!USERS_DB_NAME!
echo DATABASE_USERNAME=root
echo DATABASE_PASSWORD=!USERS_DB_PASSWORD!
echo.
echo # Kafka Configuration
echo KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
) > "%PROJECT_ROOT%\Users\.env"

echo Creating Logging/.env...
(
echo # Logging Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!LOGGING_SERVICE_PORT!
echo.
echo # Database Configuration
echo DATABASE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!LOGGING_DB_NAME!
echo DATABASE_USERNAME=root
echo DATABASE_PASSWORD=!LOGGING_DB_PASSWORD!
echo.
echo # Kafka Configuration
echo KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
) > "%PROJECT_ROOT%\Logging\.env"

echo Creating Accounts/.env...
(
echo # Accounts Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!ACCOUNTS_SERVICE_PORT!
echo.
echo # Database Configuration
echo DATABASE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!ACCOUNTS_DB_NAME!
echo DATABASE_USERNAME=root
echo DATABASE_PASSWORD=!ACCOUNTS_DB_PASSWORD!
echo.
echo # Kafka Configuration
echo KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
) > "%PROJECT_ROOT%\Accounts\.env"

echo Creating Transactions/.env...
(
echo # Transactions Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!TRANSACTIONS_SERVICE_PORT!
echo.
echo # Database Configuration
echo DATABASE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!TRANSACTIONS_DB_NAME!
echo DATABASE_USERNAME=root
echo DATABASE_PASSWORD=!TRANSACTIONS_DB_PASSWORD!
echo.
echo # Kafka Configuration
echo KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
) > "%PROJECT_ROOT%\Transactions\.env"

echo Creating BFF/.env...
(
echo # BFF Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!BFF_SERVICE_PORT!
echo.
echo # Database Configuration (using transactions DB^)
echo DATABASE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!TRANSACTIONS_DB_NAME!
echo DATABASE_USERNAME=root
echo DATABASE_PASSWORD=!BFF_DB_PASSWORD!
echo.
echo # Kafka Configuration
echo KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
echo.
echo # Microservice URLs for BFF to communicate with
echo USER_SERVICE_URL=!USERS_SERVICE_URL!
echo ACCOUNT_SERVICE_URL=!ACCOUNTS_SERVICE_URL!
echo TRANSACTION_SERVICE_URL=!TRANSACTIONS_SERVICE_URL!
) > "%PROJECT_ROOT%\BFF\.env"

echo Creating WebUI/VBank/.env...
(
echo # WebUI Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo PORT=!WEBUI_PORT!
echo.
echo # API Endpoints
echo REACT_APP_BFF_BASE_URL=http://localhost:!BFF_SERVICE_PORT!
) > "%PROJECT_ROOT%\WebUI\VBank\.env"

echo.
echo ✅ All .env files generated successfully!
echo.
echo Generated files:
echo   - Users/.env
echo   - Logging/.env
echo   - Accounts/.env
echo   - Transactions/.env
echo   - BFF/.env
echo   - WebUI/VBank/.env
echo.
echo Note: These files are automatically used by Docker Compose.
