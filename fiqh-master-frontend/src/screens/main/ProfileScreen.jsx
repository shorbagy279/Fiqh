import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { LogOut, ChevronRight, BookOpen, Star, Clock, Award, X } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';

const ProfileScreen = ({ navigate }) => {
  const { user, logout, updateUser } = useAuth();
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const [showDifficultyModal, setShowDifficultyModal] = useState(false);
  const [showRemindersModal, setShowRemindersModal] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('login');
  };

  const handleDifficultyChange = (level) => {
    updateUser({ difficultyLevel: level });
    setShowDifficultyModal(false);
  };

  const toggleReminders = () => {
    updateUser({ dailyReminders: !user?.dailyReminders });
    setShowRemindersModal(false);
  };

  const difficulties = [
    { value: 'beginner', label: 'مبتدئ', description: 'أسئلة سهلة للمبتدئين', color: 'bg-green-100 text-green-600' },
    { value: 'intermediate', label: 'متوسط', description: 'أسئلة متوسطة الصعوبة', color: 'bg-yellow-100 text-yellow-600' },
    { value: 'advanced', label: 'متقدم', description: 'أسئلة صعبة للمتقدمين', color: 'bg-red-100 text-red-600' }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-20 h-20 bg-white/20 backdrop-blur-lg rounded-full flex items-center justify-center text-4xl border-2 border-white/30 shadow-lg">
            👤
          </div>
          <div className="flex-1">
            <h1 className="text-2xl font-bold">{user?.fullName}</h1>
            <p className="text-green-100 text-sm">{user?.email}</p>
          </div>
        </div>
      </div>

      <div className="px-6 -mt-4">
        {/* Stats Summary */}
        <div className="bg-white rounded-2xl p-5 shadow-lg mb-6">
          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-3xl font-bold text-green-600">{user?.totalQuizzes || 0}</p>
              <p className="text-xs text-gray-600 mt-1">اختبار</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-blue-600">{user?.currentStreak || 0}</p>
              <p className="text-xs text-gray-600 mt-1">يوم متتالي</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-purple-600">{user?.badges?.length || 0}</p>
              <p className="text-xs text-gray-600 mt-1">شارة</p>
            </div>
          </div>
        </div>

        {/* Settings */}
        <div className="space-y-3 mb-6">
          <SettingItem 
            icon={<BookOpen />} 
            label="المرجع الديني" 
            value={user?.preferredMarjaName || "السيد السيستاني"}
            onClick={() => alert('قريباً: اختيار المرجع الديني')}
          />
          <SettingItem 
            icon={<Star />} 
            label="مستوى الصعوبة" 
            value={
              user?.difficultyLevel === 'beginner' ? 'مبتدئ' :
              user?.difficultyLevel === 'intermediate' ? 'متوسط' :
              user?.difficultyLevel === 'advanced' ? 'متقدم' : 'متوسط'
            }
            onClick={() => setShowDifficultyModal(true)}
          />
          <SettingItem 
            icon={<Clock />} 
            label="التذكيرات اليومية" 
            value={user?.dailyReminders ? "مفعّل" : "معطّل"}
            onClick={() => setShowRemindersModal(true)}
          />
          <SettingItem 
            icon={<Award />} 
            label="الإنجازات" 
            value={`${user?.badges?.length || 0} شارة`}
            onClick={() => alert('قريباً: عرض جميع الإنجازات')}
          />
        </div>

        {/* Logout Button */}
        <div className="space-y-3">
          <button 
            onClick={() => setShowLogoutConfirm(true)}
            className="w-full bg-white rounded-xl p-4 shadow-lg text-red-600 font-bold hover:bg-red-50 transition-all flex items-center justify-center gap-2 border-2 border-red-200 active:scale-98"
          >
            <LogOut size={20} />
            تسجيل الخروج
          </button>
        </div>
      </div>

      {/* Difficulty Modal */}
      {showDifficultyModal && (
        <Modal onClose={() => setShowDifficultyModal(false)} title="مستوى الصعوبة">
          <div className="space-y-3">
            {difficulties.map((diff) => (
              <button
                key={diff.value}
                onClick={() => handleDifficultyChange(diff.value)}
                className={`w-full p-4 rounded-xl text-right transition-all ${
                  user?.difficultyLevel === diff.value
                    ? 'bg-green-50 border-2 border-green-500'
                    : 'bg-gray-50 border-2 border-gray-200 hover:border-green-300'
                }`}
              >
                <div className="flex items-center gap-3">
                  <div className={`${diff.color} px-3 py-1 rounded-full text-sm font-bold`}>
                    {diff.label}
                  </div>
                  {user?.difficultyLevel === diff.value && (
                    <div className="mr-auto bg-green-500 text-white w-6 h-6 rounded-full flex items-center justify-center">
                      ✓
                    </div>
                  )}
                </div>
                <p className="text-sm text-gray-600 mt-2">{diff.description}</p>
              </button>
            ))}
          </div>
        </Modal>
      )}

      {/* Reminders Modal */}
      {showRemindersModal && (
        <Modal onClose={() => setShowRemindersModal(false)} title="التذكيرات اليومية">
          <div className="space-y-4">
            <p className="text-gray-600 text-right">
              احصل على تذكير يومي لحل الأسئلة وزيادة معرفتك الفقهية
            </p>
            <div className="space-y-3">
              <button
                onClick={toggleReminders}
                className={`w-full p-4 rounded-xl text-right transition-all ${
                  user?.dailyReminders
                    ? 'bg-green-50 border-2 border-green-500'
                    : 'bg-gray-50 border-2 border-gray-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-gray-800">
                    {user?.dailyReminders ? 'مفعّل ✓' : 'معطّل'}
                  </span>
                  <div className={`w-12 h-6 rounded-full transition-all ${
                    user?.dailyReminders ? 'bg-green-500' : 'bg-gray-300'
                  }`}>
                    <div className={`w-5 h-5 bg-white rounded-full mt-0.5 transition-all ${
                      user?.dailyReminders ? 'mr-6' : 'mr-0.5'
                    }`}></div>
                  </div>
                </div>
              </button>
            </div>
          </div>
        </Modal>
      )}

      {/* Logout Confirmation Modal */}
      {showLogoutConfirm && (
        <Modal onClose={() => setShowLogoutConfirm(false)} title="تأكيد تسجيل الخروج">
          <p className="text-gray-600 mb-6 text-right">
            هل أنت متأكد من رغبتك في تسجيل الخروج؟
          </p>
          <div className="flex gap-3">
            <button
              onClick={() => setShowLogoutConfirm(false)}
              className="flex-1 bg-gray-100 text-gray-700 py-3 rounded-xl font-bold hover:bg-gray-200 transition active:scale-98"
            >
              إلغاء
            </button>
            <button
              onClick={handleLogout}
              className="flex-1 bg-red-600 text-white py-3 rounded-xl font-bold hover:bg-red-700 transition active:scale-98"
            >
              تسجيل الخروج
            </button>
          </div>
        </Modal>
      )}

      <BottomNav currentScreen="profile" navigate={navigate} />
    </div>
  );
};

const SettingItem = ({ icon, label, value, onClick }) => (
  <button 
    onClick={onClick}
    className="w-full bg-white rounded-xl p-4 flex items-center gap-4 shadow-lg hover:shadow-xl transition-all active:scale-98"
  >
    <div className="text-green-600 bg-green-50 p-3 rounded-xl">
      {icon}
    </div>
    <div className="flex-1 text-right">
      <p className="font-bold text-gray-800">{label}</p>
      <p className="text-sm text-gray-500">{value}</p>
    </div>
    <ChevronRight className="text-gray-400" />
  </button>
);

const Modal = ({ children, onClose, title }) => (
  <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-6 z-50">
    <div className="bg-white rounded-2xl p-6 max-w-sm w-full shadow-2xl">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xl font-bold text-gray-800">{title}</h3>
        <button
          onClick={onClose}
          className="text-gray-400 hover:text-gray-600 transition p-1"
        >
          <X size={24} />
        </button>
      </div>
      {children}
    </div>
  </div>
);

export default ProfileScreen;