import React from 'react';
import { Target, CheckCircle } from 'lucide-react';

const DailyGoalCard = ({ current = 0, goal = 10 }) => {
  const progress = Math.min((current / goal) * 100, 100);
  const isCompleted = current >= goal;

  return (
    <div className="bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl p-5 text-white shadow-lg">
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="font-bold text-lg mb-1">الهدف اليومي</h3>
          <p className="text-purple-100 text-sm">{current} من {goal} سؤال</p>
        </div>
        {isCompleted ? (
          <CheckCircle size={32} className="text-yellow-300" />
        ) : (
          <Target size={32} />
        )}
      </div>

      <div className="bg-white/20 rounded-full h-3 overflow-hidden mb-2">
        <div
          className="bg-white h-full rounded-full transition-all duration-500"
          style={{ width: `${progress}%` }}
        ></div>
      </div>

      {isCompleted && (
        <p className="text-sm text-yellow-300 font-bold">
          🎉 أحسنت! أكملت هدفك اليومي!
        </p>
      )}
    </div>
  );
};

export default DailyGoalCard;
