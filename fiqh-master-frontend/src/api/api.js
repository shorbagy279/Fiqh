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
      const res = await fetch(`${API_BASE}${endpoint}`, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...options.headers
        }
      });
      
      if (!res.ok) {
        let errorMessage = 'فشل الطلب';
        let errorDetails = null;
        
        try {
          const errorData = await res.json();
          errorMessage = errorData.message || errorMessage;
          errorDetails = errorData;
        } catch (e) {
          // Couldn't parse error response
        }
        
        throw new ApiError(errorMessage, res.status, errorDetails);
      }
      
      return await res.json();
    } catch (error) {
      if (error instanceof ApiError) throw error;
      throw new ApiError('خطأ في الاتصال بالشبكة. تحقق من اتصالك بالإنترنت.', 0);
    }
  },

  // Auth endpoints
  register: (data) => api.request('/auth/register', { 
    method: 'POST', 
    body: JSON.stringify(data) 
  }),
  
  login: (data) => api.request('/auth/login', { 
    method: 'POST', 
    body: JSON.stringify(data) 
  }),
  
  getCurrentUser: (token) => api.request('/auth/me', { 
    headers: { Authorization: `Bearer ${token}` } 
  }),

  // Categories
  getCategories: (token) => api.request('/categories', { 
    headers: { Authorization: `Bearer ${token}` } 
  }),
  
  getCategoryById: (token, categoryId) => api.request(`/categories/${categoryId}`, { 
    headers: { Authorization: `Bearer ${token}` } 
  }),

  // Questions
  getRandomQuestions: (token, limit = 10) => 
    api.request(`/questions/random?limit=${limit}`, { 
      headers: { Authorization: `Bearer ${token}` } 
    }),
  
  getCategoryQuestions: (token, categoryId, limit = 10) =>
    api.request(`/questions/category/${categoryId}?limit=${limit}`, { 
      headers: { Authorization: `Bearer ${token}` } 
    }),
  
  getQuestionById: (token, questionId) =>
    api.request(`/questions/${questionId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }),

  // Quiz
  startQuiz: (token, data) => 
    api.request('/quiz/start', { 
      method: 'POST', 
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(data)
    }),
  
  submitAnswer: (token, data) =>
    api.request('/quiz/answer', {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify(data)
    }),
  
  completeQuiz: (token, quizAttemptId, timeTaken) =>
    api.request(`/quiz/complete/${quizAttemptId}?timeTaken=${timeTaken}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` }
    }),
  
  getQuizHistory: (token) =>
    api.request('/quiz/history', {
      headers: { Authorization: `Bearer ${token}` }
    }),
  
  getQuizResult: (token, quizAttemptId) =>
    api.request(`/quiz/${quizAttemptId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }),

  // User
  getUserStats: (token) => 
    api.request('/user/stats', { 
      headers: { Authorization: `Bearer ${token}` } 
    }),
  
  getBookmarks: (token) => 
    api.request('/user/bookmarks', { 
      headers: { Authorization: `Bearer ${token}` } 
    }),
  
  addBookmark: (token, questionId, notes = '') =>
    api.request(`/user/bookmarks/${questionId}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: JSON.stringify({ notes })
    }),
  
  removeBookmark: (token, questionId) =>
    api.request(`/user/bookmarks/${questionId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` }
    }),

  // Leaderboard
  getLeaderboard: (token, limit = 50) =>
    api.request(`/leaderboard?limit=${limit}`, {
      headers: { Authorization: `Bearer ${token}` }
    }),
  
  getCategoryLeaderboard: (token, categoryId, limit = 50) =>
    api.request(`/leaderboard/category/${categoryId}?limit=${limit}`, {
      headers: { Authorization: `Bearer ${token}` }
    }),
  
  getStreakLeaderboard: (token, limit = 50) =>
    api.request(`/leaderboard/streak?limit=${limit}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
};

export default api;