import { useState } from "react";
import { api } from "../utils/api";

interface OpenAccountModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

export function OpenAccountModal({ isOpen, onClose, onSuccess }: OpenAccountModalProps) {
  const [formData, setFormData] = useState({
    accountType: "SAVINGS",
    initialDeposit: "",
    currency: "USD",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      // Call the backend API to open account
      const result = await api.openAccount({
        accountType: formData.accountType,
        initialDeposit: parseFloat(formData.initialDeposit),
        currency: formData.currency,
      });
      
      console.log("Account opened successfully:", result);
      
      // Reset form
      setFormData({
        accountType: "SAVINGS",
        initialDeposit: "",
        currency: "USD",
      });
      
      onSuccess?.();
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to open account");
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Open New Account</h2>
            <button
              onClick={onClose}
              className="text-gray-400 hover:text-gray-600 text-2xl font-bold"
            >
              ×
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Account Type
              </label>
              <select
                value={formData.accountType}
                onChange={(e) => setFormData({ ...formData, accountType: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="SAVINGS">Savings Account</option>
                <option value="CHECKING">Checking Account</option>
                <option value="BUSINESS">Business Account</option>
              </select>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Initial Deposit
              </label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={formData.initialDeposit}
                onChange={(e) => setFormData({ ...formData, initialDeposit: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Enter amount"
                required
              />
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Currency
              </label>
              <select
                value={formData.currency}
                onChange={(e) => setFormData({ ...formData, currency: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="USD">USD - US Dollar</option>
                <option value="EUR">EUR - Euro</option>
                <option value="GBP">GBP - British Pound</option>
                <option value="SAR">SAR - Saudi Riyal</option>
              </select>
            </div>

            <div className="flex gap-3">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isLoading}
                className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50"
              >
                {isLoading ? "Opening..." : "Open Account"}
              </button>
            </div>
          </form>

          <div className="mt-4 p-3 bg-blue-50 rounded-md">
            <h3 className="font-medium text-blue-900 mb-2">Account Benefits:</h3>
            <ul className="text-sm text-blue-800 space-y-1">
              <li>• No monthly maintenance fees</li>
              <li>• 24/7 online banking access</li>
              <li>• Mobile app with instant notifications</li>
              <li>• Competitive interest rates</li>
              <li>• FDIC insured up to $250,000</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
