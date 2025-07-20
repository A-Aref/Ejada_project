import { useLocation } from "react-router";

export default function CatchAll() {
  const location = useLocation();
  
  // Handle Chrome DevTools requests silently
  if (location.pathname.includes('.well-known') || 
      location.pathname.includes('devtools') ||
      location.pathname.includes('.json')) {
    return null; // Return nothing for these requests
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <h2 className="mt-6 text-3xl font-extrabold text-gray-900 dark:text-white">
            404 - Page Not Found
          </h2>
          <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
            The page you're looking for doesn't exist.
          </p>
          <p className="mt-1 text-xs text-gray-500 dark:text-gray-500">
            Requested: {location.pathname}
          </p>
          <div className="mt-6">
            <a
              href="/"
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              Go back home
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}
