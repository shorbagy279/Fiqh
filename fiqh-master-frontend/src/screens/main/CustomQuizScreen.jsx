import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Settings, Clock, BookOpen, ChevronRight, ChevronDown, Check, PlayCircle, X } from 'lucide-react';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const CustomQuizScreen = ({ navigate }) => {
  const { token } = useAuth();
  const [categories, setCategories] = useState([]);
  const [allQuestions, setAllQuestions] = useState({});
  const [selectedQuestions, setSelectedQuestions] = useState([]);
  const [expandedCategory, setExpandedCategory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadingQuestions, setLoadingQuestions] = useState({});
  const [questionCount, setQuestionCount] = useState(10);
  const [timerEnabled, setTimerEnabled] = useState(false);
  const [timerMinutes, setTimerMinutes] = useState(10);
  const [showPreview, setShowPreview] = useState(false);

  useEffect(() => {
    if (token) {
      api.getCategories(token)
        .then(setCategories)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  const loadCategoryQuestions = async (categoryId) => {
    if (allQuestions[categoryId]) return;
    
    setLoadingQuestions(prev => ({ ...prev, [categoryId]: true }));
    try {
      const questions = await api.getCategoryQuestions(token, categoryId, 50);
      setAllQuestions(prev => ({ ...prev, [categoryId]: questions }));
    } catch (error) {
      console.error('Error loading questions:', error);
    } finally {
      setLoadingQuestions(prev => ({ ...prev, [categoryId]: false }));
    }
  };

  const toggleCategory = async (categoryId) => {
    if (expandedCategory === categoryId) {
      setExpandedCategory(null);
    } else {
      setExpandedCategory(categoryId);
      await loadCategoryQuestions(categoryId);
    }
  };

  const toggleQuestion = (question) => {
    const isSelected = selectedQuestions.some(q => q.id === question.id);
    if (isSelected) {
      setSelectedQuestions(selectedQuestions.filter(q => q.id !== question.id));
    } else {
      setSelectedQuestions([...selectedQuestions, question]);
    }
  };

  const handleStartQuiz = () => {
    if (selectedQuestions.length === 0) {
      alert('الرجاء اختيار سؤال واحد على الأقل');
      return;
    }

    navigate('quiz', {
      type: 'custom',
      customQuestions: selectedQuestions,
      questionCount: selectedQuestions.length,
      timerEnabled,
      timerMinutes: timerEnabled ? timerMinutes : null
    });
  };

  const questionCountOptions = [5, 10, 15, 20, 25, 30];
  const timerOptions = [5, 10, 15, 20, 30, 45, 60];

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" message="جاري التحميل..." />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-indigo-600 to-purple-700 text-white p-6 shadow-xl">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate('home')}
              className="p-2 hover:bg-white/10 rounded-lg transition"
            >
              <X size={24} />
            </button>
            <div>
              <h1 className="text-2xl font-bold flex items-center gap-2">
                <Settings size={28} />
                اختبار مخصص
              </h1>
              <p className="text-indigo-100 text-sm mt-1">اختر الأسئلة يدوياً</p>
            </div>
          </div>
          <button
            onClick={() => setShowPreview(true)}
            className="bg-white/20 backdrop-blur px-4 py-2 rounded-xl font-bold hover:bg-white/30 transition flex items-center gap-2"
          >
            <Check size={18} />
            {selectedQuestions.length}
          </button>
        </div>
      </div>

      <div className="p-6">
        {/* Settings Cards */}
        <div className="space-y-4 mb-6">
          {/* Timer Settings */}
          <div className="bg-white rounded-2xl p-5 shadow-lg">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-3">
                <div className="bg-orange-100 p-3 rounded-xl">
                  <Clock size={24} className="text-orange-600" />
                </div>
                <div>
                  <h3 className="font-bold text-gray-800">مؤقت الاختبار</h3>
                  <p className="text-sm text-gray-600">حدد وقتاً للاختبار</p>
                </div>
              </div>
              <button
                onClick={() => setTimerEnabled(!timerEnabled)}
                className={`relative w-14 h-7 rounded-full transition-all ${
                  timerEnabled ? 'bg-orange-600' : 'bg-gray-300'
                }`}
              >
                <div
                  className={`absolute w-5 h-5 bg-white rounded-full top-1 transition-all shadow-md ${
                    timerEnabled ? 'right-1' : 'right-8'
                  }`}
                ></div>
              </button>
            </div>

            {timerEnabled && (
              <div className="animate-slide-in">
                <div className="flex gap-2 overflow-x-auto pb-2 custom-scrollbar">
                  {timerOptions.map((minutes) => (
                    <button
                      key={minutes}
                      onClick={() => setTimerMinutes(minutes)}
                      className={`flex-shrink-0 px-4 py-2 rounded-xl font-bold text-sm transition ${
                        timerMinutes === minutes
                          ? 'bg-orange-600 text-white'
                          : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                      }`}
                    >
                      {minutes} دقيقة
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Question Selection Info */}
          <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-2xl p-5">
            <h3 className="font-bold text-gray-800 mb-2">تعليمات</h3>
            <ul className="text-sm text-gray-700 space-y-1">
              <li className="flex items-start gap-2">
                <span className="text-blue-600 mt-0.5">•</span>
                <span>اضغط على القسم لعرض الأسئلة</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-blue-600 mt-0.5">•</span>
                <span>اختر الأسئلة التي تريد التدرب عليها</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-blue-600 mt-0.5">•</span>
                <span>يمكنك اختيار أسئلة من أقسام مختلفة</span>
              </li>
              <li className="flex items-start gap-2">
                <span className="text-blue-600 mt-0.5">•</span>
                <span>الأسئلة المختارة: <strong>{selectedQuestions.length}</strong></span>
              </li>
            </ul>
          </div>
        </div>

        {/* Categories and Questions */}
        <div className="space-y-3">
          {categories.map(category => (
            <div key={category.id} className="bg-white rounded-2xl shadow-lg overflow-hidden">
              {/* Category Header */}
              <button
                onClick={() => toggleCategory(category.id)}
                className="w-full p-5 flex items-center gap-4 hover:bg-gray-50 transition"
              >
                <div className={`${category.color || 'bg-green-500'} w-14 h-14 rounded-xl flex items-center justify-center text-2xl shadow-sm`}>
                  {category.icon}
                </div>
                <div className="flex-1 text-right">
                  <h3 className="font-bold text-gray-800">{category.nameAr}</h3>
                  <p className="text-sm text-gray-500">{category.questionCount} سؤال</p>
                </div>
                <div className="flex items-center gap-2">
                  {selectedQuestions.filter(q => q.categoryId === category.id).length > 0 && (
                    <div className="bg-indigo-100 text-indigo-600 px-3 py-1 rounded-full text-sm font-bold">
                      {selectedQuestions.filter(q => q.categoryId === category.id).length} محدد
                    </div>
                  )}
                  {expandedCategory === category.id ? (
                    <ChevronDown className="text-gray-400" />
                  ) : (
                    <ChevronRight className="text-gray-400" />
                  )}
                </div>
              </button>

              {/* Questions List */}
              {expandedCategory === category.id && (
                <div className="border-t border-gray-100 p-4 bg-gray-50 animate-slide-in">
                  {loadingQuestions[category.id] ? (
                    <div className="flex justify-center py-8">
                      <LoadingSpinner message="جاري تحميل الأسئلة..." />
                    </div>
                  ) : (
                    <div className="space-y-2 max-h-96 overflow-y-auto custom-scrollbar">
                      {allQuestions[category.id]?.map(question => {
                        const isSelected = selectedQuestions.some(q => q.id === question.id);
                        return (
                          <button
                            key={question.id}
                            onClick={() => toggleQuestion(question)}
                            className={`w-full p-4 rounded-xl text-right transition-all ${
                              isSelected
                                ? 'bg-indigo-50 border-2 border-indigo-500'
                                : 'bg-white border-2 border-gray-200 hover:border-indigo-300'
                            }`}
                          >
                            <div className="flex items-start gap-3">
                              <div className={`flex-shrink-0 w-6 h-6 rounded-lg border-2 flex items-center justify-center ${
                                isSelected
                                  ? 'bg-indigo-600 border-indigo-600'
                                  : 'border-gray-300'
                              }`}>
                                {isSelected && <Check size={16} className="text-white" />}
                              </div>
                              <div className="flex-1">
                                <p className="font-medium text-gray-800 leading-relaxed text-sm">
                                  {question.questionAr}
                                </p>
                                {question.difficulty && (
                                  <span className={`inline-block mt-2 px-2 py-1 rounded-full text-xs font-bold ${
                                    question.difficulty === 'beginner' ? 'bg-green-100 text-green-600' :
                                    question.difficulty === 'intermediate' ? 'bg-yellow-100 text-yellow-600' :
                                    'bg-red-100 text-red-600'
                                  }`}>
                                    {question.difficulty === 'beginner' ? 'مبتدئ' : 
                                     question.difficulty === 'intermediate' ? 'متوسط' : 'متقدم'}
                                  </span>
                                )}
                              </div>
                            </div>
                          </button>
                        );
                      })}
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Start Button */}
        {selectedQuestions.length > 0 && (
          <div className="fixed bottom-6 left-6 right-6 max-w-md mx-auto">
            <button
              onClick={handleStartQuiz}
              className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 text-white py-4 rounded-2xl font-bold text-lg hover:from-indigo-700 hover:to-purple-700 transition-all shadow-2xl hover:shadow-3xl flex items-center justify-center gap-2 animate-pulse-slow"
            >
              <PlayCircle size={24} />
              ابدأ الاختبار ({selectedQuestions.length} سؤال)
            </button>
          </div>
        )}
      </div>

      {/* Preview Modal */}
      {showPreview && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-end justify-center z-50 p-4">
          <div className="bg-white rounded-t-3xl w-full max-w-md max-h-[80vh] overflow-auto p-6">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-2xl font-bold text-gray-800">الأسئلة المختارة</h2>
              <button
                onClick={() => setShowPreview(false)}
                className="text-gray-400 hover:text-gray-600 transition p-2"
              >
                <X size={24} />
              </button>
            </div>

            <div className="space-y-3 mb-6">
              {selectedQuestions.map((question, index) => (
                <div key={question.id} className="bg-gray-50 rounded-xl p-4">
                  <div className="flex items-start gap-3">
                    <div className="bg-indigo-600 text-white w-8 h-8 rounded-lg flex items-center justify-center font-bold flex-shrink-0">
                      {index + 1}
                    </div>
                    <div className="flex-1">
                      <p className="text-sm text-gray-800 font-medium leading-relaxed">
                        {question.questionAr}
                      </p>
                      <div className="flex gap-2 mt-2">
                        <span className="bg-blue-100 text-blue-600 px-2 py-1 rounded-full text-xs font-bold">
                          {question.categoryName}
                        </span>
                      </div>
                    </div>
                    <button
                      onClick={() => toggleQuestion(question)}
                      className="text-red-500 hover:text-red-700 transition"
                    >
                      <X size={20} />
                    </button>
                  </div>
                </div>
              ))}
            </div>

            <div className="bg-gradient-to-r from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-xl p-4 mb-4">
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">عدد الأسئلة:</span>
                  <span className="font-bold text-gray-800">{selectedQuestions.length}</span>
                </div>
                {timerEnabled && (
                  <div className="flex justify-between">
                    <span className="text-gray-600">المؤقت:</span>
                    <span className="font-bold text-gray-800">{timerMinutes} دقيقة</span>
                  </div>
                )}
              </div>
            </div>

            <button
              onClick={() => {
                setShowPreview(false);
                handleStartQuiz();
              }}
              className="w-full bg-indigo-600 text-white py-4 rounded-xl font-bold hover:bg-indigo-700 transition flex items-center justify-center gap-2"
            >
              <PlayCircle size={20} />
              ابدأ الاختبار
            </button>
          </div>
        </div>
      )}

      <style>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 6px;
          height: 6px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: #f1f1f1;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background: #6366f1;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background: #4f46e5;
        }
      `}</style>
    </div>
  );
};

export default CustomQuizScreen;