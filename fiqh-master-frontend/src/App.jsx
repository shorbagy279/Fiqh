import React from 'react';
import { AuthProvider } from './contexts/AuthContext';
import AppRoutes from './routes/AppRoutes';
import OfflineBanner from './components/shared/OfflineBanner';
import ToastContainer from './components/shared/ToastContainer';
import { useToast } from './hooks/useToast';
import './index.css';

const App = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

const AppContent = () => {
  const { toasts, removeToast } = useToast();

  return (
    <>
      <OfflineBanner />
      <ToastContainer toasts={toasts} removeToast={removeToast} />
      <div className="max-w-md mx-auto bg-white min-h-screen shadow-xl relative">
        <AppRoutes />
      </div>
    </>
  );
};

export default App;