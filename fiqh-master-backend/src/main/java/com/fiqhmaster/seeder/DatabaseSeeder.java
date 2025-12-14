package com.fiqhmaster.seeder;

import com.fiqhmaster.entity.*;
import com.fiqhmaster.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final MarjaRepository marjaRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Check if we need to add more questions
        long questionCount = questionRepository.count();
        
        if (marjaRepository.count() == 0) {
            log.info("🌱 Starting database seeding...");
            seedMarjas();
            seedCategories();
            seedQuestions();
            seedTestUser();
            log.info("✅ Database seeding completed!");
        } else if (questionCount < 16) {
            log.info("🌱 Adding more questions... (current: " + questionCount + ")");
            seedQuestions();
            log.info("✅ Additional questions added!");
        } else {
            log.info("📊 Database already contains data. Skipping seeding.");
        }
    }

    private void seedMarjas() {
        log.info("📖 Seeding Marjas...");
        
        Marja sistani = new Marja();
        sistani.setNameAr("السيد علي الحسيني السيستاني");
        sistani.setNameEn("Ayatollah Ali al-Sistani");
        sistani.setDescriptionAr("المرجع الديني الأعلى");
        sistani.setDescriptionEn("Grand Ayatollah");
        sistani.setIsActive(true);
        
        Marja khamenei = new Marja();
        khamenei.setNameAr("السيد علي الحسيني الخامنئي");
        khamenei.setNameEn("Ayatollah Ali Khamenei");
        khamenei.setDescriptionAr("المرشد الأعلى للجمهورية الإسلامية الإيرانية");
        khamenei.setDescriptionEn("Supreme Leader of Iran");
        khamenei.setIsActive(true);
        
        marjaRepository.saveAll(List.of(sistani, khamenei));
        log.info("✓ Marjas seeded: 2 records");
    }

    private void seedCategories() {
        log.info("📚 Seeding Categories...");
        
        Category[] categories = {
            createCategory("الطهارة", "Taharah", "أحكام الطهارة والنجاسة والوضوء والغسل", 
                "Purity, Impurity, Wudu, Ghusl", "🚿", "bg-blue-500", 1),
            createCategory("الصلاة", "Salat", "أحكام الصلاة اليومية وشروطها وأوقاتها", 
                "Daily Prayers and their conditions", "🕌", "bg-green-500", 2),
            createCategory("الصوم", "Sawm", "أحكام الصيام ومفطراته", 
                "Fasting and its invalidators", "🌙", "bg-purple-500", 3),
            createCategory("الخمس", "Khums", "الخمس وأحكامه", 
                "Khums Tax", "💰", "bg-yellow-500", 4),
            createCategory("الزكاة", "Zakat", "أحكام الزكاة", 
                "Alms", "🤲", "bg-pink-500", 5),
            createCategory("الحج", "Hajj", "أحكام الحج والعمرة", 
                "Pilgrimage", "🕋", "bg-red-500", 6),
            createCategory("النكاح", "Nikah", "أحكام الزواج والمهر", 
                "Marriage and Mahr", "💍", "bg-rose-500", 7),
            createCategory("الطلاق", "Talaq", "أحكام الطلاق والخلع", 
                "Divorce", "📜", "bg-gray-500", 8),
            createCategory("المعاملات", "Muamalat", "المعاملات المالية والتجارية", 
                "Financial Transactions", "💼", "bg-indigo-500", 9),
            createCategory("الأطعمة والأشربة", "Foods", "أحكام الأطعمة والأشربة الحلال والحرام", 
                "Halal and Haram Foods", "🍽️", "bg-orange-500", 10)
        };
        
        categoryRepository.saveAll(List.of(categories));
        log.info("✓ Categories seeded: 10 records");
    }

    private Category createCategory(String nameAr, String nameEn, String descAr, 
                                   String descEn, String icon, String color, int order) {
        Category category = new Category();
        category.setNameAr(nameAr);
        category.setNameEn(nameEn);
        category.setDescriptionAr(descAr);
        category.setDescriptionEn(descEn);
        category.setIcon(icon);
        category.setColor(color);
        category.setDisplayOrder(order);
        category.setIsActive(true);
        return category;
    }

    private void seedQuestions() {
        log.info("❓ Seeding Questions...");
        
        Category salat = categoryRepository.findByNameEn("Salat").orElseThrow();
        Category khums = categoryRepository.findByNameEn("Khums").orElseThrow();
        Category sawm = categoryRepository.findByNameEn("Sawm").orElseThrow();
        Category taharah = categoryRepository.findByNameEn("Taharah").orElseThrow();
        Category hajj = categoryRepository.findByNameEn("Hajj").orElseThrow();
        Category nikah = categoryRepository.findByNameEn("Nikah").orElseThrow();
        
        Marja sistani = marjaRepository.findById(1L).orElseThrow();

        // SALAT QUESTIONS (6 questions)
        questionRepository.save(createQuestion(salat, sistani,
            "إذا شككت بين ثلاث ركعات وأربع في صلاة الظهر بعد إكمال السجدتين، ماذا تفعل؟",
            "If you doubt between 3 and 4 rakats in Dhuhr prayer after completing both sajdahs, what should you do?",
            "أبدأ الصلاة من جديد", "أفترض أنها ثلاث وأستمر", "أفترض أنها أربع وأكمل ثم أصلي صلاة الاحتياط", "أقطع الصلاة وأسأل أحداً",
            "Start prayer over", "Assume it was 3 and continue", "Assume it was 4, finish, then pray salat al-ihtiyat", "Break prayer and ask someone",
            2,
            "حسب فتوى السيد السيستاني، إذا شككت بين الثلاث والأربع بعد إكمال السجدتين من الركعة الرابعة، تفترض أنها أربع وتكمل الصلاة، ثم تصلي صلاة الاحتياط ركعة واحدة قائماً أو ركعتين جالساً.",
            "According to Ayatollah Sistani, if you doubt between 3 and 4 after completing the second sajdah of the fourth rakat, assume it was 4, complete the prayer, then perform salat al-ihtiyat of one rakat standing or two rakats sitting.",
            "توضيح المسائل، كتاب الصلاة، مسألة 1161", "Islamic Laws, Book of Prayer, Issue 1161",
            "intermediate", "doubt,salat,rakats"));

        questionRepository.save(createQuestion(salat, sistani,
            "ما هو أول وقت صلاة الظهر؟",
            "What is the earliest time for Dhuhr prayer?",
            "طلوع الشمس", "الزوال (منتصف النهار الشرعي)", "بعد الزوال بساعة", "عند اصفرار الشمس",
            "Sunrise", "Zawal (legal midday)", "One hour after zawal", "When sun turns yellow",
            1,
            "يبدأ وقت صلاة الظهر من الزوال، وهو منتصف ما بين طلوع الشمس إلى غروبها، ويُعرف بزوال الشمس عن وسط السماء.",
            "The time for Dhuhr prayer begins at zawal, which is the midpoint between sunrise and sunset, known as when the sun passes the meridian.",
            "توضيح المسائل، مسألة 738", "Islamic Laws, Issue 738",
            "beginner", "salat,time,dhuhr"));

        questionRepository.save(createQuestion(salat, sistani,
            "كم عدد ركعات صلاة المغرب؟",
            "How many rakats are in Maghrib prayer?",
            "ركعتان", "ثلاث ركعات", "أربع ركعات", "خمس ركعات",
            "Two rakats", "Three rakats", "Four rakats", "Five rakats",
            1,
            "صلاة المغرب ثلاث ركعات، وهي من الصلوات الواجبة اليومية.",
            "Maghrib prayer consists of three rakats, and it is one of the obligatory daily prayers.",
            "توضيح المسائل، مسألة 702", "Islamic Laws, Issue 702",
            "beginner", "salat,maghrib,rakats"));

        questionRepository.save(createQuestion(salat, sistani,
            "ماذا تفعل إذا نسيت السجدة في الصلاة وتذكرتها بعد الركوع في الركعة التالية؟",
            "What should you do if you forgot a sajdah and remembered after the rukoo of the next rakat?",
            "أرجع فوراً وأسجد", "أتم الصلاة وأسجد سجدتي السهو", "أتم الصلاة وأقضي السجدة بعدها", "أعيد الصلاة من جديد",
            "Go back immediately and prostrate", "Complete prayer and do sajda al-sahw", "Complete prayer and make up the sajdah after", "Restart the prayer",
            2,
            "إذا نسيت السجدة وتجاوزت محلها، تتم الصلاة وتقضي السجدة المنسية بعد الصلاة، ثم تسجد سجدتي السهو للزيادة.",
            "If you forgot a sajdah and passed its place, complete the prayer, then make up the forgotten sajdah after prayer, followed by sajda al-sahw for the extra.",
            "توضيح المسائل، مسألة 1233", "Islamic Laws, Issue 1233",
            "intermediate", "salat,sajdah,forgetfulness"));

        // KHUMS QUESTIONS (3 questions)
        questionRepository.save(createQuestion(khums, sistani,
            "هل يجب الخمس على الذهب والفضة التي تلبسها المرأة للزينة؟",
            "Is Khums due on gold and silver jewelry that a woman wears for adornment?",
            "نعم، يجب الخمس دائماً", "لا يجب الخمس إذا كانت تستعملها للزينة", "يجب فقط إذا كانت غالية الثمن", "يجب إذا لم تلبسها لمدة سنة",
            "Yes, Khums is always due", "No Khums if used for adornment", "Only if very expensive", "Only if not worn for a year",
            1,
            "حسب فتوى السيد السيستاني، الحلي من الذهب والفضة التي تستعملها المرأة للزينة لا يجب فيها الخمس، حتى لو لم تستعملها لفترة من الزمن، ما دامت محفوظة لديها للاستعمال.",
            "According to Ayatollah Sistani, gold and silver ornaments that a woman uses for adornment do not have Khums on them, even if not worn for a period, as long as kept for wearing.",
            "توضيح المسائل، كتاب الخمس، مسألة 1775", "Islamic Laws, Book of Khums, Issue 1775",
            "beginner", "khums,jewelry,women"));

        questionRepository.save(createQuestion(khums, sistani,
            "متى يجب إخراج الخمس من الراتب الشهري؟",
            "When is Khums due on monthly salary?",
            "في نفس يوم استلام الراتب", "بعد مرور سنة من أول دخل", "بعد مرور شهر من استلامه", "بعد صرف المصاريف السنوية",
            "On the day of receiving salary", "After one year from first income", "After one month of receiving it", "After annual expenses are deducted",
            1,
            "الخمس يجب بعد مرور سنة على أول دخل، وبعد استثناء المؤونة السنوية (المصاريف الضرورية).",
            "Khums is due after one year from the first income, after deducting annual maintenance expenses (necessary expenditures).",
            "توضيح المسائل، مسألة 1769", "Islamic Laws, Issue 1769",
            "intermediate", "khums,salary,timing"));

        // SAWM QUESTIONS (4 questions)
        questionRepository.save(createQuestion(sawm, sistani,
            "ما حكم من أكل أو شرب ناسياً في نهار رمضان؟",
            "What is the ruling for someone who eats or drinks forgetfully during Ramadan?",
            "صومه باطل ويجب القضاء والكفارة", "صومه باطل ويجب القضاء فقط", "صومه صحيح", "صومه صحيح لكن يستحب القضاء",
            "Fast invalid, qadha and kaffarah required", "Fast invalid, only qadha required", "Fast is valid", "Fast valid but qadha recommended",
            2,
            "من أكل أو شرب ناسياً، صومه صحيح ولا قضاء عليه ولا كفارة. لكن إذا تذكر وهو يأكل أو يشرب، وجب عليه أن يخرج ما في فمه فوراً.",
            "Whoever eats or drinks forgetfully, their fast is valid with no qadha or kaffarah. But if they remember while eating/drinking, they must immediately expel what is in their mouth.",
            "توضيح المسائل، مسألة 1595", "Islamic Laws, Issue 1595",
            "beginner", "sawm,forgetfulness,ramadan"));

        questionRepository.save(createQuestion(sawm, sistani,
            "هل يجوز للحامل الإفطار في رمضان؟",
            "Is a pregnant woman allowed to break her fast in Ramadan?",
            "نعم، بدون أي شرط", "نعم، إذا خافت على نفسها أو جنينها", "لا، يجب عليها الصوم مطلقاً", "نعم، في الشهور الأخيرة فقط",
            "Yes, without any condition", "Yes, if she fears for herself or her fetus", "No, she must fast absolutely", "Yes, only in the last months",
            1,
            "يجوز للمرأة الحامل الإفطار إذا خافت الضرر على نفسها أو على جنينها، وعليها القضاء، وإن كان الخوف على الجنين فقط وجبت الفدية أيضاً.",
            "A pregnant woman may break her fast if she fears harm to herself or her fetus. She must make up the fast, and if the fear is only for the fetus, she must also pay fidyah.",
            "توضيح المسائل، مسألة 1629", "Islamic Laws, Issue 1629",
            "intermediate", "sawm,pregnancy,exemption"));

        // TAHARAH QUESTIONS (3 questions)
        questionRepository.save(createQuestion(taharah, sistani,
            "هل الكلب نجس؟",
            "Is a dog najis (impure)?",
            "نعم، نجس", "لا، طاهر", "نجس فقط إذا كان مؤذياً", "طاهر إذا كان منزلياً",
            "Yes, najis", "No, pure", "Najis only if harmful", "Pure if domesticated",
            0,
            "الكلب نجس على الأحوط وجوباً، وكذلك الخنزير. ويجب غسل ما لاقاه أحدهما مع الرطوبة.",
            "A dog is najis as an obligatory precaution, as is a pig. Whatever contacts them with moisture must be washed.",
            "توضيح المسائل، مسألة 88", "Islamic Laws, Issue 88",
            "beginner", "taharah,najasah,dog"));

        questionRepository.save(createQuestion(taharah, sistani,
            "كم مرة يجب غسل الإناء الذي ولغ فيه الكلب؟",
            "How many times must a container be washed if a dog licks from it?",
            "مرة واحدة بالماء", "مرتين بالماء", "ثلاث مرات، الأولى بالتراب", "سبع مرات، الأولى بالتراب",
            "Once with water", "Twice with water", "Three times, first with soil", "Seven times, first with soil",
            2,
            "يجب غسل الإناء الذي ولغ فيه الكلب ثلاث مرات، على الأحوط وجوباً أن تكون الأولى بالتراب.",
            "A container that a dog licks must be washed three times, and as an obligatory precaution, the first wash should be with soil.",
            "توضيح المسائل، مسألة 105", "Islamic Laws, Issue 105",
            "intermediate", "taharah,najasah,washing"));

        // HAJJ QUESTIONS (2 questions)
        questionRepository.save(createQuestion(hajj, sistani,
            "ما هي أول أفعال الحج؟",
            "What is the first act of Hajj?",
            "الطواف", "السعي", "الإحرام من الميقات", "الوقوف بعرفات",
            "Tawaf", "Sa'i", "Ihram from Miqat", "Standing at Arafat",
            2,
            "أول أفعال الحج هو الإحرام من الميقات، وهو النية والتلبية ولبس ثوبي الإحرام.",
            "The first act of Hajj is Ihram from the Miqat, which includes intention, talbiyah, and wearing the two ihram garments.",
            "توضيح المسائل، مسألة 2062", "Islamic Laws, Issue 2062",
            "beginner", "hajj,ihram,miqat"));

        // NIKAH QUESTIONS (2 questions)
        questionRepository.save(createQuestion(nikah, sistani,
            "هل يشترط وجود الشهود في عقد الزواج الدائم؟",
            "Are witnesses required for a permanent marriage contract?",
            "نعم، يشترط شاهدان عدلان", "لا يشترط الشهود", "يشترط فقط في زواج البكر", "يشترط إذا كان في المحكمة",
            "Yes, two just witnesses required", "Witnesses are not required", "Required only for virgin's marriage", "Required if in court",
            1,
            "لا يشترط وجود الشهود في عقد الزواج الدائم حسب فتوى السيد السيستاني، ولكن يستحب الإشهاد.",
            "According to Ayatollah Sistani, witnesses are not required for a permanent marriage contract, but having them is recommended.",
            "توضيح المسائل، مسألة 2374", "Islamic Laws, Issue 2374",
            "intermediate", "nikah,marriage,witnesses"));

        log.info("✓ Questions seeded: 16 records");
    }

    private void seedTestUser() {
        log.info("👤 Seeding Test User...");
        
        // Check if test user already exists
        if (userRepository.existsByEmail("test@fiqhmaster.com")) {
            log.info("✓ Test user already exists");
            return;
        }
        
        Marja sistani = marjaRepository.findById(1L).orElse(null);
        
        User testUser = new User();
        testUser.setEmail("test@gmail.com");
        testUser.setPassword(passwordEncoder.encode("000000"));
        testUser.setFullName("عبدالرحمن مجدي  ");
        testUser.setPreferredLanguage("ar");
        testUser.setPreferredMarja(sistani);
        testUser.setDifficultyLevel("intermediate");
        testUser.setDailyReminders(true);
        testUser.setCurrentStreak(7);
        testUser.setLongestStreak(15);
        testUser.setTotalQuizzes(47);
        testUser.setTotalCorrectAnswers(352);
        testUser.setTotalAnswers(470);
        testUser.setCurrentRank("فقيه متوسط");
        testUser.setIsActive(true);
        
        // Add some badges
        testUser.getBadges().add("🎯"); // First 10 quizzes
        testUser.getBadges().add("🔥"); // 7 day streak
        testUser.getBadges().add("⭐"); // 100 correct answers
        
        userRepository.save(testUser);
        
        log.info("✓ Test user created:");
        log.info("  📧 Email: test@gmail.com");
        log.info("  🔑 Password: 000000");
    }

    private Question createQuestion(Category category, Marja marja,
                                   String questionAr, String questionEn,
                                   String optionAAr, String optionBAr, String optionCAr, String optionDAr,
                                   String optionAEn, String optionBEn, String optionCEn, String optionDEn,
                                   int correctAnswer,
                                   String explanationAr, String explanationEn,
                                   String referenceAr, String referenceEn,
                                   String difficulty, String tags) {
        Question q = new Question();
        q.setCategory(category);
        q.setMarja(marja);
        q.setQuestionAr(questionAr);
        q.setQuestionEn(questionEn);
        q.setOptionAAr(optionAAr);
        q.setOptionBAr(optionBAr);
        q.setOptionCAr(optionCAr);
        q.setOptionDAr(optionDAr);
        q.setOptionAEn(optionAEn);
        q.setOptionBEn(optionBEn);
        q.setOptionCEn(optionCEn);
        q.setOptionDEn(optionDEn);
        q.setCorrectAnswer(correctAnswer);
        q.setExplanationAr(explanationAr);
        q.setExplanationEn(explanationEn);
        q.setReferenceAr(referenceAr);
        q.setReferenceEn(referenceEn);
        q.setDifficulty(difficulty);
        q.setTags(tags);
        q.setIsActive(true);
        return q;
    }
}