export const ProfileScreen = ({ navigate }) => {
  const { user, logout } = useAuth();
  const [showLogoutConfirm, setShowLogoutConfirm] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('login');
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <div className="flex items-center gap-4 mb-4">
          <div className="w-20 h-20 bg-white/20 backdrop-blur-lg rounded-full flex items-center justify-center text-4xl border-2 border-white/30 shadow-lg">
            👤
          </div>
          <div className="flex-1">
            <h1 className="text-2xl font-bold">{user?.fullName}</h1>
            <p className="text-green-100 text-sm">{user?.email}</p>
          </div>
        </div>
      </div>

      <div className="px-6 -mt-4">
        {/* Stats Summary */}
        <div className="bg-white rounded-2xl p-5 shadow-lg mb-6">
          <div className="grid grid-cols-3 gap-4 text-center">
            <div>
              <p className="text-3xl font-bold text-green-600">{user?.totalQuizzes || 0}</p>
              <p className="text-xs text-gray-600 mt-1">اختبار</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-blue-600">{user?.currentStreak || 0}</p>
              <p className="text-xs text-gray-600 mt-1">يوم متتالي</p>
            </div>
            <div>
              <p className="text-3xl font-bold text-purple-600">{user?.badges?.length || 0}</p>
              <p className="text-xs text-gray-600 mt-1">شارة</p>
            </div>
          </div>
        </div>

        {/* Settings */}
        <div className="space-y-3 mb-6">
          <SettingItem 
            icon={<BookOpen />} 
            label="المرجع الديني" 
            value={user?.preferredMarjaName || "السيد السيستاني"} 
          />
          <SettingItem 
            icon={<Star />} 
            label="مستوى الصعوبة" 
            value={
              user?.difficultyLevel === 'beginner' ? 'مبتدئ' :
              user?.difficultyLevel === 'intermediate' ? 'متوسط' :
              user?.difficultyLevel === 'advanced' ? 'متقدم' : 'متوسط'
            } 
          />
          <SettingItem 
            icon={<Clock />} 
            label="التذكيرات اليومية" 
            value={user?.dailyReminders ? "مفعّل" : "معطّل"} 
          />
          <SettingItem 
            icon={<Award />} 
            label="الإنجازات" 
            value={`${user?.badges?.length || 0} شارة`} 
          />
        </div>

        {/* Logout Button */}
        <div className="space-y-3">
          <button 
            onClick={() => setShowLogoutConfirm(true)}
            className="w-full bg-white rounded-xl p-4 shadow-lg text-red-600 font-bold hover:bg-red-50 transition-all flex items-center justify-center gap-2 border-2 border-red-200"
          >
            <LogOut size={20} />
            تسجيل الخروج
          </button>
        </div>
      </div>

      {/* Logout Confirmation Modal */}
      {showLogoutConfirm && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-6 z-50">
          <div className="bg-white rounded-2xl p-6 max-w-sm w-full shadow-2xl">
            <h3 className="text-xl font-bold mb-3 text-gray-800">تأكيد تسجيل الخروج</h3>
            <p className="text-gray-600 mb-6">هل أنت متأكد من رغبتك في تسجيل الخروج؟</p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowLogoutConfirm(false)}
                className="flex-1 bg-gray-100 text-gray-700 py-3 rounded-xl font-bold hover:bg-gray-200 transition"
              >
                إلغاء
              </button>
              <button
                onClick={handleLogout}
                className="flex-1 bg-red-600 text-white py-3 rounded-xl font-bold hover:bg-red-700 transition"
              >
                تسجيل الخروج
              </button>
            </div>
          </div>
        </div>
      )}

      <BottomNav currentScreen="profile" navigate={navigate} />
    </div>
  );
};

const SettingItem = ({ icon, label, value }) => (
  <button className="w-full bg-white rounded-xl p-4 flex items-center gap-4 shadow-lg hover:shadow-xl transition-all">
    <div className="text-green-600 bg-green-50 p-3 rounded-xl">
      {icon}
    </div>
    <div className="flex-1 text-right">
      <p className="font-bold text-gray-800">{label}</p>
      <p className="text-sm text-gray-500">{value}</p>
    </div>
    <ChevronRight className="text-gray-400" />
  </button>
);

