import React from 'react';
import { Home, BookOpen, BarChart3, User } from 'lucide-react';

const NavButton = ({ icon, label, active, onClick }) => (
  <button 
    onClick={onClick} 
    className={`flex flex-col items-center gap-1 transition-colors ${active ? 'text-green-600' : 'text-gray-400 hover:text-gray-600'}`}
  >
    {icon}
    <span className="text-xs font-medium">{label}</span>
  </button>
);

const BottomNav = ({ currentScreen, setScreen }) => (
  <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3 flex justify-around max-w-md mx-auto shadow-lg">
    <NavButton icon={<Home size={24} />} label="الرئيسية" active={currentScreen === 'home'} onClick={() => setScreen('home')} />
    <NavButton icon={<BookOpen size={24} />} label="الأقسام" active={currentScreen === 'categories'} onClick={() => setScreen('categories')} />
    <NavButton icon={<BarChart3 size={24} />} label="الإحصائيات" active={currentScreen === 'stats'} onClick={() => setScreen('stats')} />
    <NavButton icon={<User size={24} />} label="الملف" active={currentScreen === 'profile'} onClick={() => setScreen('profile')} />
  </div>
);

export default BottomNav;