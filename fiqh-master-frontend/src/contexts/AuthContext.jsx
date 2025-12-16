import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import api, { cache } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('fiqh_token'));
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const clearAuth = useCallback(() => {
    localStorage.removeItem('fiqh_token');
    cache.clear();
    setToken(null);
    setUser(null);
    setError(null);
  }, []);

  // Listen for auth logout events from API
  useEffect(() => {
    const handleAuthLogout = () => {
      clearAuth();
    };

    window.addEventListener('auth:logout', handleAuthLogout);
    return () => window.removeEventListener('auth:logout', handleAuthLogout);
  }, [clearAuth]);

  // Load user data on mount
  useEffect(() => {
    const loadUser = async () => {
      if (!token) {
        setLoading(false);
        return;
      }

      try {
        // Try to get from cache first
        const cachedUser = cache.get('current_user');
        if (cachedUser) {
          setUser(cachedUser);
          setError(null);
          setLoading(false);
          // Refresh in background
          api.getCurrentUser(token)
            .then(userData => {
              setUser(userData);
              cache.set('current_user', userData);
            })
            .catch(console.error);
          return;
        }

        // Fetch from API
        const userData = await api.getCurrentUser(token);
        setUser(userData);
        cache.set('current_user', userData);
        setError(null);
      } catch (err) {
        console.error('Auth error:', err);
        if (err.status === 401) {
          clearAuth();
          setError('انتهت الجلسة. يرجى تسجيل الدخول مرة أخرى.');
        } else {
          setError('فشل تحميل بيانات المستخدم');
        }
      } finally {
        setLoading(false);
      }
    };

    loadUser();
  }, [token, clearAuth]);

  const login = async (email, password) => {
    try {
      setError(null);
      setLoading(true);
      const response = await api.login({ email, password });
      localStorage.setItem('fiqh_token', response.token);
      setToken(response.token);
      setUser(response.user);
      cache.set('current_user', response.user);
      return response;
    } catch (err) {
      const errorMsg = err.message || 'فشل تسجيل الدخول';
      setError(errorMsg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const register = async (data) => {
    try {
      setError(null);
      setLoading(true);
      const response = await api.register(data);
      localStorage.setItem('fiqh_token', response.token);
      setToken(response.token);
      setUser(response.user);
      cache.set('current_user', response.user);
      return response;
    } catch (err) {
      const errorMsg = err.message || 'فشل التسجيل';
      setError(errorMsg);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const logout = useCallback(() => {
    clearAuth();
    setError(null);
  }, [clearAuth]);

  const refreshUser = async () => {
    if (!token || isRefreshing) return;

    setIsRefreshing(true);
    try {
      const updatedUser = await api.getCurrentUser(token);
      setUser(updatedUser);
      cache.set('current_user', updatedUser);
      setError(null);
    } catch (err) {
      console.error('Refresh user error:', err);
      if (err.status === 401) {
        clearAuth();
      }
    } finally {
      setIsRefreshing(false);
    }
  };

  const updateUser = useCallback((updates) => {
    setUser(prev => {
      const updated = { ...prev, ...updates };
      cache.set('current_user', updated);
      return updated;
    });
  }, []);

  const updateProfile = async (data) => {
    try {
      const updatedUser = await api.updateUserProfile(token, data);
      setUser(updatedUser);
      cache.set('current_user', updatedUser);
      return updatedUser;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const updateSettings = async (setting, value) => {
    try {
      let response;
      switch (setting) {
        case 'difficulty':
          response = await api.updateDifficultyLevel(token, value);
          break;
        case 'reminders':
          response = await api.toggleDailyReminder(token, value);
          break;
        case 'language':
          response = await api.updatePreferredLanguage(token, value);
          break;
        case 'marja':
          response = await api.updatePreferredMarja(token, value);
          break;
        default:
          throw new Error('Invalid setting');
      }
      await refreshUser();
      return response;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const value = {
    user,
    token,
    loading,
    error,
    isRefreshing,
    login,
    register,
    logout,
    refreshUser,
    updateUser,
    updateProfile,
    updateSettings,
    clearError: () => setError(null),
    isAuthenticated: !!token && !!user
  };

  return (
    <AuthContext.Provider value={value}>
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

export default AuthContext;