import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Calendar, Clock, Users, Copy, Check, ChevronLeft } from 'lucide-react';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const ScheduleExamScreen = ({ navigate, data }) => {
  const { token } = useAuth();
  const { selectedQuestions, categories } = data || {};
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    startDate: '',
    startTime: '',
    durationMinutes: 30,
    maxParticipants: null
  });
  
  const [loading, setLoading] = useState(false);
  const [examCode, setExamCode] = useState(null);
  const [copied, setCopied] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const startDateTime = new Date(`${formData.startDate}T${formData.startTime}`);
      
      const request = {
        title: formData.title,
        description: formData.description,
        startTime: startDateTime.toISOString(),
        durationMinutes: parseInt(formData.durationMinutes),
        questionIds: selectedQuestions.map(q => q.id),
        maxParticipants: formData.maxParticipants ? parseInt(formData.maxParticipants) : null
      };

      const result = await api.createScheduledExam(token, request);
      setExamCode(result.examCode);
    } catch (error) {
      alert('حدث خطأ: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const copyCode = () => {
    navigator.clipboard.writeText(examCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const shareExam = () => {
    const message = `انضم إلى الاختبار: ${formData.title}\nالكود: ${examCode}\nالوقت: ${formData.startDate} ${formData.startTime}`;
    
    if (navigator.share) {
      navigator.share({
        title: 'دعوة اختبار',
        text: message
      });
    } else {
      copyCode();
    }
  };

  if (examCode) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 p-6 flex items-center justify-center">
        <div className="bg-white rounded-3xl p-8 max-w-md w-full shadow-2xl">
          <div className="text-center mb-6">
            <div className="bg-green-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-4">
              <Check size={48} className="text-green-600" />
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">تم إنشاء الاختبار!</h2>
            <p className="text-gray-600">شارك الكود مع الطلاب</p>
          </div>

          <div className="bg-gradient-to-r from-green-500 to-green-600 rounded-2xl p-6 mb-6">
            <p className="text-white text-sm mb-2 text-center">كود الاختبار</p>
            <div className="bg-white rounded-xl p-4 flex items-center justify-between">
              <span className="text-3xl font-bold text-gray-800 tracking-wider">{examCode}</span>
              <button
                onClick={copyCode}
                className="bg-green-600 text-white p-3 rounded-lg hover:bg-green-700 transition"
              >
                {copied ? <Check size={20} /> : <Copy size={20} />}
              </button>
            </div>
          </div>

          <div className="space-y-3 mb-6">
            <InfoRow icon={<Calendar />} label="العنوان" value={formData.title} />
            <InfoRow icon={<Clock />} label="الوقت" value={`${formData.startDate} ${formData.startTime}`} />
            <InfoRow icon={<Users />} label="عدد الأسئلة" value={selectedQuestions.length} />
          </div>

          <div className="space-y-3">
            <button
              onClick={shareExam}
              className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-4 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition"
            >
              مشاركة الكود
            </button>
            <button
              onClick={() => navigate('home')}
              className="w-full bg-gray-100 text-gray-700 py-4 rounded-xl font-bold hover:bg-gray-200 transition"
            >
              العودة للرئيسية
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <div className="flex items-center gap-3 mb-4">
          <button
            onClick={() => navigate('customQuiz')}
            className="p-2 hover:bg-white/10 rounded-lg transition"
          >
            <ChevronLeft size={24} />
          </button>
          <div>
            <h1 className="text-2xl font-bold">جدولة اختبار</h1>
            <p className="text-green-100 text-sm">حدد الوقت وأنشئ كود الاختبار</p>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="p-6">
        <div className="bg-white rounded-2xl p-6 shadow-lg mb-6">
          <h3 className="font-bold text-lg mb-4 text-gray-800">معلومات الاختبار</h3>
          
          <div className="space-y-4">
            <div>
              <label className="block text-right text-sm font-bold text-gray-700 mb-2">
                عنوان الاختبار *
              </label>
              <input
                type="text"
                value={formData.title}
                onChange={(e) => setFormData({...formData, title: e.target.value})}
                className="w-full p-3 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent text-right"
                placeholder="مثال: اختبار الفقه الشهري"
                required
              />
            </div>

            <div>
              <label className="block text-right text-sm font-bold text-gray-700 mb-2">
                الوصف
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({...formData, description: e.target.value})}
                className="w-full p-3 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent text-right"
                placeholder="وصف الاختبار (اختياري)"
                rows={3}
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-right text-sm font-bold text-gray-700 mb-2">
                  التاريخ *
                </label>
                <input
                  type="date"
                  value={formData.startDate}
                  onChange={(e) => setFormData({...formData, startDate: e.target.value})}
                  min={new Date().toISOString().split('T')[0]}
                  className="w-full p-3 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  required
                />
              </div>

              <div>
                <label className="block text-right text-sm font-bold text-gray-700 mb-2">
                  الوقت *
                </label>
                <input
                  type="time"
                  value={formData.startTime}
                  onChange={(e) => setFormData({...formData, startTime: e.target.value})}
                  className="w-full p-3 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-right text-sm font-bold text-gray-700 mb-2">
                مدة الاختبار (دقيقة) *
              </label>
              <select
                value={formData.durationMinutes}
                onChange={(e) => setFormData({...formData, durationMinutes: e.target.value})}
                className="w-full p-3 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent text-right"
              >
                <option value={15}>15 دقيقة</option>
                <option value={30}>30 دقيقة</option>
                <option value={45}>45 دقيقة</option>
                <option value={60}>60 دقيقة</option>
                <option value={90}>90 دقيقة</option>
                <option value={120}>120 دقيقة</option>
              </select>
            </div>

            <div>
              <label className="block text-right text-sm font-bold text-gray-700 mb-2">
                الحد الأقصى للمشاركين (اختياري)
              </label>
              <input
                type="number"
                value={formData.maxParticipants || ''}
                onChange={(e) => setFormData({...formData, maxParticipants: e.target.value})}
                className="w-full p-3 border-2 border-gray-200 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent text-right"
                placeholder="غير محدود"
                min={1}
              />
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-2xl p-5 mb-6">
          <h3 className="font-bold text-gray-800 mb-3 text-center">ملخص الاختبار</h3>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="font-bold text-gray-800">{selectedQuestions?.length || 0}</span>
              <span className="text-gray-600">:عدد الأسئلة</span>
            </div>
            <div className="flex justify-between">
              <span className="font-bold text-gray-800">{formData.durationMinutes} دقيقة</span>
              <span className="text-gray-600">:المدة</span>
            </div>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading || !formData.title || !formData.startDate || !formData.startTime}
          className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-4 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
        >
          {loading ? (
            <>
              <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              جاري الإنشاء...
            </>
          ) : (
            <>
              <Calendar size={20} />
              إنشاء الاختبار
            </>
          )}
        </button>
      </form>
    </div>
  );
};

const InfoRow = ({ icon, label, value }) => (
  <div className="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
    <div className="flex items-center gap-2">
      <div className="text-green-600">{icon}</div>
      <span className="text-gray-600 text-sm">{label}</span>
    </div>
    <span className="font-bold text-gray-800">{value}</span>
  </div>
);

export default ScheduleExamScreen;