// src/components/cards/QuickModeCard.jsx
import React from 'react';
import { ChevronRight } from 'lucide-react';

const QuickModeCard = ({ title, description, icon, color, onClick, disabled }) => (
  <button 
    onClick={onClick}
    disabled={disabled}
    className="w-full bg-white rounded-xl p-4 flex items-center gap-4 shadow hover:shadow-md transition disabled:opacity-50 disabled:cursor-not-allowed"
  >
    <div className={`${color} w-12 h-12 rounded-xl flex items-center justify-center text-2xl shadow-sm`}>
      {icon}
    </div>
    <div className="flex-1 text-right">
      <h3 className="font-bold text-gray-800">{title}</h3>
      <p className="text-sm text-gray-500">{description}</p>
    </div>
    <ChevronRight className="text-gray-400" />
  </button>
);

export default QuickModeCard;