# Docker Compose Environment Variable Configuration

This Docker Compose setup now uses environment variables from the `.env` file for all port configurations, making it easy to customize ports without editing the docker-compose.yml file.

## Usage

### 1. From the project root directory:
```bash
cd d:\Abd-Allah\Ejada\Ejada_project
docker-compose -f config/docker-compose.yml up -d
```

### 2. Or from the config directory:
```bash
cd d:\Abd-Allah\Ejada\Ejada_project\config
docker-compose up -d
```

## Environment Variables Used

The following environment variables are now configurable in `config/.env`:

### Application Ports
- `USERS_SERVICE_PORT=8080`
- `LOGGING_SERVICE_PORT=8081` 
- `ACCOUNTS_SERVICE_PORT=8082`
- `TRANSACTIONS_SERVICE_PORT=8083`
- `BFF_SERVICE_PORT=8084`

### Infrastructure Ports
- `KAFKA_PORT=9092`
- `WSO2_HTTPS_PORT=9443`
- `WSO2_HTTP_PORT=8280`
- `WSO2_HTTPS_GATEWAY_PORT=8243`

## Benefits

1. **Easy Port Customization**: Change ports in one place (`.env` file)
2. **Environment Specific Configuration**: Different `.env` files for dev/test/prod
3. **No Docker Compose Editing**: Modify ports without touching docker-compose.yml
4. **Consistent Configuration**: Same port variables used in application configs

## Example: Changing Ports

To run services on different ports, simply modify `config/.env`:

```env
# Custom ports example
USERS_SERVICE_PORT=9080
ACCOUNTS_SERVICE_PORT=9082
BFF_SERVICE_PORT=9084
```

Then restart with:
```bash
docker-compose -f config/docker-compose.yml down
docker-compose -f config/docker-compose.yml up -d
```

## Important Notes

- Make sure to run Docker Compose from the project root or config directory
- The `.env` file in the config directory contains all the port configurations
- Individual service `.env` files are generated using the `scripts/generate-env.sh` or `scripts/generate-env.bat`
- Health checks and internal communication also use these environment variables
