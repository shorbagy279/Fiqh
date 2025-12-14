import React, { useEffect } from 'react';
import { BookOpen, Sparkles } from 'lucide-react';

const SplashScreen = ({ navigate }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      navigate('login');
    }, 2000);
    
    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-600 via-green-700 to-green-800 flex flex-col items-center justify-center text-white p-8 relative overflow-hidden">
      {/* Background decorative elements */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-20 left-10 w-32 h-32 bg-white rounded-full blur-3xl"></div>
        <div className="absolute bottom-20 right-10 w-40 h-40 bg-yellow-300 rounded-full blur-3xl"></div>
      </div>

      {/* Content */}
      <div className="relative z-10 flex flex-col items-center">
        <div className="mb-8 relative">
          <div className="absolute inset-0 bg-yellow-400 rounded-full blur-2xl opacity-30 animate-pulse"></div>
          <div className="relative bg-white/10 backdrop-blur-lg p-8 rounded-3xl border border-white/20 shadow-2xl">
            <BookOpen size={80} className="text-white" strokeWidth={1.5} />
          </div>
        </div>
        
        <h1 className="text-5xl font-bold mb-3 tracking-tight">فقه ماستر</h1>
        <h2 className="text-2xl mb-6 font-light tracking-wide">Fiqh Master</h2>
        
        <p className="text-center text-green-100 mb-8 max-w-md leading-relaxed text-lg">
          تعلم الفقه الشيعي على مذهب السيد السيستاني
          <br />
          بطريقة تفاعلية وممتعة
        </p>

        <div className="flex items-center gap-2 text-green-200 animate-pulse">
          <Sparkles size={20} />
          <span className="text-sm">جاري التحضير...</span>
          <Sparkles size={20} />
        </div>
      </div>
    </div>
  );
};

export default SplashScreen;