import React from 'react';

const StreakDisplay = ({ streak = 0 }) => (
  <div className="bg-white/10 backdrop-blur rounded-2xl p-4">
    <div className="flex justify-between items-center mb-2">
      <span className="text-sm">سلسلة الأيام</span>
      <span className="text-2xl font-bold">{streak} 🔥</span>
    </div>
    <div className="flex gap-1">
      {[1,2,3,4,5,6,7].map(day => (
        <div 
          key={day} 
          className={`flex-1 h-2 rounded transition-all ${day <= streak ? 'bg-yellow-400' : 'bg-white/20'}`}
        ></div>
      ))}
    </div>
  </div>
);

export default StreakDisplay;