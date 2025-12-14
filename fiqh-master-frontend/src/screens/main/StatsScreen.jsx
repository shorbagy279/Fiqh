import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Trophy, Check, Star, Clock, Award, TrendingUp } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import StatCard from '../../components/cards/StatCard';
import CategoryProgress from '../../components/cards/CategoryProgress';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

export const StatsScreen = ({ navigate }) => {
  const { user, token } = useAuth();
  const [stats, setStats] = useState(null);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      Promise.all([
        api.getUserStats(token),
        api.getCategories(token)
      ])
        .then(([statsData, categoriesData]) => {
          setStats(statsData);
          setCategories(categoriesData);
        })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" message="جاري تحميل الإحصائيات..." />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-2">إحصائياتك</h1>
        <p className="text-green-100">تابع تقدمك في تعلم الفقه</p>
      </div>

      <div className="px-6 -mt-6">
        {/* Rank Card */}
        <div className="bg-gradient-to-r from-purple-500 via-pink-500 to-red-500 rounded-2xl p-6 text-white mb-6 shadow-xl">
          <div className="flex items-center justify-between mb-4">
            <div className="flex-1">
              <h3 className="text-lg font-bold mb-1">الرتبة الحالية</h3>
              <p className="text-3xl font-bold mt-2">{user?.currentRank || 'فقيه مبتدئ'}</p>
              <div className="flex items-center gap-2 mt-2 text-sm">
                <TrendingUp size={16} />
                <span>استمر في التقدم!</span>
              </div>
            </div>
            <Award size={64} className="opacity-90" />
          </div>
          <div className="flex gap-2">
            {user?.badges?.map((badge, i) => (
              <div key={i} className="bg-white/20 backdrop-blur w-14 h-14 rounded-xl flex items-center justify-center text-3xl border border-white/30 shadow-lg">
                {badge}
              </div>
            ))}
            {(!user?.badges || user.badges.length === 0) && (
              <p className="text-white/80 text-sm">لم تحصل على شارات بعد</p>
            )}
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-4 mb-6">
          <StatCard 
            icon={<Trophy />} 
            label="إجمالي الاختبارات" 
            value={stats?.totalQuizzes || 0} 
            color="bg-blue-500" 
          />
          <StatCard 
            icon={<Check />} 
            label="إجابات صحيحة" 
            value={stats?.totalCorrectAnswers || 0} 
            color="bg-green-500" 
          />
          <StatCard 
            icon={<Star />} 
            label="النسبة الكلية" 
            value={`${Math.round(stats?.overallAccuracy || 0)}%`} 
            color="bg-yellow-500" 
          />
          <StatCard 
            icon={<Clock />} 
            label="سلسلة الأيام" 
            value={`${stats?.currentStreak || 0} يوم`} 
            color="bg-red-500" 
          />
        </div>

        {/* Performance by Category */}
        <div className="bg-white rounded-2xl p-5 shadow-lg mb-6">
          <h2 className="text-xl font-bold mb-4 text-gray-800 flex items-center gap-2">
            <TrendingUp size={24} className="text-green-600" />
            الأداء حسب القسم
          </h2>
          <div className="space-y-3">
            {categories.slice(0, 6).map(cat => (
              <CategoryProgress
                key={cat.id}
                name={cat.nameAr}
                icon={cat.icon}
                progress={Math.floor(Math.random() * 40 + 40)} // Replace with real data
                color={cat.color || 'bg-green-500'}
              />
            ))}
          </div>
        </div>

        {/* Achievements Section */}
        <div className="bg-white rounded-2xl p-5 shadow-lg mb-6">
          <h2 className="text-xl font-bold mb-4 text-gray-800">إنجازات قريبة</h2>
          <div className="space-y-3">
            <AchievementItem 
              icon="🎯"
              title="أول 10 اختبارات"
              current={stats?.totalQuizzes || 0}
              target={10}
            />
            <AchievementItem 
              icon="🔥"
              title="سلسلة 7 أيام"
              current={stats?.currentStreak || 0}
              target={7}
            />
            <AchievementItem 
              icon="⭐"
              title="100 إجابة صحيحة"
              current={stats?.totalCorrectAnswers || 0}
              target={100}
            />
          </div>
        </div>
      </div>

      <BottomNav currentScreen="stats" navigate={navigate} />
    </div>
  );
};

const AchievementItem = ({ icon, title, current, target }) => {
  const progress = Math.min((current / target) * 100, 100);
  return (
    <div className="bg-gray-50 rounded-xl p-4">
      <div className="flex items-center gap-3 mb-2">
        <span className="text-3xl">{icon}</span>
        <div className="flex-1">
          <h3 className="font-bold text-gray-800">{title}</h3>
          <p className="text-sm text-gray-600">{current} / {target}</p>
        </div>
      </div>
      <div className="bg-gray-200 rounded-full h-2">
        <div 
          className="bg-gradient-to-r from-green-500 to-green-600 h-2 rounded-full transition-all duration-500"
          style={{width: `${progress}%`}}
        ></div>
      </div>
    </div>
  );
};
