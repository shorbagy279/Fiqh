import React from 'react';

const CategoryProgress = ({ name, icon, progress, color }) => (
  <div className="bg-white rounded-xl p-4 shadow-md hover:shadow-lg transition">
    <div className="flex items-center gap-3 mb-3">
      <div className={`${color} w-12 h-12 rounded-xl flex items-center justify-center text-2xl shadow-sm`}>
        {icon}
      </div>
      <div className="flex-1">
        <h3 className="font-bold text-gray-800">{name}</h3>
        <p className="text-sm text-gray-600">{progress}% مكتمل</p>
      </div>
    </div>
    <div className="bg-gray-200 rounded-full h-2.5 overflow-hidden">
      <div 
        className={`${color} h-full rounded-full transition-all duration-500`} 
        style={{width: `${progress}%`}}
      ></div>
    </div>
  </div>
);

export default CategoryProgress;