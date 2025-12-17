import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Key, Calendar, Clock, Users, AlertCircle, CheckCircle, PlayCircle } from 'lucide-react';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const JoinExamScreen = ({ navigate }) => {
  const { token } = useAuth();
  const [examCode, setExamCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [exam, setExam] = useState(null);
  const [error, setError] = useState('');
  const [timeUntilStart, setTimeUntilStart] = useState('');

  useEffect(() => {
    if (!exam) return;

    const interval = setInterval(() => {
      const now = new Date();
      const start = new Date(exam.startTime);
      const diff = start - now;

      if (diff <= 0) {
        setTimeUntilStart('بدأ الاختبار!');
        setExam(prev => ({ ...prev, isStarted: true }));
      } else {
        const hours = Math.floor(diff / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);
        setTimeUntilStart(`${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [exam]);

  const handleCheckCode = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const result = await api.getExamByCode(token, examCode.toUpperCase().trim());
      setExam(result);
    } catch (err) {
      setError(err.message || 'كود غير صحيح');
      setExam(null);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinExam = async () => {
    setLoading(true);
    try {
      await api.joinExam(token, examCode.toUpperCase().trim());
      const updated = await api.getExamByCode(token, examCode.toUpperCase().trim());
      setExam(updated);
    } catch (err) {
      setError(err.message || 'فشل الانضمام');
    } finally {
      setLoading(false);
    }
  };

  const handleStartExam = async () => {
    if (!exam.isStarted) {
      setError('لم يبدأ الاختبار بعد');
      return;
    }

    setLoading(true);
    try {
      await api.startExamParticipation(token, exam.id);
      
      // Get questions for this exam
      const questions = await api.getExamQuestions(token, exam.id);
      
      navigate('quiz', {
        type: 'scheduled',
        examId: exam.id,
        customQuestions: questions,
        questionCount: questions.length,
        timerEnabled: true,
        timerMinutes: exam.durationMinutes
      });
    } catch (err) {
      setError(err.message || 'فشل بدء الاختبار');
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 p-6">
      <div className="max-w-md mx-auto">
        <div className="bg-gradient-to-br from-green-600 to-green-800 text-white rounded-3xl p-8 mb-6 shadow-xl">
          <div className="text-center mb-6">
            <div className="bg-white/20 backdrop-blur w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-4">
              <Key size={40} />
            </div>
            <h1 className="text-3xl font-bold mb-2">الانضمام لاختبار</h1>
            <p className="text-green-100">أدخل كود الاختبار المشارك</p>
          </div>

          <form onSubmit={handleCheckCode} className="space-y-4">
            <div>
              <input
                type="text"
                value={examCode}
                onChange={(e) => setExamCode(e.target.value.toUpperCase())}
                className="w-full p-4 rounded-xl text-center text-2xl font-bold text-gray-800 tracking-widest uppercase focus:ring-4 focus:ring-yellow-400 focus:outline-none"
                placeholder="XXXXXXXX"
                maxLength={8}
                required
                disabled={loading}
              />
            </div>

            {error && (
              <div className="bg-red-500/20 border border-red-300 rounded-xl p-3 flex items-center gap-2">
                <AlertCircle size={20} />
                <p className="text-sm">{error}</p>
              </div>
            )}

            <button
              type="submit"
              disabled={loading || examCode.length !== 8}
              className="w-full bg-white text-green-600 py-4 rounded-xl font-bold hover:bg-green-50 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <div className="w-5 h-5 border-2 border-green-600 border-t-transparent rounded-full animate-spin"></div>
                  جاري التحقق...
                </>
              ) : (
                <>
                  <Key size={20} />
                  التحقق من الكود
                </>
              )}
            </button>
          </form>
        </div>

        {exam && (
          <div className="bg-white rounded-3xl p-6 shadow-xl space-y-6 animate-slide-in">
            <div className="text-center pb-6 border-b border-gray-100">
              <h2 className="text-2xl font-bold text-gray-800 mb-2">{exam.title}</h2>
              {exam.description && (
                <p className="text-gray-600 text-sm">{exam.description}</p>
              )}
            </div>

            <div className="space-y-3">
              <ExamInfoRow 
                icon={<Calendar className="text-blue-600" />}
                label="التاريخ والوقت"
                value={new Date(exam.startTime).toLocaleString('ar-SA', {
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit'
                })}
              />
              
              <ExamInfoRow 
                icon={<Clock className="text-orange-600" />}
                label="المدة"
                value={`${exam.durationMinutes} دقيقة`}
              />
              
              <ExamInfoRow 
                icon={<Users className="text-purple-600" />}
                label="عدد الأسئلة"
                value={exam.totalQuestions}
              />
              
              {exam.maxParticipants && (
                <ExamInfoRow 
                  icon={<Users className="text-green-600" />}
                  label="المشاركون"
                  value={`${exam.currentParticipants} / ${exam.maxParticipants}`}
                />
              )}
            </div>

            {!exam.isStarted && (
              <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-2xl p-5 text-center">
                <Clock size={32} className="text-blue-600 mx-auto mb-2" />
                <p className="text-sm text-blue-800 mb-2">يبدأ الاختبار بعد</p>
                <p className="text-3xl font-bold text-blue-900">{timeUntilStart}</p>
              </div>
            )}

            {exam.isStarted && !exam.isExpired && (
              <div className="bg-gradient-to-r from-green-50 to-teal-50 border-2 border-green-300 rounded-2xl p-5 text-center">
                <CheckCircle size={32} className="text-green-600 mx-auto mb-2" />
                <p className="text-sm text-green-800 font-bold">الاختبار متاح الآن!</p>
              </div>
            )}

            {exam.isExpired && (
              <div className="bg-gradient-to-r from-red-50 to-pink-50 border-2 border-red-200 rounded-2xl p-5 text-center">
                <AlertCircle size={32} className="text-red-600 mx-auto mb-2" />
                <p className="text-sm text-red-800 font-bold">انتهى وقت الاختبار</p>
              </div>
            )}

            <div className="space-y-3 pt-4 border-t border-gray-100">
              {!exam.isRegistered ? (
                <button
                  onClick={handleJoinExam}
                  disabled={loading || !exam.canJoin}
                  className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-4 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                >
                  {loading ? (
                    <>
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      جاري الانضمام...
                    </>
                  ) : (
                    <>
                      <CheckCircle size={20} />
                      تسجيل الانضمام
                    </>
                  )}
                </button>
              ) : (
                <button
                  onClick={handleStartExam}
                  disabled={loading || !exam.isStarted || exam.isExpired}
                  className={`w-full py-4 rounded-xl font-bold transition flex items-center justify-center gap-2 ${
                    exam.isStarted && !exam.isExpired
                      ? 'bg-gradient-to-r from-green-600 to-green-700 text-white hover:from-green-700 hover:to-green-800 animate-pulse-slow'
                      : 'bg-gray-300 text-gray-600 cursor-not-allowed'
                  }`}
                >
                  {loading ? (
                    <>
                      <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                      جاري البدء...
                    </>
                  ) : (
                    <>
                      <PlayCircle size={24} />
                      {exam.isStarted ? 'ابدأ الاختبار الآن' : 'انتظر وقت البدء'}
                    </>
                  )}
                </button>
              )}

              <button
                onClick={() => navigate('home')}
                className="w-full bg-gray-100 text-gray-700 py-3 rounded-xl font-bold hover:bg-gray-200 transition"
              >
                العودة للرئيسية
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

const ExamInfoRow = ({ icon, label, value }) => (
  <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
    <div className="flex items-center gap-3">
      {icon}
      <span className="text-gray-600 text-sm font-medium">{label}</span>
    </div>
    <span className="font-bold text-gray-800">{value}</span>
  </div>
);

export default JoinExamScreen;