# Development Guide

## Development Environment Setup

### Prerequisites

1. **Java Development Kit (JDK) 21**
   ```bash
   # Verify installation
   java -version
   javac -version
   ```

2. **Node.js 18+ and npm**
   ```bash
   # Verify installation
   node --version
   npm --version
   ```

3. **Maven 3.6+**
   ```bash
   # Verify installation
   mvn --version
   ```

4. **MySQL 8.0+**
   - Running on localhost:3306
   - See [HOST_MYSQL_SETUP.md](HOST_MYSQL_SETUP.md) for configuration

5. **Docker and Docker Compose**
   ```bash
   # Verify installation
   docker --version
   docker-compose --version
   ```

6. **IDE/Editor**
   - IntelliJ IDEA or Visual Studio Code recommended
   - Java and Spring Boot extensions

## Project Structure

```
Ejada_project/
├── config/                     # Configuration files
│   ├── docker-compose.yml     # Docker orchestration
│   └── .env                   # Master environment variables
├── scripts/                    # Management scripts
│   ├── docker-manage.sh       # Linux/macOS Docker management
│   ├── docker-manage.bat      # Windows Docker management
│   ├── generate-env.sh        # Linux/macOS env generation
│   └── generate-env.bat       # Windows env generation
├── docs/                       # Documentation
├── Users/                      # Users microservice
├── Logging/                    # Logging microservice
├── Accounts/                   # Accounts microservice
├── Transactions/               # Transactions microservice
├── BFF/                        # Backend for Frontend service
├── WebUI/VBank/               # React frontend application
├── API_Documentation.md       # API specifications
└── Databases_Creation.sql     # Database setup script
```

## Local Development Setup

### 1. Database Setup

Create and configure the required databases:

```sql
-- Create databases
CREATE DATABASE users_service_db;
CREATE DATABASE logging_service_db;
CREATE DATABASE account_service_db;
CREATE DATABASE transaction_service_db;

-- Grant permissions
GRANT ALL PRIVILEGES ON users_service_db.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON logging_service_db.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON account_service_db.* TO 'root'@'localhost';
GRANT ALL PRIVILEGES ON transaction_service_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Infrastructure Services

Start Kafka and WSO2 API Manager:

```bash
# From project root
cd config
docker-compose up -d kafka wso2-api-manager
```

### 3. Running Services Locally

#### Option A: Run All Services with Docker
```bash
# From scripts directory
./docker-manage.sh start
```

#### Option B: Run Services Individually for Development

**Backend Services (Spring Boot):**
```bash
# Terminal 1 - Users Service
cd Users
./mvnw spring-boot:run

# Terminal 2 - Logging Service
cd Logging
./mvnw spring-boot:run

# Terminal 3 - Accounts Service
cd Accounts
./mvnw spring-boot:run

# Terminal 4 - Transactions Service
cd Transactions
./mvnw spring-boot:run

# Terminal 5 - BFF Service
cd BFF
./mvnw spring-boot:run
```

**Frontend (React):**
```bash
# Terminal 6 - WebUI
cd WebUI/VBank
npm install
npm run dev
```

## Development Workflow

### 1. Setting Up a New Feature

1. **Create feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Update configuration if needed**:
   - Modify `config/.env` for new environment variables
   - Run `scripts/generate-env.sh` to update service configs

3. **Implement the feature**:
   - Follow service-specific guidelines below
   - Write tests for new functionality
   - Update API documentation

### 2. Backend Service Development

#### Spring Boot Service Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/ejada/[service]/
│   │       ├── Application.java          # Main application class
│   │       ├── controller/               # REST controllers
│   │       ├── service/                  # Business logic
│   │       ├── repository/               # Data access
│   │       ├── model/                    # Entity classes
│   │       ├── dto/                      # Data transfer objects
│   │       ├── config/                   # Configuration classes
│   │       └── exception/                # Exception handling
│   └── resources/
│       ├── application.properties        # Default configuration
│       ├── application-docker.properties # Docker configuration
│       ├── openapi.yml                   # API specification
│       └── static/                       # Static resources
└── test/
    └── java/                             # Unit and integration tests
```

#### Development Best Practices

1. **Follow RESTful conventions**:
   ```java
   @RestController
   @RequestMapping("/api/v1/users")
   public class UserController {
       
       @GetMapping("/{id}")
       public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
           // Implementation
       }
       
       @PostMapping
       public ResponseEntity<UserDto> createUser(@RequestBody CreateUserRequest request) {
           // Implementation
       }
   }
   ```

2. **Use proper error handling**:
   ```java
   @ControllerAdvice
   public class GlobalExceptionHandler {
       
       @ExceptionHandler(EntityNotFoundException.class)
       public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
           return ResponseEntity.notFound().build();
       }
   }
   ```

3. **Implement health checks**:
   ```java
   @Component
   public class CustomHealthIndicator implements HealthIndicator {
       
       @Override
       public Health health() {
           // Check service health
           return Health.up().build();
       }
   }
   ```

### 3. Frontend Development

#### React Development Setup
```bash
cd WebUI/VBank
npm install
npm run dev  # Development server with hot reload
```

#### Project Structure
```
src/
├── components/           # Reusable UI components
├── pages/               # Page components
├── hooks/               # Custom React hooks
├── services/            # API service calls
├── utils/               # Utility functions
├── types/               # TypeScript type definitions
└── styles/              # CSS and styling
```

#### Development Guidelines

