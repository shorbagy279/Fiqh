import React, { useState } from 'react';
import { X, BookOpen, Settings } from 'lucide-react';

const QuizOptionsModal = ({ onClose, onStart, categories, type = 'random' }) => {
  const [questionCount, setQuestionCount] = useState(10);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [showCustomMode, setShowCustomMode] = useState(false);

  const questionOptions = [10, 20, 30];

  const toggleCategory = (categoryId) => {
    if (selectedCategories.includes(categoryId)) {
      setSelectedCategories(selectedCategories.filter(id => id !== categoryId));
    } else {
      setSelectedCategories([...selectedCategories, categoryId]);
    }
  };

  const handleStart = () => {
    if (showCustomMode && selectedCategories.length === 0) {
      alert('الرجاء اختيار قسم واحد على الأقل');
      return;
    }

    onStart({
      type: showCustomMode ? 'custom' : type,
      questionCount,
      categoryIds: showCustomMode ? selectedCategories : null
    });
  };

  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-3xl w-full max-w-md max-h-[90vh] overflow-hidden shadow-2xl animate-slide-in">
        {/* Header */}
        <div className="bg-gradient-to-r from-green-600 to-green-700 text-white p-6">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-2xl font-bold flex items-center gap-2">
              <Settings size={28} />
              إعدادات الاختبار
            </h2>
            <button
              onClick={onClose}
              className="p-2 hover:bg-white/20 rounded-lg transition"
            >
              <X size={24} />
            </button>
          </div>
          <p className="text-green-100 text-sm">خصص اختبارك كما تريد</p>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6 overflow-y-auto max-h-[calc(90vh-140px)] custom-scrollbar">
          {/* Question Count Section */}
          <div>
            <h3 className="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2 text-right">
              <BookOpen size={20} className="text-green-600" />
              عدد الأسئلة
            </h3>
            <div className="grid grid-cols-3 gap-3">
              {questionOptions.map((count) => (
                <button
                  key={count}
                  onClick={() => setQuestionCount(count)}
                  className={`py-4 rounded-xl font-bold text-lg transition-all transform hover:scale-105 ${
                    questionCount === count
                      ? 'bg-gradient-to-r from-green-600 to-green-700 text-white shadow-lg'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  {count}
                </button>
              ))}
            </div>
          </div>

          {/* Custom Quiz Mode Toggle */}
          <div className="bg-gradient-to-r from-purple-50 to-pink-50 rounded-2xl p-5 border-2 border-purple-200">
            <div className="flex items-center justify-between mb-2">
              <div className="flex-1 text-right">
                <h3 className="font-bold text-gray-800 flex items-center gap-2 mb-1 justify-end">
                  <span>الوضع المخصص</span>
                  <Settings size={20} className="text-purple-600" />
                </h3>
                <p className="text-sm text-gray-600">
                  اختر أقساماً محددة للاختبار
                </p>
              </div>
              <button
                onClick={() => setShowCustomMode(!showCustomMode)}
                className={`relative w-14 h-7 rounded-full transition-all flex-shrink-0 mr-4 ${
                  showCustomMode ? 'bg-purple-600' : 'bg-gray-300'
                }`}
              >
                <div
                  className={`absolute w-5 h-5 bg-white rounded-full top-1 transition-all shadow-md ${
                    showCustomMode ? 'left-1' : 'left-8'
                  }`}
                ></div>
              </button>
            </div>
          </div>

          {/* Categories Selection (only shown in custom mode) */}
          {showCustomMode && (
            <div className="animate-slide-in">
              <h3 className="text-lg font-bold text-gray-800 mb-3 text-right">
                اختر الأقسام ({selectedCategories.length} محددة)
              </h3>
              
              <div className="space-y-2 max-h-64 overflow-y-auto custom-scrollbar">
                {categories.map((cat) => {
                  const isSelected = selectedCategories.includes(cat.id);
                  return (
                    <button
                      key={cat.id}
                      onClick={() => toggleCategory(cat.id)}
                      className={`w-full p-4 rounded-xl text-right transition-all transform hover:scale-[1.02] ${
                        isSelected
                          ? 'bg-gradient-to-r from-green-500 to-green-600 text-white shadow-lg'
                          : 'bg-gray-50 text-gray-800 hover:bg-gray-100 border-2 border-gray-200'
                      }`}
                    >
                      <div className="flex items-center gap-3">
                        <div
                          className={`w-12 h-12 rounded-xl flex items-center justify-center text-2xl shadow-sm flex-shrink-0 ${
                            isSelected ? 'bg-white/20' : cat.color || 'bg-green-100'
                          }`}
                        >
                          {cat.icon}
                        </div>
                        <div className="flex-1 text-right">
                          <h4 className="font-bold">{cat.nameAr}</h4>
                          <p className={`text-xs ${isSelected ? 'text-green-100' : 'text-gray-500'}`}>
                            {cat.questionCount} سؤال
                          </p>
                        </div>
                        {isSelected && (
                          <div className="bg-white text-green-600 w-8 h-8 rounded-full flex items-center justify-center font-bold shadow-md flex-shrink-0">
                            ✓
                          </div>
                        )}
                      </div>
                    </button>
                  );
                })}
              </div>

              {selectedCategories.length > 0 && (
                <div className="mt-3 bg-blue-50 border-2 border-blue-200 rounded-xl p-3">
                  <p className="text-sm text-blue-800 text-center">
                    ستحصل على أسئلة من {selectedCategories.length} {selectedCategories.length === 1 ? 'قسم' : 'أقسام'}
                  </p>
                </div>
              )}
            </div>
          )}

          {/* Summary */}
          <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-5 border-2 border-blue-200">
            <h3 className="font-bold text-gray-800 mb-3 text-center">ملخص الاختبار</h3>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between items-center">
                <span className="font-bold text-gray-800">{questionCount} سؤال</span>
                <span className="text-gray-600">:عدد الأسئلة</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="font-bold text-gray-800">
                  {showCustomMode 
                    ? 'مخصص' 
                    : type === 'daily' 
                    ? 'التحدي اليومي' 
                    : type === 'category' 
                    ? 'قسم محدد' 
                    : 'عشوائي'}
                </span>
                <span className="text-gray-600">:النوع</span>
              </div>
              {showCustomMode && (
                <div className="flex justify-between items-center">
                  <span className="font-bold text-gray-800">{selectedCategories.length}</span>
                  <span className="text-gray-600">:الأقسام المختارة</span>
                </div>
              )}
            </div>
          </div>

          {/* Start Button */}
          <button
            onClick={handleStart}
            disabled={showCustomMode && selectedCategories.length === 0}
            className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-4 rounded-xl font-bold text-lg hover:from-green-700 hover:to-green-800 transition-all shadow-lg hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed transform hover:scale-[1.02] active:scale-98 flex items-center justify-center gap-2"
          >
            <BookOpen size={24} />
            ابدأ الاختبار
          </button>
        </div>
      </div>

      <style>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 6px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: #f1f1f1;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background: #10b981;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background: #059669;
        }
      `}</style>
    </div>
  );
};

export default QuizOptionsModal;