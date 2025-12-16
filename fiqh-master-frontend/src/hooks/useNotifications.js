// src/hooks/useNotifications.js

import { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import notificationService from '../services/notificationService';

export const useNotifications = () => {
  const { user } = useAuth();
  const [permissionGranted, setPermissionGranted] = useState(
    Notification.permission === 'granted'
  );
  const [showBanner, setShowBanner] = useState(false);

  useEffect(() => {
    if (user?.dailyReminders && !permissionGranted) {
      // Show banner to request permission
      setShowBanner(true);
    }
  }, [user?.dailyReminders, permissionGranted]);

  useEffect(() => {
    if (user && permissionGranted && user.dailyReminders) {
      // Schedule reminder checks
      notificationService.scheduleReminderCheck(user);
    }
  }, [user, permissionGranted]);

  const requestPermission = async () => {
    const granted = await notificationService.requestPermission();
    setPermissionGranted(granted);
    if (granted) {
      setShowBanner(false);
    }
    return granted;
  };

  const dismissBanner = () => {
    setShowBanner(false);
  };

  return {
    permissionGranted,
    showBanner,
    requestPermission,
    dismissBanner
  };
};