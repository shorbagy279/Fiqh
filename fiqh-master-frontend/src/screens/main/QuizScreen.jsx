import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import { BookOpen, Trophy, ArrowRight, X } from 'lucide-react';
import LoadingSpinner from '../../components/shared/LoadingSpinner';

const QuizScreen = ({ navigate, data }) => {
  const { token } = useAuth();
  const [questions, setQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showExplanation, setShowExplanation] = useState(false);
  const [score, setScore] = useState(0);
  const [quizAttemptId, setQuizAttemptId] = useState(null);
  const [startTime] = useState(Date.now());
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const startQuiz = async () => {
      try {
        const quizData = await api.startQuiz(token, {
          categoryId: data?.categoryId || null,
          quizType: data?.type || 'random',
          questionCount: 10
        });
        setQuizAttemptId(quizData.quizAttemptId);
        
        // Fetch questions (they won't include correct answer initially)
        let questionsData;
        if (data?.categoryId) {
          questionsData = await api.getCategoryQuestions(token, data.categoryId, 10);
        } else {
          questionsData = await api.getRandomQuestions(token, 10);
        }
        
        // For each question, we'll fetch the full details with answer when user selects an option
        setQuestions(questionsData);
      } catch (error) {
        console.error('Error starting quiz:', error);
      } finally {
        setLoading(false);
      }
    };
    
    if (token) startQuiz();
  }, [token, data]);

  const handleAnswer = async (index) => {
    if (showExplanation) return; // Prevent double-click
    
    setSelectedAnswer(index);
    
    const question = questions[currentQuestion];
    
    try {
      // Fetch the full question details including correct answer and explanation
      const fullQuestion = await api.getQuestionById(token, question.id);
      
      // Update the current question in the array with full details
      const updatedQuestions = [...questions];
      updatedQuestions[currentQuestion] = fullQuestion;
      setQuestions(updatedQuestions);
      
      // Now show the explanation
      setShowExplanation(true);
      
      // Check if answer is correct
      const isCorrect = index === fullQuestion.correctAnswer;
      if (isCorrect) setScore(score + 1);
      
      // Submit answer to backend
      await api.submitAnswer(token, {
        questionId: question.id,
        quizAttemptId,
        selectedAnswer: index,
        timeTakenSeconds: Math.floor((Date.now() - startTime) / 1000)
      });
    } catch (error) {
      console.error('Error submitting answer:', error);
      // Still show explanation even if API call fails
      setShowExplanation(true);
    }
  };

  const handleNext = async () => {
    if (currentQuestion >= questions.length - 1) {
      try {
        const timeTaken = Math.floor((Date.now() - startTime) / 1000);
        const result = await api.completeQuiz(token, quizAttemptId, timeTaken);
        navigate('results', { result, score, total: questions.length });
      } catch (error) {
        console.error('Error completing quiz:', error);
      }
    } else {
      setCurrentQuestion(currentQuestion + 1);
      setSelectedAnswer(null);
      setShowExplanation(false);
    }
  };

  if (loading || questions.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" message="جاري تحميل الأسئلة..." />
      </div>
    );
  }

  const question = questions[currentQuestion];
  const progress = ((currentQuestion + 1) / questions.length) * 100;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-4 shadow-xl">
        <div className="flex justify-between items-center mb-3">
          <button onClick={() => navigate('home')} className="p-1 hover:bg-white/10 rounded-lg transition">
            <X size={24} />
          </button>
          <span className="font-bold text-base">السؤال {currentQuestion + 1} من {questions.length}</span>
          <div className="w-10"></div>
        </div>
        
        {/* Progress Bar */}
        <div className="bg-white/20 rounded-full h-2 overflow-hidden">
          <div 
            className="bg-yellow-400 h-full rounded-full transition-all duration-300"
            style={{width: `${progress}%`}}
          ></div>
        </div>
      </div>

      <div className="p-4">
        {/* Question Card */}
        <div className="bg-white rounded-2xl p-5 shadow-lg mb-4">
          <div className="flex gap-2 mb-4 items-center">
            <span className={`px-3 py-1 rounded-full text-xs font-bold ${
              question.difficulty === 'beginner' ? 'bg-green-100 text-green-600' :
              question.difficulty === 'intermediate' ? 'bg-yellow-100 text-yellow-600' :
              'bg-red-100 text-red-600'
            }`}>
              {question.difficulty === 'beginner' ? 'مبتدئ' : 
               question.difficulty === 'intermediate' ? 'متوسط' : 'متقدم'}
            </span>
            {question.categoryName && (
              <span className="bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-xs font-bold">
                {question.categoryName}
              </span>
            )}
          </div>
          
          <h2 className="text-lg font-bold mb-3 text-right leading-relaxed text-gray-900">
            {question.questionAr}
          </h2>
          <p className="text-sm text-gray-500 text-left mb-5 leading-relaxed">
            {question.questionEn}
          </p>

          {/* Options */}
          <div className="space-y-3">
            {question.optionsAr?.map((option, index) => {
              let bgColor = 'bg-white hover:bg-gray-50';
              let borderColor = 'border-gray-200';
              let textColor = 'text-gray-900';
              
              if (showExplanation) {
                if (index === question.correctAnswer) {
                  bgColor = 'bg-green-50';
                  borderColor = 'border-green-500';
                  textColor = 'text-green-800';
                } else if (index === selectedAnswer) {
                  bgColor = 'bg-red-50';
                  borderColor = 'border-red-500';
                  textColor = 'text-red-800';
                }
              }

              return (
                <button
                  key={index}
                  onClick={() => !showExplanation && handleAnswer(index)}
                  disabled={showExplanation}
                  className={`w-full p-3.5 rounded-xl border-2 ${bgColor} ${borderColor} text-right transition-all relative ${!showExplanation && 'hover:border-green-400 hover:shadow-sm active:scale-98'} disabled:cursor-not-allowed`}
                >
                  <span className={`font-medium text-sm ${textColor}`}>{option}</span>
                  {showExplanation && index === question.correctAnswer && (
                    <div className="absolute left-3 top-1/2 -translate-y-1/2 bg-green-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-sm">
                      ✓
                    </div>
                  )}
                  {showExplanation && index === selectedAnswer && index !== question.correctAnswer && (
                    <div className="absolute left-3 top-1/2 -translate-y-1/2 bg-red-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-sm">
                      ✗
                    </div>
                  )}
                </button>
              );
            })}
          </div>
        </div>

        {/* Explanation */}
        {showExplanation && question.explanationAr && (
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 border-2 border-blue-200 rounded-2xl p-5 mb-4 shadow-md">
            <h3 className="font-bold text-blue-900 mb-2 flex items-center gap-2 text-base">
              <BookOpen size={18} />
              الشرح
            </h3>
            <p className="text-blue-900 mb-3 text-right leading-relaxed text-sm">
              {question.explanationAr}
            </p>
            {question.referenceAr && (
              <div className="bg-white rounded-xl p-3 shadow-sm">
                <p className="text-xs text-gray-700 text-right font-medium">
                  📚 المرجع: {question.referenceAr}
                </p>
              </div>
            )}
          </div>
        )}

        {/* Next Button */}
        {showExplanation && (
          <button
            onClick={handleNext}
            className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-3.5 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2 text-base"
          >
            {currentQuestion >= questions.length - 1 ? (
              <>
                إنهاء الاختبار
                <Trophy size={18} />
              </>
            ) : (
              <>
                السؤال التالي
                <ArrowRight size={18} />
              </>
            )}
          </button>
        )}
      </div>
    </div>
  );
};

export default QuizScreen;