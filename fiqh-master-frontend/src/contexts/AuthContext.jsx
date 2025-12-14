// src/contexts/AuthContext.jsx
import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';
import { CONFIG } from '../config/constants';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem(CONFIG.STORAGE_KEY));
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (token) {
      api.getCurrentUser(token)
        .then(setUser)
        .catch((err) => {
          console.error('Auth error:', err);
          localStorage.removeItem(CONFIG.STORAGE_KEY);
          setToken(null);
          setError('Session expired. Please login again.');
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const response = await api.login({ email, password });
    localStorage.setItem(CONFIG.STORAGE_KEY, response.token);
    setToken(response.token);
    setUser(response.user);
  };

  const register = async (data) => {
    const response = await api.register(data);
    localStorage.setItem(CONFIG.STORAGE_KEY, response.token);
    setToken(response.token);
    setUser(response.user);
  };

  const logout = () => {
    localStorage.removeItem(CONFIG.STORAGE_KEY);
    setToken(null);
    setUser(null);
  };

  const refreshUser = async () => {
    if (token) {
      const updatedUser = await api.getCurrentUser(token);
      setUser(updatedUser);
    }
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, error, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};