// Updated src/App.jsx

import React, { useEffect } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import AppRoutes from './routes/AppRoutes';
import OfflineBanner from './components/shared/OfflineBanner';
import ToastContainer from './components/shared/ToastContainer';
import { useToast } from './hooks/useToast';
import notificationService from './services/notificationService';
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
  const { user } = useAuth();

  // Initialize notifications when user is logged in
  useEffect(() => {
    if (user && user.dailyReminders) {
      // Request permission if not already granted
      if (Notification.permission === 'default') {
        notificationService.requestPermission();
      }
      
      // Start reminder checks
      if (Notification.permission === 'granted') {
        notificationService.scheduleReminderCheck(user);
      }
    }
  }, [user]);

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