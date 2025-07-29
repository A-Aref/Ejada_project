import { useState } from "react";
import { api } from "../utils/api";

interface TransactionFormProps {
  onSuccess?: () => void;
  onCancel?: () => void;
}

export function TransactionForm({ onSuccess, onCancel }: TransactionFormProps) {
  const [formData, setFormData] = useState({
    amount: "",
    toAccount: "",
    fromAccount: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [transactionId, setTransactionId] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      // Initiate transaction
      const result = await api.initiateTransaction({
        amount: parseFloat(formData.amount),
        toAccount: formData.toAccount,
        fromAccount: formData.fromAccount,
      });
      
      setTransactionId(result.transactionId);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to initiate transaction");
    } finally {
      setIsLoading(false);
    }
  };

  const handleExecute = async () => {
    if (!transactionId) return;
    
    setIsLoading(true);
    try {
      await api.executeTransaction(transactionId);
      onSuccess?.();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to execute transaction");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto bg-white/80 backdrop-blur-sm p-6 rounded-lg shadow-md border border-white/20">
      <h3 className="text-lg font-semibold mb-4">New Transaction</h3>
      
      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}

      {!transactionId ? (
        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700">Amount</label>
            <input
              type="number"
              step="0.01"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md"
              required
            />
          </div>
          
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700">From Account</label>
            <input
              type="text"
              value={formData.fromAccount}
              onChange={(e) => setFormData({ ...formData, fromAccount: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="Your account number"
              required
            />
          </div>
          
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700">To Account</label>
            <input
              type="text"
              value={formData.toAccount}
              onChange={(e) => setFormData({ ...formData, toAccount: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md"
              placeholder="Recipient account number"
              required
            />
          </div>
          
          <div className="flex gap-2">
            <button
              type="submit"
              disabled={isLoading}
              className="flex-1 bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 disabled:opacity-50"
            >
              {isLoading ? "Processing..." : "Initiate Transaction"}
            </button>
            <button
              type="button"
              onClick={onCancel}
              className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded hover:bg-gray-400"
            >
              Cancel
            </button>
          </div>
        </form>
      ) : (
        <div>
          <p className="mb-4 text-green-600">Transaction initiated successfully!</p>
          <p className="mb-4 text-sm text-gray-600">Transaction ID: {transactionId}</p>
          
          <div className="flex gap-2">
            <button
              onClick={handleExecute}
              disabled={isLoading}
              className="flex-1 bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700 disabled:opacity-50"
            >
              {isLoading ? "Executing..." : "Execute Transaction"}
            </button>
            <button
              onClick={onCancel}
              className="flex-1 bg-gray-300 text-gray-700 py-2 px-4 rounded hover:bg-gray-400"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
