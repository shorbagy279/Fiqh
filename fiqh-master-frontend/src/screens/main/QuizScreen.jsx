import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { useQuiz } from '../../hooks/useCustomHooks';
import { BookOpen, Trophy, ArrowRight, ArrowLeft, X, Clock, Bookmark, BookmarkCheck, Eye, AlertCircle } from 'lucide-react';
import LoadingSpinner from '../../components/shared/LoadingSpinner';
import api from '../../services/api';

const QuizScreen = ({ navigate, data }) => {
  const { token } = useAuth();
  const {
    quiz,
    questions: quizQuestions,
    currentQuestion,
    answers,
    loading: quizLoading,
    startQuiz,
    submitAnswer,
    completeQuiz,
    nextQuestion,
    previousQuestion,
    progress,
    isLastQuestion,
    setCurrentQuestion
  } = useQuiz();

  const [questions, setQuestions] = useState([]);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showExplanation, setShowExplanation] = useState(false);
  const [fullQuestion, setFullQuestion] = useState(null);
  const [questionStartTime, setQuestionStartTime] = useState(Date.now());
  const [score, setScore] = useState(0);
  const [bookmarked, setBookmarked] = useState(false);
  const [submittingAnswer, setSubmittingAnswer] = useState(false);
  const [elapsedTime, setElapsedTime] = useState(0);
  const [isReviewMode, setIsReviewMode] = useState(false);
  const [questionAnswers, setQuestionAnswers] = useState({});
  const [highestAnsweredIndex, setHighestAnsweredIndex] = useState(-1);
  
  const [quizTimer, setQuizTimer] = useState(null);
  const [timerWarning, setTimerWarning] = useState(false);
  const timerRef = useRef(null);

  useEffect(() => {
    if (data?.timerEnabled && data?.timerMinutes) {
      const totalSeconds = data.timerMinutes * 60;
      setQuizTimer(totalSeconds);
    }
  }, [data?.timerEnabled, data?.timerMinutes]);

  useEffect(() => {
    if (quizTimer === null || quizTimer <= 0) return;

    const timer = setInterval(() => {
      setQuizTimer(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          handleTimeUp();
          return 0;
        }
        
        if (prev === 60 && !timerWarning) {
          setTimerWarning(true);
        }
        
        return prev - 1;
      });
    }, 1000);

    timerRef.current = timer;
    return () => clearInterval(timer);
  }, [quizTimer !== null]);

  const handleTimeUp = async () => {
    alert('انتهى الوقت! سيتم إنهاء الاختبار.');
    await handleForceExit();
  };

  const formatQuizTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  useEffect(() => {
    if (quizTimer !== null) return;
    
    const timer = setInterval(() => {
      setElapsedTime(prev => prev + 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [quizTimer]);

  useEffect(() => {
    if (data?.customQuestions && data.customQuestions.length > 0) {
      setQuestions(data.customQuestions);
    } else if (quizQuestions && quizQuestions.length > 0) {
      setQuestions(quizQuestions);
    }
  }, [data?.customQuestions, quizQuestions]);

  useEffect(() => {
    const initQuiz = async () => {
      if (data?.customQuestions && data.customQuestions.length > 0) {
        try {
          await startQuiz({
            categoryId: null,
            quizType: 'custom',
            questionCount: data.customQuestions.length
          });
        } catch (error) {
          console.error('Error starting custom quiz:', error);
        }
        return;
      }

      try {
        await startQuiz({
          categoryId: data?.categoryId || null,
          quizType: data?.type || 'random',
          questionCount: data?.questionCount || 10
        });
      } catch (error) {
        console.error('Error starting quiz:', error);
        navigate('home');
      }
    };

    if (token && !quiz && !data?.customQuestions) {
      initQuiz();
    }
  }, [token, quiz, data, startQuiz, navigate]);

  useEffect(() => {
    const answeredIndices = questions
      .map((q, idx) => questionAnswers[q.id] ? idx : -1)
      .filter(idx => idx !== -1);
    
    if (answeredIndices.length > 0) {
      setHighestAnsweredIndex(Math.max(...answeredIndices));
    }
  }, [questionAnswers, questions]);

  useEffect(() => {
    const question = questions[currentQuestion];
    
    const wasAnswered = questionAnswers[question?.id];
    
    if (wasAnswered) {
      setIsReviewMode(true);
      setSelectedAnswer(wasAnswered.selectedAnswer);
      setFullQuestion(wasAnswered.fullQuestion);
      setShowExplanation(true);
    } else {
      setIsReviewMode(false);
      setSelectedAnswer(null);
      setShowExplanation(false);
      setFullQuestion(null);
      setQuestionStartTime(Date.now());
    }
    
    if (question) {
      setBookmarked(question.isBookmarked || false);
    }
  }, [currentQuestion, questions, questionAnswers]);

  const handleAnswer = async (index) => {
    if (showExplanation || submittingAnswer || isReviewMode) return;
    
    setSubmittingAnswer(true);
    setSelectedAnswer(index);
    
    const question = questions[currentQuestion];
    const timeTaken = Math.floor((Date.now() - questionStartTime) / 1000);
    
    try {
      if (quiz) {
        await submitAnswer(question.id, index, timeTaken);
      }
      
      const fullQuestionData = await api.getQuestionById(token, question.id);
      setFullQuestion(fullQuestionData);
      setShowExplanation(true);
      
      setQuestionAnswers(prev => ({
        ...prev,
        [question.id]: {
          selectedAnswer: index,
          fullQuestion: fullQuestionData,
          isCorrect: index === fullQuestionData.correctAnswer
        }
      }));
      
      if (index === fullQuestionData.correctAnswer) {
        setScore(prev => prev + 1);
      }
    } catch (error) {
      console.error('Error submitting answer:', error);
    } finally {
      setSubmittingAnswer(false);
    }
  };

  const handleNext = async () => {
    if (isLastQuestion) {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
      try {
        const result = quiz ? await completeQuiz() : null;
        navigate('results', { result, score, total: questions.length });
      } catch (error) {
        console.error('Error completing quiz:', error);
      }
    } else {
      nextQuestion();
    }
  };

  const handlePrevious = () => {
    if (currentQuestion > 0) {
      previousQuestion();
    }
  };

  const handleQuestionNavigate = (index) => {
    const targetQuestion = questions[index];
    const wasAnswered = questionAnswers[targetQuestion?.id];
    
    if (wasAnswered && index !== currentQuestion) {
      setCurrentQuestion(index);
    }
  };

  const returnToCurrentQuestion = () => {
    for (let i = 0; i < questions.length; i++) {
      if (!questionAnswers[questions[i].id]) {
        setCurrentQuestion(i);
        return;
      }
    }
    setCurrentQuestion(questions.length - 1);
  };

  const handleBookmark = async () => {
    const question = questions[currentQuestion];
    try {
      if (bookmarked) {
        await api.removeBookmark(token, question.id);
        setBookmarked(false);
      } else {
        await api.addBookmark(token, question.id);
        setBookmarked(true);
      }
    } catch (error) {
      console.error('Error toggling bookmark:', error);
    }
  };

  const handleForceExit = async () => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
    }

    // Calculate score from answered questions
    const answeredCount = Object.keys(questionAnswers).length;
    const correctCount = Object.values(questionAnswers).filter(a => a.isCorrect).length;
    
    try {
      // Complete the quiz with current score
      if (quiz) {
        const result = await completeQuiz();
        navigate('results', { 
          result, 
          score: correctCount, 
          total: questions.length,
          answeredCount 
        });
      } else {
        navigate('results', { 
          score: correctCount, 
          total: questions.length,
          answeredCount 
        });
      }
    } catch (error) {
      console.error('Error completing quiz:', error);
      // Even if API fails, show results
      navigate('results', { 
        score: correctCount, 
        total: questions.length,
        answeredCount 
      });
    }
  };

  const handleExit = () => {
    const answeredCount = Object.keys(questionAnswers).length;
    const unansweredCount = questions.length - answeredCount;
    
    let message = 'هل أنت متأكد من إنهاء الاختبار؟\n\n';
    message += `الأسئلة المجاب عليها: ${answeredCount}\n`;
    message += `الأسئلة غير المجابة: ${unansweredCount}\n\n`;
    message += 'ستحسب الأسئلة غير المجابة كإجابات خاطئة.';
    
    if (window.confirm(message)) {
      handleForceExit();
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  if ((quizLoading && !data?.customQuestions) || questions.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <LoadingSpinner size="lg" message="جاري تحميل الأسئلة..." />
      </div>
    );
  }

  const question = fullQuestion || questions[currentQuestion];
  const displayQuestion = questions[currentQuestion];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-4 shadow-xl">
        <div className="flex justify-between items-center mb-3">
          <button 
            onClick={handleExit} 
            className="p-2 hover:bg-white/10 rounded-lg transition"
          >
            <X size={24} />
          </button>
          
          <div className="flex items-center gap-4">
            {quizTimer !== null ? (
              <div className={`flex items-center gap-2 px-3 py-1 rounded-full ${
                timerWarning ? 'bg-red-500 animate-pulse' : 'bg-white/10'
              }`}>
                <Clock size={16} />
                <span className="font-bold text-sm">{formatQuizTimer(quizTimer)}</span>
              </div>
            ) : (
              <div className="flex items-center gap-2 bg-white/10 px-3 py-1 rounded-full">
                <Clock size={16} />
                <span className="font-bold text-sm">{formatTime(elapsedTime)}</span>
              </div>
            )}
            <span className="font-bold text-base">
              {currentQuestion + 1} / {questions.length}
            </span>
          </div>
          
          <button
            onClick={handleBookmark}
            className="p-2 hover:bg-white/10 rounded-lg transition"
          >
            {bookmarked ? <BookmarkCheck size={24} /> : <Bookmark size={24} />}
          </button>
        </div>
        
        <div className="bg-white/20 rounded-full h-2 overflow-hidden">
          <div 
            className="bg-yellow-400 h-full rounded-full transition-all duration-300"
            style={{width: `${progress}%`}}
          ></div>
        </div>

        {timerWarning && quizTimer > 0 && (
          <div className="mt-2 bg-red-500/20 border border-red-300 rounded-lg p-2 flex items-center gap-2 animate-pulse">
            <AlertCircle size={16} />
            <span className="text-sm font-bold">تبقى أقل من دقيقة!</span>
          </div>
        )}

        <div className="mt-2 text-center">
          {isReviewMode ? (
            <div className="flex items-center justify-center gap-2 bg-purple-500/30 px-3 py-1 rounded-full">
              <Eye size={16} />
              <span className="text-sm font-bold">وضع المراجعة - لا يمكن تغيير الإجابة</span>
            </div>
          ) : (
            <span className="text-sm text-green-100">
              النتيجة الحالية: {score} / {Object.keys(questionAnswers).length}
            </span>
          )}
        </div>
      </div>

      <div className="p-4">
        <div className={`bg-white rounded-2xl p-5 shadow-lg mb-4 ${isReviewMode ? 'border-2 border-purple-300' : ''}`}>
          {isReviewMode && (
            <div className="bg-purple-50 border border-purple-200 rounded-lg p-3 mb-4 flex items-center gap-2">
              <Eye size={20} className="text-purple-600" />
              <p className="text-sm text-purple-800 font-medium">
                أنت في وضع المراجعة. هذا السؤال تم الإجابة عليه مسبقاً ولا يمكن تغيير الإجابة.
              </p>
            </div>
          )}
          
          <div className="flex gap-2 mb-4 items-center flex-wrap">
            {displayQuestion.difficulty && (
              <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                displayQuestion.difficulty === 'beginner' ? 'bg-green-100 text-green-600' :
                displayQuestion.difficulty === 'intermediate' ? 'bg-yellow-100 text-yellow-600' :
                'bg-red-100 text-red-600'
              }`}>
                {displayQuestion.difficulty === 'beginner' ? 'مبتدئ' : 
                 displayQuestion.difficulty === 'intermediate' ? 'متوسط' : 'متقدم'}
              </span>
            )}
            {displayQuestion.categoryName && (
              <span className="bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-xs font-bold">
                {displayQuestion.categoryName}
              </span>
            )}
          </div>
          
          <h2 className="text-lg font-bold mb-3 text-right leading-relaxed text-gray-900">
            {displayQuestion.questionAr}
          </h2>
          {displayQuestion.questionEn && (
            <p className="text-sm text-gray-500 text-left mb-5 leading-relaxed">
              {displayQuestion.questionEn}
            </p>
          )}

          <div className="space-y-3">
            {displayQuestion.optionsAr?.map((option, index) => {
              let bgColor = 'bg-white hover:bg-gray-50';
              let borderColor = 'border-gray-200';
              let textColor = 'text-gray-900';
              let icon = null;
              let cursor = 'cursor-pointer';
              
              if (showExplanation && fullQuestion) {
                if (index === fullQuestion.correctAnswer) {
                  bgColor = 'bg-green-50';
                  borderColor = 'border-green-500';
                  textColor = 'text-green-800';
                  icon = (
                    <div className="absolute left-3 top-1/2 -translate-y-1/2 bg-green-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-sm">
                      ✓
                    </div>
                  );
                } else if (index === selectedAnswer) {
                  bgColor = 'bg-red-50';
                  borderColor = 'border-red-500';
                  textColor = 'text-red-800';
                  icon = (
                    <div className="absolute left-3 top-1/2 -translate-y-1/2 bg-red-500 text-white w-6 h-6 rounded-full flex items-center justify-center text-sm">
                      ✗
                    </div>
                  );
                }
                cursor = 'cursor-not-allowed';
              }

              return (
                <button
                  key={index}
                  onClick={() => !showExplanation && !isReviewMode && handleAnswer(index)}
                  disabled={showExplanation || submittingAnswer || isReviewMode}
                  className={`w-full p-3.5 rounded-xl border-2 ${bgColor} ${borderColor} text-right transition-all relative ${
                    !showExplanation && !submittingAnswer && !isReviewMode && 'hover:border-green-400 hover:shadow-sm active:scale-98'
                  } ${cursor}`}
                >
                  <span className={`font-medium text-sm ${textColor} block pr-2`}>
                    {option}
                  </span>
                  {icon}
                </button>
              );
            })}
          </div>
        </div>

        {showExplanation && fullQuestion?.explanationAr && (
          <div className="bg-gradient-to-br from-blue-50 to-blue-100 border-2 border-blue-200 rounded-2xl p-5 mb-4 shadow-md">
            <h3 className="font-bold text-blue-900 mb-2 flex items-center gap-2 text-base">
              <BookOpen size={18} />
              الشرح
            </h3>
            <p className="text-blue-900 mb-3 text-right leading-relaxed text-sm">
              {fullQuestion.explanationAr}
            </p>
            {fullQuestion.explanationEn && (
              <p className="text-blue-800 mb-3 text-left leading-relaxed text-sm italic">
                {fullQuestion.explanationEn}
              </p>
            )}
            {fullQuestion.referenceAr && (
              <div className="bg-white rounded-xl p-3 shadow-sm">
                <p className="text-xs text-gray-700 text-right font-medium">
                  📚 المرجع: {fullQuestion.referenceAr}
                </p>
              </div>
            )}
          </div>
        )}

       <div className="flex gap-3 mb-4">
  {currentQuestion > 0 && showExplanation && !isReviewMode && (
    <button
      onClick={handlePrevious}
      className="flex-1 bg-gray-200 text-gray-700 py-3.5 rounded-xl font-bold hover:bg-gray-300 transition-all shadow-md flex items-center justify-center gap-2 text-base"
    >
      <ArrowLeft size={18} />
      السابق
    </button>
  )}
  
  {isReviewMode && (
    <button
      onClick={returnToCurrentQuestion}
      className="flex-1 bg-purple-600 text-white py-3.5 rounded-xl font-bold hover:bg-purple-700 transition-all shadow-lg flex items-center justify-center gap-2 text-base"
    >
      العودة للسؤال الحالي
      <ArrowRight size={18} />
    </button>
  )}
  
  {showExplanation && !isReviewMode && (
    <button
      onClick={handleNext}
      className="flex-1 bg-gradient-to-r from-green-600 to-green-700 text-white py-3.5 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2 text-base"
    >
      {isLastQuestion ? (
        <>
          إنهاء الاختبار
          <Trophy size={18} />
        </>
      ) : (
        <>
          التالي
          <ArrowRight size={18} />
        </>
      )}
    </button>
  )}
</div>

{/* Add Force Finish Button - Show if user has answered some questions */}
{Object.keys(questionAnswers).length > 0 && !showExplanation && (
  <button
    onClick={handleForceExit}
    className="w-full bg-gradient-to-r from-orange-500 to-red-500 text-white py-3.5 rounded-xl font-bold hover:from-orange-600 hover:to-red-600 transition-all shadow-lg flex items-center justify-center gap-2 text-base mb-4"
  >
    <Trophy size={18} />
    إنهاء الاختبار الآن ({Object.keys(questionAnswers).length} مُجاب)
  </button>
)}

{/* Show finish button if all questions answered */}
{Object.keys(questionAnswers).length === questions.length && isReviewMode && (
  <button
    onClick={handleForceExit}
    className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-4 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2 text-lg mb-4 animate-pulse-slow"
  >
    <Trophy size={24} />
    انتهيت! اضغط لرؤية النتيجة
  </button>
)}

        {questions.length > 1 && (
          <div className="bg-white rounded-xl p-4 shadow-md">
            <h3 className="text-sm font-bold text-gray-700 mb-2 text-center">
              الانتقال السريع (مراجعة الأسئلة المُجابة)
            </h3>
            <p className="text-xs text-gray-500 text-center mb-3">
              يمكنك مراجعة أي سؤال تمت الإجابة عليه - لا يمكن تغيير الإجابات
            </p>
            <div className="flex flex-wrap gap-2 justify-center">
              {questions.map((q, index) => {
                const wasAnswered = questionAnswers[q?.id];
                const isCurrent = index === currentQuestion;
                const canNavigate = wasAnswered && !isCurrent;
                
                return (
                  <button
                    key={index}
                    onClick={() => handleQuestionNavigate(index)}
                    disabled={!canNavigate}
                    className={`w-10 h-10 rounded-lg font-bold text-sm transition-all relative ${
                      isCurrent && !isReviewMode
                        ? 'bg-green-600 text-white shadow-md ring-2 ring-green-300'
                        : isCurrent && isReviewMode
                        ? 'bg-purple-600 text-white shadow-md ring-2 ring-purple-300'
                        : wasAnswered
                        ? 'bg-purple-100 text-purple-600 hover:bg-purple-200 cursor-pointer hover:scale-105'
                        : 'bg-gray-100 text-gray-400 cursor-not-allowed opacity-40'
                    }`}
                    title={
                      isCurrent 
                        ? isReviewMode ? 'السؤال المُراجَع حالياً' : 'السؤال الحالي'
                        : canNavigate
                        ? `مراجعة السؤال ${index + 1}`
                        : 'لم تتم الإجابة بعد'
                    }
                  >
                    {index + 1}
                    {wasAnswered && !isCurrent && (
                      <div className="absolute -top-1 -right-1 bg-purple-500 text-white rounded-full w-4 h-4 flex items-center justify-center">
                        <Eye size={10} />
                      </div>
                    )}
                  </button>
                );
              })}
            </div>
            
            <div className="mt-4 flex flex-wrap gap-3 justify-center text-xs">
              <div className="flex items-center gap-1">
                <div className="w-4 h-4 bg-green-600 rounded"></div>
                <span className="text-gray-600">السؤال الحالي</span>
              </div>
              <div className="flex items-center gap-1">
                <div className="w-4 h-4 bg-purple-600 rounded"></div>
                <span className="text-gray-600">المُراجَع حالياً</span>
              </div>
              <div className="flex items-center gap-1">
                <div className="w-4 h-4 bg-purple-100 border border-purple-300 rounded flex items-center justify-center">
                  <Eye size={8} className="text-purple-600" />
                </div>
                <span className="text-gray-600">يمكن المراجعة</span>
              </div>
              <div className="flex items-center gap-1">
                <div className="w-4 h-4 bg-gray-100 rounded"></div>
                <span className="text-gray-600">لم تُجَب بعد</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default QuizScreen;