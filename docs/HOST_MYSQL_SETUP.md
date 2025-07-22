# Host MySQL Setup Guide

## Prerequisites

Before running the Docker Compose setup, you need to ensure your host MySQL is properly configured:

### 1. MySQL Installation
Ensure MySQL 8.0+ is installed and running on your host machine on port 3306.

### 2. Database Creation
Run the following commands in your MySQL client to create the required databases:

```sql
CREATE DATABASE IF NOT EXISTS users_service_db;
CREATE DATABASE IF NOT EXISTS logging_service_db;
CREATE DATABASE IF NOT EXISTS account_service_db;
CREATE DATABASE IF NOT EXISTS transaction_service_db;
```

Or simply run the provided SQL script:
```bash
mysql -u root -p < Databases_Creation.sql
```

### 3. User Permissions
Ensure the root user (or create a dedicated user) has access to these databases:

```sql
-- Grant permissions to root user
GRANT ALL PRIVILEGES ON users_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON logging_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON account_service_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON transaction_service_db.* TO 'root'@'%';
FLUSH PRIVILEGES;
```

### 4. Network Configuration
Make sure MySQL is configured to accept connections from Docker containers:

#### For Windows (my.ini):
```ini
[mysqld]
bind-address = 0.0.0.0
```

#### For Linux/macOS (my.cnf):
```ini
[mysqld]
bind-address = 0.0.0.0
```

After changing the configuration, restart MySQL service.

### 5. Firewall Configuration
Ensure port 3306 is accessible:

#### Windows:
```powershell
netsh advfirewall firewall add rule name="MySQL" dir=in action=allow protocol=TCP localport=3306
```

#### Linux:
```bash
sudo ufw allow 3306
```

### 6. Verify Connection
Test that Docker can connect to your host MySQL:

```bash
# Test connection from Docker
docker run --rm mysql:8.0 mysql -h host.docker.internal -u root -p12345678 -e "SELECT 1"
```

## Updated Configuration

The Docker Compose configuration has been updated to:

1. **Remove the MySQL container** - Uses your host MySQL instead
2. **Update connection strings** - All services now connect to `host.docker.internal:3306`
3. **Remove MySQL dependencies** - Services only depend on Kafka now
4. **Update Kafka configuration** - Uses the new KRaft-mode Apache Kafka image

## Connection Details

- **Host**: `host.docker.internal` (from Docker containers)
- **Port**: `3306`
- **Username**: `root`
- **Passwords**: 
  - Users, Logging, Accounts, Transactions: `12345678`
  - BFF: `Flywithme1`

## Troubleshooting

### Connection Issues
1. Check if MySQL is running: `systemctl status mysql` (Linux) or Services (Windows)
2. Verify port binding: `netstat -an | grep 3306`
3. Check MySQL logs for connection errors
4. Test direct connection: `mysql -u root -p -h localhost`

### Permission Issues
1. Verify user has privileges: `SHOW GRANTS FOR 'root'@'%';`
2. Check MySQL user table: `SELECT user, host FROM mysql.user;`
3. Ensure root user exists for '%' host

### Docker Network Issues
1. Test host.docker.internal resolution from container
2. Check Docker Desktop settings for host networking
3. Verify firewall allows Docker subnet access

## Security Notes

- Change default passwords in production
- Create dedicated database users instead of using root
- Configure SSL/TLS for database connections
- Restrict network access to necessary hosts only
