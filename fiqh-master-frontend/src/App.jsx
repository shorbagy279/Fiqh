import React from 'react';
import { AuthProvider } from './contexts/AuthContext';
import AppRoutes from './routes/AppRoutes';
import './index.css';

const App = () => {
  return (
    <AuthProvider>
      <div className="max-w-md mx-auto bg-white min-h-screen shadow-xl">
        <AppRoutes />
      </div>
    </AuthProvider>
  );
};

export default App;