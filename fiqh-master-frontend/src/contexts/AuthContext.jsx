import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('fiqh_token'));
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const clearAuth = useCallback(() => {
    localStorage.removeItem('fiqh_token');
    setToken(null);
    setUser(null);
  }, []);

  useEffect(() => {
    if (token) {
      api.getCurrentUser(token)
        .then(userData => {
          setUser(userData);
          setError(null);
        })
        .catch(err => {
          console.error('Auth error:', err);
          if (err.status === 401) {
            clearAuth();
            setError('انتهت الجلسة. يرجى تسجيل الدخول مرة أخرى.');
          } else {
            setError('فشل تحميل بيانات المستخدم');
          }
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token, clearAuth]);

  const login = async (email, password) => {
    try {
      setError(null);
      const response = await api.login({ email, password });
      localStorage.setItem('fiqh_token', response.token);
      setToken(response.token);
      setUser(response.user);
      return response;
    } catch (err) {
      const errorMsg = err.message || 'فشل تسجيل الدخول';
      setError(errorMsg);
      throw err;
    }
  };

  const register = async (data) => {
    try {
      setError(null);
      const response = await api.register(data);
      localStorage.setItem('fiqh_token', response.token);
      setToken(response.token);
      setUser(response.user);
      return response;
    } catch (err) {
      const errorMsg = err.message || 'فشل التسجيل';
      setError(errorMsg);
      throw err;
    }
  };

  const logout = useCallback(() => {
    clearAuth();
    setError(null);
  }, [clearAuth]);

  const refreshUser = async () => {
    if (token) {
      try {
        const updatedUser = await api.getCurrentUser(token);
        setUser(updatedUser);
        setError(null);
      } catch (err) {
        console.error('Refresh user error:', err);
        if (err.status === 401) {
          clearAuth();
        }
      }
    }
  };

  const updateUser = (updates) => {
    setUser(prev => ({ ...prev, ...updates }));
  };

  return (
    <AuthContext.Provider value={{ 
      user, 
      token, 
      loading, 
      error, 
      login, 
      register, 
      logout, 
      refreshUser,
      updateUser,
      clearError: () => setError(null)
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
