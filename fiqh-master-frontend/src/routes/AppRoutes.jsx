import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import LoadingSpinner from '../components/shared/LoadingSpinner';

// Auth Screens
import SplashScreen from '../screens/auth/SplashScreen';
import LoginScreen from '../screens/auth/LoginScreen';
import RegisterScreen from '../screens/auth/RegisterScreen';

// Main Screens
import HomeScreen from '../screens/main/HomeScreen';
import CategoriesScreen from '../screens/main/CategoriesScreen';
import QuizScreen from '../screens/main/QuizScreen';
import CustomQuizScreen from '../screens/main/CustomQuizScreen';
import ResultsScreen from '../screens/main/ResultsScreen';
import StatsScreen from '../screens/main/StatsScreen';
import ProfileScreen from '../screens/main/ProfileScreen';
import BookmarksScreen from '../screens/main/BookmarksScreen';
import LeaderboardScreen from '../screens/main/LeaderboardScreen';

// New Scheduled Exam Screens
import ScheduleExamScreen from '../screens/main/ScheduleExamScreen';
import JoinExamScreen from '../screens/main/JoinExamScreen';

const AppRoutes = () => {
  const { user, loading } = useAuth();
  const [currentScreen, setCurrentScreen] = useState('splash');
  const [screenData, setScreenData] = useState({});

  useEffect(() => {
    if (!loading) {
      if (!user) {
        setCurrentScreen('login');
      } else if (currentScreen === 'splash' || currentScreen === 'login' || currentScreen === 'register') {
        setCurrentScreen('home');
      }
    }
  }, [user, loading, currentScreen]);

  const navigate = (screen, data = {}) => {
    setCurrentScreen(screen);
    setScreenData(data);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-green-600 to-green-800 flex items-center justify-center">
        <LoadingSpinner size="lg" message="جاري التحميل..." />
      </div>
    );
  }

  const screens = {
    splash: <SplashScreen navigate={navigate} />,
    login: <LoginScreen navigate={navigate} />,
    register: <RegisterScreen navigate={navigate} />,
    home: <HomeScreen navigate={navigate} />,
    categories: <CategoriesScreen navigate={navigate} />,
    quiz: <QuizScreen navigate={navigate} data={screenData} />,
    customQuiz: <CustomQuizScreen navigate={navigate} />,
    scheduleExam: <ScheduleExamScreen navigate={navigate} data={screenData} />,
    joinExam: <JoinExamScreen navigate={navigate} />,
    results: <ResultsScreen navigate={navigate} data={screenData} />,
    stats: <StatsScreen navigate={navigate} />,
    profile: <ProfileScreen navigate={navigate} />,
    bookmarks: <BookmarksScreen navigate={navigate} />,
    leaderboard: <LeaderboardScreen navigate={navigate} />
  };

  return screens[currentScreen] || screens.home;
};

export default AppRoutes;