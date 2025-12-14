import React, { useState, useEffect, createContext, useContext } from 'react';
import { BookOpen, Trophy, BarChart3, User, Home, ChevronRight, Check, X, Star, Clock, Award, Bookmark, RefreshCw, LogOut, Menu } from 'lucide-react';

// ============================================================================
// API SERVICE
// ============================================================================

const API_BASE = 'http://localhost:8080/api';

const api = {
  // Auth endpoints
  register: async (data) => {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Registration failed');
    return res.json();
  },
  
  login: async (data) => {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Login failed');
    return res.json();
  },
  
  getCurrentUser: async (token) => {
    const res = await fetch(`${API_BASE}/auth/me`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch user');
    return res.json();
  },
  
  // Category endpoints
  getCategories: async (token) => {
    const res = await fetch(`${API_BASE}/categories`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch categories');
    return res.json();
  },
  
  // Question endpoints
  getRandomQuestions: async (token, limit = 10) => {
    const res = await fetch(`${API_BASE}/questions/random?limit=${limit}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch questions');
    return res.json();
  },
  
  getCategoryQuestions: async (token, categoryId, limit = 10) => {
    const res = await fetch(`${API_BASE}/questions/category/${categoryId}?limit=${limit}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch questions');
    return res.json();
  },
  
  // Quiz endpoints
  startQuiz: async (token, data) => {
    const res = await fetch(`${API_BASE}/quiz/start`, {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Failed to start quiz');
    return res.json();
  },
  
  submitAnswer: async (token, data) => {
    const res = await fetch(`${API_BASE}/quiz/answer`, {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Failed to submit answer');
    return res.json();
  },
  
  completeQuiz: async (token, quizAttemptId, timeTaken) => {
    const res = await fetch(`${API_BASE}/quiz/complete/${quizAttemptId}?timeTaken=${timeTaken}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to complete quiz');
    return res.json();
  },
  
  getQuizHistory: async (token) => {
    const res = await fetch(`${API_BASE}/quiz/history`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch history');
    return res.json();
  },
  
  // User endpoints
  getUserStats: async (token) => {
    const res = await fetch(`${API_BASE}/user/stats`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch stats');
    return res.json();
  },
  
  getBookmarks: async (token) => {
    const res = await fetch(`${API_BASE}/user/bookmarks`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch bookmarks');
    return res.json();
  },
  
  addBookmark: async (token, questionId, notes) => {
    const res = await fetch(`${API_BASE}/user/bookmarks/${questionId}`, {
      method: 'POST',
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ notes })
    });
    if (!res.ok) throw new Error('Failed to add bookmark');
    return res.json();
  },
  
  removeBookmark: async (token, questionId) => {
    const res = await fetch(`${API_BASE}/user/bookmarks/${questionId}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to remove bookmark');
    return res.json();
  },
  
  // Leaderboard endpoints
  getLeaderboard: async (token, limit = 50) => {
    const res = await fetch(`${API_BASE}/leaderboard?limit=${limit}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    if (!res.ok) throw new Error('Failed to fetch leaderboard');
    return res.json();
  }
};

// ============================================================================
// AUTH CONTEXT
// ============================================================================

const AuthContext = createContext(null);

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.getCurrentUser(token)
        .then(setUser)
        .catch(() => {
          localStorage.removeItem('token');
          setToken(null);
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const response = await api.login({ email, password });
    localStorage.setItem('token', response.token);
    setToken(response.token);
    setUser(response.user);
  };

  const register = async (data) => {
    const response = await api.register(data);
    localStorage.setItem('token', response.token);
    setToken(response.token);
    setUser(response.user);
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

const useAuth = () => useContext(AuthContext);

// ============================================================================
// MAIN APP
// ============================================================================

const AppContent = () => {
  const { user, loading } = useAuth();
  const [currentScreen, setCurrentScreen] = useState('splash');

  useEffect(() => {
    if (!loading) {
      if (!user) {
        setCurrentScreen('login');
      } else if (currentScreen === 'splash' || currentScreen === 'login' || currentScreen === 'register') {
        setCurrentScreen('home');
      }
    }
  }, [user, loading]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex items-center justify-center">
        <div className="text-white text-xl">Loading...</div>
      </div>
    );
  }

  return (
    <div className="max-w-md mx-auto bg-white min-h-screen" style={{fontFamily: 'Arial, sans-serif'}}>
      {currentScreen === 'splash' && <SplashScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'login' && <LoginScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'register' && <RegisterScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'home' && <HomeScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'categories' && <CategoriesScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'quiz' && <QuizScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'stats' && <StatsScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'profile' && <ProfileScreen setScreen={setCurrentScreen} />}
      {currentScreen === 'bookmarks' && <BookmarksScreen setScreen={setCurrentScreen} />}
    </div>
  );
};

const FiqhQuizApp = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

// ============================================================================
// SCREENS
// ============================================================================

const SplashScreen = ({ setScreen }) => (
  <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex flex-col items-center justify-center text-white p-8">
    <div className="text-6xl mb-6">📖</div>
    <h1 className="text-4xl font-bold mb-2">فقه ماستر</h1>
    <h2 className="text-xl mb-8">Fiqh Master</h2>
    <p className="text-center text-green-100 mb-12 max-w-md">
      تعلم الفقه الشيعي على مذهب السيد السيستاني بطريقة تفاعلية وممتعة
    </p>
    <button 
      onClick={() => setScreen('login')}
      className="bg-white text-green-600 px-8 py-3 rounded-full font-bold hover:bg-green-50 transition"
    >
      ابدأ الآن
    </button>
  </div>
);

const LoginScreen = ({ setScreen }) => {
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      await login(email, password);
      setScreen('home');
    } catch (err) {
      setError('خطأ في البريد الإلكتروني أو كلمة المرور');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex flex-col items-center justify-center p-6">
      <div className="bg-white rounded-3xl p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <div className="text-5xl mb-4">📖</div>
          <h2 className="text-2xl font-bold text-gray-800">تسجيل الدخول</h2>
          <p className="text-gray-600 mt-2">مرحباً بعودتك!</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-right text-sm font-medium text-gray-700 mb-2">
              البريد الإلكتروني
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
              placeholder="example@email.com"
              required
            />
          </div>

          <div>
            <label className="block text-right text-sm font-medium text-gray-700 mb-2">
              كلمة المرور
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
              placeholder="••••••"
              required
            />
          </div>

          {error && (
            <div className="bg-red-50 text-red-600 px-4 py-3 rounded-xl text-center text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-600 text-white py-3 rounded-xl font-bold hover:bg-green-700 transition disabled:opacity-50"
          >
            {loading ? 'جاري الدخول...' : 'دخول'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-gray-600">
            ليس لديك حساب؟{' '}
            <button
              onClick={() => setScreen('register')}
              className="text-green-600 font-bold hover:text-green-700"
            >
              سجل الآن
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

const RegisterScreen = ({ setScreen }) => {
  const { register } = useAuth();
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
    preferredLanguage: 'ar'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      await register(formData);
      setScreen('home');
    } catch (err) {
      setError('فشل التسجيل. حاول مرة أخرى.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex flex-col items-center justify-center p-6">
      <div className="bg-white rounded-3xl p-8 w-full max-w-md">
        <div className="text-center mb-8">
          <div className="text-5xl mb-4">📖</div>
          <h2 className="text-2xl font-bold text-gray-800">إنشاء حساب جديد</h2>
          <p className="text-gray-600 mt-2">انضم إلينا اليوم!</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-right text-sm font-medium text-gray-700 mb-2">
              الاسم الكامل
            </label>
            <input
              type="text"
              value={formData.fullName}
              onChange={(e) => setFormData({...formData, fullName: e.target.value})}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
              placeholder="محمد أحمد"
              required
            />
          </div>

          <div>
            <label className="block text-right text-sm font-medium text-gray-700 mb-2">
              البريد الإلكتروني
            </label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
              placeholder="example@email.com"
              required
            />
          </div>

          <div>
            <label className="block text-right text-sm font-medium text-gray-700 mb-2">
              كلمة المرور
            </label>
            <input
              type="password"
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-green-500 focus:border-transparent"
              placeholder="••••••"
              required
              minLength={6}
            />
          </div>

          {error && (
            <div className="bg-red-50 text-red-600 px-4 py-3 rounded-xl text-center text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-green-600 text-white py-3 rounded-xl font-bold hover:bg-green-700 transition disabled:opacity-50"
          >
            {loading ? 'جاري التسجيل...' : 'تسجيل'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-gray-600">
            لديك حساب بالفعل؟{' '}
            <button
              onClick={() => setScreen('login')}
              className="text-green-600 font-bold hover:text-green-700"
            >
              سجل الدخول
            </button>
          </p>
        </div>
      </div>
    </div>
  );
};

const HomeScreen = ({ setScreen }) => {
  const { user, token } = useAuth();
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    if (token) {
      api.getCategories(token).then(setCategories).catch(console.error);
    }
  }, [token]);

  return (
    <div className="min-h-screen bg-gray-50 pb-20">
      <div className="bg-gradient-to-br from-green-600 to-green-700 text-white p-6 rounded-b-3xl">
        <h1 className="text-2xl font-bold mb-2">السلام عليكم 👋</h1>
        <p className="text-green-100">{user?.fullName || 'مستخدم'}</p>
        
        <div className="mt-6 bg-white/10 backdrop-blur rounded-2xl p-4">
          <div className="flex justify-between items-center mb-2">
            <span className="text-sm">سلسلة الأيام</span>
            <span className="text-2xl font-bold">{user?.currentStreak || 0} 🔥</span>
          </div>
          <div className="flex gap-1">
            {[1,2,3,4,5,6,7].map(day => (
              <div key={day} className={`flex-1 h-2 rounded ${day <= (user?.currentStreak || 0) ? 'bg-yellow-400' : 'bg-white/20'}`}></div>
            ))}
          </div>
        </div>
      </div>

      <div className="p-6">
        <div className="bg-gradient-to-r from-yellow-400 to-orange-500 rounded-2xl p-6 text-white mb-6 shadow-lg">
          <div className="flex items-center gap-3 mb-3">
            <Trophy size={32} />
            <div>
              <h3 className="font-bold text-lg">التحدي اليومي</h3>
              <p className="text-sm text-yellow-100">10 أسئلة • 5 دقائق</p>
            </div>
          </div>
          <button 
            onClick={() => setScreen('quiz')}
            className="bg-white text-orange-500 w-full py-3 rounded-xl font-bold hover:bg-orange-50 transition"
          >
            ابدأ التحدي
          </button>
        </div>

        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <BookOpen size={24} className="text-green-600" />
          اختر طريقة التعلم
        </h2>

        <div className="space-y-3">
          <QuickModeCard
            title="اختبار عشوائي"
            description="10 أسئلة من جميع الأقسام"
            icon="🎲"
            color="bg-blue-500"
            onClick={() => setScreen('quiz')}
          />
          <QuickModeCard
            title="اختر قسماً محدداً"
            description="تدرب على موضوع معين"
            icon="📚"
            color="bg-purple-500"
            onClick={() => setScreen('categories')}
          />
          <QuickModeCard
            title="الأسئلة المحفوظة"
            description="راجع الأسئلة الصعبة"
            icon="⭐"
            color="bg-pink-500"
            onClick={() => setScreen('bookmarks')}
          />
        </div>
      </div>

      <BottomNav currentScreen="home" setScreen={setScreen} />
    </div>
  );
};

const QuickModeCard = ({ title, description, icon, color, onClick }) => (
  <button 
    onClick={onClick}
    className="w-full bg-white rounded-xl p-4 flex items-center gap-4 shadow hover:shadow-md transition"
  >
    <div className={`${color} w-12 h-12 rounded-xl flex items-center justify-center text-2xl`}>
      {icon}
    </div>
    <div className="flex-1 text-right">
      <h3 className="font-bold">{title}</h3>
      <p className="text-sm text-gray-500">{description}</p>
    </div>
    <ChevronRight className="text-gray-400" />
  </button>
);

const CategoriesScreen = ({ setScreen }) => {
  const { token } = useAuth();
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    if (token) {
      api.getCategories(token).then(setCategories).catch(console.error);
    }
  }, [token]);

  return (
    <div className="min-h-screen bg-gray-50 pb-20">
      <div className="bg-green-600 text-white p-6">
        <h1 className="text-2xl font-bold">أقسام الفقه</h1>
        <p className="text-green-100">اختر القسم الذي تريد التدرب عليه</p>
      </div>

      <div className="p-6 grid grid-cols-2 gap-4">
        {categories.map(cat => (
          <button
            key={cat.id}
            onClick={() => setScreen('quiz')}
            className="bg-white rounded-2xl p-4 shadow hover:shadow-lg transition"
          >
            <div className={`${cat.color || 'bg-green-500'} w-16 h-16 rounded-2xl flex items-center justify-center text-3xl mb-3 mx-auto`}>
              {cat.icon}
            </div>
            <h3 className="font-bold text-center mb-1">{cat.nameAr}</h3>
            <p className="text-xs text-gray-500 text-center">{cat.nameEn}</p>
            <p className="text-xs text-green-600 text-center mt-2">{cat.questionCount} سؤال</p>
          </button>
        ))}
      </div>

      <BottomNav currentScreen="categories" setScreen={setScreen} />
    </div>
  );
};

const QuizScreen = ({ setScreen }) => {
  const { token } = useAuth();
  const [questions, setQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showExplanation, setShowExplanation] = useState(false);
  const [score, setScore] = useState(0);
  const [quizAttemptId, setQuizAttemptId] = useState(null);
  const [startTime] = useState(Date.now());

  useEffect(() => {
    const startQuiz = async () => {
      try {
        const quizData = await api.startQuiz(token, {
          categoryId: null,
          quizType: 'random',
          questionCount: 10
        });
        setQuizAttemptId(quizData.quizAttemptId);
        
        const questionsData = await api.getRandomQuestions(token, 10);
        setQuestions(questionsData);
      } catch (error) {
        console.error('Error starting quiz:', error);
      }
    };
    
    if (token) startQuiz();
  }, [token]);

  const handleAnswer = async (index) => {
    setSelectedAnswer(index);
    setShowExplanation(true);
    
    const question = questions[currentQuestion];
    const isCorrect = index === question.correctAnswer;
    
    if (isCorrect) setScore(score + 1);
    
    try {
      await api.submitAnswer(token, {
        questionId: question.id,
        quizAttemptId,
        selectedAnswer: index,
        timeTakenSeconds: Math.floor((Date.now() - startTime) / 1000)
      });
    } catch (error) {
      console.error('Error submitting answer:', error);
    }
  };

  const handleNext = async () => {
    if (currentQuestion >= questions.length - 1) {
      try {
        const timeTaken = Math.floor((Date.now() - startTime) / 1000);
        await api.completeQuiz(token, quizAttemptId, timeTaken);
        setScreen('home');
      } catch (error) {
        console.error('Error completing quiz:', error);
      }
    } else {
      setCurrentQuestion(currentQuestion + 1);
      setSelectedAnswer(null);
      setShowExplanation(false);
    }
  };

  if (questions.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-xl text-gray-600">Loading questions...</div>
      </div>
    );
  }

  const question = questions[currentQuestion];

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-green-600 text-white p-6">
        <div className="flex justify-between items-center mb-4">
          <button onClick={() => setScreen('home')} className="text-white">
            <X size={24} />
          </button>
          <span className="font-bold">السؤال {currentQuestion + 1} من {questions.length}</span>
          <div className="w-6"></div>
        </div>
        <div className="bg-white/20 rounded-full h-2">
          <div 
            className="bg-yellow-400 h-2 rounded-full transition-all duration-300"
            style={{width: `${((currentQuestion + 1) / questions.length) * 100}%`}}
          ></div>
        </div>
      </div>

      <div className="p-6">
        <div className="bg-white rounded-2xl p-6 shadow-lg mb-6">
          <div className="flex gap-2 mb-4">
            <span className="bg-green-100 text-green-600 px-3 py-1 rounded-full text-xs font-bold">
              {question.difficulty}
            </span>
          </div>
          
          <h2 className="text-xl font-bold mb-2 text-right leading-relaxed">
            {question.questionAr}
          </h2>
          <p className="text-sm text-gray-500 text-left mb-6">
            {question.questionEn}
          </p>

          <div className="space-y-3">
            {question.optionsAr?.map((option, index) => {
              let bgColor = 'bg-gray-50';
              let borderColor = 'border-gray-200';
              let textColor = 'text-gray-800';
              
              if (showExplanation) {
                if (index === question.correctAnswer) {
                  bgColor = 'bg-green-50';
                  borderColor = 'border-green-500';
                  textColor = 'text-green-700';
                } else if (index === selectedAnswer) {
                  bgColor = 'bg-red-50';
                  borderColor = 'border-red-500';
                  textColor = 'text-red-700';
                }
              }

              return (
                <button
                  key={index}
                  onClick={() => !showExplanation && handleAnswer(index)}
                  disabled={showExplanation}
                  className={`w-full p-4 rounded-xl border-2 ${bgColor} ${borderColor} text-right transition relative ${!showExplanation && 'hover:border-green-500'}`}
                >
                  <span className={`font-medium ${textColor}`}>{option}</span>
                  {showExplanation && index === question.correctAnswer && (
                    <Check className="absolute left-4 top-1/2 -translate-y-1/2 text-green-600" size={24} />
                  )}
                  {showExplanation && index === selectedAnswer && index !== question.correctAnswer && (
                    <X className="absolute left-4 top-1/2 -translate-y-1/2 text-red-600" size={24} />
                  )}
                </button>
              );
            })}
          </div>
        </div>

        {showExplanation && question.explanationAr && (
          <div className="bg-blue-50 border-2 border-blue-200 rounded-2xl p-6 mb-6">
            <h3 className="font-bold text-blue-900 mb-3 flex items-center gap-2">
              <BookOpen size={20} />
              الشرح
            </h3>
            <p className="text-blue-800 mb-4 text-right leading-relaxed">
              {question.explanationAr}
            </p>
            {question.referenceAr && (
              <div className="bg-white rounded-lg p-3">
                <p className="text-xs text-gray-600 text-right">
                  📚 المرجع: {question.referenceAr}
                </p>
              </div>
            )}
          </div>
        )}

        {showExplanation && (
          <button
            onClick={handleNext}
            className="w-full bg-green-600 text-white py-4 rounded-xl font-bold hover:bg-green-700 transition"
          >
            {currentQuestion >= questions.length - 1 ? 'إنهاء الاختبار' : 'السؤال التالي'}
          </button>
        )}
      </div>
    </div>
  );
};

const StatsScreen = ({ setScreen }) => {
  const { user, token } = useAuth();
  const [stats, setStats] = useState(null);
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    if (token) {
      api.getUserStats(token).then(setStats).catch(console.error);
      api.getCategories(token).then(setCategories).catch(console.error);
    }
  }, [token]);

  if (!stats) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-xl text-gray-600">Loading stats...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-20">
      <div className="bg-green-600 text-white p-6">
        <h1 className="text-2xl font-bold mb-2">إحصائياتك</h1>
        <p className="text-green-100">تابع تقدمك في تعلم الفقه</p>
      </div>

      <div className="p-6">
        <div className="bg-gradient-to-r from-purple-500 to-pink-500 rounded-2xl p-6 text-white mb-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-lg font-bold">الرتبة الحالية</h3>
              <p className="text-2xl font-bold mt-2">{user?.currentRank || 'فقيه مبتدئ'}</p>
            </div>
            <Award size={48} />
          </div>
          <div className="flex gap-2">
            {user?.badges?.map((badge, i) => (
              <div key={i} className="bg-white/20 backdrop-blur w-12 h-12 rounded-xl flex items-center justify-center text-2xl">
                {badge}
              </div>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 mb-6">
          <StatCard icon={<Trophy />} label="إجمالي الاختبارات" value={stats.totalQuizzes || 0} color="bg-blue-500" />
          <StatCard icon={<Check />} label="إجابات صحيحة" value={stats.totalCorrectAnswers || 0} color="bg-green-500" />
          <StatCard icon={<Star />} label="النسبة الكلية" value={`${Math.round(stats.overallAccuracy || 0)}%`} color="bg-yellow-500" />
          <StatCard icon={<Clock />} label="سلسلة الأيام" value={`${stats.currentStreak || 0} يوم`} color="bg-red-500" />
        </div>

        <h2 className="text-xl font-bold mb-4">الأداء حسب القسم</h2>
        <div className="space-y-3">
          {categories.slice(0, 4).map(cat => (
            <CategoryProgress
              key={cat.id}
              name={cat.nameAr}
              icon={cat.icon}
              progress={Math.floor(Math.random() * 40 + 60)}
              color={cat.color}
            />
          ))}
        </div>
      </div>

      <BottomNav currentScreen="stats" setScreen={setScreen} />
    </div>
  );
};

const StatCard = ({ icon, label, value, color }) => (
  <div className="bg-white rounded-xl p-4 shadow">
    <div className={`${color} w-10 h-10 rounded-lg flex items-center justify-center text-white mb-3`}>
      {icon}
    </div>
    <p className="text-2xl font-bold mb-1">{value}</p>
    <p className="text-sm text-gray-500">{label}</p>
  </div>
);

const CategoryProgress = ({ name, icon, progress, color }) => (
  <div className="bg-white rounded-xl p-4 shadow">
    <div className="flex items-center gap-3 mb-3">
      <div className={`${color} w-10 h-10 rounded-lg flex items-center justify-center text-xl`}>
        {icon}
      </div>
      <div className="flex-1">
        <h3 className="font-bold">{name}</h3>
        <p className="text-sm text-gray-500">{progress}% مكتمل</p>
      </div>
    </div>
    <div className="bg-gray-200 rounded-full h-2">
      <div className={`${color} h-2 rounded-full`} style={{width: `${progress}%`}}></div>
    </div>
  </div>
);

const ProfileScreen = ({ setScreen }) => {
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    setScreen('login');
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-20">
      <div className="bg-green-600 text-white p-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-20 h-20 bg-white/20 backdrop-blur rounded-full flex items-center justify-center text-3xl">
            👤
          </div>
          <div>
            <h1 className="text-2xl font-bold">{user?.fullName}</h1>
            <p className="text-green-100">{user?.email}</p>
          </div>
        </div>
      </div>

      <div className="p-6 space-y-3">
        <SettingItem icon={<BookOpen />} label="المرجع الديني" value={user?.preferredMarjaName || "السيد السيستاني"} />
        <SettingItem icon={<Star />} label="مستوى الصعوبة" value="متوسط" />
        <SettingItem icon={<Clock />} label="التذكيرات اليومية" value={user?.dailyReminders ? "مفعّل" : "معطّل"} />
        <SettingItem icon={<Award />} label="الإنجازات" value={`${user?.badges?.length || 0} شارة`} />
        
        <div className="pt-4">
          <button 
            onClick={handleLogout}
            className="w-full bg-white rounded-xl p-4 shadow text-red-600 font-bold hover:bg-red-50 transition flex items-center justify-center gap-2"
          >
            <LogOut size={20} />
            تسجيل الخروج
          </button>
        </div>
      </div>

      <BottomNav currentScreen="profile" setScreen={setScreen} />
    </div>
  );
};

const SettingItem = ({ icon, label, value }) => (
  <button className="w-full bg-white rounded-xl p-4 flex items-center gap-4 shadow hover:shadow-md transition">
    <div className="text-green-600">
      {icon}
    </div>
    <div className="flex-1 text-right">
      <p className="font-bold">{label}</p>
      <p className="text-sm text-gray-500">{value}</p>
    </div>
    <ChevronRight className="text-gray-400" />
  </button>
);

const BookmarksScreen = ({ setScreen }) => {
  const { token } = useAuth();
  const [bookmarks, setBookmarks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.getBookmarks(token)
        .then(setBookmarks)
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [token]);

  const handleRemoveBookmark = async (questionId) => {
    try {
      await api.removeBookmark(token, questionId);
      setBookmarks(bookmarks.filter(b => b.id !== questionId));
    } catch (error) {
      console.error('Error removing bookmark:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-20">
      <div className="bg-green-600 text-white p-6">
        <h1 className="text-2xl font-bold mb-2">الأسئلة المحفوظة</h1>
        <p className="text-green-100">راجع الأسئلة التي حفظتها</p>
      </div>

      <div className="p-6">
        {loading ? (
          <div className="text-center py-8 text-gray-600">Loading...</div>
        ) : bookmarks.length === 0 ? (
          <div className="text-center py-8">
            <Bookmark size={48} className="mx-auto text-gray-300 mb-4" />
            <p className="text-gray-600">لا توجد أسئلة محفوظة بعد</p>
          </div>
        ) : (
          <div className="space-y-4">
            {bookmarks.map((question) => (
              <div key={question.id} className="bg-white rounded-xl p-4 shadow">
                <div className="flex justify-between items-start mb-2">
                  <span className="bg-green-100 text-green-600 px-3 py-1 rounded-full text-xs font-bold">
                    {question.categoryName}
                  </span>
                  <button
                    onClick={() => handleRemoveBookmark(question.id)}
                    className="text-red-500 hover:text-red-700"
                  >
                    <X size={20} />
                  </button>
                </div>
                <h3 className="font-bold text-right mb-2">{question.questionAr}</h3>
                <p className="text-sm text-gray-500 text-left">{question.questionEn}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      <BottomNav currentScreen="bookmarks" setScreen={setScreen} />
    </div>
  );
};

const BottomNav = ({ currentScreen, setScreen }) => (
  <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-4 py-3 flex justify-around max-w-md mx-auto">
    <NavButton icon={<Home size={24} />} label="الرئيسية" active={currentScreen === 'home'} onClick={() => setScreen('home')} />
    <NavButton icon={<BookOpen size={24} />} label="الأقسام" active={currentScreen === 'categories'} onClick={() => setScreen('categories')} />
    <NavButton icon={<BarChart3 size={24} />} label="الإحصائيات" active={currentScreen === 'stats'} onClick={() => setScreen('stats')} />
    <NavButton icon={<User size={24} />} label="الملف" active={currentScreen === 'profile'} onClick={() => setScreen('profile')} />
  </div>
);

const NavButton = ({ icon, label, active, onClick }) => (
  <button onClick={onClick} className={`flex flex-col items-center gap-1 ${active ? 'text-green-600' : 'text-gray-400'}`}>
    {icon}
    <span className="text-xs">{label}</span>
  </button>
);

export default FiqhQuizApp;