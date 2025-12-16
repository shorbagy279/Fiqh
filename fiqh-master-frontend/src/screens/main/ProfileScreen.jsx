import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { LogOut, ChevronRight, BookOpen, Star, Clock, Award, X, Bell, Globe, Target, Zap, TrendingUp, Calendar } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const ProfileScreen = ({ navigate }) => {
  const { user, logout, updateUser, token } = useAuth();
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);
  const [showDifficultyModal, setShowDifficultyModal] = useState(false);
  const [showRemindersModal, setShowRemindersModal] = useState(false);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.getUserStats(token)
        .then(setStats)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  const handleLogout = () => {
    logout();
    navigate('login');
  };

  const handleDifficultyChange = async (level) => {
    try {
      await api.updateDifficultyLevel(token, level);
      updateUser({ difficultyLevel: level });
      setShowDifficultyModal(false);
    } catch (error) {
      console.error('Error updating difficulty:', error);
    }
  };

  const toggleReminders = async () => {
    try {
      const newValue = !user?.dailyReminders;
      await api.toggleDailyReminder(token, newValue);
      updateUser({ dailyReminders: newValue });
      setShowRemindersModal(false);
    } catch (error) {
      console.error('Error toggling reminders:', error);
    }
  };

  const difficulties = [
    { value: 'beginner', label: 'مبتدئ', description: 'أسئلة سهلة للمبتدئين', color: 'bg-green-100 text-green-600', icon: '🌱' },
    { value: 'intermediate', label: 'متوسط', description: 'أسئلة متوسطة الصعوبة', color: 'bg-yellow-100 text-yellow-600', icon: '📚' },
    { value: 'advanced', label: 'متقدم', description: 'أسئلة صعبة للمتقدمين', color: 'bg-red-100 text-red-600', icon: '🎓' }
  ];

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" message="جاري التحميل..." />
      </div>
    );
  }

  const accuracy = user?.totalAnswers > 0 
    ? Math.round((user.totalCorrectAnswers / user.totalAnswers) * 100) 
    : 0;

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-20 h-20 bg-white/20 backdrop-blur-lg rounded-full flex items-center justify-center text-4xl border-4 border-white/30 shadow-lg">
            👤
          </div>
          <div className="flex-1">
            <h1 className="text-2xl font-bold">{user?.fullName}</h1>
            <p className="text-green-100 text-sm">{user?.email}</p>
            <div className="flex items-center gap-2 mt-2">
              <div className="bg-yellow-500 px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                <Award size={12} />
                {user?.currentRank}
              </div>
              <div className="bg-white/20 px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                <Calendar size={12} />
                عضو منذ {new Date(user?.createdAt).toLocaleDateString('ar-SA', { year: 'numeric', month: 'short' })}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="px-6 -mt-4">
        {/* Stats Summary Cards */}
        <div className="grid grid-cols-2 gap-3 mb-6">
          <StatsCard 
            icon={<BookOpen className="text-blue-600" />}
            label="إجمالي الاختبارات"
            value={user?.totalQuizzes || 0}
            bgColor="bg-blue-50"
          />
          <StatsCard 
            icon={<Star className="text-yellow-600" />}
            label="إجابات صحيحة"
            value={user?.totalCorrectAnswers || 0}
            bgColor="bg-yellow-50"
          />
          <StatsCard 
            icon={<TrendingUp className="text-green-600" />}
            label="نسبة الدقة"
            value={`${accuracy}%`}
            bgColor="bg-green-50"
          />
          <StatsCard 
            icon={<Zap className="text-orange-600" />}
            label="سلسلة الأيام"
            value={`${user?.currentStreak || 0} يوم`}
            bgColor="bg-orange-50"
          />
        </div>

        {/* Detailed Stats */}
        <div className="bg-white rounded-2xl p-5 shadow-lg mb-6">
          <h3 className="font-bold text-lg mb-4 text-gray-800 flex items-center gap-2">
            <TrendingUp size={20} className="text-green-600" />
            إحصائيات مفصلة
          </h3>
          <div className="space-y-3">
            <DetailedStat 
              label="إجمالي الأسئلة المجابة"
              value={user?.totalAnswers || 0}
              icon="📊"
            />
            <DetailedStat 
              label="أطول سلسلة متتالية"
              value={`${user?.longestStreak || 0} يوم`}
              icon="🔥"
            />
            <DetailedStat 
              label="عدد الشارات المكتسبة"
              value={user?.badges?.length || 0}
              icon="🏆"
            />
            {stats?.overallAccuracy && (
              <DetailedStat 
                label="الدقة الإجمالية"
                value={`${Math.round(stats.overallAccuracy)}%`}
                icon="🎯"
              />
            )}
          </div>
        </div>

        {/* Badges Section */}
        {user?.badges && user.badges.length > 0 && (
          <div className="bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl p-6 text-white mb-6 shadow-xl">
            <h3 className="font-bold text-lg mb-4 flex items-center gap-2">
              <Award size={24} />
              شاراتي ({user.badges.length})
            </h3>
            <div className="flex flex-wrap gap-3">
              {user.badges.map((badge, i) => (
                <div key={i} className="bg-white/20 backdrop-blur w-16 h-16 rounded-xl flex items-center justify-center text-4xl border-2 border-white/30 shadow-lg hover:scale-110 transition">
                  {badge}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Settings Section */}
        <div className="space-y-3 mb-6">
          <h3 className="font-bold text-lg text-gray-800 mb-3">الإعدادات</h3>
          
          <SettingItem 
            icon={<Target className="text-purple-600" />}
            label="مستوى الصعوبة" 
            value={
              user?.difficultyLevel === 'beginner' ? 'مبتدئ' :
              user?.difficultyLevel === 'intermediate' ? 'متوسط' :
              user?.difficultyLevel === 'advanced' ? 'متقدم' : 'متوسط'
            }
            onClick={() => setShowDifficultyModal(true)}
            bgColor="bg-purple-50"
          />
          
          <SettingItem 
            icon={<Bell className={user?.dailyReminders ? 'text-green-600' : 'text-gray-400'} />}
            label="التذكيرات اليومية" 
            value={user?.dailyReminders ? "مفعّل" : "معطّل"}
            onClick={() => setShowRemindersModal(true)}
            bgColor={user?.dailyReminders ? "bg-green-50" : "bg-gray-50"}
          />
          
          <SettingItem 
            icon={<Globe className="text-blue-600" />}
            label="اللغة المفضلة" 
            value={user?.preferredLanguage === 'ar' ? 'العربية' : 'English'}
            onClick={() => alert('قريباً: تغيير اللغة')}
            bgColor="bg-blue-50"
          />
          
          <SettingItem 
            icon={<BookOpen className="text-indigo-600" />}
            label="المرجع الديني" 
            value={user?.preferredMarjaName || "السيد السيستاني"}
            onClick={() => alert('قريباً: اختيار المرجع الديني')}
            bgColor="bg-indigo-50"
          />
        </div>

        {/* Quick Actions */}
        <div className="space-y-3 mb-6">
          <h3 className="font-bold text-lg text-gray-800 mb-3">إجراءات سريعة</h3>
          
          <ActionButton
            icon="📈"
            label="الإحصائيات التفصيلية"
            onClick={() => navigate('stats')}
            color="bg-gradient-to-r from-blue-500 to-blue-600"
          />
          
          <ActionButton
            icon="⭐"
            label="الأسئلة المحفوظة"
            onClick={() => navigate('bookmarks')}
            color="bg-gradient-to-r from-pink-500 to-pink-600"
          />
          
          <ActionButton
            icon="🏆"
            label="لوحة المتصدرين"
            onClick={() => navigate('leaderboard')}
            color="bg-gradient-to-r from-yellow-500 to-orange-500"
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
                  <div className="text-3xl">{diff.icon}</div>
                  <div className="flex-1">
                    <div className={`${diff.color} px-3 py-1 rounded-full text-sm font-bold inline-block mb-2`}>
                      {diff.label}
                    </div>
                    <p className="text-sm text-gray-600">{diff.description}</p>
                  </div>
                  {user?.difficultyLevel === diff.value && (
                    <div className="bg-green-500 text-white w-8 h-8 rounded-full flex items-center justify-center font-bold shadow-md">
                      ✓
                    </div>
                  )}
                </div>
              </button>
            ))}
          </div>
        </Modal>
      )}

      {/* Reminders Modal */}
      {showRemindersModal && (
        <Modal onClose={() => setShowRemindersModal(false)} title="التذكيرات اليومية">
          <div className="space-y-4">
            <div className="bg-blue-50 border-2 border-blue-200 rounded-xl p-4">
              <p className="text-sm text-blue-800 text-right leading-relaxed">
                📱 احصل على تذكير يومي لحل الأسئلة وزيادة معرفتك الفقهية والحفاظ على سلسلة أيامك
              </p>
            </div>
            
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
                  <div className="text-right">
                    <div className="font-bold text-gray-800 mb-1">
                      {user?.dailyReminders ? '✅ التذكيرات مفعّلة' : '⭕ التذكيرات معطّلة'}
                    </div>
                    <p className="text-sm text-gray-600">
                      {user?.dailyReminders 
                        ? 'ستتلقى تذكيراً يومياً في الساعة 9 صباحاً' 
                        : 'اضغط لتفعيل التذكيرات اليومية'}
                    </p>
                  </div>
                  <div className={`w-14 h-7 rounded-full transition-all flex-shrink-0 mr-4 ${
                    user?.dailyReminders ? 'bg-green-500' : 'bg-gray-300'
                  }`}>
                    <div className={`w-5 h-5 bg-white rounded-full mt-1 transition-all shadow-md ${
                      user?.dailyReminders ? 'mr-8' : 'mr-1'
                    }`}></div>
                  </div>
                </div>
              </button>
            </div>

            {user?.dailyReminders && (
              <div className="bg-gradient-to-r from-green-50 to-teal-50 border-2 border-green-200 rounded-xl p-4">
                <p className="text-sm text-green-800 text-center font-bold">
                  🎯 رائع! ستساعدك التذكيرات على الحفاظ على سلسلة أيامك
                </p>
              </div>
            )}
          </div>
        </Modal>
      )}

      {/* Logout Confirmation Modal */}
      {showLogoutConfirm && (
        <Modal onClose={() => setShowLogoutConfirm(false)} title="تأكيد تسجيل الخروج">
          <p className="text-gray-600 mb-6 text-right leading-relaxed">
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

const StatsCard = ({ icon, label, value, bgColor }) => (
  <div className={`${bgColor} rounded-xl p-4 shadow-md`}>
    <div className="flex items-center gap-2 mb-2">
      {icon}
    </div>
    <p className="text-2xl font-bold text-gray-800 mb-1">{value}</p>
    <p className="text-xs text-gray-600">{label}</p>
  </div>
);

const DetailedStat = ({ label, value, icon }) => (
  <div className="flex justify-between items-center py-2 border-b border-gray-100 last:border-0">
    <div className="flex items-center gap-2">
      <span className="text-xl">{icon}</span>
      <span className="text-gray-700">{label}</span>
    </div>
    <span className="font-bold text-gray-800">{value}</span>
  </div>
);

const SettingItem = ({ icon, label, value, onClick, bgColor }) => (
  <button 
    onClick={onClick}
    className={`w-full ${bgColor} rounded-xl p-4 flex items-center gap-4 shadow-md hover:shadow-lg transition-all active:scale-98`}
  >
    <div className="p-3 rounded-xl bg-white shadow-sm">
      {icon}
    </div>
    <div className="flex-1 text-right">
      <p className="font-bold text-gray-800">{label}</p>
      <p className="text-sm text-gray-600">{value}</p>
    </div>
    <ChevronRight className="text-gray-400" />
  </button>
);

const ActionButton = ({ icon, label, onClick, color }) => (
  <button
    onClick={onClick}
    className={`w-full ${color} text-white rounded-xl p-4 shadow-lg hover:shadow-xl transition-all active:scale-98 flex items-center gap-3`}
  >
    <span className="text-3xl">{icon}</span>
    <span className="font-bold text-lg flex-1 text-right">{label}</span>
    <ChevronRight size={24} />
  </button>
);

const Modal = ({ children, onClose, title }) => (
  <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-6 z-50">
    <div className="bg-white rounded-2xl p-6 max-w-sm w-full shadow-2xl animate-slide-in">
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