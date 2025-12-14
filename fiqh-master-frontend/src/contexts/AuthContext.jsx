import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('fiqh_token'));
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (token) {
      api.getCurrentUser(token)
        .then(userData => {
          setUser(userData);
          setError(null);
        })
        .catch(err => {
          console.error('Auth error:', err);
          localStorage.removeItem('fiqh_token');
          setToken(null);
          setError('انتهت الجلسة. يرجى تسجيل الدخول مرة أخرى.');
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    try {
      const response = await api.login({ email, password });
      localStorage.setItem('fiqh_token', response.token);
      setToken(response.token);
      setUser(response.user);
      setError(null);
      return response;
    } catch (err) {
      setError(err.message || 'فشل تسجيل الدخول');
      throw err;
    }
  };

  const register = async (data) => {
    try {
      const response = await api.register(data);
      localStorage.setItem('fiqh_token', response.token);
      setToken(response.token);
      setUser(response.user);
      setError(null);
      return response;
    } catch (err) {
      setError(err.message || 'فشل التسجيل');
      throw err;
    }
  };

  const logout = () => {
    localStorage.removeItem('fiqh_token');
    setToken(null);
    setUser(null);
    setError(null);
  };

  const refreshUser = async () => {
    if (token) {
      try {
        const updatedUser = await api.getCurrentUser(token);
        setUser(updatedUser);
      } catch (err) {
        console.error('Refresh user error:', err);
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
      updateUser 
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