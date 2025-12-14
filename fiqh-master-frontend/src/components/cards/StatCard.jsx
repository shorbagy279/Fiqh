// src/components/cards/StatCard.jsx
import React from 'react';

const StatCard = ({ icon, label, value, color }) => (
  <div className="bg-white rounded-xl p-4 shadow-md hover:shadow-lg transition">
    <div className={`${color} w-12 h-12 rounded-xl flex items-center justify-center text-white mb-3 shadow-sm`}>
      {icon}
    </div>
    <p className="text-2xl font-bold mb-1 text-gray-800">{value}</p>
    <p className="text-sm text-gray-600">{label}</p>
  </div>
);

export default StatCard;