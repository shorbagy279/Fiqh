const API_BASE = 'http://localhost:8080/api';

class ApiError extends Error {
  constructor(message, status, details = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
  }
}

// Utility functions for local caching
export const cache = {
  set(key, data, ttl = 300000) { // 5 minutes default
    const item = {
      data,
      expiry: Date.now() + ttl
    };
    localStorage.setItem(`cache_${key}`, JSON.stringify(item));
  },

  get(key) {
    const item = localStorage.getItem(`cache_${key}`);
    if (!item) return null;

    try {
      const { data, expiry } = JSON.parse(item);
      if (Date.now() > expiry) {
        localStorage.removeItem(`cache_${key}`);
        return null;
      }
      return data;
    } catch (e) {
      return null;
    }
  },

  clear() {
    Object.keys(localStorage)
      .filter(key => key.startsWith('cache_'))
      .forEach(key => localStorage.removeItem(key));
  }
};

// Request interceptor for adding common headers
const createHeaders = (token, additionalHeaders = {}) => {
  const headers = {
    'Content-Type': 'application/json',
    ...additionalHeaders
  };
  
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  return headers;
};

// Retry logic for failed requests
const retryRequest = async (fn, retries = 3, delay = 1000) => {
  try {
    return await fn();
  } catch (error) {
    if (retries === 0 || error.status === 401 || error.status === 403) {
      throw error;
    }
    await new Promise(resolve => setTimeout(resolve, delay));
    return retryRequest(fn, retries - 1, delay * 2);
  }
};

const api = {
  async request(endpoint, options = {}) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 30000);
      
      const res = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        signal: controller.signal,
        headers: createHeaders(options.token, options.headers)
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
        
        if (res.status === 401) {
          errorMessage = 'انتهت جلستك. يرجى تسجيل الدخول مرة أخرى';
          localStorage.removeItem('fiqh_token');
          window.dispatchEvent(new CustomEvent('auth:logout'));
        } else if (res.status === 404) {
          errorMessage = errorDetails?.message || 'المورد المطلوب غير موجود';
        } else if (res.status === 409) {
          errorMessage = errorDetails?.message || 'تعارض في البيانات';
        } else if (res.status >= 500) {
          errorMessage = 'خطأ في الخادم. حاول مرة أخرى لاحقاً';
        }
        
        throw new ApiError(errorMessage, res.status, errorDetails);
      }
      
      // Handle empty responses
      const contentType = res.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        return await res.json();
      }
      
      return null;
    } catch (error) {
      if (error.name === 'AbortError') {
        throw new ApiError('انتهت مهلة الطلب. تحقق من اتصالك بالإنترنت', 0);
      }
      if (error instanceof ApiError) throw error;
      throw new ApiError('خطأ في الاتصال بالشبكة. تحقق من اتصالك بالإنترنت.', 0);
    }
  },

  // ============ Auth Endpoints ============
  async register(data) {
    return api.request('/auth/register', { 
      method: 'POST', 
      body: JSON.stringify(data) 
    });
  },
  
  async login(data) {
    return api.request('/auth/login', { 
      method: 'POST', 
      body: JSON.stringify(data) 
    });
  },
  
  async getCurrentUser(token) {
    return api.request('/auth/me', { token });
  },

  // ============ Category Endpoints ============
  async getCategories(token) {
    return retryRequest(() => api.request('/categories', { token }));
  },
  
  async getCategoryById(token, categoryId) {
    return api.request(`/categories/${categoryId}`, { token });
  },

  // ============ Question Endpoints ============
  async getRandomQuestions(token, limit = 10) {
    return api.request(`/questions/random?limit=${limit}`, { token });
  },
  
  async getCategoryQuestions(token, categoryId, limit = 10) {
    return api.request(`/questions/category/${categoryId}?limit=${limit}`, { token });
  },
  
  async getQuestionById(token, questionId) {
    return api.request(`/questions/${questionId}`, { token });
  },

  // ============ Quiz Endpoints ============
  async startQuiz(token, data) {
    return api.request('/quiz/start', { 
      method: 'POST', 
      token,
      body: JSON.stringify(data)
    });
  },
  
  async submitAnswer(token, data) {
    return api.request('/quiz/answer', {
      method: 'POST',
      token,
      body: JSON.stringify(data)
    });
  },
  
  async completeQuiz(token, quizAttemptId, timeTaken) {
    return api.request(`/quiz/complete/${quizAttemptId}?timeTaken=${timeTaken}`, {
      method: 'POST',
      token
    });
  },
  
  async getQuizHistory(token, page = 0, size = 20) {
    return api.request(`/quiz/history?page=${page}&size=${size}`, { token });
  },
  
  async getQuizResult(token, quizAttemptId) {
    return api.request(`/quiz/${quizAttemptId}`, { token });
  },

  // ============ User Endpoints ============
  async getUserProfile(token) {
    return api.request('/user/profile', { token });
  },

  async updateUserProfile(token, data) {
    return api.request('/user/profile', {
      method: 'PUT',
      token,
      body: JSON.stringify(data)
    });
  },

  async getUserStats(token) {
    return api.request('/user/stats', { token });
  },
  
  async getBookmarks(token) {
    return api.request('/user/bookmarks', { token });
  },
  
  async addBookmark(token, questionId, notes = '') {
    return api.request(`/user/bookmarks/${questionId}`, {
      method: 'POST',
      token,
      body: JSON.stringify({ notes })
    });
  },
  
  async removeBookmark(token, questionId) {
    return api.request(`/user/bookmarks/${questionId}`, {
      method: 'DELETE',
      token
    });
  },

  async updateBookmarkNotes(token, questionId, notes) {
    return api.request(`/user/bookmarks/${questionId}/notes`, {
      method: 'PUT',
      token,
      body: JSON.stringify({ notes })
    });
  },

  async updateDifficultyLevel(token, level) {
    return api.request('/user/settings/difficulty', {
      method: 'PUT',
      token,
      body: JSON.stringify({ level })
    });
  },

  async toggleDailyReminder(token, enabled) {
    return api.request('/user/settings/reminders', {
      method: 'PUT',
      token,
      body: JSON.stringify({ enabled })
    });
  },

  async updatePreferredLanguage(token, language) {
    return api.request('/user/settings/language', {
      method: 'PUT',
      token,
      body: JSON.stringify({ language })
    });
  },

  async updatePreferredMarja(token, marjaId) {
    return api.request('/user/settings/marja', {
      method: 'PUT',
      token,
      body: JSON.stringify({ marjaId })
    });
  },

  // ============ Leaderboard Endpoints ============
  async getLeaderboard(token, limit = 50) {
    return api.request(`/leaderboard?limit=${limit}`, { token });
  },
  
  async getCategoryLeaderboard(token, categoryId, limit = 50) {
    return api.request(`/leaderboard/category/${categoryId}?limit=${limit}`, { token });
  },
  
  async getStreakLeaderboard(token, limit = 50) {
    return api.request(`/leaderboard/streak?limit=${limit}`, { token });
  },

  async getUserRank(token) {
    return api.request('/leaderboard/me', { token });
  }
};

export default api;
export { ApiError };