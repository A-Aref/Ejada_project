# WSO2 API Integration

This project includes a simple integration with WSO2 API Management for banking operations.

## API Endpoints

The application connects to the following WSO2 API endpoints:

- **POST** `/auth/login` - User authentication
- **POST** `/auth/register` - User registration  
- **GET** `/dashboard` - Dashboard data (user profile, accounts, transactions)
- **POST** `/transactions/initiate` - Initiate a new transaction
- **POST** `/transactions/{id}/execute` - Execute a transaction

## Configuration

1. **Get WSO2 Consumer Credentials:**
   - Login to WSO2 API Manager Developer Portal (usually `https://localhost:9443/devportal`)
   - Create a new Application or use existing one
   - Subscribe to the VBank API
   - Go to "Production Keys" tab and generate keys
   - Copy the `Consumer Key` and `Consumer Secret`

2. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```

3. Update your `.env` file with the credentials:
   ```
   WSO2_API_URL=/api
   WSO2_OAUTH2_URL=/oauth2/token
   WSO2_CONSUMER_KEY=your_actual_consumer_key
   WSO2_CONSUMER_SECRET=your_actual_consumer_secret
   ```

## Authentication

The application uses WSO2 OAuth2 authentication flow with client credentials:

1. **OAuth2 Token Endpoint**: `/oauth2/token` (proxied to `http://localhost:9443/oauth2/token`)
   - Requires Basic Authentication with Consumer Key and Consumer Secret
   - Gets access token using username/password grant type
   - Token is stored in localStorage for subsequent requests

2. **Client Credentials Authentication**:
   - Uses `Authorization: Basic base64(consumer_key:consumer_secret)` header
   - Consumer Key and Consumer Secret from WSO2 Application

3. **Login Flow**:
   - First calls OAuth2 token endpoint with client credentials and user credentials
   - Stores the access token for API requests
   - Then calls user login endpoint to get user profile data
   - All subsequent API requests use the OAuth2 access token

## API Service

The main API service is located in `app/utils/api.ts` and provides simple functions for:

- `api.login(email, password)` - Login user
- `api.register(userData)` - Register new user
- `api.getDashboard()` - Get dashboard data
- `api.initiateTransaction(data)` - Start a transaction
- `api.executeTransaction(id)` - Complete a transaction

## Components

- **Login/Register forms** - Use the API for authentication
- **Account dashboard** - Displays data from WSO2 API
- **Transaction form** - Allows users to create and execute transactions

## Error Handling

The API service includes basic error handling and will throw errors with descriptive messages that can be displayed to users.
