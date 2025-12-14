// src/services/api.js
import { CONFIG } from '../config/constants';

class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

const api = {
  async request(endpoint, options = {}) {
    try {
      const res = await fetch(`${CONFIG.API_BASE}${endpoint}`, {
        ...options,
        headers: {
          'Content-Type': 'application/json',
          ...options.headers
        }
      });
      
      if (!res.ok) {
        const error = await res.json().catch(() => ({ message: 'Request failed' }));
        throw new ApiError(error.message || 'Request failed', res.status);
      }
      
      return res.json();
    } catch (error) {
      if (error instanceof ApiError) throw error;
      throw new ApiError('Network error. Please check your connection.', 0);
    }
  },

  // Auth
  register: (data) => api.request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  login: (data) => api.request('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  getCurrentUser: (token) => api.request('/auth/me', { headers: { Authorization: `Bearer ${token}` } }),

  // Categories & Questions
  getCategories: (token) => api.request('/categories', { headers: { Authorization: `Bearer ${token}` } }),
  getRandomQuestions: (token, limit = 10) => 
    api.request(`/questions/random?limit=${limit}`, { headers: { Authorization: `Bearer ${token}` } }),
  getCategoryQuestions: (token, categoryId, limit = 10) =>
    api.request(`/questions/category/${categoryId}?limit=${limit}`, { headers: { Authorization: `Bearer ${token}` } }),

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

  // User
  getUserStats: (token) => api.request('/user/stats', { headers: { Authorization: `Bearer ${token}` } }),
  getBookmarks: (token) => api.request('/user/bookmarks', { headers: { Authorization: `Bearer ${token}` } }),
  addBookmark: (token, questionId, notes) =>
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
};

export default api;