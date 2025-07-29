// Simple WSO2 API utility
const API_BASE_URL = import.meta.env.VITE_WSO2_API_URL || '/api';
const OAUTH2_TOKEN_URL = import.meta.env.VITE_WSO2_OAUTH2_URL || '/oauth2/token';
const CONSUMER_KEY = import.meta.env.VITE_WSO2_CONSUMER_KEY || 'your_consumer_key';
const CONSUMER_SECRET = import.meta.env.VITE_WSO2_CONSUMER_SECRET || 'your_consumer_secret';

// Debug environment variables (remove in production)
console.log('Environment Variables:', {
  API_BASE_URL,
  OAUTH2_TOKEN_URL,
  CONSUMER_KEY: CONSUMER_KEY?.slice(0, 8) + '...',
  CONSUMER_SECRET: CONSUMER_SECRET ? '***set***' : 'NOT_SET'
});


// Simple fetch wrapper with automatic token management
async function apiCall(endpoint: string, options: RequestInit = {}) {
  let token = localStorage.getItem('authToken');
  
  // If no token exists, try to get a client credentials token
  if (!token) {
    try {
      console.log('No token found, getting client credentials token...');
      token = await getClientCredentialsToken();
      if (token) {
        localStorage.setItem('authToken', token);
      }
    } catch (err) {
      console.error('Failed to get client credentials token:', err);
      throw new Error('Authentication required');
    }
  }
  
  console.log(`Making API call to ${endpoint} with token`);
  if (token) {
    inspectToken(token);
  }
  
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'APP-NAME': 'PORTAL',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    // If 401, token might be expired, try to refresh
    if (response.status === 401) {
      console.log('Token expired, trying to refresh with client credentials...');
      localStorage.removeItem('authToken');
      try {
        token = await getClientCredentialsToken();
        if (token) {
          localStorage.setItem('authToken', token);
          console.log('Got new client credentials token, retrying request...');
          // Retry the request with new token
          return fetch(`${API_BASE_URL}${endpoint}`, {
            headers: {
              'Content-Type': 'application/json',
              'Accept': 'application/json',
              'APP-NAME': 'PORTAL',
              Authorization: `Bearer ${token}`,
              ...options.headers,
            },
            ...options,
          }).then(async retryResponse => {
            if (!retryResponse.ok) {
              const retryErrorText = await retryResponse.text();
              console.error('Retry request also failed:', {
                status: retryResponse.status,
                body: retryErrorText
              });
              throw new Error(`API Error after retry: ${retryResponse.status} - ${retryErrorText}`);
            }
            return retryResponse.json();
          });
        }
      } catch (refreshErr) {
        console.error('Token refresh failed:', refreshErr);
        throw new Error('Authentication failed - please login again');
      }
    }
    
    const errorText = await response.text();
    console.error('API Error:', {
      endpoint,
      status: response.status,
      statusText: response.statusText,
      body: errorText
    });
    throw new Error(`API Error: ${response.status} - ${errorText}`);
  }

  return response.json();
}

// Helper function to get client credentials token
// Debugging function to inspect token
function inspectToken(token: string) {
  try {
    // Check if JWT format
    const parts = token.split('.');
    if (parts.length === 3) {
      const payload = JSON.parse(atob(parts[1]));
      console.log('Token payload:', payload);
      console.log('Token expiry:', new Date(payload.exp * 1000));
      console.log('Token scope:', payload.scope);
      console.log('Token audience:', payload.aud);
    } else {
      console.log('Token is not JWT format:', token.substring(0, 50) + '...');
    }
  } catch (err) {
    console.log('Failed to parse token:', err);
  }
}

async function getClientCredentialsToken(): Promise<string | null> {
  const clientCredentials = btoa(`${CONSUMER_KEY}:${CONSUMER_SECRET}`);
  
  const response = await fetch(OAUTH2_TOKEN_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': `Basic ${clientCredentials}`,
      'Accept': 'application/json',
    },
    body: new URLSearchParams({
      grant_type: 'client_credentials',
      scope: 'default'
    }),
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`OAuth2 Error: ${response.status} - ${errorText}`);
  }

  const tokenData = await response.json();
  return tokenData.access_token;
}

