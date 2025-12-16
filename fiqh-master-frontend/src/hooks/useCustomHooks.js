import { useState, useEffect, useCallback, useRef } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api, { cache } from '../services/api';

// ============ useApi Hook ============
// Generic hook for API calls with loading, error, and caching
export const useApi = (apiFunc, dependencies = [], options = {}) => {
  const { token } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const mounted = useRef(true);
  
  const {
    cache: enableCache = false,
    cacheKey = '',
    cacheTTL = 300000,
    immediate = true
  } = options;

  const fetchData = useCallback(async (...args) => {
    if (!token) return;

    // Check cache first
    if (enableCache && cacheKey) {
      const cachedData = cache.get(cacheKey);
      if (cachedData) {
        setData(cachedData);
        setLoading(false);
        return cachedData;
      }
    }

    try {
      setLoading(true);
      setError(null);
      const result = await apiFunc(token, ...args);
      
      if (mounted.current) {
        setData(result);
        if (enableCache && cacheKey) {
          cache.set(cacheKey, result, cacheTTL);
        }
      }
      
      return result;
    } catch (err) {
      if (mounted.current) {
        setError(err.message || 'حدث خطأ');
      }
      throw err;
    } finally {
      if (mounted.current) {
        setLoading(false);
      }
    }
  }, [token, apiFunc, enableCache, cacheKey, cacheTTL]);

  useEffect(() => {
    mounted.current = true;
    if (immediate) {
      fetchData();
    }
    return () => {
      mounted.current = false;
    };
  }, dependencies);

  const refetch = useCallback(() => {
    return fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch, fetchData };
};

// ============ useCategories Hook ============
export const useCategories = () => {
  return useApi(
    api.getCategories,
    [],
    { cache: true, cacheKey: 'categories', cacheTTL: 600000 }
  );
};

// ============ useUserStats Hook ============
export const useUserStats = () => {
  return useApi(
    api.getUserStats,
    [],
    { cache: true, cacheKey: 'user_stats', cacheTTL: 60000 }
  );
};

// ============ useQuizHistory Hook ============
export const useQuizHistory = (page = 0, size = 20) => {
  return useApi(
    (token) => api.getQuizHistory(token, page, size),
    [page, size],
    { cache: false }
  );
};

// ============ useBookmarks Hook ============
export const useBookmarks = () => {
  const { token } = useAuth();
  const [bookmarks, setBookmarks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchBookmarks = useCallback(async () => {
    if (!token) return;

    try {
      setLoading(true);
      const data = await api.getBookmarks(token);
      setBookmarks(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    fetchBookmarks();
  }, [fetchBookmarks]);

  const addBookmark = async (questionId, notes = '') => {
    try {
      await api.addBookmark(token, questionId, notes);
      await fetchBookmarks();
    } catch (err) {
      throw err;
    }
  };

  const removeBookmark = async (questionId) => {
    try {
      await api.removeBookmark(token, questionId);
      setBookmarks(bookmarks.filter(b => b.id !== questionId));
    } catch (err) {
      throw err;
    }
  };

  const updateNotes = async (questionId, notes) => {
    try {
      await api.updateBookmarkNotes(token, questionId, notes);
      await fetchBookmarks();
    } catch (err) {
      throw err;
    }
  };

  return { bookmarks, loading, error, addBookmark, removeBookmark, updateNotes, refetch: fetchBookmarks };
};

// ============ useLeaderboard Hook ============
export const useLeaderboard = (type = 'global', categoryId = null, limit = 50) => {
  const { token } = useAuth();
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [userRank, setUserRank] = useState(null);

  useEffect(() => {
    const fetchLeaderboard = async () => {
      if (!token) return;

      try {
        setLoading(true);
        let data;
        
        switch (type) {
          case 'category':
            data = await api.getCategoryLeaderboard(token, categoryId, limit);
            break;
          case 'streak':
            data = await api.getStreakLeaderboard(token, limit);
            break;
          default:
            data = await api.getLeaderboard(token, limit);
        }
        
        setLeaderboard(data);
        
        // Fetch user's rank
        try {
          const rank = await api.getUserRank(token);
          setUserRank(rank);
        } catch (e) {
          // If endpoint doesn't exist yet, skip
          console.log('User rank endpoint not available');
        }
        
        setError(null);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchLeaderboard();
  }, [token, type, categoryId, limit]);

  return { leaderboard, userRank, loading, error };
};

// ============ useQuiz Hook ============
export const useQuiz = () => {
  const { token } = useAuth();
  const [quiz, setQuiz] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(false);
  const [startTime] = useState(Date.now());

  const startQuiz = async (options) => {
    setLoading(true);
    try {
      const quizData = await api.startQuiz(token, options);
      setQuiz(quizData);
      
      let questionsData;
      if (options.categoryId) {
        questionsData = await api.getCategoryQuestions(token, options.categoryId, options.questionCount || 10);
      } else {
        questionsData = await api.getRandomQuestions(token, options.questionCount || 10);
      }
      
      setQuestions(questionsData);
      setCurrentQuestion(0);
      setAnswers({});
    } catch (err) {
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const submitAnswer = async (questionId, selectedAnswer, timeTaken) => {
    try {
      await api.submitAnswer(token, {
        questionId,
        quizAttemptId: quiz.quizAttemptId,
        selectedAnswer,
        timeTakenSeconds: timeTaken
      });
      
      setAnswers(prev => ({
        ...prev,
        [questionId]: selectedAnswer
      }));
    } catch (err) {
      throw err;
    }
  };

  const completeQuiz = async () => {
    const timeTaken = Math.floor((Date.now() - startTime) / 1000);
    try {
      const result = await api.completeQuiz(token, quiz.quizAttemptId, timeTaken);
      return result;
    } catch (err) {
      throw err;
    }
  };

  const nextQuestion = () => {
    if (currentQuestion < questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1);
    }
  };

  const previousQuestion = () => {
    if (currentQuestion > 0) {
      setCurrentQuestion(currentQuestion - 1);
    }
  };

  const goToQuestion = (index) => {
    if (index >= 0 && index < questions.length) {
      setCurrentQuestion(index);
    }
  };

  return {
    quiz,
    questions,
    currentQuestion,
    answers,
    loading,
    startQuiz,
    submitAnswer,
    completeQuiz,
    nextQuestion,
    previousQuestion,
    goToQuestion,
    progress: questions.length > 0 ? ((currentQuestion + 1) / questions.length) * 100 : 0,
    isLastQuestion: currentQuestion === questions.length - 1,
    setCurrentQuestion
  };
};

// ============ useLocalStorage Hook ============
export const useLocalStorage = (key, initialValue) => {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.error(error);
      return initialValue;
    }
  });

  const setValue = useCallback((value) => {
    try {
      const valueToStore = value instanceof Function ? value(storedValue) : value;
      setStoredValue(valueToStore);
      window.localStorage.setItem(key, JSON.stringify(valueToStore));
    } catch (error) {
      console.error(error);
    }
  }, [key, storedValue]);

  return [storedValue, setValue];
};

// ============ useDebounce Hook ============
export const useDebounce = (value, delay = 500) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};

// ============ usePagination Hook ============
export const usePagination = (initialPage = 0, initialSize = 20) => {
  const [page, setPage] = useState(initialPage);
  const [size, setSize] = useState(initialSize);

  const nextPage = () => setPage(p => p + 1);
  const prevPage = () => setPage(p => Math.max(0, p - 1));
  const goToPage = (pageNum) => setPage(Math.max(0, pageNum));
  const changeSize = (newSize) => {
    setSize(newSize);
    setPage(0);
  };

  return { page, size, nextPage, prevPage, goToPage, changeSize };
};

// ============ useIntersectionObserver Hook ============
export const useIntersectionObserver = (options = {}) => {
  const [isIntersecting, setIsIntersecting] = useState(false);
  const targetRef = useRef(null);

  useEffect(() => {
    const target = targetRef.current;
    if (!target) return;

    const observer = new IntersectionObserver(([entry]) => {
      setIsIntersecting(entry.isIntersecting);
    }, options);

    observer.observe(target);

    return () => {
      observer.disconnect();
    };
  }, [options]);

  return [targetRef, isIntersecting];
};

// ============ useOnlineStatus Hook ============
export const useOnlineStatus = () => {
  const [isOnline, setIsOnline] = useState(navigator.onLine);

  useEffect(() => {
    const handleOnline = () => setIsOnline(true);
    const handleOffline = () => setIsOnline(false);

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  return isOnline;
};