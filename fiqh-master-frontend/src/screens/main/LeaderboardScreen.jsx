import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { Trophy } from 'lucide-react';
import BottomNav from '../../components/shared/BottomNav';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const LeaderboardScreen = ({ navigate }) => {
  const { token } = useAuth();
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('global');

  useEffect(() => {
    if (token) {
      const fetchData = activeTab === 'global' 
        ? api.getLeaderboard(token)
        : api.getStreakLeaderboard(token);
      
      fetchData
        .then(setLeaderboard)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token, activeTab]);

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-2 flex items-center gap-2">
          <Trophy size={28} />
          لوحة المتصدرين
        </h1>
        <p className="text-green-100">تنافس مع المستخدمين الآخرين</p>
      </div>

      <div className="px-6 -mt-4">
        {/* Tabs */}
        <div className="flex gap-2 bg-white rounded-xl p-1 shadow-lg mb-6">
          <button
            onClick={() => setActiveTab('global')}
            className={`flex-1 py-3 rounded-lg font-bold transition ${
              activeTab === 'global' 
                ? 'bg-green-600 text-white' 
                : 'text-gray-600 hover:bg-gray-100'
            }`}
          >
            الأعلى نقاطاً
          </button>
          <button
            onClick={() => setActiveTab('streak')}
            className={`flex-1 py-3 rounded-lg font-bold transition ${
              activeTab === 'streak' 
                ? 'bg-green-600 text-white' 
                : 'text-gray-600 hover:bg-gray-100'
            }`}
          >
            الأطول سلسلة
          </button>
        </div>

        {/* Leaderboard */}
        {loading ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner message="جاري التحميل..." />
          </div>
        ) : (
          <div className="space-y-3">
            {leaderboard.slice(0, 50).map((entry, index) => (
              <LeaderboardItem 
                key={entry.userId}
                rank={index + 1}
                entry={entry}
                activeTab={activeTab}
              />
            ))}
          </div>
        )}
      </div>

      <BottomNav currentScreen="leaderboard" navigate={navigate} />
    </div>
  );
};

const LeaderboardItem = ({ rank, entry, activeTab }) => {
  const getMedalColor = (rank) => {
    if (rank === 1) return 'from-yellow-400 to-yellow-600';
    if (rank === 2) return 'from-gray-300 to-gray-500';
    if (rank === 3) return 'from-orange-400 to-orange-600';
    return 'from-gray-100 to-gray-200';
  };

  return (
    <div className="bg-white rounded-xl p-4 shadow-lg hover:shadow-xl transition-all flex items-center gap-4">
      <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${getMedalColor(rank)} flex items-center justify-center text-white font-bold text-lg shadow-md`}>
        {rank <= 3 ? ['🥇', '🥈', '🥉'][rank - 1] : rank}
      </div>
      <div className="flex-1">
        <h3 className="font-bold text-gray-800">{entry.fullName}</h3>
        <p className="text-sm text-gray-500">
          {activeTab === 'global' 
            ? `${entry.totalCorrectAnswers} إجابة صحيحة`
            : `${entry.currentStreak} يوم متتالي`
          }
        </p>
      </div>
      <div className="text-right">
        <span className="bg-green-100 text-green-600 px-3 py-1 rounded-full text-xs font-bold">
          {entry.currentRank}
        </span>
      </div>
    </div>
  );
};

export default LeaderboardScreen;