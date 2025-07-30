import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/account";
import { api } from "../utils/api";
import { TransactionForm } from "../components/TransactionForm";
import { OpenAccountModal } from "../components/OpenAccountModal";

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
  const [showTransactionForm, setShowTransactionForm] = useState(false);
  const [showOpenAccountModal, setShowOpenAccountModal] = useState(false);
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
    
    // Fetch dashboard data from WSO2 API
    fetchDashboardData();
  }, [navigate]);

  const fetchDashboardData = async () => {
    try {
      const dashboardData = await api.getDashboard();
      console.log('Dashboard data received:', dashboardData);
      
      // Set user profile from dashboard response
      setUserProfile({
        email: dashboardData.email,
        firstName: dashboardData.firstName,
        lastName: dashboardData.lastName
      });
      
      if (dashboardData.accounts) {
        setAccounts(dashboardData.accounts);
        
        // Extract transactions from accounts and organize by accountId
        const transactionsByAccount: Record<string, Transaction[]> = {};
        dashboardData.accounts.forEach((account: any) => {
          if (account.accountTransactions) {
            // Map the API transaction structure to frontend structure
            transactionsByAccount[account.accountId] = account.accountTransactions.map((tx: any) => ({
              transactionId: tx.transactionId,
              date: tx.timestamp,
              description: tx.description || 'Transaction',
              amount: tx.amount,
              type: tx.amount >= 0 ? 'CREDIT' : 'DEBIT',
              balance: account.balance // You might need to calculate running balance if available
            }));
          }
        });
        setTransactions(transactionsByAccount);
        console.log('Transactions organized by account:', transactionsByAccount);
      }
    } catch (err) {
      console.error("Failed to fetch dashboard data:", err);
      setError("Failed to load dashboard data");
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
                <div className="flex gap-2">
                  <button 
                    onClick={() => setShowTransactionForm(true)}
                    className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-md text-sm font-medium"
                  >
                    New Transaction
                  </button>
                  <button 
                    onClick={() => setShowOpenAccountModal(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium"
                  >
                    Open New Account
                  </button>
                </div>
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

          {/* Transaction Form Modal */}
          {showTransactionForm && (
            <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
              <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                <TransactionForm
                  onSuccess={() => {
                    setShowTransactionForm(false);
                    fetchDashboardData(); // Refresh data after successful transaction
                  }}
                  onCancel={() => setShowTransactionForm(false)}
                />
              </div>
            </div>
          )}

          {/* Open Account Modal */}
          <OpenAccountModal
            isOpen={showOpenAccountModal}
            onClose={() => setShowOpenAccountModal(false)}
            onSuccess={() => {
              setShowOpenAccountModal(false);
              fetchDashboardData(); // Refresh data after successful account opening
            }}
          />
        </div>
      </div>
    </div>
  );
}
