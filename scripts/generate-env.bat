@echo off
setlocal enabledelayedexpansion

REM Script to generate individual .env files from master .env
REM This script reads the master .env file and creates service-specific .env files

echo Generating individual .env files from master .env...

REM Check if master .env exists
if not exist "..\config\.env" (
    echo Warning: Master .env file not found in config folder!
    exit /b 1
)

REM Read master .env file and set variables
for /f "usebackq tokens=1,2 delims==" %%A in ("..\config\.env") do (
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
echo SPRING_APPLICATION_NAME=users
echo.
echo # Database Configuration
echo SPRING_DATASOURCE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!USERS_DB_NAME!
echo SPRING_DATASOURCE_USERNAME=root
echo SPRING_DATASOURCE_PASSWORD=!USERS_DB_PASSWORD!
echo SPRING_JPA_HIBERNATE_DDL_AUTO=update
echo SPRING_JPA_SHOW_SQL=true
echo SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
echo.
echo # Kafka Configuration
echo SPRING_KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
echo SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
echo SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
echo SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false
echo.
echo # External Service URLs
echo LOGGING_SERVICE_URL=!LOGGING_SERVICE_URL!
echo ACCOUNTS_SERVICE_URL=!ACCOUNTS_SERVICE_URL!
echo TRANSACTIONS_SERVICE_URL=!TRANSACTIONS_SERVICE_URL!
echo BFF_SERVICE_URL=!BFF_SERVICE_URL!
echo.
echo # Actuator Configuration
echo MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
echo MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
echo.
echo # Spring Profile
echo SPRING_PROFILES_ACTIVE=docker
) > ..\Users\.env

echo Creating Logging/.env...
(
echo # Logging Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!LOGGING_SERVICE_PORT!
echo SPRING_APPLICATION_NAME=logging
echo.
echo # Database Configuration
echo SPRING_DATASOURCE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!LOGGING_DB_NAME!
echo SPRING_DATASOURCE_USERNAME=root
echo SPRING_DATASOURCE_PASSWORD=!LOGGING_DB_PASSWORD!
echo SPRING_JPA_HIBERNATE_DDL_AUTO=update
echo SPRING_JPA_SHOW_SQL=true
echo SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
echo.
echo # Kafka Configuration (Consumer^)
echo SPRING_KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
echo SPRING_KAFKA_CONSUMER_KEY_DESERIALIZER=org.apache.kafka.common.serialization.StringDeserializer
echo SPRING_KAFKA_CONSUMER_VALUE_DESERIALIZER=org.springframework.kafka.support.serializer.JsonDeserializer
echo SPRING_KAFKA_CONSUMER_PROPERTIES_SPRING_JSON_TRUSTED_PACKAGES=*
echo SPRING_KAFKA_CONSUMER_PROPERTIES_SPRING_JSON_USE_TYPE_HEADERS=false
echo SPRING_KAFKA_CONSUMER_PROPERTIES_SPRING_JSON_VALUE_DEFAULT_TYPE=java.util.HashMap
echo SPRING_KAFKA_CONSUMER_GROUP_ID=logging-group
echo.
echo # External Service URLs
echo USERS_SERVICE_URL=!USERS_SERVICE_URL!
echo ACCOUNTS_SERVICE_URL=!ACCOUNTS_SERVICE_URL!
echo TRANSACTIONS_SERVICE_URL=!TRANSACTIONS_SERVICE_URL!
echo BFF_SERVICE_URL=!BFF_SERVICE_URL!
echo.
echo # Actuator Configuration
echo MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
echo MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
echo.
echo # Spring Profile
echo SPRING_PROFILES_ACTIVE=docker
) > ..\Logging\.env

echo Creating Accounts/.env...
(
echo # Accounts Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!ACCOUNTS_SERVICE_PORT!
echo SPRING_APPLICATION_NAME=accounts
echo.
echo # Database Configuration
echo SPRING_DATASOURCE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!ACCOUNTS_DB_NAME!
echo SPRING_DATASOURCE_USERNAME=root
echo SPRING_DATASOURCE_PASSWORD=!ACCOUNTS_DB_PASSWORD!
echo SPRING_JPA_HIBERNATE_DDL_AUTO=update
echo SPRING_JPA_SHOW_SQL=true
echo SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
echo.
echo # Kafka Configuration
echo SPRING_KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
echo SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
echo SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
echo SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false
echo.
echo # External Service URLs
echo USERS_SERVICE_URL=!USERS_SERVICE_URL!
echo LOGGING_SERVICE_URL=!LOGGING_SERVICE_URL!
echo TRANSACTIONS_SERVICE_URL=!TRANSACTIONS_SERVICE_URL!
echo BFF_SERVICE_URL=!BFF_SERVICE_URL!
echo.
echo # Actuator Configuration
echo MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
echo MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
echo.
echo # Spring Profile
echo SPRING_PROFILES_ACTIVE=docker
) > ..\Accounts\.env

