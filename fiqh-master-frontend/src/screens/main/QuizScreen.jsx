export const QuizScreen = ({ navigate, data }) => {
  const { token } = useAuth();
  const [questions, setQuestions] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [showExplanation, setShowExplanation] = useState(false);
  const [score, setScore] = useState(0);
  const [quizAttemptId, setQuizAttemptId] = useState(null);
  const [startTime] = useState(Date.now());
  const [answerTimes, setAnswerTimes] = useState([]);
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
        
        const questionsData = data?.categoryId
          ? await api.getCategoryQuestions(token, data.categoryId, 10)
          : await api.getRandomQuestions(token, 10);
        
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
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <div className="flex justify-between items-center mb-4">
          <button onClick={() => navigate('home')} className="p-2 hover:bg-white/10 rounded-lg transition">
            <X size={24} />
          </button>
          <span className="font-bold text-lg">السؤال {currentQuestion + 1} من {questions.length}</span>
          <div className="w-10"></div>
        </div>
        
        {/* Progress Bar */}
        <div className="bg-white/20 rounded-full h-3 overflow-hidden">
          <div 
            className="bg-gradient-to-r from-yellow-400 to-yellow-300 h-full rounded-full transition-all duration-500 shadow-lg"
            style={{width: `${progress}%`}}
          ></div>
        </div>
        
        <div className="flex justify-between items-center mt-2 text-sm">
          <span className="text-green-100">النتيجة: {score}/{questions.length}</span>
          <span className="text-green-100">{Math.round(progress)}% مكتمل</span>
        </div>
      </div>

      <div className="p-6">
        {/* Question Card */}
        <div className="bg-white rounded-2xl p-6 shadow-xl mb-6">
          <div className="flex gap-2 mb-4">
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
          
          <h2 className="text-xl font-bold mb-3 text-right leading-relaxed text-gray-800">
            {question.questionAr}
          </h2>
          <p className="text-sm text-gray-500 text-left mb-6 leading-relaxed">
            {question.questionEn}
          </p>

          {/* Options */}
          <div className="space-y-3">
            {question.optionsAr?.map((option, index) => {
              let bgColor = 'bg-gray-50 hover:bg-gray-100';
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
                  className={`w-full p-4 rounded-xl border-2 ${bgColor} ${borderColor} text-right transition-all relative ${!showExplanation && 'hover:border-green-500 hover:shadow-md'} disabled:cursor-not-allowed`}
                >
                  <span className={`font-medium ${textColor}`}>{option}</span>
                  {showExplanation && index === question.correctAnswer && (
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 bg-green-500 text-white w-8 h-8 rounded-full flex items-center justify-center">
                      ✓
                    </div>
                  )}
                  {showExplanation && index === selectedAnswer && index !== question.correctAnswer && (
                    <div className="absolute left-4 top-1/2 -translate-y-1/2 bg-red-500 text-white w-8 h-8 rounded-full flex items-center justify-center">
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
          <div className="bg-gradient-to-br from-blue-50 to-indigo-50 border-2 border-blue-200 rounded-2xl p-6 mb-6 shadow-lg">
            <h3 className="font-bold text-blue-900 mb-3 flex items-center gap-2 text-lg">
              <BookOpen size={22} />
              الشرح التفصيلي
            </h3>
            <p className="text-blue-800 mb-4 text-right leading-relaxed">
              {question.explanationAr}
            </p>
            {question.referenceAr && (
              <div className="bg-white rounded-xl p-4 shadow-sm">
                <p className="text-sm text-gray-700 text-right font-medium">
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
            className="w-full bg-gradient-to-r from-green-600 to-green-700 text-white py-4 rounded-xl font-bold hover:from-green-700 hover:to-green-800 transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
          >
            {currentQuestion >= questions.length - 1 ? (
              <>
                <Trophy size={20} />
                إنهاء الاختبار
              </>
            ) : (
              <>
                السؤال التالي
                <ArrowRight size={20} />
              </>
            )}
          </button>
        )}
      </div>
    </div>
  );
};