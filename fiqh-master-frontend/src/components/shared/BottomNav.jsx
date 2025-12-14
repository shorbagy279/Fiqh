import React from 'react';
import { Home, BookOpen, BarChart3, User } from 'lucide-react';

const NavButton = ({ icon, label, active, onClick }) => (
  <button 
    onClick={onClick} 
    className={`flex flex-col items-center gap-1 transition-colors min-w-[60px] ${active ? 'text-green-600' : 'text-gray-400 hover:text-gray-600'}`}
  >
    {icon}
    <span className="text-xs font-medium">{label}</span>
  </button>
);

const BottomNav = ({ currentScreen, navigate }) => (
  <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3 flex justify-around max-w-md mx-auto shadow-lg z-50">
    <NavButton 
      icon={<Home size={24} />} 
      label="الرئيسية" 
      active={currentScreen === 'home'} 
      onClick={() => navigate('home')} 
    />
    <NavButton 
      icon={<BookOpen size={24} />} 
      label="الأقسام" 
      active={currentScreen === 'categories'} 
      onClick={() => navigate('categories')} 
    />
    <NavButton 
      icon={<BarChart3 size={24} />} 
      label="الإحصائيات" 
      active={currentScreen === 'stats'} 
      onClick={() => navigate('stats')} 
    />
    <NavButton 
      icon={<User size={24} />} 
      label="الملف" 
      active={currentScreen === 'profile'} 
      onClick={() => navigate('profile')} 
    />
  </div>
);

export default BottomNav;