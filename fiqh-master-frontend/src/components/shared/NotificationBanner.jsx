// src/components/shared/NotificationBanner.jsx

import React from 'react';
import { Bell, X } from 'lucide-react';

const NotificationBanner = ({ onAllow, onDismiss }) => {
  return (
    <div className="fixed top-16 left-4 right-4 z-40 max-w-md mx-auto animate-slide-in">
      <div className="bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-2xl p-4 shadow-2xl">
        <div className="flex items-start gap-3">
          <div className="bg-white/20 p-2 rounded-lg">
            <Bell size={24} />
          </div>
          <div className="flex-1">
            <h3 className="font-bold mb-1">تفعيل التذكيرات</h3>
            <p className="text-sm text-orange-100 mb-3">
              اسمح بإرسال الإشعارات للحصول على تذكير يومي لحل الأسئلة
            </p>
            <div className="flex gap-2">
              <button
                onClick={onAllow}
                className="flex-1 bg-white text-orange-600 py-2 px-4 rounded-lg font-bold hover:bg-orange-50 transition text-sm"
              >
                السماح
              </button>
              <button
                onClick={onDismiss}
                className="bg-white/20 text-white py-2 px-4 rounded-lg font-bold hover:bg-white/30 transition text-sm"
              >
                لاحقاً
              </button>
            </div>
          </div>
          <button
            onClick={onDismiss}
            className="text-white/80 hover:text-white transition"
          >
            <X size={20} />
          </button>
        </div>
      </div>
    </div>
  );
};

export default NotificationBanner;