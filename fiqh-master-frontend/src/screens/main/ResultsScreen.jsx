import React from 'react';
import { RefreshCw } from 'lucide-react';

const ResultsScreen = ({ navigate, data }) => {
  const { result, score, total } = data || {};
  const percentage = ((score || 0) / (total || 10)) * 100;
  
  let message = '';
  let emoji = '';
  let bgGradient = '';
  
  if (percentage >= 90) {
    message = 'ممتاز! أنت فقيه متميز! 🌟';
    emoji = '🎉';
    bgGradient = 'from-yellow-500 to-orange-500';
  } else if (percentage >= 70) {
    message = 'جيد جداً! استمر في التعلم! 👏';
    emoji = '✨';
    bgGradient = 'from-green-500 to-teal-500';
  } else if (percentage >= 50) {
    message = 'جيد! مع المزيد من الممارسة ستتحسن! 💪';
    emoji = '📚';
    bgGradient = 'from-blue-500 to-indigo-500';
  } else {
    message = 'لا بأس، حاول مرة أخرى! 🤲';
    emoji = '🌱';
    bgGradient = 'from-purple-500 to-pink-500';
  }

  return (
    <div className={`min-h-screen bg-gradient-to-br ${bgGradient} text-white p-6 flex flex-col items-center justify-center`}>
      <div className="text-8xl mb-6 animate-bounce">{emoji}</div>
      <h1 className="text-4xl font-bold mb-3">اكتمل الاختبار!</h1>
      <p className="text-xl mb-10 text-center">{message}</p>

      <div className="bg-white/10 backdrop-blur-xl rounded-3xl p-8 w-full max-w-md mb-8 shadow-2xl border border-white/20">
        <div className="text-center mb-8">
          <div className="text-7xl font-bold mb-3">{score}/{total}</div>
          <div className="text-3xl font-semibold">{Math.round(percentage)}%</div>
        </div>

        <div className="space-y-4">
          <StatRow icon="✓" label="إجابات صحيحة" value={score} />
          <StatRow icon="✗" label="إجابات خاطئة" value={total - score} />
          {result?.timeTakenSeconds && (
            <StatRow icon="⏱" label="الوقت المستغرق" value={`${Math.floor(result.timeTakenSeconds / 60)}:${(result.timeTakenSeconds % 60).toString().padStart(2, '0')}`} />
          )}
        </div>
      </div>

      <div className="space-y-3 w-full max-w-md">
        <button
          onClick={() => navigate('quiz', data?.quizData || {})}
          className="w-full bg-white text-green-600 py-4 rounded-xl font-bold hover:bg-gray-100 transition-all shadow-lg flex items-center justify-center gap-2"
        >
          <RefreshCw size={20} />
          حاول مرة أخرى
        </button>
        <button
          onClick={() => navigate('home')}
          className="w-full bg-white/20 backdrop-blur text-white py-4 rounded-xl font-bold hover:bg-white/30 transition-all border border-white/30"
        >
          العودة للرئيسية
        </button>
      </div>
    </div>
  );
};

const StatRow = ({ icon, label, value }) => (
  <div className="flex items-center justify-between py-2">
    <div className="flex items-center gap-3">
      <span className="text-2xl">{icon}</span>
      <span className="font-medium">{label}</span>
    </div>
    <span className="font-bold text-xl">{value}</span>
  </div>
);

export default ResultsScreen;