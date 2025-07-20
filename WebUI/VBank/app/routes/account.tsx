import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/account";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "My Account - VBank" },
    { name: "description", content: "View your VBank account details" },
  ];
}

interface User {
  userId: string;
  username: string;
  email?: string;
  firstName?: string;
  lastName?: string;
}

interface Account {
  accountId: string;
  accountNumber: string;
  balance: number;
  accountType: string;
  status: string;
}

interface Transaction {
  transactionId: string;
  date: string;
  description: string;
  amount: number;
  type: 'DEBIT' | 'CREDIT';
  balance: number;
}

export default function Account() {
  const [user, setUser] = useState<User | null>(null);
  const [userProfile, setUserProfile] = useState<any>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [expandedAccounts, setExpandedAccounts] = useState<Set<string>>(new Set());
  const [transactions, setTransactions] = useState<Record<string, Transaction[]>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    // Check if user is logged in
    const userData = localStorage.getItem("user");
    if (!userData) {
      navigate("/");
      return;
    }

    const parsedUser = JSON.parse(userData);
    setUser(parsedUser);
    // TODO: Commented out for UI testing
    // fetchUserProfile(parsedUser.userId);
    // fetchUserAccounts(parsedUser.userId);
    
    // Mock data for UI testing
    setUserProfile({
      firstName: "John",
      lastName: "Doe",
      email: parsedUser.email || "john.doe@example.com",
      phone: "+1-555-123-4567",
      address: "123 Main St, City, State 12345"
    });
    
    setAccounts([
      {
        accountId: "acc-123",
        accountNumber: "1234567890",
        accountType: "CHECKING",
        balance: 2500.75,
        status: "ACTIVE"
      },
      {
        accountId: "acc-456", 
        accountNumber: "0987654321",
        accountType: "SAVINGS",
        balance: 15000.00,
        status: "INACTIVE"
      }
    ]);
    
    // Mock transaction data
    setTransactions({
      "acc-123": [
        {
          transactionId: "txn-001",
          date: "2025-07-20",
          description: "Online Purchase - Amazon",
          amount: -89.99,
          type: "DEBIT",
          balance: 2500.75
        },
        {
          transactionId: "txn-002",
          date: "2025-07-19",
          description: "Salary Deposit",
          amount: 3500.00,
          type: "CREDIT",
          balance: 2590.74
        },
        {
          transactionId: "txn-003",
          date: "2025-07-18",
          description: "ATM Withdrawal",
          amount: -200.00,
          type: "DEBIT",
          balance: -909.26
        },
        {
          transactionId: "txn-004",
          date: "2025-07-17",
          description: "Grocery Store",
          amount: -125.50,
          type: "DEBIT",
          balance: -709.26
        }
      ],
      "acc-456": [
        {
          transactionId: "txn-005",
          date: "2025-07-15",
          description: "Interest Payment",
          amount: 45.50,
          type: "CREDIT",
          balance: 15000.00
        },
        {
          transactionId: "txn-006",
          date: "2025-07-10",
          description: "Transfer from Checking",
          amount: 1000.00,
          type: "CREDIT",
          balance: 14954.50
        },
        {
          transactionId: "txn-007",
          date: "2025-07-05",
          description: "Investment Withdrawal",
          amount: -500.00,
          type: "DEBIT",
          balance: 13954.50
        }
      ]
    });
    
    setIsLoading(false);
  }, [navigate]);

  const fetchUserProfile = async (userId: string) => {
    try {
      // TODO: Commented out for UI testing
      // const response = await fetch(`http://localhost:8081/users/${userId}/profile`);
      // if (response.ok) {
      //   const profileData = await response.json();
      //   setUserProfile(profileData);
      // }
    } catch (err) {
      console.error("Failed to fetch user profile:", err);
    }
  };

  const fetchUserAccounts = async (userId: string) => {
    try {
      // TODO: Commented out for UI testing
      // const response = await fetch(`http://localhost:8082/accounts/users/${userId}`);
      // if (response.ok) {
      //   const accountData = await response.json();
      //   setAccounts(accountData.accounts || []);
      // } else if (response.status === 404) {
      //   // No accounts found, which is fine
      //   setAccounts([]);
      // }
    } catch (err) {
      console.error("Failed to fetch accounts:", err);
      setError("Failed to load accounts");
    } finally {
      setIsLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/");
  };

  const toggleAccountExpansion = (accountId: string) => {
    const newExpanded = new Set(expandedAccounts);
    if (newExpanded.has(accountId)) {
      newExpanded.delete(accountId);
    } else {
      newExpanded.add(accountId);
    }
    setExpandedAccounts(newExpanded);
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  };

  const getAccountTypeColor = (type: string) => {
    switch (type.toLowerCase()) {
      case "savings":
        return "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200";
      case "checking":
        return "bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200";
      case "credit":
        return "bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200";
      default:
        return "bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-200";
    }
  };

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case "active":
        return "bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200";
      case "inactive":
        return "bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200";
      case "suspended":
        return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200";
      default:
        return "bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-200";
    }
  };

  if (!user) {
    return null; // Will redirect to login
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Header */}
      <div className="bg-white dark:bg-gray-800 shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <span className="text-2xl font-bold text-blue-600">VBank</span>
              <span className="ml-4 text-lg font-medium text-gray-900 dark:text-white">
                My Account
              </span>
            </div>
            <div className="flex items-center space-x-4">
              
              <button
                onClick={handleLogout}
                className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">
          {/* User Profile Section */}
          <div className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg mb-6">
            <div className="px-4 py-5 sm:p-6">
              <h2 className="text-lg leading-6 font-medium text-gray-900 dark:text-white mb-4">
                User Profile
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
                    Username
                  </dt>
                  <dd className="mt-1 text-sm text-gray-900 dark:text-white">
                    {user.username}
                  </dd>
                </div>
                {userProfile && (
                  <>
                    <div>
                      <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
                        Email
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900 dark:text-white">
                        {userProfile.email}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
                        First Name
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900 dark:text-white">
                        {userProfile.firstName}
                      </dd>
                    </div>
                    <div>
                      <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
                        Last Name
                      </dt>
                      <dd className="mt-1 text-sm text-gray-900 dark:text-white">
                        {userProfile.lastName}
                      </dd>
                    </div>
                  </>
                )}
                <div>
                  <dt className="text-sm font-medium text-gray-500 dark:text-gray-400">
                    User ID
                  </dt>
                  <dd className="mt-1 text-sm text-gray-900 dark:text-white font-mono">
                    {user.userId}
                  </dd>
                </div>
              </div>
            </div>
          </div>

          {/* Accounts Section */}
          <div className="bg-white dark:bg-gray-800 overflow-hidden shadow rounded-lg">
            <div className="px-4 py-5 sm:p-6">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg leading-6 font-medium text-gray-900 dark:text-white">
                  My Accounts
                </h2>
                <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium">
                  Open New Account
                </button>
              </div>

              {isLoading ? (
                <div className="flex justify-center py-8">
                  <svg
                    className="animate-spin h-8 w-8 text-blue-600"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    ></circle>
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    ></path>
                  </svg>
                </div>
              ) : error ? (
                <div className="text-center py-8">
                  <p className="text-red-600 dark:text-red-400">{error}</p>
                </div>
              ) : accounts.length === 0 ? (
                <div className="text-center py-8">
                  <div className="text-gray-400 dark:text-gray-500">
                    <svg
                      className="mx-auto h-12 w-12"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"
                      />
                    </svg>
                  </div>
                  <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">
                    No accounts
                  </h3>
                  <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
                    You don't have any bank accounts yet. Open your first account to get started.
                  </p>
                </div>
              ) : (
                <div className="grid gap-4">
                  {accounts.map((account) => {
                    const isExpanded = expandedAccounts.has(account.accountId);
                    const accountTransactions = transactions[account.accountId] || [];
                    
                    return (
                      <div
                        key={account.accountId}
                        className="border border-gray-200 dark:border-gray-700 rounded-lg overflow-hidden"
                      >
                        {/* Account Header - Clickable */}
                        <div
                          onClick={() => toggleAccountExpansion(account.accountId)}
                          className="p-4 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors cursor-pointer"
                        >
                          <div className="flex justify-between items-start">
                            <div className="flex-1">
                              <div className="flex items-center space-x-3">
                                <h3 className="text-sm font-medium text-gray-900 dark:text-white">
                                  Account #{account.accountNumber}
                                </h3>
                                <span
                                  className={`px-2 py-1 text-xs font-medium rounded-full ${getAccountTypeColor(
                                    account.accountType
                                  )}`}
                                >
                                  {account.accountType}
                                </span>
                                <span
                                  className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(
                                    account.status
                                  )}`}
                                >
                                  {account.status}
                                </span>
                              </div>
                              <p className="mt-1 text-xs text-gray-500 dark:text-gray-400 font-mono">
                                ID: {account.accountId}
                              </p>
                            </div>
                            <div className="flex items-center space-x-3">
                              <div className="text-right">
                                <p className="text-lg font-semibold text-gray-900 dark:text-white">
                                  {formatCurrency(account.balance)}
                                </p>
                                <p className="text-xs text-gray-500 dark:text-gray-400">
                                  Current Balance
                                </p>
                              </div>
                              {/* Expand/Collapse Icon */}
                              <svg
                                className={`h-5 w-5 text-gray-400 transition-transform duration-200 ${
                                  isExpanded ? "rotate-180" : ""
                                }`}
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                              >
                                <path
                                  strokeLinecap="round"
                                  strokeLinejoin="round"
                                  strokeWidth="2"
                                  d="M19 9l-7 7-7-7"
                                />
                              </svg>
                            </div>
                          </div>
                        </div>

                        {/* Transactions Section - Expandable */}
                        {isExpanded && (
                          <div className="border-t border-gray-200 dark:border-gray-700 bg-gray-900 dark:bg-gray-750">
                            <div className="p-4">
                              <h4 className="text-sm font-medium text-gray-900 dark:text-white mb-3">
                                Recent Transactions
                              </h4>
                              {accountTransactions.length === 0 ? (
                                <p className="text-sm text-gray-500 dark:text-gray-400 text-center py-4">
                                  No transactions found
                                </p>
                              ) : (
                                <div className="space-y-3">
                                  {accountTransactions.map((transaction) => (
                                    <div
                                      key={transaction.transactionId}
                                      className="flex justify-between items-center py-2 px-3 bg-white dark:bg-gray-800 rounded border border-gray-200 dark:border-gray-600"
                                    >
                                      <div className="flex-1">
                                        <p className="text-sm font-medium text-gray-900 dark:text-white">
                                          {transaction.description}
                                        </p>
                                        <p className="text-xs text-gray-500 dark:text-gray-400">
                                          {formatDate(transaction.date)} • ID: {transaction.transactionId}
                                        </p>
                                      </div>
                                      <div className="text-right">
                                        <p
                                          className={`text-sm font-semibold ${
                                            transaction.amount >= 0
                                              ? "text-green-600 dark:text-green-400"
                                              : "text-red-600 dark:text-red-400"
                                          }`}
                                        >
                                          {transaction.amount >= 0 ? "+" : ""}{formatCurrency(transaction.amount)}
                                        </p>
                                        <p className="text-xs text-gray-500 dark:text-gray-400">
                                          Balance: {formatCurrency(transaction.balance)}
                                        </p>
                                      </div>
                                    </div>
                                  ))}
                                </div>
                              )}
                            </div>
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
