import React from 'react';
import { RefreshCw, Home, TrendingUp } from 'lucide-react';

const ResultsScreen = ({ navigate, data }) => {
  const { result, score, total, answeredCount } = data || {};
  const actualAnswered = answeredCount || total || 10;
  const actualScore = score || 0;
  const actualTotal = total || 10;
  const percentage = (actualScore / actualTotal) * 100;
  const unansweredCount = actualTotal - actualAnswered;
  
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
      <p className="text-xl mb-10 text-center px-4">{message}</p>

      <div className="bg-white/10 backdrop-blur-xl rounded-3xl p-8 w-full max-w-md mb-8 shadow-2xl border border-white/20">
        <div className="text-center mb-8">
          <div className="text-7xl font-bold mb-3">{actualScore}/{actualTotal}</div>
          <div className="text-3xl font-semibold mb-2">{Math.round(percentage)}%</div>
          {unansweredCount > 0 && (
            <div className="bg-white/20 rounded-lg p-2 mt-4">
              <p className="text-sm">
                تم الإجابة على {actualAnswered} من {actualTotal} سؤال
              </p>
            </div>
          )}
        </div>

        <div className="space-y-4">
          <StatRow icon="✓" label="إجابات صحيحة" value={actualScore} />
          <StatRow icon="✗" label="إجابات خاطئة" value={actualAnswered - actualScore} />
          {unansweredCount > 0 && (
            <StatRow 
              icon="⏭" 
              label="أسئلة غير مجابة" 
              value={unansweredCount}
              warning={true}
            />
          )}
          {result?.timeTakenSeconds && (
            <StatRow 
              icon="⏱" 
              label="الوقت المستغرق" 
              value={`${Math.floor(result.timeTakenSeconds / 60)}:${(result.timeTakenSeconds % 60).toString().padStart(2, '0')}`} 
            />
          )}
          <StatRow 
            icon="📊" 
            label="الدقة" 
            value={`${Math.round((actualScore / actualAnswered) * 100)}%`} 
          />
        </div>

        {/* Performance indicator */}
        <div className="mt-6 pt-6 border-t border-white/20">
          <div className="flex items-center justify-center gap-2 mb-2">
            <TrendingUp size={20} />
            <span className="font-bold">الأداء</span>
          </div>
          <div className="bg-white/10 rounded-full h-3 overflow-hidden">
            <div
              className={`h-full rounded-full transition-all duration-1000 ${
                percentage >= 90 ? 'bg-yellow-400' :
                percentage >= 70 ? 'bg-green-400' :
                percentage >= 50 ? 'bg-blue-400' : 'bg-purple-400'
              }`}
              style={{ width: `${percentage}%` }}
            ></div>
          </div>
        </div>
      </div>

      {/* Tips for improvement */}
      {percentage < 70 && (
        <div className="bg-white/10 backdrop-blur-xl rounded-2xl p-5 w-full max-w-md mb-6 border border-white/20">
          <h3 className="font-bold text-center mb-3 flex items-center justify-center gap-2">
            💡 نصائح للتحسين
          </h3>
          <ul className="space-y-2 text-sm">
            <li className="flex items-start gap-2">
              <span>•</span>
              <span>راجع الإجابات الخاطئة واقرأ الشروحات بتمعن</span>
            </li>
            <li className="flex items-start gap-2">
              <span>•</span>
              <span>احفظ الأسئلة الصعبة لمراجعتها لاحقاً</span>
            </li>
            <li className="flex items-start gap-2">
              <span>•</span>
              <span>حاول حل اختبارات يومية للحفاظ على سلسلتك</span>
            </li>
          </ul>
        </div>
      )}

      <div className="space-y-3 w-full max-w-md">
        <button
          onClick={() => navigate('quiz', data?.quizData || {})}
          className="w-full bg-white text-purple-600 py-4 rounded-xl font-bold hover:bg-gray-100 transition-all shadow-lg flex items-center justify-center gap-2 active:scale-98"
        >
          <RefreshCw size={20} />
          حاول مرة أخرى
        </button>
        <button
          onClick={() => navigate('stats')}
          className="w-full bg-white/20 backdrop-blur text-white py-4 rounded-xl font-bold hover:bg-white/30 transition-all border border-white/30 flex items-center justify-center gap-2 active:scale-98"
        >
          <TrendingUp size={20} />
          عرض الإحصائيات
        </button>
        <button
          onClick={() => navigate('home')}
          className="w-full bg-white/10 backdrop-blur text-white py-4 rounded-xl font-bold hover:bg-white/20 transition-all border border-white/20 flex items-center justify-center gap-2 active:scale-98"
        >
          <Home size={20} />
          العودة للرئيسية
        </button>
      </div>
    </div>
  );
};

const StatRow = ({ icon, label, value, warning }) => (
  <div className={`flex items-center justify-between py-2 ${warning ? 'bg-yellow-500/20 rounded-lg px-3' : ''}`}>
    <div className="flex items-center gap-3">
      <span className="text-2xl">{icon}</span>
      <span className="font-medium">{label}</span>
    </div>
    <span className="font-bold text-xl">{value}</span>
  </div>
);

export default ResultsScreen;