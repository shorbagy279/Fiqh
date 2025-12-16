const API_BASE = 'http://localhost:8080/api';

class ApiError extends Error {
  constructor(message, status, details = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
  }
}

const api = {
  async request(endpoint, options = {}) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 30000); // 30s timeout
      
      const res = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        signal: controller.signal,
        headers: {
          'Content-Type': 'application/json',
          ...options.headers
        }
      });
      
      clearTimeout(timeoutId);
      
      if (!res.ok) {
        let errorMessage = 'فشل الطلب';
        let errorDetails = null;
        
        try {
          const errorData = await res.json();
          errorMessage = errorData.message || errorData.error || errorMessage;
          errorDetails = errorData;
        } catch (e) {
          errorMessage = `خطأ ${res.status}: ${res.statusText}`;
        }
        
        // Handle specific status codes
        if (res.status === 401) {
          errorMessage = 'انتهت جلستك. يرجى تسجيل الدخول مرة أخرى';
          localStorage.removeItem('fiqh_token');
          // Don't redirect here, let the component handle it
        } else if (res.status === 404) {
          errorMessage = errorDetails?.message || 'المورد المطلوب غير موجود';
        } else if (res.status === 409) {
          errorMessage = errorDetails?.message || 'تعارض في البيانات';
        } else if (res.status >= 500) {
          errorMessage = 'خطأ في الخادم. حاول مرة أخرى لاحقاً';
        }
        
        throw new ApiError(errorMessage, res.status, errorDetails);
      }
      
      return await res.json();
    } catch (error) {
      if (error.name === 'AbortError') {
        throw new ApiError('انتهت مهلة الطلب. تحقق من اتصالك بالإنترنت', 0);
      }
      if (error instanceof ApiError) throw error;
      throw new ApiError('خطأ في الاتصال بالشبكة. تحقق من اتصالك بالإنترنت.', 0);
    }
  },

  // Auth endpoints
  register(data) {
    return api.request('/auth/register', { 
      method: 'POST', 
      body: JSON.stringify(data) 
    });
  },
  
  login(data) {
    return api.request('/auth/login', { 
      method: 'POST', 
      body: JSON.stringify(data) 
    });
  },
  
  getCurrentUser(token) {
    return api.request('/auth/me', { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },

  // Category endpoints
  getCategories(token) {
    return api.request('/categories', { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },
  
  getCategoryById(token, categoryId) {
    return api.request(`/categories/${categoryId}`, { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },

  // Question endpoints
  getRandomQuestions(token, limit = 10) {
    return api.request(`/questions/random?limit=${limit}`, { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },
  
  getCategoryQuestions(token, categoryId, limit = 10) {
    return api.request(`/questions/category/${categoryId}?limit=${limit}`, { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },
  
  getQuestionById(token, questionId) {
    return api.request(`/questions/${questionId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Quiz endpoints
  startQuiz(token, data) {
    return api.request('/quiz/start', { 
      method: 'POST', 
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(data)
    });
  },
  
  submitAnswer(token, data) {
    return api.request('/quiz/answer', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(data)
    });
  },
  
  completeQuiz(token, quizAttemptId, timeTaken) {
    return api.request(`/quiz/complete/${quizAttemptId}?timeTaken=${timeTaken}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` }
    });
  },
  
  getQuizHistory(token) {
    return api.request('/quiz/history', {
      headers: { Authorization: `Bearer ${token}` }
    });
  },
  
  getQuizResult(token, quizAttemptId) {
    return api.request(`/quiz/${quizAttemptId}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // User endpoints
  getUserStats(token) {
    return api.request('/user/stats', { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },
  
  getBookmarks(token) {
    return api.request('/user/bookmarks', { 
      headers: { Authorization: `Bearer ${token}` } 
    });
  },
  
  addBookmark(token, questionId, notes = '') {
    return api.request(`/user/bookmarks/${questionId}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify({ notes })
    });
  },
  
  removeBookmark(token, questionId) {
    return api.request(`/user/bookmarks/${questionId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` }
    });
  },

  // Leaderboard endpoints
  getLeaderboard(token, limit = 50) {
    return api.request(`/leaderboard?limit=${limit}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },
  
  getCategoryLeaderboard(token, categoryId, limit = 50) {
    return api.request(`/leaderboard/category/${categoryId}?limit=${limit}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  },
  
  getStreakLeaderboard(token, limit = 50) {
    return api.request(`/leaderboard/streak?limit=${limit}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
};

export default api;
export { ApiError };
