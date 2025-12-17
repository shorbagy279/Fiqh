
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Trophy, BookOpen, Sparkles, Zap, Target, Star, TrendingUp, Award } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import StreakDisplay from '../../components/shared/StreakDisplay';
import QuickModeCard from '../../components/cards/QuickModeCard';
import LoadingSpinner from '../../components/shared/LoadingSpinner';
import QuizOptionsModal from '../../components/modals/QuizOptionsModal';
import NotificationBanner from '../../components/shared/NotificationBanner';
import { useNotifications } from '../../hooks/useNotifications';

const HomeScreen = ({ navigate }) => {
  const { user, token } = useAuth();
  const [categories, setCategories] = useState([]);
  const [categoryProgress, setCategoryProgress] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showQuizOptions, setShowQuizOptions] = useState(false);
  const [selectedQuizType, setSelectedQuizType] = useState(null);
  
  const { 
    showBanner: showNotificationBanner, 
    requestPermission, 
    dismissBanner 
  } = useNotifications();

  useEffect(() => {
    if (token) {
      Promise.all([
        api.getCategories(token),
        api.getCategoryProgress(token)
      ])
        .then(([categoriesData, progressData]) => {
          setCategories(categoriesData);
          setCategoryProgress(progressData.categories || []);
        })
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'صباح الخير';
    if (hour < 18) return 'مساء الخير';
    return 'مساء الخير';
  };

  const openQuizOptions = (type) => {
    setSelectedQuizType(type);
    setShowQuizOptions(true);
  };

  const handleStartQuiz = (options) => {
    setShowQuizOptions(false);
    navigate('quiz', options);
  };

  const handleAllowNotifications = async () => {
    const granted = await requestPermission();
    if (granted) {
      // You can show a success message here
      console.log('Notifications enabled');
    }
  };

  const accuracy = user?.totalAnswers > 0 
    ? Math.round((user.totalCorrectAnswers / user.totalAnswers) * 100) 
    : 0;

  // Calculate next rank progress
  const getNextRankProgress = () => {
    const quizzes = user?.totalQuizzes || 0;
    if (quizzes >= 100) return { current: 100, target: 200, percentage: 50 };
    if (quizzes >= 50) return { current: quizzes, target: 100, percentage: ((quizzes - 50) / 50) * 100 };
    if (quizzes >= 20) return { current: quizzes, target: 50, percentage: ((quizzes - 20) / 30) * 100 };
    if (quizzes >= 10) return { current: quizzes, target: 20, percentage: ((quizzes - 10) / 10) * 100 };
    return { current: quizzes, target: 10, percentage: (quizzes / 10) * 100 };
  };

  const rankProgress = getNextRankProgress();

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Notification Permission Banner */}
      {showNotificationBanner && (
        <NotificationBanner 
          onAllow={handleAllowNotifications}
          onDismiss={dismissBanner}
        />
      )}

      {/* Header with gradient */}
      <div className="bg-gradient-to-br from-green-600 via-green-700 to-green-800 text-white p-6 rounded-b-[2.5rem] shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-2xl font-bold mb-1 flex items-center gap-2">
              {getGreeting()} 👋
            </h1>
            <p className="text-green-100 font-medium">{user?.fullName || 'مستخدم'}</p>
            <div className="flex items-center gap-2 mt-2">
              <div className="bg-yellow-400 text-yellow-900 px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                <Award size={12} />
                {user?.currentRank}
              </div>
            </div>
          </div>
          <div className="bg-white/10 backdrop-blur-lg p-3 rounded-2xl border border-white/20">
            <BookOpen size={32} className="text-white" />
          </div>
        </div>
        
        <StreakDisplay streak={user?.currentStreak || 0} />
      </div>

      <div className="px-6 -mt-6">
        {/* Daily Challenge Card */}
        <div className="bg-gradient-to-r from-yellow-400 via-orange-500 to-red-500 rounded-2xl p-6 text-white mb-6 shadow-xl transform hover:scale-[1.02] transition-transform">
          <div className="flex items-start justify-between mb-4">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <Trophy size={28} />
                <h3 className="font-bold text-xl">التحدي اليومي</h3>
              </div>
              <p className="text-yellow-100 text-sm flex items-center gap-2">
                <Zap size={16} />
                احصل على نقاط مضاعفة
              </p>
            </div>
            <div className="bg-white/20 backdrop-blur-lg p-2 rounded-xl">
              <Sparkles size={24} />
            </div>
          </div>
          <button 
            onClick={() => openQuizOptions('daily')}
            className="bg-white text-orange-600 w-full py-3.5 rounded-xl font-bold hover:bg-orange-50 transition-all shadow-lg flex items-center justify-center gap-2"
          >
            <Target size={20} />
            ابدأ التحدي الآن
          </button>
        </div>

        {/* Quick Actions Section */}
        <div className="mb-6">
          <h2 className="text-xl font-bold mb-4 flex items-center gap-2 text-gray-800">
            <BookOpen size={24} className="text-green-600" />
            اختر طريقة التعلم
          </h2>

          
<div className="space-y-3">
  <QuickModeCard
    title="اختبار عشوائي"
    description="أسئلة متنوعة من جميع الأقسام"
    icon="🎲"
    color="bg-gradient-to-br from-blue-500 to-blue-600"
    onClick={() => openQuizOptions('random')}
  />
  <QuickModeCard
    title="اختر قسماً محدداً"
    description="تدرب على موضوع معين"
    icon="📚"
    color="bg-gradient-to-br from-purple-500 to-purple-600"
    onClick={() => navigate('categories')}
  />
  <QuickModeCard
    title="اختبار مخصص متقدم"
    description="اختر الأسئلة يدوياً ووقت الاختبار"
    icon="⚙️"
    color="bg-gradient-to-br from-indigo-500 to-indigo-600"
    onClick={() => navigate('customQuiz')}
  />
  <QuickModeCard
    title="انضم لاختبار مجدول"
    description="أدخل كود الاختبار للانضمام"
    icon="🎫"
    color="bg-gradient-to-br from-teal-500 to-cyan-600"
    onClick={() => navigate('joinExam')}
  />
  <QuickModeCard
    title="الأسئلة المحفوظة"
    description="راجع الأسئلة الصعبة"
    icon="⭐"
    color="bg-gradient-to-br from-pink-500 to-pink-600"
    onClick={() => navigate('bookmarks')}
  />
  <QuickModeCard
    title="لوحة المتصدرين"
    description="تنافس مع المستخدمين"
    icon="🏆"
    color="bg-gradient-to-br from-yellow-500 to-orange-500"
    onClick={() => navigate('leaderboard')}
  />
</div>
        </div>

        {/* Enhanced Stats Preview with Real Data */}
        {user && (
          <div className="bg-white rounded-2xl p-6 shadow-lg mb-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-lg text-gray-800 flex items-center gap-2">
                <TrendingUp size={20} className="text-green-600" />
                إحصائياتك السريعة
              </h3>
              <button
                onClick={() => navigate('stats')}
                className="text-green-600 text-sm font-bold hover:text-green-700"
              >
                عرض الكل ←
              </button>
            </div>
            
            <div className="grid grid-cols-2 gap-4 mb-4">
              <MiniStatCard
                icon={<Trophy className="text-green-600" />}
                value={user.totalQuizzes || 0}
                label="اختبار"
                bgColor="bg-green-50"
              />
              <MiniStatCard
                icon={<Star className="text-blue-600" />}
                value={user.totalCorrectAnswers || 0}
                label="إجابة صحيحة"
                bgColor="bg-blue-50"
              />
              <MiniStatCard
                icon={<Target className="text-purple-600" />}
                value={`${accuracy}%`}
                label="دقة"
                bgColor="bg-purple-50"
              />
              <MiniStatCard
                icon={<Zap className="text-orange-600" />}
                value={`${user.currentStreak || 0}`}
                label="يوم متتالي"
                bgColor="bg-orange-50"
              />
            </div>

            {/* Progress Bar with Real Data */}
            <div className="bg-gray-100 rounded-xl p-4">
              <div className="flex justify-between items-center mb-2">
                <span className="text-sm text-gray-600">التقدم نحو الرتبة التالية</span>
                <span className="text-sm font-bold text-gray-800">
                  {rankProgress.current}/{rankProgress.target}
                </span>
              </div>
              <div className="bg-gray-200 rounded-full h-2 overflow-hidden">
                <div
                  className="bg-gradient-to-r from-green-500 to-green-600 h-full rounded-full transition-all duration-500"
                  style={{ width: `${Math.min(rankProgress.percentage, 100)}%` }}
                ></div>
              </div>
            </div>

            {/* Badges Preview */}
            {user.badges && user.badges.length > 0 && (
              <div className="mt-4 pt-4 border-t border-gray-100">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-bold text-gray-700">الشارات المكتسبة</span>
                  <span className="text-xs text-gray-500">{user.badges.length} شارة</span>
                </div>
                <div className="flex gap-2 overflow-x-auto pb-2">
                  {user.badges.slice(0, 5).map((badge, i) => (
                    <div
                      key={i}
                      className="bg-gradient-to-br from-yellow-100 to-orange-100 w-12 h-12 rounded-xl flex items-center justify-center text-2xl flex-shrink-0 border-2 border-yellow-300 shadow-sm"
                    >
                      {badge}
                    </div>
                  ))}
                  {user.badges.length > 5 && (
                    <div className="bg-gray-100 w-12 h-12 rounded-xl flex items-center justify-center text-xs font-bold text-gray-600 flex-shrink-0">
                      +{user.badges.length - 5}
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Real Category Progress */}
        {loading ? (
          <div className="flex justify-center py-8">
            <LoadingSpinner message="جاري تحميل التقدم..." />
          </div>
        ) : categoryProgress.length > 0 && (
          <div className="bg-white rounded-2xl p-6 shadow-lg mb-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-bold text-lg text-gray-800">تقدمك في الأقسام</h3>
              <button
                onClick={() => navigate('stats')}
                className="text-green-600 text-sm font-bold hover:text-green-700"
              >
                التفاصيل ←
              </button>
            </div>
            <div className="space-y-3">
              {categoryProgress.slice(0, 5).map((cat) => (
                <CategoryProgressCard
                  key={cat.categoryId}
                  name={cat.categoryName}
                  icon={cat.icon}
                  progress={cat.progress}
                  accuracy={cat.accuracy}
                  questionsAnswered={cat.questionsAnswered}
                  totalQuestions={cat.totalQuestions}
                  color={cat.color || 'bg-green-500'}
                />
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Quiz Options Modal */}
      {showQuizOptions && (
        <QuizOptionsModal
          onClose={() => setShowQuizOptions(false)}
          onStart={handleStartQuiz}
          categories={categories}
          type={selectedQuizType}
        />
      )}

      <BottomNav currentScreen="home" navigate={navigate} />
    </div>
  );
};

const MiniStatCard = ({ icon, value, label, bgColor }) => (
  <div className={`${bgColor} rounded-xl p-3 flex flex-col items-center justify-center`}>
    <div className="mb-2">{icon}</div>
    <p className="text-2xl font-bold text-gray-800 mb-1">{value}</p>
    <p className="text-xs text-gray-600 text-center">{label}</p>
  </div>
);

const CategoryProgressCard = ({ name, icon, progress, accuracy, questionsAnswered, totalQuestions, color }) => (
  <div className="bg-gray-50 rounded-xl p-4">
    <div className="flex items-center gap-3 mb-3">
      <div className={`${color} w-12 h-12 rounded-xl flex items-center justify-center text-2xl shadow-sm flex-shrink-0`}>
        {icon}
      </div>
      <div className="flex-1">
        <h3 className="font-bold text-gray-800">{name}</h3>
        <p className="text-xs text-gray-600">
          {questionsAnswered} من {totalQuestions} سؤال • دقة {accuracy}%
        </p>
      </div>
      <div className="text-right">
        <div className="text-2xl font-bold text-gray-800">{progress}%</div>
      </div>
    </div>
    <div className="bg-gray-200 rounded-full h-2.5 overflow-hidden">
      <div 
        className={`${color} h-full rounded-full transition-all duration-500`} 
        style={{width: `${progress}%`}}
      ></div>
    </div>
  </div>
);

export default HomeScreen;