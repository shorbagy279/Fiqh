import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { History, Trophy, Clock, Calendar, ChevronRight, X } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const QuizHistoryScreen = ({ navigate }) => {
  const { token } = useAuth();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedQuiz, setSelectedQuiz] = useState(null);

  useEffect(() => {
    if (token) {
      api.getQuizHistory(token)
        .then(setHistory)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ar-SA', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const getGradeColor = (percentage) => {
    if (percentage >= 90) return 'text-yellow-600 bg-yellow-100';
    if (percentage >= 70) return 'text-green-600 bg-green-100';
    if (percentage >= 50) return 'text-blue-600 bg-blue-100';
    return 'text-red-600 bg-red-100';
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-2 flex items-center gap-2">
          <History size={28} />
          سجل الاختبارات
        </h1>
        <p className="text-green-100">راجع أدائك في الاختبارات السابقة</p>
      </div>

      <div className="p-6">
        {loading ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner message="جاري تحميل السجل..." />
          </div>
        ) : history.length === 0 ? (
          <div className="text-center py-12">
            <div className="bg-gray-100 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4">
              <History size={48} className="text-gray-300" />
            </div>
            <h3 className="text-xl font-bold text-gray-700 mb-2">لا توجد اختبارات سابقة</h3>
            <p className="text-gray-500 mb-6">ابدأ أول اختبار لك الآن!</p>
            <button
              onClick={() => navigate('home')}
              className="bg-green-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-green-700 transition"
            >
              ابدأ اختباراً جديداً
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {history.map((quiz) => (
              <button
                key={quiz.id}
                onClick={() => setSelectedQuiz(quiz)}
                className="w-full bg-white rounded-xl p-5 shadow-lg hover:shadow-xl transition-all active:scale-98 text-right"
              >
                <div className="flex items-start justify-between mb-3">
                  <div className="flex-1">
                    {quiz.categoryName && (
                      <span className="bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-xs font-bold inline-block mb-2">
                        {quiz.categoryName}
                      </span>
                    )}
                    <h3 className="font-bold text-gray-800 text-lg mb-1">
                      {quiz.quizType === 'daily' ? 'التحدي اليومي' : 
                       quiz.quizType === 'category' ? 'اختبار القسم' : 'اختبار عشوائي'}
                    </h3>
                  </div>
                  <div className={`${getGradeColor(quiz.scorePercentage)} px-4 py-2 rounded-xl font-bold text-xl`}>
                    {Math.round(quiz.scorePercentage)}%
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-3 mb-3">
                  <div className="text-center">
                    <div className="text-2xl font-bold text-gray-800">{quiz.correctAnswers}/{quiz.totalQuestions}</div>
                    <div className="text-xs text-gray-600">صحيح</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-gray-800">{formatTime(quiz.timeTakenSeconds)}</div>
                    <div className="text-xs text-gray-600">الوقت</div>
                  </div>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-gray-800">{quiz.totalQuestions}</div>
                    <div className="text-xs text-gray-600">أسئلة</div>
                  </div>
                </div>

                <div className="flex items-center justify-between text-sm text-gray-500 pt-3 border-t border-gray-100">
                  <div className="flex items-center gap-1">
                    <Calendar size={14} />
                    <span>{formatDate(quiz.completedAt)}</span>
                  </div>
                  <ChevronRight size={16} />
                </div>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Quiz Detail Modal */}
      {selectedQuiz && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-end justify-center z-50 p-4">
          <div className="bg-white rounded-t-3xl w-full max-w-md max-h-[80vh] overflow-auto p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-800">تفاصيل الاختبار</h2>
              <button
                onClick={() => setSelectedQuiz(null)}
                className="text-gray-400 hover:text-gray-600 transition p-2"
              >
                <X size={24} />
              </button>
            </div>

            <div className="space-y-4">
              <div className="bg-gradient-to-r from-green-500 to-green-600 rounded-xl p-6 text-white text-center">
                <div className="text-5xl font-bold mb-2">{Math.round(selectedQuiz.scorePercentage)}%</div>
                <div className="text-green-100">النتيجة الإجمالية</div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <StatBox
                  icon={<Trophy />}
                  label="إجابات صحيحة"
                  value={`${selectedQuiz.correctAnswers}/${selectedQuiz.totalQuestions}`}
                  color="bg-green-100 text-green-600"
                />
                <StatBox
                  icon={<Clock />}
                  label="الوقت المستغرق"
                  value={formatTime(selectedQuiz.timeTakenSeconds)}
                  color="bg-blue-100 text-blue-600"
                />
              </div>

              {selectedQuiz.categoryName && (
                <div className="bg-gray-50 rounded-xl p-4">
                  <div className="text-sm text-gray-600 mb-1">القسم</div>
                  <div className="font-bold text-gray-800">{selectedQuiz.categoryName}</div>
                </div>
              )}

              <div className="bg-gray-50 rounded-xl p-4">
                <div className="text-sm text-gray-600 mb-1">التاريخ</div>
                <div className="font-bold text-gray-800">{formatDate(selectedQuiz.completedAt)}</div>
              </div>

              <button
                onClick={() => {
                  setSelectedQuiz(null);
                  navigate('quiz', { 
                    type: selectedQuiz.quizType,
                    categoryId: selectedQuiz.categoryId 
                  });
                }}
                className="w-full bg-green-600 text-white py-4 rounded-xl font-bold hover:bg-green-700 transition"
              >
                إعادة الاختبار
              </button>
            </div>
          </div>
        </div>
      )}

      <BottomNav currentScreen="history" navigate={navigate} />
    </div>
  );
};

const StatBox = ({ icon, label, value, color }) => (
  <div className={`${color} rounded-xl p-4`}>
    <div className="flex items-center gap-2 mb-2">
      {icon}
      <span className="text-xs font-medium">{label}</span>
    </div>
    <div className="text-2xl font-bold">{value}</div>
  </div>
);

export default QuizHistoryScreen;