echo Creating Transactions/.env...
(
echo # Transactions Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!TRANSACTIONS_SERVICE_PORT!
echo SPRING_APPLICATION_NAME=transactions
echo.
echo # Database Configuration
echo SPRING_DATASOURCE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!TRANSACTIONS_DB_NAME!
echo SPRING_DATASOURCE_USERNAME=root
echo SPRING_DATASOURCE_PASSWORD=!TRANSACTIONS_DB_PASSWORD!
echo SPRING_JPA_HIBERNATE_DDL_AUTO=update
echo SPRING_JPA_SHOW_SQL=true
echo SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
echo.
echo # Kafka Configuration
echo SPRING_KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
echo SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
echo SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
echo SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false
echo.
echo # External Service URLs
echo USERS_SERVICE_URL=!USERS_SERVICE_URL!
echo LOGGING_SERVICE_URL=!LOGGING_SERVICE_URL!
echo ACCOUNTS_SERVICE_URL=!ACCOUNTS_SERVICE_URL!
echo BFF_SERVICE_URL=!BFF_SERVICE_URL!
echo.
echo # Actuator Configuration
echo MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
echo MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
echo.
echo # Spring Profile
echo SPRING_PROFILES_ACTIVE=docker
) > ..\Transactions\.env

echo Creating BFF/.env...
(
echo # BFF Service Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo SERVER_PORT=!BFF_SERVICE_PORT!
echo SPRING_APPLICATION_NAME=bff
echo.
echo # Database Configuration (using transactions DB^)
echo SPRING_DATASOURCE_URL=jdbc:mysql://!MYSQL_HOST!:!MYSQL_PORT!/!TRANSACTIONS_DB_NAME!
echo SPRING_DATASOURCE_USERNAME=root
echo SPRING_DATASOURCE_PASSWORD=!BFF_DB_PASSWORD!
echo SPRING_JPA_HIBERNATE_DDL_AUTO=update
echo SPRING_JPA_SHOW_SQL=true
echo SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect
echo.
echo # Kafka Configuration
echo SPRING_KAFKA_BOOTSTRAP_SERVERS=!KAFKA_BOOTSTRAP_SERVERS!
echo SPRING_KAFKA_PRODUCER_KEY_SERIALIZER=org.apache.kafka.common.serialization.StringSerializer
echo SPRING_KAFKA_PRODUCER_VALUE_SERIALIZER=org.springframework.kafka.support.serializer.JsonSerializer
echo SPRING_KAFKA_PRODUCER_PROPERTIES_SPRING_JSON_ADD_TYPE_HEADERS=false
echo.
echo # Microservice URLs for BFF to communicate with
echo SERVICES_USER_BASE_URL=!USERS_SERVICE_URL!
echo SERVICES_ACCOUNT_BASE_URL=!ACCOUNTS_SERVICE_URL!
echo SERVICES_TRANSACTION_BASE_URL=!TRANSACTIONS_SERVICE_URL!
echo SERVICES_LOGGING_BASE_URL=!LOGGING_SERVICE_URL!
echo.
echo # Actuator Configuration
echo MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
echo MANAGEMENT_ENDPOINT_HEALTH_SHOW_DETAILS=always
echo.
echo # Spring Profile
echo SPRING_PROFILES_ACTIVE=docker
) > ..\BFF\.env

echo Creating WebUI/VBank/.env...
(
echo # WebUI Environment Configuration
echo # Generated from master .env file
echo.
echo # Server Configuration
echo PORT=!WEBUI_PORT!
echo NODE_ENV=production
echo.
echo # API Endpoints
echo REACT_APP_BFF_BASE_URL=http://localhost:!BFF_SERVICE_PORT!
echo REACT_APP_API_MANAGER_URL=http://localhost:!WSO2_HTTP_PORT!
echo.
echo # Service URLs (for direct access if needed^)
echo REACT_APP_USERS_SERVICE_URL=http://localhost:!USERS_SERVICE_PORT!
echo REACT_APP_LOGGING_SERVICE_URL=http://localhost:!LOGGING_SERVICE_PORT!
echo REACT_APP_ACCOUNTS_SERVICE_URL=http://localhost:!ACCOUNTS_SERVICE_PORT!
echo REACT_APP_TRANSACTIONS_SERVICE_URL=http://localhost:!TRANSACTIONS_SERVICE_PORT!
echo.
echo # WSO2 API Manager URLs
echo REACT_APP_WSO2_GATEWAY_URL=http://localhost:!WSO2_HTTP_PORT!
echo REACT_APP_WSO2_HTTPS_GATEWAY_URL=https://localhost:!WSO2_HTTPS_GATEWAY_PORT!
echo REACT_APP_WSO2_CONSOLE_URL=https://localhost:!WSO2_HTTPS_PORT!
echo.
echo # Build Configuration
echo GENERATE_SOURCEMAP=false
echo BUILD_PATH=./build
) > ..\WebUI\VBank\.env

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
