import React from 'react';
import { AlertCircle } from 'lucide-react';

const ErrorAlert = ({ message, onRetry }) => (
  <div className="bg-red-50 border-2 border-red-200 rounded-xl p-4 flex items-start gap-3">
    <AlertCircle className="text-red-500 flex-shrink-0 mt-0.5" size={20} />
    <div className="flex-1">
      <p className="text-red-800 text-sm">{message}</p>
      {onRetry && (
        <button onClick={onRetry} className="mt-2 text-sm text-red-600 font-bold hover:text-red-700">
          حاول مرة أخرى
        </button>
      )}
    </div>
  </div>
);

export default ErrorAlert;