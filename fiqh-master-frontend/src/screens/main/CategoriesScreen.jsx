import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import BottomNav from '../../components/shared/BottomNav';
import LoadingSpinner from '../../components/shared/LoadingSpinner';
import QuizOptionsModal from '../../components/modals/QuizOptionsModal';

const CategoriesScreen = ({ navigate }) => {
  const { token } = useAuth();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showQuizOptions, setShowQuizOptions] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);

  useEffect(() => {
    if (token) {
      api.getCategories(token)
        .then(setCategories)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  const handleCategoryClick = (category) => {
    setSelectedCategory(category);
    setShowQuizOptions(true);
  };

  const handleStartQuiz = (options) => {
    setShowQuizOptions(false);
    navigate('quiz', {
      ...options,
      categoryId: selectedCategory.id
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-1">أقسام الفقه</h1>
        <p className="text-green-100 text-sm">اختر القسم الذي تريد التدرب عليه</p>
      </div>

      <div className="p-6">
        {loading ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner message="جاري تحميل الأقسام..." />
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-4">
            {categories.map(cat => (
              <button
                key={cat.id}
                onClick={() => handleCategoryClick(cat)}
                className="bg-white rounded-2xl p-5 shadow-md hover:shadow-xl transform hover:scale-105 transition-all active:scale-95"
              >
                <div className={`${cat.color || 'bg-green-500'} w-16 h-16 rounded-2xl flex items-center justify-center text-3xl mb-3 mx-auto shadow-md`}>
                  {cat.icon}
                </div>
                <h3 className="font-bold text-center mb-1 text-gray-800 text-base">{cat.nameAr}</h3>
                <p className="text-xs text-gray-500 text-center mb-2">{cat.nameEn}</p>
                <div className="bg-green-50 text-green-600 text-xs font-bold px-3 py-1 rounded-full text-center">
                  {cat.questionCount} سؤال
                </div>
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Quiz Options Modal */}
      {showQuizOptions && selectedCategory && (
        <QuizOptionsModal
          onClose={() => setShowQuizOptions(false)}
          onStart={handleStartQuiz}
          categories={categories}
          type="category"
        />
      )}

      <BottomNav currentScreen="categories" navigate={navigate} />
    </div>
  );
};

export default CategoriesScreen;