1. **Use TypeScript for type safety**:
   ```typescript
   interface User {
     id: string;
     name: string;
     email: string;
   }
   
   const UserProfile: React.FC<{ user: User }> = ({ user }) => {
     return <div>{user.name}</div>;
   };
   ```

2. **Create reusable components**:
   ```typescript
   // components/Button.tsx
   interface ButtonProps {
     variant: 'primary' | 'secondary';
     onClick: () => void;
     children: React.ReactNode;
   }
   
   export const Button: React.FC<ButtonProps> = ({ variant, onClick, children }) => {
     return (
       <button className={`btn btn-${variant}`} onClick={onClick}>
         {children}
       </button>
     );
   };
   ```

3. **Use proper API service layer**:
   ```typescript
   // services/userService.ts
   export const userService = {
     async getUser(id: string): Promise<User> {
       const response = await fetch(`/api/v1/users/${id}`);
       return response.json();
     },
     
     async createUser(user: CreateUserRequest): Promise<User> {
       const response = await fetch('/api/v1/users', {
         method: 'POST',
         headers: { 'Content-Type': 'application/json' },
         body: JSON.stringify(user),
       });
       return response.json();
     }
   };
   ```

## Testing

### Backend Testing

#### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldCreateUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
        User savedUser = new User(1L, "John", "john@example.com");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserDto result = userService.createUser(request);
        
        // Then
        assertThat(result.getName()).isEqualTo("John");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }
}
```

#### Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateAndRetrieveUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest("John", "john@example.com");
        
        // When
        ResponseEntity<UserDto> createResponse = restTemplate.postForEntity(
            "/api/v1/users", request, UserDto.class);
        
        // Then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().getName()).isEqualTo("John");
    }
}
```

### Frontend Testing

#### Component Tests
```typescript
// components/__tests__/Button.test.tsx
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from '../Button';

describe('Button', () => {
  test('calls onClick when clicked', () => {
    const handleClick = jest.fn();
    
    render(
      <Button variant="primary" onClick={handleClick}>
        Click me
      </Button>
    );
    
    fireEvent.click(screen.getByText('Click me'));
    
    expect(handleClick).toHaveBeenCalledTimes(1);
  });
});
```

## Code Quality

### Code Style and Formatting

#### Backend (Java)
- Use Google Java Style Guide
- Configure IDE formatting rules
- Use CheckStyle for consistency

#### Frontend (TypeScript/React)
- Use Prettier for formatting
- ESLint for code quality
- Configure in `.eslintrc.js` and `.prettierrc`

### Git Workflow

1. **Commit Guidelines**:
   ```bash
   git commit -m "feat(users): add user creation endpoint"
   git commit -m "fix(transactions): handle insufficient funds error"
   git commit -m "docs: update API documentation"
   ```

2. **Branch Naming**:
   - `feature/user-management`
   - `bugfix/transaction-validation`
   - `hotfix/security-patch`

3. **Pull Request Process**:
   - Create descriptive PR title and description
   - Include test coverage information
   - Request code review from team members
   - Ensure CI/CD checks pass

## Environment Configuration

### Development Environment Variables

Create a `.env.local` file for local development overrides:

```bash
# Local development overrides
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/users_service_db
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
REACT_APP_API_BASE_URL=http://localhost:8084
```

### Profile-based Configuration

Use Spring profiles for different environments:

```properties
# application-dev.properties
spring.jpa.show-sql=true
logging.level.com.ejada=DEBUG

# application-prod.properties
spring.jpa.show-sql=false
logging.level.com.ejada=INFO
```

## Troubleshooting Development Issues

### Common Issues

1. **Port conflicts**:
   ```bash
   # Find process using port
   lsof -i :8080  # macOS/Linux
   netstat -ano | findstr :8080  # Windows
   ```

2. **Database connection issues**:
   - Verify MySQL is running
   - Check credentials in application.properties
   - Ensure database exists

3. **Kafka connection issues**:
   - Verify Kafka container is running
   - Check bootstrap-servers configuration
   - Review Kafka logs

### Debugging Tips

1. **Backend services**:
   - Use IDE debugger with remote debugging
   - Check application logs
   - Use Actuator endpoints for health checks

2. **Frontend**:
   - Use browser developer tools
   - Check network requests in DevTools
   - Use React Developer Tools extension

## Performance Optimization

### Backend Optimization

1. **Database performance**:
   - Use proper indexing
   - Optimize queries
   - Implement connection pooling

2. **API performance**:
   - Implement caching where appropriate
   - Use pagination for large result sets
   - Optimize JSON serialization

### Frontend Optimization

1. **Bundle optimization**:
   - Use code splitting
   - Implement lazy loading
   - Optimize images and assets

2. **Runtime performance**:
   - Use React.memo for expensive components
   - Optimize re-renders
   - Implement proper state management

## Security Considerations

### Development Security

1. **Secrets management**:
   - Never commit secrets to version control
   - Use environment variables
   - Rotate development credentials regularly

2. **API security**:
   - Implement proper authentication
   - Validate all inputs
   - Use HTTPS in all environments

3. **Database security**:
   - Use prepared statements
   - Implement proper access controls
   - Regular security updates

## Deployment

### Local Deployment
```bash
# Full system deployment
cd scripts
./docker-manage.sh start

# Individual service deployment
cd config
docker-compose up -d users-service
```

### Production Considerations

1. **Configuration management**:
   - Use environment-specific configurations
   - Implement proper secret management
   - Configure monitoring and alerting

2. **Scaling considerations**:
   - Design for horizontal scaling
   - Implement proper load balancing
   - Monitor resource usage

For more detailed deployment information, see [DOCKER_README.md](DOCKER_README.md).