// API functions
export const api = {
  // Get OAuth2 token for user authentication
  // Note: This requires the user to exist in WSO2 with the correct password
  // Currently not used in login flow - using client credentials instead
  getToken: async (username: string, password: string) => {
    // Create Basic Auth header for client credentials
    const clientCredentials = btoa(`${CONSUMER_KEY}:${CONSUMER_SECRET}`);
    
    console.log('OAuth2 Request Details:', {
      url: OAUTH2_TOKEN_URL,
      consumerKey: CONSUMER_KEY,
      consumerSecret: CONSUMER_SECRET ? '***hidden***' : 'NOT_SET',
      basicAuth: clientCredentials,
      requestBody: {
        grant_type: 'password',
        username: username,
        password: '***hidden***',
        scope: 'default'
      }
    });
    
    const response = await fetch(OAUTH2_TOKEN_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': `Basic ${clientCredentials}`,
        'Accept': 'application/json',
      },
      body: new URLSearchParams({
        grant_type: 'password',
        username: username,
        password: password,
        scope: 'default'
      }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('OAuth2 Error Response:', {
        status: response.status,
        statusText: response.statusText,
        headers: Object.fromEntries(response.headers.entries()),
        body: errorText
      });
      throw new Error(`OAuth2 Error: ${response.status} - ${errorText}`);
    }

    const tokenData = await response.json();
    console.log('OAuth2 Success Response:', tokenData);
    
    // Store access token
    if (tokenData.access_token) {
      localStorage.setItem('authToken', tokenData.access_token);
    }
    
    return tokenData;
  },

  // Login - authenticate with backend and get WSO2 token
  login: async (username: string, password: string) => {
    console.log('Login called with username:', username);

    // First get WSO2 client credentials token for API calls
    const token = await getClientCredentialsToken();
    
    if (!token) {
      throw new Error('Failed to authenticate with WSO2');
    }
    
    // Store token temporarily for the login API call
    localStorage.setItem('authToken', token);
    
    try {
      // Call backend login endpoint to validate user credentials
      const loginResponse = await apiCall('/users/login', {
        method: 'POST',
        body: JSON.stringify({
          username: username,
          password: password
        }),
      });
      
      console.log('Backend login response:', loginResponse);
      
      // Store user info from backend response
      const userData = {
        userId: loginResponse.userId || loginResponse.id || username,
        username: username,
        email: loginResponse.email || username,
        token: token,
        ...loginResponse // Include any additional user data from backend
      };
      
      localStorage.setItem('user', JSON.stringify(userData));
      
      return userData;
      
    } catch (error) {
      // Remove token if login failed
      localStorage.removeItem('authToken');
      console.error('Backend login failed:', error);
      throw new Error('Invalid username or password');
    }
  },

  // Register
  register: (userData: {username:string; email: string; password: string; firstName: string; lastName: string }) =>
    apiCall('/users/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    }),

  // Dashboard data
  getDashboard: () => {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    console.log('Fetching dashboard for user:', user);
    const userId = user.userId || user.username || user.email;
    if (!userId) {
      throw new Error('User ID not found - please login again');
    }
    return apiCall(`/bff/dashboard/${userId}`);
  },

  // Initiate transaction
  initiateTransaction: (transactionData: { amount: number; toAccount: string; fromAccount: string; }) =>
    apiCall('/transactions/transfer/initiation', {
      method: 'POST',
      body: JSON.stringify(transactionData),
    }),

  // Execute transaction
  executeTransaction: (transactionId: string) =>
    apiCall(`/transactions/transfer/execution`, {
      method: 'POST',
      body: JSON.stringify({ transactionId }),
    }),

  // Open new account
  openAccount: (accountData: { accountType: string; initialDeposit: number; currency: string; }) =>
    apiCall('/accounts/open', {
      method: 'POST',
      body: JSON.stringify(accountData),
    }),
};
