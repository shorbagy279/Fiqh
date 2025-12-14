export const BookmarksScreen = ({ navigate }) => {
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
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-gray-100 pb-24">
      {/* Header */}
      <div className="bg-gradient-to-br from-green-600 to-green-800 text-white p-6 shadow-xl">
        <h1 className="text-2xl font-bold mb-2 flex items-center gap-2">
          <Bookmark size={28} />
          الأسئلة المحفوظة
        </h1>
        <p className="text-green-100">راجع الأسئلة التي حفظتها للمراجعة</p>
      </div>

      <div className="p-6">
        {loading ? (
          <div className="flex justify-center py-12">
            <LoadingSpinner message="جاري تحميل الأسئلة..." />
          </div>
        ) : bookmarks.length === 0 ? (
          <div className="text-center py-12">
            <div className="bg-gray-100 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4">
              <Bookmark size={48} className="text-gray-300" />
            </div>
            <h3 className="text-xl font-bold text-gray-700 mb-2">لا توجد أسئلة محفوظة</h3>
            <p className="text-gray-500 mb-6">احفظ الأسئلة الصعبة لمراجعتها لاحقاً</p>
            <button
              onClick={() => navigate('quiz')}
              className="bg-green-600 text-white px-6 py-3 rounded-xl font-bold hover:bg-green-700 transition"
            >
              ابدأ اختباراً جديداً
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            {bookmarks.map((question) => (
              <div key={question.id} className="bg-white rounded-xl p-5 shadow-lg hover:shadow-xl transition-all">
                <div className="flex justify-between items-start mb-3">
                  <span className="bg-green-100 text-green-600 px-3 py-1 rounded-full text-xs font-bold">
                    {question.categoryName}
                  </span>
                  <button
                    onClick={() => handleRemoveBookmark(question.id)}
                    className="text-red-500 hover:text-red-700 bg-red-50 p-2 rounded-lg hover:bg-red-100 transition"
                  >
                    <X size={18} />
                  </button>
                </div>
                <h3 className="font-bold text-right mb-2 text-gray-800 leading-relaxed">
                  {question.questionAr}
                </h3>
                <p className="text-sm text-gray-500 text-left leading-relaxed">
                  {question.questionEn}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>

      <BottomNav currentScreen="bookmarks" navigate={navigate} />
    </div>
  );
};

