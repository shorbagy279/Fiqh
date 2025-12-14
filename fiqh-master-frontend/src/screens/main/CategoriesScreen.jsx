import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { X } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

export const CategoriesScreen = ({ navigate }) => {
  const { token } = useAuth();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.getCategories(token)
        .then(setCategories)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <div className="flex items-center justify-between mb-2">
          <h1 className="text-2xl font-bold">أقسام الفقه</h1>
          <button onClick={() => navigate('home')} className="p-2 hover:bg-white/10 rounded-lg transition">
            <X size={24} />
          </button>
        </div>
        <p className="text-green-100">اختر القسم الذي تريد التدرب عليه</p>
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
                onClick={() => navigate('quiz', { type: 'category', categoryId: cat.id })}
                className="bg-white rounded-2xl p-5 shadow-lg hover:shadow-xl transform hover:scale-105 transition-all"
              >
                <div className={`${cat.color || 'bg-green-500'} w-16 h-16 rounded-2xl flex items-center justify-center text-3xl mb-3 mx-auto shadow-md`}>
                  {cat.icon}
                </div>
                <h3 className="font-bold text-center mb-1 text-gray-800">{cat.nameAr}</h3>
                <p className="text-xs text-gray-500 text-center mb-2">{cat.nameEn}</p>
                <div className="bg-green-50 text-green-600 text-xs font-bold px-3 py-1 rounded-full text-center">
                  {cat.questionCount} سؤال
                </div>
              </button>
            ))}
          </div>
        )}
      </div>

      <BottomNav currentScreen="categories" navigate={navigate} />
    </div>
  );
};
