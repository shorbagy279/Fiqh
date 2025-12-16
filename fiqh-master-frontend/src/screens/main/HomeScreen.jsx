import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Trophy, BookOpen, Sparkles, Zap, Target, Star } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import StreakDisplay from '../../components/shared/StreakDisplay';
import QuickModeCard from '../../components/cards/QuickModeCard';
import LoadingSpinner from '../../components/shared/LoadingSpinner';
import QuizOptionsModal from '../../components/modals/QuizOptionsModal';

const HomeScreen = ({ navigate }) => {
  const { user, token } = useAuth();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showQuizOptions, setShowQuizOptions] = useState(false);
  const [selectedQuizType, setSelectedQuizType] = useState(null);

  useEffect(() => {
    if (token) {
      api.getCategories(token)
        .then(setCategories)
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

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header with gradient */}
      <div className="bg-gradient-to-br from-green-600 via-green-700 to-green-800 text-white p-6 rounded-b-[2.5rem] shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-2xl font-bold mb-1 flex items-center gap-2">
              {getGreeting()} 👋
            </h1>
            <p className="text-green-100 font-medium">{user?.fullName || 'مستخدم'}</p>
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
              title="اختبار مخصص"
              description="اختر الأقسام والأسئلة حسب رغبتك"
              icon="⚙️"
              color="bg-gradient-to-br from-indigo-500 to-indigo-600"
              onClick={() => openQuizOptions('custom')}
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

        {/* Stats Preview */}
        {user && (
          <div className="bg-white rounded-2xl p-6 shadow-lg mb-6">
            <h3 className="font-bold text-lg mb-4 text-gray-800">إحصائياتك السريعة</h3>
            <div className="grid grid-cols-3 gap-4">
              <div className="text-center">
                <div className="bg-green-100 w-12 h-12 rounded-xl flex items-center justify-center mx-auto mb-2">
                  <Trophy className="text-green-600" size={24} />
                </div>
                <p className="text-2xl font-bold text-gray-800">{user.totalQuizzes || 0}</p>
                <p className="text-xs text-gray-600">اختبار</p>
              </div>
              <div className="text-center">
                <div className="bg-blue-100 w-12 h-12 rounded-xl flex items-center justify-center mx-auto mb-2">
                  <Star className="text-blue-600" size={24} />
                </div>
                <p className="text-2xl font-bold text-gray-800">{user.totalCorrectAnswers || 0}</p>
                <p className="text-xs text-gray-600">إجابة صحيحة</p>
              </div>
              <div className="text-center">
                <div className="bg-purple-100 w-12 h-12 rounded-xl flex items-center justify-center mx-auto mb-2">
                  <Zap className="text-purple-600" size={24} />
                </div>
                <p className="text-2xl font-bold text-gray-800">
                  {user.totalAnswers > 0 
                    ? Math.round((user.totalCorrectAnswers / user.totalAnswers) * 100) 
                    : 0}%
                </p>
                <p className="text-xs text-gray-600">دقة</p>
              </div>
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

export default HomeScreen;