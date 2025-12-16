import React, { useState } from 'react';
import { Search, Filter, X } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';

const SearchScreen = ({ navigate }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState('all');
  const [selectedCategory, setSelectedCategory] = useState('all');

  const difficulties = [
    { value: 'all', label: 'الكل' },
    { value: 'beginner', label: 'مبتدئ' },
    { value: 'intermediate', label: 'متوسط' },
    { value: 'advanced', label: 'متقدم' }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-4 flex items-center gap-2">
          <Search size={28} />
          البحث في الأسئلة
        </h1>

        {/* Search Input */}
        <div className="relative">
          <Search className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="ابحث عن سؤال..."
            className="w-full pr-12 pl-4 py-3 rounded-xl text-gray-900 focus:ring-2 focus:ring-yellow-400 focus:outline-none"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery('')}
              className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
            >
              <X size={20} />
            </button>
          )}
        </div>
      </div>

      <div className="p-6">
        {/* Filters */}
        <div className="bg-white rounded-xl p-4 shadow-lg mb-6">
          <div className="flex items-center gap-2 mb-4">
            <Filter size={20} className="text-green-600" />
            <h3 className="font-bold text-gray-800">تصفية النتائج</h3>
          </div>

          {/* Difficulty Filter */}
          <div className="mb-4">
            <label className="block text-sm font-bold text-gray-700 mb-2 text-right">
              مستوى الصعوبة
            </label>
            <div className="flex gap-2 flex-wrap">
              {difficulties.map((diff) => (
                <button
                  key={diff.value}
                  onClick={() => setSelectedDifficulty(diff.value)}
                  className={`px-4 py-2 rounded-xl font-bold text-sm transition ${
                    selectedDifficulty === diff.value
                      ? 'bg-green-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  {diff.label}
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Search Results Placeholder */}
        <div className="text-center py-12">
          <div className="bg-gray-100 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4">
            <Search size={48} className="text-gray-300" />
          </div>
          <h3 className="text-xl font-bold text-gray-700 mb-2">ابحث عن سؤال</h3>
          <p className="text-gray-500">استخدم البحث والفلاتر أعلاه</p>
        </div>
      </div>

      <BottomNav currentScreen="search" navigate={navigate} />
    </div>
  );
};

export default SearchScreen;
