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
        long questionCount = questionRepository.count();
        
        if (marjaRepository.count() == 0) {
            log.info("🌱 Starting enhanced database seeding...");
            seedMarjas();
            seedCategories();
            seedAllQuestions();
            seedTestUser();
            log.info("✅ Enhanced database seeding completed with 50+ questions!");
        } else if (questionCount < 50) {
            log.info("🌱 Adding more questions... (current: " + questionCount + ")");
            seedAllQuestions();
            log.info("✅ Additional questions added! Total now: " + questionRepository.count());
        } else {
            log.info("📊 Database already contains sufficient data (" + questionCount + " questions). Skipping seeding.");
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
            "Halal and Haram Foods", "🍽️", "bg-orange-500", 10),
        createCategory("الأمر بالمعروف", "Amr bil Maroof", "الأمر بالمعروف والنهي عن المنكر", 
            "Enjoining Good and Forbidding Evil", "⚖️", "bg-teal-500", 11),
        createCategory("الأيمان والنذور", "Oaths and Vows", "أحكام الأيمان والنذور والعهود",
            "Oaths, Vows and Covenants", "🤝", "bg-cyan-500", 12)
    };
    
    categoryRepository.saveAll(List.of(categories));
    log.info("✓ Categories seeded: 12 records");
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

   private void seedAllQuestions() {
    log.info("❓ Seeding 50+ Questions from Sistani's Rulings...");
    
    Category salat = categoryRepository.findByNameEn("Salat").orElseThrow();
    Category khums = categoryRepository.findByNameEn("Khums").orElseThrow();
    Category sawm = categoryRepository.findByNameEn("Sawm").orElseThrow();
    Category taharah = categoryRepository.findByNameEn("Taharah").orElseThrow();
    Category hajj = categoryRepository.findByNameEn("Hajj").orElseThrow();
    Category nikah = categoryRepository.findByNameEn("Nikah").orElseThrow();
    Category talaq = categoryRepository.findByNameEn("Talaq").orElseThrow();
    Category muamalat = categoryRepository.findByNameEn("Muamalat").orElseThrow();
    Category foods = categoryRepository.findByNameEn("Foods").orElseThrow();
    Category zakat = categoryRepository.findByNameEn("Zakat").orElseThrow();
    Category amr = categoryRepository.findByNameEn("Amr bil Maroof").orElseThrow();
    Category oaths = categoryRepository.findByNameEn("Oaths and Vows").orElseThrow();
    
    Marja sistani = marjaRepository.findById(1L).orElseThrow();

        // =============== SALAT QUESTIONS (12 questions) ===============
        
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

        questionRepository.save(createQuestion(salat, sistani,
            "إذا نسيت التشهد في الركعة الثانية وقمت للركعة الثالثة، ماذا تفعل؟",
            "If you forgot tashahhud in the second rakat and stood up for the third, what should you do?",
            "أرجع فوراً وأتشهد", "أستمر في الصلاة إذا تذكرت بعد الركوع", "أعيد الصلاة من البداية", "أقطع الصلاة",
            "Go back immediately and do tashahhud", "Continue if you remembered after rukoo", "Restart the prayer", "Break the prayer",
            1,
            "إذا تذكرت قبل الركوع وجب الجلوس والتشهد، وإذا تذكرت بعد الركوع تكمل الصلاة ثم تسجد سجدتي السهو.",
            "If you remember before rukoo, you must sit and do tashahhud. If you remember after rukoo, complete the prayer then do sajda al-sahw.",
            "sistani.org/20087", "sistani.org/20087",
            "intermediate", "salat,tashahhud,forgetfulness"));

        questionRepository.save(createQuestion(salat, sistani,
            "هل يجوز الصلاة من جلوس لشخص سليم يشعر بالتعب؟",
            "Is it permissible for a healthy person to pray sitting due to tiredness?",
            "نعم، يجوز", "لا، صلاته باطلة", "يجوز فقط في صلاة النافلة", "يجوز إذا كان التعب شديداً",
            "Yes, it is permissible", "No, the prayer is invalid", "Only in voluntary prayers", "Only if very tired",
            1,
            "صلاة الشخص السليم من جلوس لمجرد الإحساس بالتعب والإرهاق باطلة، ويجب الصلاة من قيام إلا عند العجز الحقيقي.",
            "The prayer of a healthy person sitting merely due to tiredness is invalid. One must pray standing unless truly unable.",
            "sistani.org/22305", "sistani.org/22305",
            "beginner", "salat,sitting,conditions"));

        questionRepository.save(createQuestion(salat, sistani,
            "إذا سقط المصلي أثناء الصلاة على الأرض، ما حكم صلاته؟",
            "If someone falls to the ground during prayer, what is the ruling?",
            "تبطل الصلاة ويجب الاستئناف", "يقوم ويكمل صلاته من حيث وصل", "يجب البدء من الركعة الأولى", "يتم الصلاة من الأرض",
            "Prayer is invalid, must restart", "Stand up and complete from where he was", "Must start from first rakat", "Complete prayer from the ground",
            1,
            "السقوط في أثناء الصلاة في حد ذاته لا يمنع من إكمالها، فيقوم ويكمل صلاته من حيث وصل قبل السقوط.",
            "Falling during prayer in itself does not prevent its completion. One can stand up and complete the prayer from where they were.",
            "sistani.org/5238", "sistani.org/5238",
            "intermediate", "salat,falling,continuation"));

        questionRepository.save(createQuestion(salat, sistani,
            "هل يجب الترتيب بين صلاة الظهر والعصر؟",
            "Is sequence required between Dhuhr and Asr prayers?",
            "لا يجب الترتيب", "نعم، يجب تقديم الظهر على العصر", "يجوز تقديم أيهما شئت", "الترتيب مستحب فقط",
            "No sequence required", "Yes, Dhuhr must precede Asr", "Either can be prayed first", "Sequence is only recommended",
            1,
            "يعتبر الترتيب بين الصلاتين، فلا يجوز تقديم العصر على الظهر عمداً. لكن إذا صلى العصر قبل الظهر نسياناً صحت صلاته.",
            "Sequence is required between the two prayers. Deliberately praying Asr before Dhuhr is not allowed, but if done forgetfully the prayer is valid.",
            "sistani.org/5046", "sistani.org/5046",
            "beginner", "salat,sequence,timing"));

        questionRepository.save(createQuestion(salat, sistani,
            "ما حكم من يترك التسبيحات الأربع في الركعتين الثالثة والرابعة في صلاة الجماعة ظناً أن الإمام يتحملها؟",
            "What is the ruling for someone who omits the four tasbihat in the 3rd and 4th rakats in congregation, thinking the imam takes responsibility?",
            "صلاته صحيحة", "عليه الإعادة إذا كان جاهلاً مقصراً", "عليه سجود السهو فقط", "يجب قضاء الصلوات",
            "Prayer is valid", "Must repeat if ignorant through negligence", "Only sajda al-sahw required", "Must make up the prayers",
            1,
            "إذا كان جاهلاً قاصراً فلا شيء عليه، وإذا كان مقصراً لزمته الإعادة ومع مضي الوقت يجب القضاء.",
            "If ignorant through no fault of his own, nothing is required. If negligent, he must repeat, and if the time has passed, he must make up the prayer.",
            "sistani.org/5026", "sistani.org/5026",
            "intermediate", "salat,tasbihat,congregation"));

        questionRepository.save(createQuestion(salat, sistani,
            "هل يجوز قطع الصلاة للرد على الهاتف أو فتح الباب؟",
            "Is it permissible to break prayer to answer the phone or open the door?",
            "لا يجوز مطلقاً", "نعم يجوز، والأحوط تركه", "يجوز فقط في حالة الضرورة", "يجوز في صلاة النافلة فقط",
            "Not permissible at all", "Yes permissible, though precaution is to avoid", "Only in necessity", "Only in voluntary prayers",
            1,
            "نعم يجوز قطع الصلاة للإجابة على الهاتف أو لفتح باب الدار وإن كان الأحوط تركه.",
            "Yes, it is permissible to break prayer to answer the phone or open the door, though the precautionary measure is to avoid it.",
            "sistani.org/5243", "sistani.org/5243",
            "intermediate", "salat,interruption,ruling"));

        questionRepository.save(createQuestion(salat, sistani,
            "هل يجب رفع الساقين عن الأرض في التجافي؟",
            "Is it required to lift the legs off the ground during tajafi (sitting between prostrations)?",
            "لا يجب", "نعم يجب على النهج المتعارف", "يكفي رفع العجز", "مستحب فقط",
            "Not required", "Yes, required in the customary manner", "Lifting the hips is sufficient", "Only recommended",
            1,
            "يعتبر في التجافي رفع الساقين عن الأرض على النهج المتعارف، ولا يكفي رفع العجز على الأحوط.",
            "Tajafi requires lifting the legs off the ground in the customary manner. As a precaution, lifting only the hips is not sufficient.",
            "sistani.org/21736", "sistani.org/21736",
            "advanced", "salat,tajafi,sitting"));

        questionRepository.save(createQuestion(salat, sistani,
            "إذا صليت صلاة وبعد الانتهاء عرفت أنك سجدت سجدة واحدة فقط، ما الحكم؟",
            "If after finishing prayer you realize you only did one sajdah, what is the ruling?",
            "تعيد الصلاة", "تقضي سجدة واحدة", "تسجد سجدتي السهو", "لا شيء عليك",
            "Repeat the prayer", "Make up one sajdah", "Do sajda al-sahw", "Nothing required",
            1,
            "تقضي سجدة واحدة بعد الصلاة.",
            "You make up one sajdah after the prayer.",
            "sistani.org/8975", "sistani.org/8975",
            "intermediate", "salat,sajdah,makeup"));

        // =============== TAHARAH QUESTIONS (8 questions) ===============
        
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

        questionRepository.save(createQuestion(taharah, sistani,
            "هل يجوز الوضوء في الحمام أثناء الاستحمام؟",
            "Is it permissible to perform wudu while showering?",
            "لا يجوز", "نعم يجوز بشرط النية", "يجوز فقط إذا كان الماء طاهراً", "الأحوط تركه",
            "Not permissible", "Yes, with proper intention", "Only if water is pure", "Precautionary to avoid",
            1,
            "يجوز الوضوء أثناء الاستحمام بشرط استيفاء جميع شروطه من النية والترتيب والموالاة.",
            "Wudu is permissible while showering provided all conditions are met including intention, sequence, and continuity.",
            "أحكام الوضوء", "Rulings of Wudu",
            "beginner", "taharah,wudu,shower"));

        questionRepository.save(createQuestion(taharah, sistani,
            "ما حكم الوضوء بماء الورد أو ماء الزهر؟",
            "What is the ruling on wudu with rose water or orange blossom water?",
            "يجوز", "لا يجوز، يجب أن يكون ماءً مطلقاً", "يجوز إذا كان مخلوطاً بالماء", "يجوز للضرورة فقط",
            "Permissible", "Not permissible, must be pure water", "Permissible if mixed with water", "Only in necessity",
            1,
            "الوضوء بماء الورد أو ماء الزهر غير صحيح، لأنه يشترط في ماء الوضوء أن يكون مطلقاً لا مضافاً.",
            "Wudu with rose water or orange blossom water is invalid, as wudu water must be absolute (mutlaq) not mixed (mudaf).",
            "توضيح المسائل، أحكام المياه", "Islamic Laws, Water Rulings",
            "intermediate", "taharah,wudu,water"));

        questionRepository.save(createQuestion(taharah, sistani,
            "هل يجب الغسل من الجنابة فوراً؟",
            "Is ghusl from janaba required immediately?",
            "نعم، فوراً", "لا، لكن يجب قبل الصلاة", "مستحب فوراً", "يجب خلال ساعة",
            "Yes, immediately", "No, but required before prayer", "Recommended immediately", "Required within an hour",
            1,
            "لا يجب الغسل من الجنابة فوراً، لكن يجب قبل الصلاة وقراءة القرآن والدخول إلى المساجد.",
            "Ghusl from janaba is not required immediately, but is required before prayer, Quran recitation, and entering mosques.",
            "توضيح المسائل، أحكام الغسل", "Islamic Laws, Ghusl Rulings",
            "beginner", "taharah,ghusl,janaba"));

        questionRepository.save(createQuestion(taharah, sistani,
            "إذا شككت في الوضوء أثناء الصلاة، ما الحكم؟",
            "If you doubt about wudu during prayer, what is the ruling?",
            "تعيد الوضوء والصلاة", "تستمر في الصلاة", "تقطع الصلاة وتتوضأ", "تسأل شخصاً آخر",
            "Repeat wudu and prayer", "Continue the prayer", "Break prayer and do wudu", "Ask someone else",
            1,
            "إذا شككت في الوضوء أثناء الصلاة، تمضي في صلاتك ولا تلتفت للشك، لأن الشك بعد الدخول في الصلاة لا يُعتَنى به.",
            "If you doubt about wudu during prayer, continue and ignore the doubt, as doubt after entering prayer is not considered.",
            "توضيح المسائل، الشكوك", "Islamic Laws, Doubts",
            "intermediate", "taharah,wudu,doubt"));

        questionRepository.save(createQuestion(taharah, sistani,
            "هل يجب الترتيب في غسل الوجه واليدين في الوضوء؟",
            "Is sequence required in washing the face and hands in wudu?",
            "لا يجب", "نعم، الوجه ثم اليد اليمنى ثم اليسرى", "يجوز البدء باليدين", "الترتيب مستحب",
            "Not required", "Yes, face then right hand then left", "May start with hands", "Sequence recommended",
            1,
            "يجب الترتيب في الوضوء: غسل الوجه، ثم اليد اليمنى، ثم اليد اليسرى، ثم مسح الرأس، ثم القدمين.",
            "Sequence is required in wudu: wash face, then right hand, then left hand, then wipe head, then feet.",
            "sistani.org، الشرائط في الوضوء", "sistani.org, Conditions of Wudu",
            "beginner", "taharah,wudu,sequence"));

        questionRepository.save(createQuestion(taharah, sistani,
            "هل تنتقض الطهارة بلمس الميت؟",
            "Is purity nullified by touching a dead body?",
            "نعم، يجب الغسل", "لا، لكن يستحب الغسل", "يجب الوضوء فقط", "يبطل الوضوء والغسل معاً",
            "Yes, ghusl required", "No, but ghusl recommended", "Only wudu required", "Both wudu and ghusl nullified",
            0,
            "مس الميت بعد برودته وقبل تغسيله يوجب غسلاً خاصاً يسمى غسل مس الميت.",
            "Touching a dead body after it has cooled but before it is washed requires a special ghusl called ghusl mas al-mayyit.",
            "توضيح المسائل، مس الميت", "Islamic Laws, Touching the Dead",
            "intermediate", "taharah,ghusl,dead"));

        // =============== SAWM QUESTIONS (8 questions) ===============
        
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

        questionRepository.save(createQuestion(sawm, sistani,
            "هل يجوز استعمال بخاخ الربو في نهار رمضان؟",
            "Is using an asthma inhaler permissible during Ramadan fasting?",
            "لا يجوز مطلقاً", "يجوز وصومه صحيح", "يجوز لكن يبطل الصوم", "يجوز للضرورة فقط",
            "Not permissible at all", "Permissible and fast valid", "Permissible but invalidates fast", "Only in necessity",
            2,
            "استعمال بخاخ الربو في نهار رمضان يبطل الصوم على الأحوط وجوباً، ويجب القضاء.",
            "Using an asthma inhaler during Ramadan invalidates the fast as an obligatory precaution, and qadha is required.",
            "sistani.org، الصوم والبخاخ", "sistani.org, Fasting and Inhaler",
            "intermediate", "sawm,medicine,inhaler"));

        questionRepository.save(createQuestion(sawm, sistani,
            "هل يجب الصوم على المسافر في رمضان؟",
            "Is fasting required for a traveler in Ramadan?",
            "نعم، يجب مطلقاً", "لا، يفطر ويقضي", "يصوم إذا كان السفر قصيراً", "يصوم إذا كان من عمله",
            "Yes, absolutely required", "No, breaks fast and makes up", "Fasts if short travel", "Fasts if travel is his work",
            1,
            "المسافر يفطر في رمضان ويقضي بعده، إلا إذا كان السفر من عمله ككثير السفر.",
            "A traveler breaks the fast in Ramadan and makes it up later, unless travel is part of his work like a frequent traveler.",
            "sistani.org، صوم المسافر", "sistani.org, Traveler's Fast",
            "intermediate", "sawm,travel,exemption"));

        questionRepository.save(createQuestion(sawm, sistani,
            "ما حكم ابتلاع البلغم في نهار رمضان؟",
            "What is the ruling on swallowing phlegm during Ramadan?",
            "يبطل الصوم", "لا يبطل الصوم", "يبطل إذا كان كثيراً", "الأحوط القضاء",
            "Invalidates fast", "Does not invalidate", "Invalidates if excessive", "Precautionary to make up",
            1,
            "ابتلاع البلغم لا يبطل الصوم.",
            "Swallowing phlegm does not invalidate the fast.",
            "sistani.org/02333", "sistani.org/02333",
            "beginner", "sawm,phlegm,ruling"));

        questionRepository.save(createQuestion(sawm, sistani,
            "هل يجوز للصائم تنظيف أسنانه بالفرشاة؟",
            "Is it permissible for a fasting person to brush teeth?",
            "لا يجوز", "يجوز بشرط عدم ابتلاع الماء", "يجوز بدون معجون فقط", "مكروه",
            "Not permissible", "Permissible if no water swallowed", "Only without toothpaste", "Disliked",
            1,
            "يجوز للصائم تنظيف أسنانه بالفرشاة والمعجون، بشرط عدم ابتلاع الماء أو المعجون.",
            "A fasting person may brush teeth with toothpaste, provided no water or paste is swallowed.",
            "sistani.org/02330", "sistani.org/02330",
            "beginner", "sawm,brushing,permissibility"));

        questionRepository.save(createQuestion(sawm, sistani,
            "ما حكم صوم من تعمد البقاء على جنابة حتى الفجر في رمضان؟",
            "What is the ruling for someone who deliberately remains in janaba until Fajr in Ramadan?",
            "صومه صحيح", "صومه باطل ويجب القضاء", "صومه باطل ويجب القضاء والكفارة", "الأحوط القضاء",
            "Fast is valid", "Fast invalid, qadha required", "Fast invalid, qadha and kaffarah required", "Precautionary to make up",
            1,
            "من تعمد البقاء على جنابة حتى طلوع الفجر في صوم شهر رمضان، بطل صومه ووجب عليه القضاء.",
            "Whoever deliberately remains in janaba until Fajr during Ramadan fasting, the fast is invalid and qadha is required.",
            "sistani.org، الجنابة في نهار رمضان", "sistani.org, Janaba in Ramadan",
            "intermediate", "sawm,janaba,ruling"));

        questionRepository.save(createQuestion(sawm, sistani,
            "هل يجوز صوم يوم عاشوراء؟",
            "Is fasting on the day of Ashura permissible?",
            "نعم، مستحب", "لا، مكروه", "حرام", "لا بأس به",
            "Yes, recommended", "No, disliked", "Forbidden", "No problem",
            1,
            "صوم يوم عاشوراء مكروه.",
            "Fasting on the day of Ashura is disliked (makruh).",
            "sistani.org، صوم يوم عاشوراء", "sistani.org, Ashura Fasting",
            "beginner", "sawm,ashura,ruling"));

        // =============== KHUMS QUESTIONS (5 questions) ===============
        
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

        questionRepository.save(createQuestion(khums, sistani,
            "هل يجب الخمس على بيت السكن؟",
            "Is Khums due on a residential house?",
            "نعم، يجب مطلقاً", "لا يجب إذا اشتري من أرباح السنة", "يجب إذا كان فاخراً", "لا يجب على بيت السكن",
            "Yes, absolutely", "Not due if bought from annual profit", "Due if luxurious", "Not due on residential house",
            3,
            "بيت السكن الذي اشتراه من أرباح سنته لا يجب فيه الخمس، بشرط أن يكون مناسباً لشأنه.",
            "A residential house bought from the year's profit does not require Khums, provided it is appropriate to one's status.",
            "أحكام الخمس", "Khums Rulings",
            "intermediate", "khums,house,exemption"));

        questionRepository.save(createQuestion(khums, sistani,
            "هل يجب الخمس على الهدية المستلمة؟",
            "Is Khums due on a received gift?",
            "نعم، يجب بعد سنة", "لا يجب على الهدية", "يجب إذا كانت كبيرة", "يجب إذا لم تستعمل",
            "Yes, after one year", "Not due on gifts", "Due if large", "Due if not used",
            1,
            "الهدية لا يجب فيها الخمس، ولكن إذا بقي منها ما يزيد على المؤونة حتى رأس السنة الخمسية وجب فيه الخمس.",
            "Gifts do not require Khums, but if anything remains beyond maintenance until the Khums year-end and exceeds expenses, Khums is due on it.",
            "أحكام الخمس", "Khums Rulings",
            "intermediate", "khums,gift,ruling"));

        questionRepository.save(createQuestion(khums, sistani,
            "هل يجب الخمس على السيارة المستعملة للعمل؟",
            "Is Khums due on a car used for work?",
            "نعم، دائماً", "لا، إذا كانت من مؤونة السنة", "يجب بعد خمس سنوات", "يجب إذا كانت فاخرة",
            "Yes, always", "No, if from annual expenses", "Due after five years", "Due if luxurious",
            1,
            "السيارة المستعملة في العمل وكانت من مؤونة السنة لا يجب فيها الخمس.",
            "A car used for work that is from annual maintenance does not require Khums.",
            "أحكام الخمس", "Khums Rulings",
            "beginner", "khums,car,work"));

        // =============== HAJJ QUESTIONS (4 questions) ===============
        
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

        questionRepository.save(createQuestion(hajj, sistani,
            "هل يجوز التظليل للمحرم في الحج؟",
            "Is shading permissible for someone in ihram during Hajj?",
            "لا يجوز مطلقاً", "يجوز للضرورة", "يجوز للنساء والأطفال", "يجوز للجميع",
            "Not permissible at all", "Permissible for necessity", "Permissible for women and children", "Permissible for all",
            1,
            "لا يجوز للمحرم أن يستظل من الشمس والمطر حال السير، ولكن يجوز للمرأة والطفل، وكذا للمضطر.",
            "A muhrim (person in ihram) may not shade from sun or rain while moving, but women, children, and those in necessity may.",
            "sistani.org، التظليل في الحج", "sistani.org, Shading in Hajj",
            "intermediate", "hajj,ihram,shading"));

        questionRepository.save(createQuestion(hajj, sistani,
            "ما حكم من نسي الطواف في الحج وعاد إلى بلده؟",
            "What is the ruling for someone who forgot tawaf in Hajj and returned home?",
            "حجه باطل", "يجب عليه الرجوع", "ينوب عنه أحد", "يجب القضاء في العام القادم",
            "Hajj invalid", "Must return", "Someone does it on his behalf", "Must make up next year",
            1,
            "من نسي الطواف ورجع إلى بلده، يجب عليه الرجوع لأدائه إن أمكن، وإلا استناب.",
            "Whoever forgot tawaf and returned home must go back to perform it if possible, otherwise appoint a deputy.",
            "أحكام الطواف", "Tawaf Rulings",
            "advanced", "hajj,tawaf,forgetfulness"));

        questionRepository.save(createQuestion(hajj, sistani,
            "هل يجوز للمرأة الحائض أن تطوف؟",
            "May a menstruating woman perform tawaf?",
            "نعم، يجوز", "لا، حتى تطهر", "يجوز مع الكفارة", "يجوز طواف النساء فقط",
            "Yes, permissible", "No, until she purifies", "Permissible with kaffarah", "Only tawaf al-nisa",
            1,
            "لا يجوز للحائض أن تطوف حتى تطهر من حيضها.",
            "A menstruating woman may not perform tawaf until she purifies from her menstruation.",
            "أحكام الطواف", "Tawaf Rulings",
            "beginner", "hajj,tawaf,menstruation"));

        // =============== NIKAH QUESTIONS (4 questions) ===============
        
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

        questionRepository.save(createQuestion(nikah, sistani,
            "هل يجوز الزواج بالكتابية (المسيحية أو اليهودية)؟",
            "Is marriage to a woman of the Book (Christian or Jewish) permissible?",
            "نعم، مطلقاً", "نعم، متعة فقط على الأحوط", "لا، حرام مطلقاً", "يجوز بإذن الحاكم الشرعي",
            "Yes, absolutely", "Yes, temporary marriage only as precaution", "No, absolutely forbidden", "Permissible with religious authority permission",
            1,
            "يجوز الزواج المنقطع (المتعة) بالكتابية على الأحوط وجوباً، ولا يجوز الزواج الدائم بها.",
            "Temporary marriage (mut'a) with a woman of the Book is permissible as an obligatory precaution, but permanent marriage is not.",
            "أحكام النكاح", "Marriage Rulings",
            "advanced", "nikah,marriage,kitabiya"));

        questionRepository.save(createQuestion(nikah, sistani,
            "ما هو أقل مهر يمكن أن يُحدد في عقد الزواج؟",
            "What is the minimum mahr that can be set in a marriage contract?",
            "لا يوجد حد أدنى", "مثقال من الذهب", "مئة درهم", "ما يتراضى عليه الطرفان",
            "No minimum", "One mithqal of gold", "One hundred dirhams", "Whatever both parties agree",
            3,
            "لا حد لأقل المهر، فيصح ما تراضيا عليه ولو قل.",
            "There is no minimum for mahr; whatever both parties agree upon is valid, even if small.",
            "أحكام المهر", "Mahr Rulings",
            "beginner", "nikah,mahr,minimum"));

        questionRepository.save(createQuestion(nikah, sistani,
            "هل يشترط إذن الأب لزواج البنت البكر؟",
            "Is the father's permission required for a virgin girl's marriage?",
            "نعم، يشترط على الأحوط", "لا يشترط مطلقاً", "يشترط إذا كانت قاصراً", "يستحب فقط",
            "Yes, required as precaution", "Not required at all", "Required if minor", "Only recommended",
            0,
            "يشترط على الأحوط وجوباً إذن الأب أو الجد للأب في زواج البنت البكر الرشيدة.",
            "As an obligatory precaution, permission of the father or paternal grandfather is required for marriage of a mature virgin girl.",
            "توضيح المسائل، أولياء العقد", "Islamic Laws, Marriage Guardians",
            "intermediate", "nikah,permission,virgin"));

        // =============== FOODS QUESTIONS (4 questions) ===============
        
        questionRepository.save(createQuestion(foods, sistani,
            "هل يجوز أكل الأسماك التي ليس لها فلس؟",
            "Is eating fish without scales permissible?",
            "نعم، يجوز", "لا، حرام", "يجوز بعضها", "مكروه",
            "Yes, permissible", "No, forbidden", "Some are permissible", "Disliked",
            1,
            "لا يجوز أكل السمك الذي ليس له فلس، ويحرم من الأسماك ما عدا ذات الفلس.",
            "Eating fish without scales is not permissible. Among fish, only those with scales are lawful.",
            "توضيح المسائل، الأسماك", "Islamic Laws, Fish",
            "beginner", "foods,fish,scales"));

        questionRepository.save(createQuestion(foods, sistani,
            "ما حكم الجيلاتين المستخرج من الخنزير؟",
            "What is the ruling on gelatin extracted from pig?",
            "حلال", "حرام", "حلال بعد الاستحالة", "مشتبه",
            "Halal", "Haram", "Halal after transformation", "Doubtful",
            2,
            "الجيلاتين إذا استحال عن حقيقته الأولى فهو طاهر وحلال، وإلا فهو نجس وحرام.",
            "Gelatin that has been transformed from its original state is pure and halal, otherwise it is impure and haram.",
            "sistani.org/02067", "sistani.org/02067",
            "intermediate", "foods,gelatin,pig"));

        questionRepository.save(createQuestion(foods, sistani,
            "هل يجوز أكل لحم ذُبح بغير الطريقة الشرعية؟",
            "Is eating meat not slaughtered according to Islamic method permissible?",
            "نعم", "لا", "يجوز من أهل الكتاب", "يجوز للضرورة",
            "Yes", "No", "Permissible from People of the Book", "Permissible in necessity",
            1,
            "لا يجوز أكل لحم لم يذبح بالطريقة الشرعية، ويشترط في الذبح أن يكون الذابح مسلماً أو من أهل الكتاب.",
            "Eating meat not slaughtered according to Islamic method is not permissible. The slaughterer must be Muslim or from People of the Book.",
            "أحكام الذباحة", "Slaughter Rulings",
            "beginner", "foods,meat,slaughter"));

        questionRepository.save(createQuestion(foods, sistani,
            "هل يجوز شرب عصير العنب المغلي؟",
            "Is drinking boiled grape juice permissible?",
            "نعم، دائماً", "لا، إذا غلى بنفسه", "يجوز إذا ذهب ثلثاه", "مكروه",
            "Yes, always", "No, if it boiled by itself", "Permissible if two-thirds evaporated", "Disliked",
            2,
            "عصير العنب إذا غلى بنفسه أو بالنار يحرم، ويحل إذا ذهب ثلثاه بالغليان.",
            "Grape juice becomes haram if it boils by itself or by fire, and becomes halal if two-thirds evaporates through boiling.",
            "sistani.org/02262", "sistani.org/02262",
            "advanced", "foods,juice,grape"));

        // =============== ADDITIONAL DIVERSE QUESTIONS (5 questions) ===============

        questionRepository.save(createQuestion(muamalat, sistani,
            "هل يجوز بيع الأسهم في البورصة؟",
            "Is selling stocks in the stock market permissible?",
            "نعم، مطلقاً", "نعم، إذا كانت الشركة حلالاً", "لا، حرام", "مكروه",
            "Yes, absolutely", "Yes, if the company is halal", "No, haram", "Disliked",
            1,
            "يجوز بيع وشراء الأسهم إذا كانت الشركة لا تتعامل بالحرام.",
            "Buying and selling stocks is permissible if the company does not deal in haram.",
            "sistani.org، بيع الأسهم", "sistani.org, Stock Trading",
            "intermediate", "muamalat,stocks,trading"));

        questionRepository.save(createQuestion(talaq, sistani,
            "هل يجب حضور الشاهدين في الطلاق؟",
            "Are two witnesses required to be present for divorce?",
            "لا يشترط", "نعم، يشترط على الأحوط", "يشترط فقط في المحكمة", "مستحب",
            "Not required", "Yes, required as precaution", "Only in court", "Recommended",
            1,
            "يشترط في صحة الطلاق حضور شاهدين عادلين حال إيقاع الطلاق.",
            "Validity of divorce requires presence of two just witnesses at the time of pronouncing divorce.",
            "أحكام الطلاق", "Divorce Rulings",
            "intermediate", "talaq,divorce,witnesses"));

        questionRepository.save(createQuestion(zakat, sistani,
            "هل تجب زكاة الفطرة على الطفل؟",
            "Is Zakat al-Fitr obligatory for a child?",
            "نعم، على الطفل نفسه", "نعم، على وليه", "لا تجب", "مستحبة",
            "Yes, on the child himself", "Yes, on his guardian", "Not obligatory", "Recommended",
            1,
            "زكاة الفطرة واجبة على من تجب نفقته، فيجب على الأب إخراجها عن أولاده الصغار.",
            "Zakat al-Fitr is obligatory on behalf of those whose maintenance is obligatory, so a father must pay it for his young children.",
            "أحكام زكاة الفطرة", "Zakat al-Fitr Rulings",
            "beginner", "zakat,fitr,children"));

        questionRepository.save(createQuestion(muamalat, sistani,
            "هل يجوز أخذ القرض من البنك الربوي؟",
            "Is taking a loan from an interest-based bank permissible?",
            "نعم", "لا، مطلقاً", "يجوز بنية عدم الرد", "يجوز للضرورة",
            "Yes", "No, absolutely", "Permissible with intention not to repay", "Permissible in necessity",
            3,
            "لا يجوز أخذ القرض الربوي إلا في حالة الاضطرار، ويجب استنفاد كل السبل الأخرى.",
            "Taking an interest-based loan is not permissible except in necessity, and all other means must be exhausted.",
            "أحكام الربا", "Interest Rulings",
            "advanced", "muamalat,loan,interest"));
        
        // =============== ADDITIONAL 200 QUESTIONS ===============
// Add these to your DatabaseSeeder.java in the seedAllQuestions() method

// =============== MORE SALAT QUESTIONS (25 questions) ===============

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الالتفات يميناً وشمالاً في الصلاة؟",
    "Is turning right and left permissible during prayer?",
    "لا يجوز مطلقاً", "يجوز إذا لم يستدبر القبلة", "يجوز للضرورة", "يجوز في صلاة النافلة فقط",
    "Not permissible at all", "Permissible if not turning away from Qibla", "Only in necessity", "Only in voluntary prayer",
    1,
    "يجوز الالتفات في الصلاة يميناً وشمالاً ما لم يصل إلى حد الاستدبار، والأولى عدم الالتفات.",
    "Turning right and left during prayer is permissible as long as it doesn't reach the point of facing away from Qibla, though it's better not to turn.",
    "sistani.org/5247", "sistani.org/5247",
    "beginner", "salat,turning,movement"));

questionRepository.save(createQuestion(salat, sistani,
    "إذا دخل المصلي في صلاة الجماعة متأخراً ركعة، ماذا يفعل؟",
    "If someone joins congregational prayer one rakat late, what should they do?",
    "يتابع الإمام في كل شيء", "يقضي الركعة بعد سلام الإمام", "يعيد الصلاة منفرداً", "يكمل الصلاة من تلقاء نفسه",
    "Follow imam in everything", "Make up the rakat after imam's salam", "Repeat prayer alone", "Complete prayer independently",
    1,
    "إذا أدرك المأموم الإمام في الركعة الثانية، يكمل صلاته بعد تسليم الإمام بركعة واحدة.",
    "If the follower joins the imam in the second rakat, he completes his prayer after the imam's salam with one rakat.",
    "أحكام صلاة الجماعة", "Congregational Prayer Rulings",
    "intermediate", "salat,congregation,late"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم قراءة سورة العصر في الصلاة الواجبة؟",
    "What is the ruling on reciting Surat al-Asr in obligatory prayer?",
    "لا تجوز", "تجوز وتكفي", "يجب قراءة سورة أطول", "تجوز في النافلة فقط",
    "Not permissible", "Permissible and sufficient", "Must read longer surah", "Only in voluntary prayer",
    1,
    "يجوز قراءة أي سورة في الصلاة، حتى السور القصيرة كسورة العصر والكوثر.",
    "It is permissible to recite any surah in prayer, even short ones like Al-Asr and Al-Kawthar.",
    "أحكام القراءة في الصلاة", "Prayer Recitation Rulings",
    "beginner", "salat,recitation,surah"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز صلاة النافلة جالساً مع القدرة على القيام؟",
    "Is it permissible to pray voluntary prayer sitting while able to stand?",
    "لا يجوز", "يجوز", "مكروه", "يجوز بنصف الثواب",
    "Not permissible", "Permissible", "Disliked", "Permissible with half reward",
    1,
    "يجوز صلاة النافلة جالساً مع القدرة على القيام، ولكن ثواب الجالس نصف ثواب القائم.",
    "Praying voluntary prayer sitting while able to stand is permissible, but the reward of sitting is half that of standing.",
    "أحكام النوافل", "Voluntary Prayer Rulings",
    "beginner", "salat,voluntary,sitting"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من صلى بثوب نجس ناسياً أو جاهلاً؟",
    "What is the ruling for someone who prayed in impure clothing forgetfully or ignorantly?",
    "صلاته باطلة", "صلاته صحيحة", "يعيد في الوقت", "يسجد سجدتي السهو",
    "Prayer invalid", "Prayer valid", "Repeat within time", "Do sajda al-sahw",
    1,
    "إذا صلى بثوب نجس جاهلاً أو ناسياً ثم علم، فصلاته صحيحة ولا إعادة عليه.",
    "If someone prayed in impure clothing ignorantly or forgetfully then learned, their prayer is valid and need not be repeated.",
    "أحكام الطهارة في الصلاة", "Purity in Prayer Rulings",
    "intermediate", "salat,najasah,forgetfulness"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب السجود على الأرض أو ما أنبتت الأرض؟",
    "Is sajdah required on earth or what grows from earth?",
    "لا، يجوز على أي شيء", "نعم، على الأحوط وجوباً", "يجوز على السجاد", "يجوز على الورق",
    "No, permissible on anything", "Yes, as obligatory precaution", "Permissible on carpet", "Permissible on paper",
    1,
    "يجب السجود على الأرض أو ما أنبتت الأرض مما لا يؤكل ولا يلبس، كالتربة والحجر والخشب.",
    "Sajdah must be on earth or what grows from earth that is not eaten or worn, like soil, stone, and wood.",
    "أحكام السجود", "Sajdah Rulings",
    "intermediate", "salat,sajdah,surface"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم البكاء في الصلاة؟",
    "What is the ruling on crying during prayer?",
    "يبطل الصلاة", "لا يبطل إن كان لخشية الله", "يبطل إن كان بصوت", "مكروه",
    "Invalidates prayer", "Doesn't invalidate if from fear of Allah", "Invalidates if with sound", "Disliked",
    1,
    "البكاء في الصلاة لا يبطلها إذا كان لخشية الله، وإنما يبطلها إذا كان للأمور الدنيوية.",
    "Crying in prayer doesn't invalidate it if from fear of Allah, but invalidates it if for worldly matters.",
    "قواطع الصلاة", "Prayer Invalidators",
    "intermediate", "salat,crying,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل تجب صلاة الجمعة في زمن الغيبة؟",
    "Is Friday prayer obligatory during the occultation?",
    "نعم واجبة عيناً", "واجبة تخييراً مع الظهر", "مستحبة", "غير مشروعة",
    "Yes, individually obligatory", "Obligatory as choice with Dhuhr", "Recommended", "Not legislated",
    1,
    "صلاة الجمعة في زمن الغيبة واجبة تخييرية، أي يتخير المكلف بينها وبين صلاة الظهر.",
    "Friday prayer during occultation is optionally obligatory, meaning one can choose between it and Dhuhr prayer.",
    "أحكام صلاة الجمعة", "Friday Prayer Rulings",
    "advanced", "salat,friday,obligation"));

questionRepository.save(createQuestion(salat, sistani,
    "كم عدد النوافل اليومية؟",
    "How many daily voluntary prayers are there?",
    "17 ركعة", "34 ركعة", "51 ركعة", "68 ركعة",
    "17 rakats", "34 rakats", "51 rakats", "68 rakats",
    1,
    "النوافل اليومية 34 ركعة: 8 للظهر، 8 للعصر، 4 للمغرب، 2 بعد العشاء، 11 لصلاة الليل، وركعة الوتر.",
    "Daily voluntary prayers are 34 rakats: 8 for Dhuhr, 8 for Asr, 4 for Maghrib, 2 after Isha, 11 for night prayer, and Witr.",
    "النوافل اليومية", "Daily Voluntary Prayers",
    "beginner", "salat,voluntary,count"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز تأخير الصلاة عن أول وقتها بلا عذر؟",
    "Is delaying prayer from its earliest time permissible without excuse?",
    "لا يجوز", "يجوز مع الكراهة", "يجوز", "يجوز في بعض الصلوات",
    "Not permissible", "Permissible but disliked", "Permissible", "Permissible for some prayers",
    2,
    "يجوز تأخير الصلاة عن أول وقتها، لكن الأفضل المبادرة إلى الصلاة في أول الوقت.",
    "Delaying prayer from its earliest time is permissible, but it's better to hasten to pray at the beginning of its time.",
    "أوقات الصلاة", "Prayer Times",
    "beginner", "salat,timing,delay"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم صلاة من صلى قبل دخول الوقت؟",
    "What is the ruling on prayer performed before its time?",
    "صحيحة", "باطلة ويجب الإعادة", "باطلة إلا إذا كان جاهلاً", "مكروهة",
    "Valid", "Invalid and must repeat", "Invalid unless ignorant", "Disliked",
    1,
    "من صلى قبل دخول الوقت عامداً، فصلاته باطلة ويجب عليه الإعادة.",
    "Whoever prays before its time deliberately, their prayer is invalid and must be repeated.",
    "شروط الصلاة", "Prayer Conditions",
    "intermediate", "salat,timing,early"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز قراءة سور العزائم في الصلاة الواجبة؟",
    "Is reciting surahs with obligatory sajdah permissible in obligatory prayer?",
    "نعم، يجوز", "لا يجوز", "يجوز في النافلة فقط", "يجوز بشرط السجود فوراً",
    "Yes, permissible", "Not permissible", "Only in voluntary prayer", "Permissible if prostrating immediately",
    1,
    "لا يجوز قراءة سور العزائم الأربع في الصلاة الواجبة، وهي: السجدة، فصلت، النجم، العلق.",
    "Reciting the four surahs with obligatory sajdah is not permissible in obligatory prayer: Al-Sajdah, Fussilat, An-Najm, Al-Alaq.",
    "القراءة في الصلاة", "Recitation in Prayer",
    "intermediate", "salat,recitation,azaim"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الضحك في الصلاة؟",
    "What is the ruling on laughing during prayer?",
    "يبطل الصلاة مطلقاً", "يبطل إذا كان بصوت", "لا يبطل", "مكروه",
    "Invalidates prayer absolutely", "Invalidates if with sound", "Doesn't invalidate", "Disliked",
    1,
    "الضحك في الصلاة بصوت يبطلها، أما التبسم فلا يبطلها.",
    "Laughing with sound during prayer invalidates it, but smiling doesn't.",
    "قواطع الصلاة", "Prayer Invalidators",
    "beginner", "salat,laughing,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل تجب الطمأنينة في الركوع والسجود؟",
    "Is stillness required in rukoo and sajdah?",
    "لا تجب", "تجب في الركوع فقط", "تجب في السجود فقط", "تجب فيهما",
    "Not required", "Required in rukoo only", "Required in sajdah only", "Required in both",
    3,
    "تجب الطمأنينة في الركوع والسجود، بمعنى الاستقرار وعدم الحركة.",
    "Stillness is required in rukoo and sajdah, meaning stability and absence of movement.",
    "أركان الصلاة", "Prayer Pillars",
    "beginner", "salat,stillness,rukoo"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم صلاة من نسي قراءة الفاتحة في الصلاة؟",
    "What is the ruling for someone who forgot to recite Al-Fatiha in prayer?",
    "صلاته صحيحة", "صلاته باطلة", "يسجد سجدتي السهو", "يعيد الركعة",
    "Prayer valid", "Prayer invalid", "Do sajda al-sahw", "Repeat the rakat",
    1,
    "من نسي قراءة الفاتحة حتى ركع، فصلاته باطلة ويجب إعادتها.",
    "Whoever forgot to recite Al-Fatiha until rukoo, their prayer is invalid and must be repeated.",
    "واجبات الصلاة", "Prayer Obligations",
    "intermediate", "salat,fatiha,forgetfulness"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الصلاة في مكان مغصوب؟",
    "Is praying in usurped place permissible?",
    "نعم، يجوز", "لا، الصلاة باطلة", "يجوز للضرورة", "يجوز إذا كان جاهلاً",
    "Yes, permissible", "No, prayer invalid", "Permissible in necessity", "Permissible if ignorant",
    1,
    "الصلاة في المكان المغصوب باطلة، سواء كان عالماً أو جاهلاً على الأحوط.",
    "Prayer in a usurped place is invalid, whether knowingly or ignorantly, as a precaution.",
    "مكان المصلي", "Place of Prayer",
    "intermediate", "salat,place,usurpation"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الكلام في الصلاة؟",
    "What is the ruling on speaking during prayer?",
    "يبطل الصلاة مطلقاً", "يبطل إذا كان عمداً", "لا يبطل", "يبطل إذا كان بحرفين فأكثر",
    "Invalidates prayer absolutely", "Invalidates if deliberate", "Doesn't invalidate", "Invalidates if two letters or more",
    3,
    "الكلام عمداً في الصلاة بحرفين فأكثر يبطلها، أما الحرف الواحد أو السهو فلا.",
    "Deliberately speaking two letters or more during prayer invalidates it, but one letter or forgetfulness doesn't.",
    "قواطع الصلاة", "Prayer Invalidators",
    "intermediate", "salat,speaking,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز العدول من الحاضرة إلى الأداء؟",
    "Is switching from make-up prayer to on-time prayer permissible?",
    "نعم، يجوز", "لا، لا يجوز", "يجوز في بعض الحالات", "يجوز إذا لم يتجاوز النصف",
    "Yes, permissible", "No, not permissible", "Permissible in some cases", "Permissible if not past halfway",
    2,
    "يجوز العدول من قضاء الصلاة إلى الأداء إذا تذكر أن الوقت لم يخرج، بشرط عدم تجاوز محل العدول.",
    "Switching from make-up prayer to on-time prayer is permissible if one remembers the time hasn't passed, provided the point of switching hasn't been exceeded.",
    "أحكام القضاء", "Make-up Prayer Rulings",
    "advanced", "salat,qadha,switching"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من شك في عدد الركعات في صلاة الصبح؟",
    "What is the ruling for someone who doubts the number of rakats in Fajr prayer?",
    "يبني على الأكثر", "يبني على الأقل", "يعيد الصلاة", "يستمر ولا يلتفت",
    "Assume the more", "Assume the less", "Repeat prayer", "Continue and ignore",
    2,
    "الشك في عدد ركعات صلاة الصبح (أو المغرب) مبطل للصلاة، فيجب إعادتها.",
    "Doubt about the number of rakats in Fajr (or Maghrib) prayer invalidates it, so it must be repeated.",
    "الشكوك المبطلة", "Invalidating Doubts",
    "intermediate", "salat,doubt,fajr"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب ستر القدمين في صلاة المرأة؟",
    "Is covering the feet required in women's prayer?",
    "نعم، يجب", "لا، لا يجب", "يجب ظهر القدم فقط", "الأحوط وجوباً",
    "Yes, required", "No, not required", "Only top of feet required", "Obligatory precaution",
    3,
    "يجب على المرأة ستر جميع بدنها في الصلاة حتى الرأس والقدمين على الأحوط وجوباً.",
    "A woman must cover her entire body in prayer including head and feet as an obligatory precaution.",
    "ستر المرأة في الصلاة", "Women's Covering in Prayer",
    "beginner", "salat,women,covering"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم التثاؤب في الصلاة؟",
    "What is the ruling on yawning during prayer?",
    "يبطل الصلاة", "لا يبطل لكنه مكروه", "لا بأس به", "يجب منعه",
    "Invalidates prayer", "Doesn't invalidate but disliked", "No problem", "Must prevent it",
    1,
    "التثاؤب في الصلاة مكروه، ويستحب دفعه ما أمكن.",
    "Yawning during prayer is disliked, and it's recommended to prevent it as much as possible.",
    "مكروهات الصلاة", "Prayer Dislikes",
    "beginner", "salat,yawning,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل تجزئ النية القلبية في الصلاة؟",
    "Is mental intention sufficient in prayer?",
    "لا، يجب التلفظ", "نعم، تكفي النية القلبية", "يستحب التلفظ", "الأحوط التلفظ",
    "No, must verbalize", "Yes, mental intention sufficient", "Verbalization recommended", "Precautionary to verbalize",
    1,
    "تكفي النية القلبية في الصلاة، ولا يجب التلفظ بها، بل التلفظ بدعة.",
    "Mental intention suffices in prayer, verbalization is not required, and verbalizing it is an innovation.",
    "النية في الصلاة", "Intention in Prayer",
    "beginner", "salat,intention,verbalization"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز قراءة القرآن من المصحف في الصلاة؟",
    "Is reading Quran from mushaf permissible during prayer?",
    "لا يجوز", "يجوز في النافلة فقط", "يجوز مطلقاً", "يجوز للضرورة",
    "Not permissible", "Only in voluntary prayer", "Absolutely permissible", "Only in necessity",
    2,
    "يجوز القراءة من المصحف في الصلاة، سواء في الفريضة أو النافلة.",
    "Reading from the mushaf in prayer is permissible, whether in obligatory or voluntary prayer.",
    "القراءة في الصلاة", "Recitation in Prayer",
    "intermediate", "salat,mushaf,reading"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم صلاة الآيات عند الزلزال؟",
    "What is the ruling on prayer of signs during earthquake?",
    "واجبة فوراً", "مستحبة", "واجبة إذا كان قوياً", "غير مشروعة",
    "Immediately obligatory", "Recommended", "Obligatory if strong", "Not legislated",
    0,
    "صلاة الآيات واجبة عند حدوث الزلزال، ويجب أداؤها فوراً.",
    "Prayer of signs is obligatory when earthquake occurs, and must be performed immediately.",
    "صلاة الآيات", "Prayer of Signs",
    "intermediate", "salat,ayat,earthquake"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز المشي في الصلاة؟",
    "Is walking during prayer permissible?",
    "لا يجوز مطلقاً", "يجوز للضرورة", "يجوز خطوة أو خطوتين", "يجوز في النافلة",
    "Not permissible at all", "Permissible in necessity", "One or two steps permissible", "Permissible in voluntary prayer",
    1,
    "لا يجوز المشي في الصلاة إلا للضرورة، كدفع حيوان أو إطفاء نار.",
    "Walking during prayer is not permissible except in necessity, like repelling an animal or extinguishing fire.",
    "أفعال الصلاة", "Prayer Actions",
    "intermediate", "salat,walking,movement"));

// =============== MORE TAHARAH QUESTIONS (20 questions) ===============

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجوز الوضوء بالماء المشكوك في نجاسته؟",
    "Is wudu with water of doubtful impurity permissible?",
    "لا يجوز", "يجوز مع الكراهة", "يجوز", "يجب التيمم",
    "Not permissible", "Permissible but disliked", "Permissible", "Must do tayammum",
    2,
    "يجوز الوضوء بالماء المشكوك في نجاسته، لأن الأصل في الماء الطهارة.",
    "Wudu with water of doubtful impurity is permissible, as the original state of water is purity.",
    "الماء المشكوك", "Doubtful Water",
    "intermediate", "taharah,water,doubt"));

questionRepository.save(createQuestion(taharah, sistani,
    "كم مرة يجب غسل اليد في الوضوء؟",
    "How many times must the hand be washed in wudu?",
    "مرة واحدة", "مرتين", "ثلاث مرات", "مرة واحدة والثانية مستحبة",
    "Once", "Twice", "Three times", "Once, second recommended",
    3,
    "يجب غسل اليدين في الوضوء مرة واحدة، والمرة الثانية مستحبة.",
    "Washing hands in wudu is required once, and the second time is recommended.",
    "كيفية الوضوء", "How to Perform Wudu",
    "beginner", "taharah,wudu,washing"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب غسل باطن الأنف في الوضوء؟",
    "Is washing inside the nose required in wudu?",
    "نعم، يجب", "لا، لا يجب", "يجب في الغسل فقط", "مستحب",
    "Yes, required", "No, not required", "Required in ghusl only", "Recommended",
    1,
    "لا يجب غسل باطن الأنف في الوضوء، وإنما الواجب غسل ظاهر الأنف.",
    "Washing inside the nose is not required in wudu; only washing the outside of the nose is obligatory.",
    "حدود الوضوء", "Wudu Boundaries",
    "beginner", "taharah,wudu,nose"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الوضوء الجبيري على الجبيرة؟",
    "What is the ruling on wudu with splint (jabira)?",
    "باطل", "صحيح مع المسح عليها", "صحيح مع غسلها", "يجب التيمم",
    "Invalid", "Valid with wiping over it", "Valid with washing it", "Must do tayammum",
    1,
    "إذا كان على العضو جبيرة، يجب المسح عليها في الوضوء بدلاً من غسلها.",
    "If there is a splint on the limb, wiping over it is required in wudu instead of washing.",
    "الوضوء الجبيري", "Jabira Wudu",
    "intermediate", "taharah,wudu,jabira"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل ينقض الوضوء بخروج المذي؟",
    "Does wudu break with discharge of madhiy?",
    "نعم، ينقض", "لا، لا ينقض", "ينقض إذا كان كثيراً", "يوجب الغسل",
    "Yes, breaks", "No, doesn't break", "Breaks if excessive", "Requires ghusl",
    0,
    "خروج المذي ناقض للوضوء ويوجب الوضوء، ولا يوجب الغسل.",
    "Discharge of madhiy breaks wudu and requires wudu, but doesn't require ghusl.",
    "نواقض الوضوء", "Wudu Nullifiers",
    "intermediate", "taharah,wudu,madhiy"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الاستحمام في حوض السباحة للجنب؟",
    "What is the ruling on bathing in swimming pool for one in janaba?",
    "لا يجوز", "يجوز وي��زئ عن الغسل", "يجوز لكن لا يجزئ", "يجوز إذا كان الماء كراً",
    "Not permissible", "Permissible and suffices for ghusl", "Permissible but doesn't suffice", "Permissible if water is kur",
    3,
    "يجوز الاغتسال في حوض السباحة إذا كان ماؤه كراً، ويجزئ عن غسل الجنابة.",
    "Bathing in a swimming pool is permissible if its water is kur, and suffices for ghusl janaba.",
    "الغسل الارتماسي", "Immersion Ghusl",
    "intermediate", "taharah,ghusl,pool"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب الوضوء لدخول المسجد؟",
    "Is wudu required for entering the mosque?",
    "نعم، يجب", "لا، لا يجب", "يجب للمسجد الحرام فقط", "مستحب",
    "Yes, required", "No, not required", "Required for Masjid al-Haram only", "Recommended",
    3,
    "لا يجب الوضوء لدخول المسجد، ولكنه مستحب.",
    "Wudu is not required for entering the mosque, but it is recommended.",
    "آداب المسجد", "Mosque Etiquettes",
    "beginner", "taharah,wudu,mosque"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم البول واقفاً؟",
    "What is the ruling on urinating while standing?",
    "حرام", "مكروه شديداً", "جائز", "مكروه إذا كان بلا حاجة",
    "Forbidden", "Strongly disliked", "Permissible", "Disliked if without need",
    1,
    "البول واقفاً مكروه كراهة شديدة، خصوصاً إذا لم تكن هناك حاجة.",
    "Urinating while standing is strongly disliked, especially if there's no need.",
    "آداب قضاء الحاجة", "Toilet Etiquettes",
    "beginner", "taharah,urination,standing"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يطهر الثوب النجس بغسله في الغسالة؟",
    "Does impure clothing become pure by washing in washing machine?",
    "نعم، بشرط جريان الماء", "لا، لا يطهر", "يطهر بغسلة واحدة", "يطهر بثلاث غسلات",
    "Yes, provided water flows", "No, doesn't purify", "Purified with one wash", "Purified with three washes",
    0,
    "يطهر الثوب النجس بغسله في الغسالة بشرط جريان الماء عليه وخروج الماء المتنجس.",
    "Impure clothing is purified by washing in the machine provided water flows over it and the contaminated water exits.",
    "تطهير الثوب", "Purifying Clothing",
    "intermediate", "taharah,purification,washing"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب الاستبراء بعد البول؟",
    "Is istibra required after urination?",
    "نعم واجب", "لا، مستحب فقط", "واجب للرجل فقط", "واجب على الأحوط",
    "Yes, obligatory", "No, only recommended", "Obligatory for men only", "Obligatory as precaution",
    1,
    "الاستبراء بعد البول مستحب للرجل والمرأة، وفائدته عدم الحكم بنجاسة الرطوبة الخارجة بعده.",
    "Istibra after urination is recommended for both men and women, and its benefit is not judging wetness that exits afterward as impure.",
    "الاستبراء", "Istibra",
    "intermediate", "taharah,istibra,urination"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم مني الإنسان؟",
    "What is the ruling on human semen?",
    "طاهر", "نجس", "نجس للرجل طاهر للمرأة", "طاهر إن كان من حلال",
    "Pure", "Impure", "Impure for men, pure for women", "Pure if from halal",
    1,
    "مني الإنسان نجس، سواء كان من رجل أو امرأة.",
    "Human semen is impure, whether from a man or woman.",
    "الأعيان النجسة", "Impure Substances",
    "beginner", "taharah,najasah,semen"));

questionRepository.save(createQuestion(taharah, sistani,
    "كم مرة يجب غسل الثوب المتنجس بالبول؟",
    "How many times must clothing impurified by urine be washed?",
    "مرة واحدة", "مرتين", "ثلاث مرات", "حتى يزول الأثر",
    "Once", "Twice", "Three times", "Until trace is removed",
    1,
    "يكفي غسل الثوب المتنجس بالبول مرتين بالماء القليل، أو مرة واحدة بالكر.",
    "Washing clothing impurified by urine twice with little water, or once with kur water, is sufficient.",
    "تطهير المتنجسات", "Purifying Impurities",
    "intermediate", "taharah,purification,urine"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجوز التيمم مع وجود الماء الكافي؟",
    "Is tayammum permissible with sufficient water available?",
    "لا يجوز", "يجوز للمريض", "يجوز إذا كان الماء بارداً", "يجوز للضرورة",
    "Not permissible", "Permissible for sick", "Permissible if water is cold", "Permissible in necessity",
    3,
    "لا يجوز التيمم مع وجود الماء إلا لضرورة، كالخوف من الضرر باستعمال الماء.",
    "Tayammum is not permissible with water available except for necessity, like fearing harm from using water.",
    "شروط التيمم", "Tayammum Conditions",
    "intermediate", "taharah,tayammum,water"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم دم الحيض؟",
    "What is the ruling on menstrual blood?",
    "طاهر", "نجس", "نجس إذا كان كثيراً", "طاهر إن كان قليلاً",
    "Pure", "Impure", "Impure if excessive", "Pure if little",
    1,
    "دم الحيض نجس، ويجب تطهير ما يصيبه.",
    "Menstrual blood is impure, and what it touches must be purified.",
    "الأعيان النجسة", "Impure Substances",
    "beginner", "taharah,najasah,blood"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب الموالاة في غسل الجنابة؟",
    "Is continuity required in ghusl janaba?",
    "نعم، يجب", "لا، لا يجب", "يجب في الترتيبي فقط", "يجب في الارتماسي فقط",
    "Yes, required", "No, not required", "Required in sequential only", "Required in immersion only",
    1,
    "لا تجب الموالاة في غسل الجنابة الترتيبي، خلافاً للوضوء.",
    "Continuity is not required in sequential ghusl janaba, unlike wudu.",
    "أحكام الغسل", "Ghusl Rulings",
    "intermediate", "taharah,ghusl,continuity"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم ماء البئر إذا وقعت فيه نجاسة؟",
    "What is the ruling on well water if impurity falls in it?",
    "ينجس كله", "لا ينجس", "ينجس إلا إذا كان كراً", "ينجس السطح فقط",
    "All becomes impure", "Doesn't become impure", "Becomes impure unless kur", "Only surface becomes impure",
    1,
    "ماء البئر لا ينجس بوقوع النجاسة فيه، بل يبقى على طهارته.",
    "Well water doesn't become impure by impurity falling in it, but remains pure.",
    "أحكام المياه", "Water Rulings",
    "advanced", "taharah,water,well"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجوز الوضوء بماء المطر؟",
    "Is wudu with rainwater permissible?",
    "نعم", "لا", "يجوز إذا تجمع", "مكروه",
    "Yes", "No", "Permissible if collected", "Disliked",
    0,
    "يجوز الوضوء بماء المطر، وهو من المياه الطاهرة المطلقة.",
    "Wudu with rainwater is permissible, and it is among the pure absolute waters.",
    "أنواع المياه", "Types of Water",
    "beginner", "taharah,water,rain"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الشك في الحدث بعد اليقين بالطهارة؟",
    "What is the ruling on doubting hadath after certainty of purity?",
    "يجب الوضوء", "لا يجب", "يستحب الوضوء", "يجب على الأحوط",
    "Must do wudu", "Not required", "Wudu recommended", "Required as precaution",
    1,
    "إذا شك في الحدث بعد اليقين بالطهارة، يبني على اليقين ولا يجب عليه الوضوء.",
    "If one doubts hadath after certainty of purity, rely on certainty and wudu is not required.",
    "الشك في الطهارة", "Doubt in Purity",
    "intermediate", "taharah,doubt,certainty"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب النية في الغسل؟",
    "Is intention required in ghusl?",
    "نعم، يجب", "لا، لا يجب", "يجب التلفظ بها", "مستحبة",
    "Yes, required", "No, not required", "Must verbalize it", "Recommended",
    0,
    "تجب النية في الغسل، وهي القصد القلبي لامتثال أمر الله تعالى.",
    "Intention is required in ghusl, which is the mental resolve to obey Allah's command.",
    "شروط الغسل", "Ghusl Conditions",
    "beginner", "taharah,ghusl,intention"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الماء المستعمل في الوضوء؟",
    "What is the ruling on water used in wudu?",
    "نجس", "مكروه", "طاهر", "مشكوك",
    "Impure", "Disliked", "Pure", "Doubtful",
    2,
    "الماء المستعمل في الوضوء أو الغسل طاهر، ويجوز استعماله مرة أخرى.",
    "Water used in wudu or ghusl is pure, and can be used again.",
    "الماء المستعمل", "Used Water",
    "intermediate", "taharah,water,used"));

// =============== MORE SAWM QUESTIONS (20 questions) ===============

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم من أفطر عمداً في رمضان؟",
    "What is the ruling for someone who breaks fast deliberately in Ramadan?",
    "القضاء فقط", "القضاء والكفارة", "الكفارة فقط", "التوبة فقط",
    "Only qadha", "Qadha and kaffarah", "Only kaffarah", "Only repentance",
    1,
    "من أفطر عمداً في نهار رمضان، وجب عليه القضاء والكفارة معاً.",
    "Whoever breaks fast deliberately during Ramadan, qadha and kaffarah are both required.",
    "كفارة الصوم", "Fast Kaffarah",
    "intermediate", "sawm,kaffarah,deliberate"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما هي كفارة الإفطار العمدي في رمضان؟",
    "What is the kaffarah for deliberately breaking fast in Ramadan?",
    "إطعام ستين مسكيناً", "صيام ستين يوماً", "عتق رقبة أو صيام شهرين أو إطعام ستين", "الاستغفار فقط",
    "Feed sixty poor", "Fast sixty days", "Free slave or fast two months or feed sixty", "Only seek forgiveness",
    2,
    "كفارة الإفطار العمدي في رمضان: عتق رقبة، أو صيام شهرين متتابعين، أو إطعام ستين مسكيناً.",
    "Kaffarah for deliberately breaking fast in Ramadan: freeing a slave, or fasting two consecutive months, or feeding sixty poor.",
    "أحكام الكفارة", "Kaffarah Rulings",
    "intermediate", "sawm,kaffarah,types"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز للمريض الإفطار في رمضان؟",
    "Is it permissible for sick person to break fast in Ramadan?",
    "لا يجوز", "يجوز إذا كان الصوم يضره", "يجوز مطلقاً", "يجوز للأمراض المزمنة فقط",
    "Not permissible", "Permissible if fasting harms him", "Absolutely permissible", "Only for chronic diseases",
    1,
    "يجوز للمريض الإفطار إذا كان الصوم يضره أو يشق عليه مشقة لا تتحمل عادة.",
    "A sick person may break fast if fasting harms him or causes unbearable hardship.",
    "أعذار الإفطار", "Excuses for Breaking Fast",
    "beginner", "sawm,illness,exemption"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم القيء في نهار رمضان؟",
    "What is the ruling on vomiting during Ramadan?",
    "يبطل الصوم مطلقاً", "لا يبطل إذا كان قهرياً", "يبطل إذا كان عمدياً", "لا يبطل مطلقاً",
    "Invalidates fast absolutely", "Doesn't invalidate if involuntary", "Invalidates if deliberate", "Doesn't invalidate at all",
    2,
    "التقيؤ عمداً يبطل الصوم، أما القيء القهري فلا يبطله.",
    "Deliberate vomiting invalidates the fast, but involuntary vomiting doesn't.",
    "مبطلات الصوم", "Fast Invalidators",
    "intermediate", "sawm,vomiting,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز الصوم للشيخ الهرم؟",
    "Is fasting permissible for very old person?",
    "واجب", "غير واجب ويفدي", "مستحب", "مكروه",
    "Obligatory", "Not obligatory and pays fidyah", "Recommended", "Disliked",
    1,
    "الشيخ الهرم الذي لا يستطيع الصوم، يسقط عنه الصوم ويجب عليه الفدية عن كل يوم.",
    "A very old person who cannot fast is exempted from fasting and must pay fidyah for each day.",
    "أحكام الشيخ الهرم", "Elderly Person Rulings",
    "intermediate", "sawm,elderly,exemption"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما مقدار الفدية عن كل يوم من رمضان؟",
    "What is the amount of fidyah for each day of Ramadan?",
    "نصف صاع", "صاع واحد", "مُدّ واحد", "750 غراماً",
    "Half sa'", "One sa'", "One mudd", "750 grams",
    3,
    "مقدار الفدية عن كل يوم من رمضان: إطعام مسكين بمقدار 750 غراماً من الطعام.",
    "The amount of fidyah for each day of Ramadan: feeding a poor person about 750 grams of food.",
    "مقدار الفدية", "Fidyah Amount",
    "intermediate", "sawm,fidyah,amount"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب قضاء الصوم الفائت فوراً؟",
    "Must missed fasts be made up immediately?",
    "نعم، فوراً", "لا، قبل رمضان القادم", "لا، في أي وقت", "يستحب فوراً",
    "Yes, immediately", "No, before next Ramadan", "No, any time", "Recommended immediately",
    1,
    "لا يجب قضاء الصوم فوراً، بل يجوز تأخيره إلى ما قبل رمضان القادم.",
    "Making up fasts immediately is not required; it may be delayed until before next Ramadan.",
    "قضاء الصوم", "Making Up Fasts",
    "beginner", "sawm,qadha,timing"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم من نام في رمضان ولم يستيقظ إلا بعد الغروب؟",
    "What is the ruling for someone who slept in Ramadan and didn't wake up until after sunset?",
    "صومه باطل", "صومه صحيح", "يجب القضاء", "يجب الكفارة",
    "Fast invalid", "Fast valid", "Must make up", "Kaffarah required",
    1,
    "من نام في نهار رمضان من الصباح إلى المساء، فصومه صحيح.",
    "Whoever slept during Ramadan from morning to evening, their fast is valid.",
    "أحكام النوم في الصوم", "Sleep in Fasting",
    "beginner", "sawm,sleep,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز استعمال السواك في نهار رمضان؟",
    "Is using siwak permissible during Ramadan?",
    "لا يجوز", "يجوز", "مكروه", "يجوز قبل الزوال فقط",
    "Not permissible", "Permissible", "Disliked", "Only before noon permissible",
    1,
    "يجوز استعمال السواك في نهار رمضان، ولا كراهة فيه.",
    "Using siwak during Ramadan is permissible, and there's no dislike in it.",
    "آداب الصيام", "Fasting Etiquettes",
    "beginner", "sawm,siwak,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الحقنة الشرجية في نهار رمضان؟",
    "What is the ruling on rectal injection during Ramadan?",
    "تبطل الصوم", "لا تبطل", "تبطل على الأحوط", "تبطل إذا كانت مغذية",
    "Invalidates fast", "Doesn't invalidate", "Invalidates as precaution", "Invalidates if nutritious",
    2,
    "الحقنة الشرجية تبطل الصوم على الأحوط وجوباً.",
    "Rectal injection invalidates the fast as an obligatory precaution.",
    "مبطلات الصوم", "Fast Invalidators",
    "intermediate", "sawm,injection,rectal"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب الإمساك على من أصبح مفطراً لعذر ثم زال عذره؟",
    "Must someone who began day with excuse then excuse was removed abstain?",
    "نعم، يجب", "لا، لا يجب", "يستحب", "يجب في بعض الأعذار",
    "Yes, required", "No, not required", "Recommended", "Required for some excuses",
    1,
    "من أصبح مفطراً لعذر ثم زال عذره، لا يجب عليه الإمساك، لكن يستحب.",
    "Whoever began day with excuse then excuse was removed, abstaining is not required but recommended.",
    "الإمساك بقية النهار", "Abstaining Rest of Day",
    "intermediate", "sawm,excuse,abstaining"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم يوم العيد؟",
    "What is the ruling on fasting on Eid day?",
    "واجب", "مستحب", "حرام", "مكروه",
    "Obligatory", "Recommended", "Forbidden", "Disliked",
    2,
    "صوم يوم العيد (الفطر والأضحى) حرام.",
    "Fasting on Eid day (Fitr and Adha) is forbidden.",
    "الصوم المحرم", "Forbidden Fasting",
    "beginner", "sawm,eid,prohibition"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز صوم التطوع قبل قضاء رمضان؟",
    "Is voluntary fasting permissible before making up Ramadan?",
    "لا يجوز", "يجوز", "يجوز بعد شعبان", "مكروه",
    "Not permissible", "Permissible", "Permissible after Sha'ban", "Disliked",
    1,
    "يجوز صوم التطوع قبل قضاء رمضان، ولكن الأولى البدء بالقضاء.",
    "Voluntary fasting before making up Ramadan is permissible, but starting with qadha is better.",
    "صوم التطوع", "Voluntary Fasting",
    "intermediate", "sawm,voluntary,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الارتماس في الماء للصائم؟",
    "What is the ruling on full immersion in water for fasting person?",
    "يبطل الصوم", "لا يبطل", "يبطل على الأحوط", "مكروه",
    "Invalidates fast", "Doesn't invalidate", "Invalidates as precaution", "Disliked",
    2,
    "الارتماس الكامل في الماء يبطل الصوم على الأحوط وجوباً.",
    "Complete immersion in water invalidates the fast as an obligatory precaution.",
    "مبطلات الصوم", "Fast Invalidators",
    "intermediate", "sawm,immersion,water"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب على المسافر القضاء إذا صام في السفر؟",
    "Must traveler make up if he fasted while traveling?",
    "نعم، يجب", "لا، لا يجب", "يجب إذا كان عامداً", "يستحب",
    "Yes, required", "No, not required", "Required if deliberate", "Recommended",
    0,
    "إذا صام المسافر في رمضان، فصومه باطل ويجب عليه القضاء.",
    "If a traveler fasted in Ramadan, his fast is invalid and he must make it up.",
    "صوم المسافر", "Traveler's Fast",
    "intermediate", "sawm,traveler,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الكذب على الله ورسوله في نهار رمضان؟",
    "What is the ruling on lying about Allah and His Messenger during Ramadan?",
    "يبطل الصوم", "لا يبطل لكنه حرام", "يبطل على الأحوط", "مكروه",
    "Invalidates fast", "Doesn't invalidate but haram", "Invalidates as precaution", "Disliked",
    0,
    "الكذب على الله ورسوله من مبطلات الصوم، ويوجب القضاء والكفارة.",
    "Lying about Allah and His Messenger invalidates the fast and requires qadha and kaffarah.",
    "مبطلات الصوم", "Fast Invalidators",
    "advanced", "sawm,lying,invalidator"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز للمرضع الإفطار في رمضان؟",
    "Is it permissible for nursing mother to break fast in Ramadan?",
    "نعم، بدون شرط", "نعم، إذا خافت على نفسها أو الطفل", "لا يجوز", "يجوز بفدية",
    "Yes, without condition", "Yes, if fears for herself or child", "Not permissible", "Permissible with fidyah",
    1,
    "يجوز للمرضع الإفطار إذا خافت الضرر على نفسها أو على الطفل، وعليها القضاء والفدية.",
    "A nursing mother may break fast if she fears harm to herself or the child, and must make up with fidyah.",
    "صوم المرضع", "Nursing Mother's Fast",
    "intermediate", "sawm,nursing,exemption"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الجماع في نهار رمضان؟",
    "What is the ruling on intercourse during Ramadan?",
    "حرام ويبطل الصوم", "حرام ولا يبطل", "مكروه", "يبطل بلا كفارة",
    "Forbidden and invalidates fast", "Forbidden but doesn't invalidate", "Disliked", "Invalidates without kaffarah",
    0,
    "الجماع في نهار رمضان حرام ومن مبطلات الصوم، ويوجب القضاء والكفارة المغلظة.",
    "Intercourse during Ramadan is forbidden and invalidates the fast, requiring qadha and severe kaffarah.",
    "مبطلات الصوم", "Fast Invalidators",
    "advanced", "sawm,intercourse,kaffarah"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز قضاء رمضان في يوم الشك؟",
    "Is making up Ramadan permissible on day of doubt?",
    "نعم", "لا", "يجوز بنية الندب", "يجوز بنية ما في الذمة",
    "Yes", "No", "Permissible with voluntary intention", "Permissible with whatever is due",
    3,
    "يجوز قضاء رمضان في يوم الشك بنية ما في الذمة، أو بنية الندب.",
    "Making up Ramadan on day of doubt is permissible with intention of whatever is due, or voluntary.",
    "يوم الشك", "Day of Doubt",
    "advanced", "sawm,doubt,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم من أخر قضاء رمضان حتى دخل رمضان التالي؟",
    "What is the ruling for someone who delayed Ramadan qadha until next Ramadan?",
    "القضاء فقط", "القضاء والفدية", "الفدية فقط", "الكفارة",
    "Only qadha", "Qadha and fidyah", "Only fidyah", "Kaffarah",
    1,
    "من أخر القضاء عن عذر حتى دخل رمضان التالي، يجب عليه القضاء والفدية عن كل يوم.",
    "Whoever delayed qadha with excuse until next Ramadan, must make up and pay fidyah for each day.",
    "تأخير القضاء", "Delaying Qadha",
    "intermediate", "sawm,qadha,delay"));

// =============== MORE KHUMS QUESTIONS (15 questions) ===============

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في المال الموهوب؟",
    "Is Khums due on gifted money?",
    "نعم، فوراً", "لا، إلا بعد سنة", "نعم، إذا زاد عن المؤونة", "لا يجب مطلقاً",
    "Yes, immediately", "No, except after a year", "Yes, if exceeds expenses", "Not required at all",
    2,
    "المال الموهوب إذا زاد عن المؤونة في نهاية السنة الخمسية، وجب فيه الخمس.",
    "Gifted money if it exceeds expenses at the end of the Khums year, Khums is due on it.",
    "أحكام الهدية", "Gift Rulings",
    "intermediate", "khums,gift,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "متى يبدأ حساب السنة الخمسية؟",
    "When does the Khums year begin calculation?",
    "من أول محرم", "من أول دخل يملكه", "من بلوغ الإنسان", "من الزواج",
    "From first of Muharram", "From first income owned", "From person's maturity", "From marriage",
    1,
    "تبدأ السنة الخمسية من أول دخل يملكه الإنسان، ويكون رأس سنته الخمسية.",
    "The Khums year begins from the first income a person owns, which becomes their Khums year-end.",
    "رأس السنة الخمسية", "Khums Year-End",
    "intermediate", "khums,year,beginning"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في المهر؟",
    "Is Khums due on mahr?",
    "نعم، يجب", "لا يجب", "يجب إذا زاد عن حاجتها", "يجب بعد سنة",
    "Yes, required", "Not required", "Required if exceeds her needs", "Required after a year",
    2,
    "المهر لا يجب فيه الخمس، إلا إذا زاد عن مؤونتها وبقي إلى نهاية السنة.",
    "Mahr doesn't have Khums, unless it exceeds her maintenance and remains until year-end.",
    "الخمس والمهر", "Khums and Mahr",
    "intermediate", "khums,mahr,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس على الديون؟",
    "Is Khums due on debts?",
    "نعم", "لا", "يجب على الدائن", "يجب إذا استلمها",
    "Yes", "No", "Due on creditor", "Due if received",
    1,
    "الدين لا يجب فيه الخمس على المدين، ولا على الدائن حتى يستلمه.",
    "Debt doesn't have Khums on the debtor, nor on the creditor until he receives it.",
    "الخمس والديون", "Khums and Debts",
    "advanced", "khums,debt,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الميراث؟",
    "What is the ruling on Khums in inheritance?",
    "يجب فوراً", "لا يجب", "يجب بعد سنة", "يجب إذا زاد عن المؤونة",
    "Due immediately", "Not due", "Due after a year", "Due if exceeds expenses",
    3,
    "الميراث لا يجب فيه الخمس، إلا إذا زاد عن المؤونة وبقي إلى نهاية السنة.",
    "Inheritance doesn't have Khums, unless it exceeds maintenance and remains until year-end.",
    "الخمس والميراث", "Khums and Inheritance",
    "intermediate", "khums,inheritance,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الكتب الدراسية؟",
    "Is Khums due on school books?",
    "نعم", "لا، إذا كانت للدراسة", "يجب بعد التخرج", "يجب إذا كانت قيمة",
    "Yes", "No, if for study", "Due after graduation", "Due if valuable",
    1,
    "الكتب الدراسية التي يحتاجها الطالب لا يجب فيها الخمس.",
    "School books that a student needs don't have Khums.",
    "الخمس والكتب", "Khums and Books",
    "beginner", "khums,books,study"));

questionRepository.save(createQuestion(khums, sistani,
    "كم مقدار الخمس؟",
    "How much is Khums?",
    "10%", "20%", "25%", "50%",
    "10%", "20%", "25%", "50%",
    1,
    "مقدار الخمس الواجب هو خمس المال، أي 20%.",
    "The required Khums amount is one-fifth of the money, which is 20%.",
    "مقدار الخمس", "Khums Amount",
    "beginner", "khums,amount,percentage"));

questionRepository.save(createQuestion(khums, sistani,
    "لمن يدفع الخمس؟",
    "To whom is Khums paid?",
    "للفقراء فقط", "للعلماء", "لمرجع التقليد أو وكيله", "لأي مسجد",
    "Only to poor", "To scholars", "To marja or his representative", "To any mosque",
    2,
    "الخمس يدفع لمرجع التقليد أو وكيله المأذون، وهو يقسمه على مستحقيه.",
    "Khums is paid to the marja of taqleed or his authorized representative, who distributes it to its deserving recipients.",
    "مصرف الخمس", "Khums Recipients",
    "intermediate", "khums,payment,recipient"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الأثاث المنزلي؟",
    "Is Khums due on home furniture?",
    "نعم، دائماً", "لا، إذا كان من مؤونة السنة", "يجب بعد خمس سنوات", "يجب إذا كان فاخراً",
    "Yes, always", "No, if from year's expenses", "Due after five years", "Due if luxurious",
    1,
    "الأثاث المنزلي الذي اشتراه من أرباح سنته لا يجب فيه الخمس.",
    "Home furniture bought from the year's profit doesn't have Khums.",
    "الخمس والأثاث", "Khums and Furniture",
    "beginner", "khums,furniture,home"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الجهاز (جهاز العروس)؟",
    "Is Khums due on dowry (bride's trousseau)?",
    "نعم، يجب", "لا يجب", "يجب بعد سنة", "يجب على الزوج",
    "Yes, required", "Not required", "Required after a year", "Required on husband",
    1,
    "جهاز العروس لا يجب فيه الخمس على الزوجة.",
    "The bride's trousseau doesn't have Khums on the wife.",
    "الخمس وجهاز العروس", "Khums and Trousseau",
    "intermediate", "khums,trousseau,bride"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس على الطفل؟",
    "Is Khums obligatory on a child?",
    "نعم", "لا، حتى البلوغ", "نعم، على وليه", "يجب بعد البلوغ على الماضي",
    "Yes", "No, until maturity", "Yes, on guardian", "Due after maturity on past",
    1,
    "لا يجب الخمس على الطفل حتى يبلغ.",
    "Khums is not obligatory on a child until maturity.",
    "شروط وجوب الخمس", "Khums Obligation Conditions",
    "beginner", "khums,child,obligation"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في المال الحرام؟",
    "What is the ruling on Khums in haram money?",
    "لا خمس فيه", "يجب فيه الخمس", "يجب رده لصاحبه", "يجب التصدق به",
    "No Khums on it", "Khums required on it", "Must return to owner", "Must give as charity",
    2,
    "المال الحرام يجب رده إلى صاحبه إن عُرف، وإلا فالتصدق به، ولا يجزئ إخراج الخمس منه.",
    "Haram money must be returned to its owner if known, otherwise given as charity, and paying Khums from it doesn't suffice.",
    "المال الحرام", "Haram Money",
    "advanced", "khums,haram,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجوز تأخير دفع الخمس؟",
    "Is delaying payment of Khums permissible?",
    "لا يجوز", "يجوز بإذن المرجع", "يجوز لمدة سنة", "يجوز للضرورة",
    "Not permissible", "Permissible with marja permission", "Permissible for one year", "Permissible in necessity",
    1,
    "لا يجوز تأخير دفع الخمس إلا بإذن من الحاكم الشرعي أو وكيله.",
    "Delaying Khums payment is not permissible except with permission from religious authority or his representative.",
    "تأخير الخمس", "Delaying Khums",
    "intermediate", "khums,delay,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الأموال المقترضة؟",
    "Is Khums due on borrowed money?",
    "نعم", "لا", "يجب عند السداد", "يجب إذا استثمرت",
    "Yes", "No", "Due upon repayment", "Due if invested",
    1,
    "الأموال المقترضة لا يجب فيها الخمس، لأنها ليست من أرباحه.",
    "Borrowed money doesn't have Khums, as it's not from one's profit.",
    "الخمس والقروض", "Khums and Loans",
    "intermediate", "khums,loan,borrowed"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يسقط الخمس بالجهل؟",
    "Does Khums drop due to ignorance?",
    "نعم، يسقط", "لا، لا يسقط", "يسقط للجاهل القاصر", "يسقط إذا كان معذوراً",
    "Yes, drops", "No, doesn't drop", "Drops for unavoidably ignorant", "Drops if excused",
    2,
    "لا يسقط الخمس بالجهل، بل يجب إخراجه ولو بعد سنين.",
    "Khums doesn't drop due to ignorance; it must be paid even after years.",
    "الجهل بالخمس", "Ignorance of Khums",
    "advanced", "khums,ignorance,ruling"));

    // First, add these new categories in seedCategories():


// Then retrieve them in seedAllQuestions():


// =============== MORE NIKAH QUESTIONS (50 questions: 131-180) ===============

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز الجمع بين الأختين في الزواج؟",
    "Is marrying two sisters simultaneously permissible?",
    "نعم، يجوز", "لا، حرام", "يجوز في المتعة", "يجوز بإذنهما",
    "Yes, permissible", "No, forbidden", "Permissible in muta", "Permissible with their permission",
    1,
    "لا يجوز الجمع بين الأختين في النكاح، لا دائماً ولا منقطعاً.",
    "Combining two sisters in marriage, neither permanent nor temporary, is not permissible.",
    "منهاج الصالحين، مسألة 201", "Minhaj al-Salihin, Issue 201",
    "beginner", "nikah,sisters,forbidden"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز الزواج من عمة الزوجة أو خالتها بدون إذنها؟",
    "Is marrying wife's aunt without her permission permissible?",
    "نعم، يجوز", "لا، يحتاج إذنها", "يجوز بعد طلاقها", "يجوز في المتعة",
    "Yes, permissible", "No, needs her permission", "Permissible after divorcing her", "Permissible in muta",
    1,
    "لا يجوز أن يتزوج عمة زوجته أو خالتها إلا بإذنها.",
    "Marrying wife's paternal or maternal aunt is not permissible except with her permission.",
    "منهاج الصالحين، مسألة 201", "Minhaj al-Salihin, Issue 201",
    "intermediate", "nikah,aunt,permission"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا تزوج عمة زوجته بدون إذن الزوجة، ما الحكم؟",
    "If he marries wife's aunt without wife's permission, what's the ruling?",
    "الزواجان باطلان", "زواج العمة باطل", "كلاهما صحيح", "له خيار الإبقاء على إحداهما",
    "Both marriages invalid", "Aunt marriage invalid", "Both valid", "He has option to keep one",
    1,
    "إذا تزوج العمة أو الخالة بدون إذن الزوجة بطل عقد العمة أو الخالة دون الأولى.",
    "If he marries aunt without wife's permission, the aunt's marriage is invalid but the first remains valid.",
    "منهاج الصالحين، مسألة 201", "Minhaj al-Salihin, Issue 201",
    "advanced", "nikah,aunt,invalidity"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز الزواج من بنت الأخ أو بنت الأخت بدون إذن زوجته؟",
    "Is marrying brother's or sister's daughter permissible without wife's permission?",
    "لا يجوز", "يجوز", "يجوز بإذنها", "يجوز في المتعة",
    "Not permissible", "Permissible", "Permissible with her permission", "Permissible in muta",
    1,
    "يجوز أن يتزوج بنت أخي زوجته أو بنت أختها بدون إذنها.",
    "Marrying wife's brother's daughter or sister's daughter without her permission is permissible.",
    "منهاج الصالحين، مسألة 201", "Minhaj al-Salihin, Issue 201",
    "intermediate", "nikah,niece,permission"));

questionRepository.save(createQuestion(nikah, sistani,
    "ما حكم العقد على امرأة في عدة الغير؟",
    "What's the ruling on contracting marriage with woman in another's waiting period?",
    "صحيح", "باطل", "صحيح إن جهل", "صحيح بعد العدة",
    "Valid", "Invalid", "Valid if ignorant", "Valid after waiting period",
    1,
    "لا يجوز التزويج بالمرأة المعتدة من غيره، فلو عقد عليها في العدة بطل العقد.",
    "Marrying a woman in another's waiting period is not permissible; if contracted during it, the marriage is invalid.",
    "منهاج الصالحين، مسألة 204", "Minhaj al-Salihin, Issue 204",
    "advanced", "nikah,iddah,prohibition"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا عقد على امرأة في عدتها وهو يعلم، ثم دخل بها، ما الحكم؟",
    "If he contracts with woman in her waiting period knowingly and consummates, what's ruling?",
    "تحرم عليه مؤقتاً", "تحرم عليه مؤبداً", "تحل له بعد العدة", "يجب عقد جديد",
    "Forbidden temporarily", "Forbidden perpetually", "Permissible after waiting period", "New contract required",
    1,
    "إذا عقد على المعتدة عالماً عامداً ودخل بها حرمت عليه مؤبداً.",
    "If he contracts with woman in waiting period knowingly intentionally and consummates, she's forbidden to him perpetually.",
    "منهاج الصالحين، مسألة 204", "Minhaj al-Salihin, Issue 204",
    "advanced", "nikah,iddah,perpetual"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز العقد على المطلقة رجعياً قبل انقضاء عدتها؟",
    "Is contracting with revocably divorced woman before her waiting period ends permissible?",
    "نعم، من نفس الزوج", "لا، من أي أحد", "يجوز من غيره", "يجوز بإذنها",
    "Yes, from same husband", "No, from anyone", "Permissible from another", "Permissible with her permission",
    1,
    "لا يجوز لغير الزوج العقد على المطلقة الرجعية ما دامت في العدة.",
    "No one except the husband may contract with revocably divorced woman while she's in waiting period.",
    "منهاج الصالحين، مسألة 204", "Minhaj al-Salihin, Issue 204",
    "intermediate", "nikah,divorce,iddah"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز الزواج بزوجة الأب؟",
    "Is marrying father's wife permissible?",
    "نعم", "لا، مطلقاً", "نعم، بعد وفاته", "نعم، بعد طلاقها",
    "Yes", "No, absolutely", "Yes, after his death", "Yes, after her divorce",
    1,
    "تحرم زوجة الأب وإن لم يدخل بها.",
    "Father's wife is forbidden even if not consummated.",
    "منهاج الصالحين، مسألة 205", "Minhaj al-Salihin, Issue 205",
    "beginner", "nikah,father,prohibition"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا زنى بامرأة، هل تحرم عليه أمها أو بنتها؟",
    "If he commits adultery with woman, do her mother or daughter become forbidden?",
    "نعم، تحرمان", "لا، لا تحرمان", "تحرم الأم فقط", "تحرم البنت فقط",
    "Yes, both forbidden", "No, neither forbidden", "Only mother forbidden", "Only daughter forbidden",
    1,
    "الزنا لا يوجب الحرمة، فلا تحرم عليه أم المزني بها ولا بنتها.",
    "Adultery doesn't establish prohibition; neither mother nor daughter of adulteress becomes forbidden.",
    "منهاج الصالحين، مسألة 208", "Minhaj al-Salihin, Issue 208",
    "advanced", "nikah,zina,prohibition"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا عقد على امرأة ثم زنى بأمها، ما الحكم؟",
    "If he contracts with woman then commits adultery with her mother, what's ruling?",
    "تحرم الزوجة", "لا تحرم الزوجة", "يبطل العقد", "تحرم مؤقتاً",
    "Wife becomes forbidden", "Wife doesn't become forbidden", "Contract invalid", "Temporarily forbidden",
    1,
    "لو عقد على امرأة ثم زنى بأمها لم تحرم عليه زوجته.",
    "If he contracts with woman then commits adultery with her mother, his wife doesn't become forbidden.",
    "منهاج الصالحين، مسألة 208", "Minhaj al-Salihin, Issue 208",
    "advanced", "nikah,zina,wife"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز النظر إلى المخطوبة قبل العقد؟",
    "Is looking at fiancée before contract permissible?",
    "نعم، مع اللذة", "نعم، بدون لذة", "نعم، بقصد الزواج", "لا يجوز",
    "Yes, with pleasure", "Yes, without pleasure", "Yes, with marriage intent", "Not permissible",
    2,
    "يجوز النظر إلى المرأة التي يريد خطبتها بقصد الزواج، بدون تلذذ وريبة.",
    "Looking at woman he intends to propose to for marriage is permissible, without pleasure or suspicion.",
    "sistani.org/qa/النظر للمخطوبة", "sistani.org/qa/looking at fiancee",
    "intermediate", "nikah,looking,fiancee"));

questionRepository.save(createQuestion(nikah, sistani,
    "ما حكم الخلوة بالأجنبية؟",
    "What's the ruling on seclusion with non-mahram woman?",
    "حرام", "جائز", "مكروه", "جائز بلا خوف فتنة",
    "Forbidden", "Permissible", "Disliked", "Permissible without fear of temptation",
    2,
    "الخلوة بالأجنبية مكروهة، وإذا خيف الوقوع في الحرام حرمت.",
    "Seclusion with non-mahram woman is disliked, and if falling into forbidden is feared, it's forbidden.",
    "sistani.org/464", "sistani.org/464",
    "intermediate", "nikah,seclusion,woman"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجب على الزوج القسم بين زوجاته؟",
    "Must husband divide nights equally among wives?",
    "نعم، واجب", "لا، مستحب", "واجب مع المطالبة", "واجب للدائمات فقط",
    "Yes, obligatory", "No, recommended", "Obligatory with request", "Only for permanent wives",
    2,
    "يجب على الزوج القسم بين زوجاته الدائمات، فيبيت عند كل واحدة ليلة.",
    "Husband must divide nights among permanent wives, spending night with each one.",
    "منهاج الصالحين، الحقوق الزوجية", "Minhaj al-Salihin, Marital Rights",
    "intermediate", "nikah,equality,nights"));

questionRepository.save(createQuestion(nikah, sistani,
    "ما حكم إسقاط الزوجة لحقها في القسم؟",
    "What's ruling on wife waiving her right to equal nights?",
    "لا يصح", "يصح", "يصح مؤقتاً", "يصح بعوض",
    "Not valid", "Valid", "Valid temporarily", "Valid with compensation",
    1,
    "يجوز للزوجة أن تسقط حقها من القسم، أو تهبه لضرتها.",
    "Wife may waive her right to nights, or gift it to co-wife.",
    "منهاج الصالحين، القسم", "Minhaj al-Salihin, Division",
    "advanced", "nikah,waiver,rights"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجب الإنفاق على الزوجة المتمتع بها؟",
    "Is maintenance obligatory for temporary wife?",
    "نعم", "لا، إلا بالشرط", "نعم، للحامل فقط", "نعم، بعد سنة",
    "Yes", "No, except if stipulated", "Yes, only if pregnant", "Yes, after one year",
    1,
    "لا تجب النفقة على الزوجة المتمتع بها إلا إذا اشترطت ذلك في العقد.",
    "Maintenance for temporary wife is not obligatory unless she stipulates it in contract.",
    "sistani.org/6395", "sistani.org/6395",
    "intermediate", "nikah,muta,maintenance"));

questionRepository.save(createQuestion(nikah, sistani,
    "كم مقدار مهر المثل؟",
    "What is the amount of comparable mahr?",
    "حسب العرف", "دينار واحد", "ما تراضيا عليه", "مهر الأمثال",
    "According to custom", "One dinar", "What they agreed", "Mahr of comparable women",
    3,
    "مهر المثل هو مهر مثيلاتها من النساء من طبقتها وعشيرتها.",
    "Comparable mahr is mahr of her comparable women from her class and tribe.",
    "منهاج الصالحين، المهر", "Minhaj al-Salihin, Mahr",
    "advanced", "nikah,mahr,comparable"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا طلقها قبل الدخول، كم يدفع من المهر؟",
    "If he divorces before consummation, how much mahr does he pay?",
    "الكل", "النصف", "لا شيء", "حسب المدة",
    "All", "Half", "Nothing", "According to duration",
    1,
    "إذا طلقها قبل الدخول استحقت نصف المهر المسمى.",
    "If he divorces her before consummation, she deserves half the specified mahr.",
    "منهاج الصالحين، مسألة 338", "Minhaj al-Salihin, Issue 338",
    "intermediate", "nikah,divorce,mahr"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا مات قبل الدخول، كم تأخذ من المهر؟",
    "If he dies before consummation, how much mahr does she take?",
    "النصف", "الكل", "لا شيء", "الربع",
    "Half", "All", "Nothing", "Quarter",
    1,
    "إذا مات الزوج قبل الدخول استحقت الزوجة جميع المهر.",
    "If husband dies before consummation, wife deserves all the mahr.",
    "منهاج الصالحين، مسألة 338", "Minhaj al-Salihin, Issue 338",
    "intermediate", "nikah,death,mahr"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز تأجيل المهر كله؟",
    "Is deferring all mahr permissible?",
    "لا يجوز", "يجوز", "يجوز نصفه", "يجوز بإذن القاضي",
    "Not permissible", "Permissible", "Half permissible", "Permissible with judge permission",
    1,
    "يجوز تأجيل المهر كله أو بعضه بالتراضي.",
    "Deferring all or part of mahr by mutual agreement is permissible.",
    "منهاج الصالحين، المهر", "Minhaj al-Salihin, Mahr",
    "beginner", "nikah,mahr,deferment"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا لم يذكر المهر في العقد، ما الحكم؟",
    "If mahr not mentioned in contract, what's the ruling?",
    "العقد باطل", "العقد صحيح ولها مهر المثل", "العقد صحيح بلا مهر", "يجب تجديد العقد",
    "Contract invalid", "Contract valid, she gets comparable mahr", "Contract valid without mahr", "Must renew contract",
    1,
    "إذا لم يذكر المهر في العقد صح العقد ووجب مهر المثل.",
    "If mahr not mentioned in contract, contract is valid and comparable mahr is due.",
    "منهاج الصالحين، مسألة 335", "Minhaj al-Salihin, Issue 335",
    "intermediate", "nikah,mahr,omission"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز جعل تعليم القرآن مهراً؟",
    "Is making Quran teaching the mahr permissible?",
    "نعم", "لا", "يجوز مع ذكر قيمته", "مكروه",
    "Yes", "No", "Permissible with stating value", "Disliked",
    0,
    "يجوز أن يكون المهر تعليم سورة من القرآن أو عمل من الأعمال.",
    "Mahr may be teaching a Quran chapter or a work from works.",
    "منهاج الصالحين، المهر", "Minhaj al-Salihin, Mahr",
    "intermediate", "nikah,mahr,quran"));

questionRepository.save(createQuestion(nikah, sistani,
    "إذا اختلفا في مقدار المهر، من يُصدَّق؟",
    "If they differ about mahr amount, who is believed?",
    "الزوج", "الزوجة", "لا أحد، يحتاج بينة", "يُقسم بينهما",
    "Husband", "Wife", "No one, needs evidence", "Divided between them",
    2,
    "إذا اختلفا في مقدار المهر، فالقول قول من ينكر الزيادة مع يمينه.",
    "If they differ about mahr amount, the word is of whoever denies the increase with oath.",
    "منهاج الصالحين، المهر", "Minhaj al-Salihin, Mahr",
    "advanced", "nikah,mahr,dispute"));

questionRepository.save(createQuestion(nikah, sistani,
    "متى يستقر المهر كاملاً؟",
    "When does full mahr become settled?",
    "بالعقد", "بالدخول", "بالحمل", "بمرور سنة",
    "At contract", "At consummation", "At pregnancy", "After one year",
    1,
    "يستقر المهر كاملاً بالدخول.",
    "Full mahr becomes settled upon consummation.",
    "منهاج الصالحين، المهر", "Minhaj al-Salihin, Mahr",
    "intermediate", "nikah,mahr,settlement"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجب على الزوج إسكان زوجته في بيت مستقل؟",
    "Must husband house wife in independent home?",
    "نعم، دائماً", "لا، إلا باشتراط", "يكفي غرفة مستقلة", "حسب العرف",
    "Yes, always", "No, except if stipulated", "Independent room suffices", "According to custom",
    1,
    "لا يجب على الزوج إسكان زوجته في بيت مستقل إلا إذا اشترطت ذلك.",
    "Husband need not house wife in independent home unless she stipulates it.",
    "منهاج الصالحين، الحقوق الزوجية", "Minhaj al-Salihin, Marital Rights",
    "intermediate", "nikah,housing,independent"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز للزوجة الخروج من المنزل بدون إذن الزوج؟",
    "May wife leave home without husband's permission?",
    "نعم", "لا، مطلقاً", "نعم، للضرورة", "لا، إلا لحقها الواجب",
    "Yes", "No, absolutely", "Yes, for necessity", "No, except for obligatory right",
    3,
    "لا يجوز للزوجة الخروج من البيت إلا بإذن زوجها، إلا في حقوقها الواجبة.",
    "Wife may not leave home except with husband's permission, except for her obligatory rights.",
    "منهاج الصالحين، الحقوق الزوجية", "Minhaj al-Salihin, Marital Rights",
    "intermediate", "nikah,leaving,permission"));

questionRepository.save(createQuestion(nikah, sistani,
    "ما حكم نشوز الزوجة؟",
    "What's the ruling on wife's disobedience (nushuz)?",
    "تسقط نفقتها", "تُطلَّق", "تُعزَّر", "لا شيء",
    "Her maintenance drops", "She's divorced", "She's disciplined", "Nothing",
    0,
    "إذا نشزت الزوجة سقطت نفقتها.",
    "If wife disobeys (nushuz), her maintenance drops.",
    "منهاج الصالحين، مسألة 361", "Minhaj al-Salihin, Issue 361",
    "intermediate", "nikah,nushuz,maintenance"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز للزوج ضرب زوجته؟",
    "May husband beat his wife?",
    "نعم، للتأديب", "لا، مطلقاً", "نعم، بإذن القاضي", "نعم، ضرباً خفيفاً",
    "Yes, for discipline", "No, absolutely", "Yes, with judge permission", "Yes, light beating",
    1,
    "لا يجوز للزوج ضرب زوجته.",
    "Husband may not beat his wife.",
    "sistani.org/584", "sistani.org/584",
    "beginner", "nikah,beating,prohibition"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل تجب طاعة الزوج في المعصية؟",
    "Is obeying husband in sin obligatory?",
    "نعم", "لا", "أحياناً", "حسب نوع المعصية",
    "Yes", "No", "Sometimes", "Depends on sin type",
    1,
    "لا طاعة لمخلوق في معصية الخالق.",
    "No obedience to creature in disobedience of Creator.",
    "القواعد الفقهية", "Fiqh Principles",
    "beginner", "nikah,obedience,sin"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز للزوج منع زوجته من زيارة والديها؟",
    "May husband prevent wife from visiting her parents?",
    "نعم", "لا، إلا لمصلحة", "لا مطلقاً", "نعم، إن تضرر",
    "Yes", "No, except for interest", "No absolutely", "Yes, if harmed",
    3,
    "إذا كان في زيارتها لوالديها إضرار بحقوق الزوج جاز له منعها.",
    "If her visiting parents harms husband's rights, he may prevent her.",
    "منهاج الصالحين، الحقوق الزوجية", "Minhaj al-Salihin, Marital Rights",
    "advanced", "nikah,visiting,parents"));

questionRepository.save(createQuestion(nikah, sistani,
    "ما حكم سفر الزوجة بدون إذن الزوج؟",
    "What's ruling on wife traveling without husband's permission?",
    "جائز", "حرام", "حرام إن تعارض مع حقه", "مكروه",
    "Permissible", "Forbidden", "Forbidden if conflicts with his right", "Disliked",
    2,
    "لا يجوز للزوجة السفر بدون إذن زوجها إذا كان منافياً لحقه.",
    "Wife may not travel without husband's permission if it contradicts his right.",
    "منهاج الصالحين، الحقوق الزوجية", "Minhaj al-Salihin, Marital Rights",
    "intermediate", "nikah,travel,permission"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل يجوز للزوج إجبار زوجته على العمل؟",
    "May husband force wife to work?",
    "نعم", "لا", "نعم، إن احتاج", "نعم، بعوض",
    "Yes", "No", "Yes, if needed", "Yes, with compensation",
    1,
    "لا يجوز للزوج إجبار زوجته على العمل خارج البيت.",
    "Husband may not force wife to work outside home.",
    "منهاج الصالحين، الحقوق الزوجية", "Minhaj al-Salihin, Marital Rights",
    "intermediate", "nikah,work,force"));

questionRepository.save(createQuestion(nikah, sistani,
    "هل تجب على الزوجة خدمة المنزل؟",
    "Is housework obligatory on wife?",
    "نعم، واجبة", "لا، إلا بالشرط", "واجبة حسب العرف", "مستحبة",
    "Yes, obligatory", "No, except if stipulated", "Obligatory per custom", "Recommended",
    1,
    "لا يجب على الزوجة خدمة البيت، إلا إذا اشترط ذلك في العقد أو كان من شأنها.",
    "Housework is not obligatory on wife, unless stipulated in contract or customary for her.",
    "منهاج الصالحين، مسألة 362", "Minhaj al-Salihin, Issue 362",
    "intermediate", "nikah,housework,obligation"));

questionRepository.save(createQuestion(taharah, sistani,
            "هل يجب الغسل بعد الجماع الذي لم يحصل فيه إنزال؟",
            "Is ghusl required after intercourse without ejaculation?",
            "لا يجب", "نعم، يجب مطلقاً", "يجب على الرجل فقط", "يجب على المرأة فقط",
            "Not required", "Yes, absolutely required", "Required for man only", "Required for woman only",
            1,
            "يجب الغسل من الجنابة بمجرد دخول الحشفة في القُبُل أو الدُبُر، ولو لم يحصل إنزال.",
            "Ghusl from janaba is required upon penetration in the front or back passage, even without ejaculation.",
            "أحكام الجنابة", "Janaba Rulings",
            "intermediate", "taharah,ghusl,intercourse"));

// =============== ADDITIONAL 100 QUESTIONS FROM SISTANI.ORG ===============

// MORE SALAT QUESTIONS (15)
questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الصلاة خلف إمام لا يعتقد بعدالته؟",
    "Is praying behind an imam whose justice you don't believe in permissible?",
    "نعم، يجوز", "لا، لا يجوز", "يجوز إن كان مستوراً", "يجوز للضرورة",
    "Yes, permissible", "No, not permissible", "Permissible if covered", "Permissible in necessity",
    2,
    "يجوز الصلاة خلف من لا يعلم فسقه، حتى وإن لم تثبت عدالته، إذا كان مستور الحال.",
    "Praying behind someone whose corruption is not known is permissible, even if justice not proven, if his state is covered.",
    "sistani.org/1175", "sistani.org/1175",
    "intermediate", "salat,imam,justice"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الصلاة في المسجد الذي فيه قبر؟",
    "What's the ruling on praying in mosque with grave?",
    "باطلة", "صحيحة إن لم يسجد على القبر", "مكروهة", "صحيحة مطلقاً",
    "Invalid", "Valid if not prostrating on grave", "Disliked", "Absolutely valid",
    1,
    "تصح الصلاة في المسجد الذي فيه قبر، ما لم يكن القبر أمام المصلي بحيث يكون ساجداً عليه.",
    "Prayer in mosque with grave is valid, unless grave is in front of worshipper such that he prostrates on it.",
    "sistani.org/5252", "sistani.org/5252",
    "intermediate", "salat,mosque,grave"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب القيام عند قراءة آيات السجدة الواجبة في الصلاة؟",
    "Must one stand when reciting verses of obligatory sajdah in prayer?",
    "نعم", "لا، يجوز الجلوس", "لا يجوز قراءتها", "يجب ثم يسجد",
    "Yes", "No, sitting permissible", "Reciting them not permissible", "Must then prostrate",
    2,
    "لا يجوز قراءة آيات السجدة الواجبة في الفريضة، وأما في النافلة فيقرأها ويسجد.",
    "Reciting verses of obligatory sajdah in obligatory prayer is not permissible, but in voluntary prayer recite and prostrate.",
    "sistani.org/5228", "sistani.org/5228",
    "advanced", "salat,sajdah,verses"));

questionRepository.save(createQuestion(salat, sistani,
    "إذا نسي المأموم قراءة الفاتحة في الأوليين، ما حكم صلاته؟",
    "If follower forgot Al-Fatiha in first two rakats, what's prayer ruling?",
    "باطلة", "صحيحة", "يعيد الركعة", "يسجد سجدتي السهو",
    "Invalid", "Valid", "Repeat rakat", "Do sajda al-sahw",
    1,
    "إذا نسي المأموم القراءة في الأوليين فصلاته صحيحة، لأن قراءة الإمام تكفيه.",
    "If follower forgot recitation in first two, his prayer is valid, as imam's recitation suffices.",
    "sistani.org/5095", "sistani.org/5095",
    "intermediate", "salat,congregation,fatiha"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز المشي في الصلاة لإغلاق الباب أو إطفاء النار؟",
    "Is walking in prayer to close door or extinguish fire permissible?",
    "لا يجوز", "يجوز للضرورة", "يجوز مطلقاً", "يبطل الصلاة",
    "Not permissible", "Permissible for necessity", "Absolutely permissible", "Invalidates prayer",
    1,
    "يجوز المشي في الصلاة للضرورة كإطفاء نار أو إغلاق باب، بشرط أن لا يستدبر القبلة.",
    "Walking in prayer for necessity like extinguishing fire or closing door is permissible, provided not turning from qibla.",
    "sistani.org/5243", "sistani.org/5243",
    "intermediate", "salat,walking,necessity"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الإشارة باليد في الصلاة؟",
    "What's ruling on gesturing with hand in prayer?",
    "تبطل الصلاة", "لا تبطل", "تبطل إن كانت كثيرة", "مكروهة",
    "Invalidates prayer", "Doesn't invalidate", "Invalidates if excessive", "Disliked",
    1,
    "الإشارة باليد في الصلاة لا تبطلها، إلا إذا كانت كثيرة موجبة لمحو صورة الصلاة.",
    "Gesturing with hand in prayer doesn't invalidate it, unless excessive causing erasure of prayer form.",
    "sistani.org/5249", "sistani.org/5249",
    "intermediate", "salat,gesture,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الصلاة على الفراش؟",
    "Is praying on bed permissible?",
    "لا يجوز", "يجوز إن كان مستقراً", "يجوز للمريض فقط", "مكروه",
    "Not permissible", "Permissible if stable", "Only for sick", "Disliked",
    1,
    "يجوز الصلاة على الفراش إذا كان مستقراً بحيث يتمكن من الصلاة عليه.",
    "Praying on bed is permissible if stable such that one can pray on it.",
    "sistani.org/5299", "sistani.org/5299",
    "beginner", "salat,bed,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من نسي السجود الواحد حتى ركع في الركعة التالية؟",
    "What's ruling for who forgot one sajdah until rukoo in next rakat?",
    "يعيد الصلاة", "يقضي السجدة بعد الصلاة", "يرجع ويسجد", "صلاته باطلة",
    "Repeat prayer", "Make up sajdah after prayer", "Return and prostrate", "Prayer invalid",
    1,
    "من نسي سجدة واحدة وتجاوز محلها يقضيها بعد الصلاة، ويسجد سجدتي السهو.",
    "Whoever forgot one sajdah and passed its place makes it up after prayer, and does sajda al-sahw.",
    "sistani.org/5199", "sistani.org/5199",
    "intermediate", "salat,sajdah,forgotten"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب رفع اليدين عند التكبير؟",
    "Is raising hands at takbir required?",
    "نعم، واجب", "لا، مستحب", "واجب في تكبيرة الإحرام فقط", "مكروه",
    "Yes, obligatory", "No, recommended", "Only in opening takbir obligatory", "Disliked",
    1,
    "رفع اليدين عند التكبير مستحب وليس بواجب.",
    "Raising hands at takbir is recommended not obligatory.",
    "sistani.org/5177", "sistani.org/5177",
    "beginner", "salat,hands,takbir"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الصلاة في الثوب المتنجس بالدم القليل؟",
    "What's ruling on praying in clothing impurified by little blood?",
    "باطلة", "صحيحة إن كان أقل من درهم", "صحيحة مطلقاً", "مكروهة",
    "Invalid", "Valid if less than dirham", "Absolutely valid", "Disliked",
    1,
    "إذا كان الدم أقل من درهم وكان من دم الجروح أو القروح صحت الصلاة فيه.",
    "If blood is less than dirham and from wounds or sores, prayer in it is valid.",
    "sistani.org/5310", "sistani.org/5310",
    "intermediate", "salat,blood,clothing"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب التسبيح بالعربية في الصلاة؟",
    "Is tasbih in Arabic required in prayer?",
    "نعم، يجب", "لا، يجوز بأي لغة", "يجب للقادر", "مستحب",
    "Yes, required", "No, any language permissible", "Required for able", "Recommended",
    2,
    "يجب التسبيح بالعربية لمن قدر عليها، ويجوز بغيرها لمن لم يقدر.",
    "Tasbih in Arabic is required for who can, and in other language permissible for who cannot.",
    "sistani.org/5168", "sistani.org/5168",
    "intermediate", "salat,tasbih,arabic"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم قراءة الفاتحة من المصحف في الصلاة الواجبة؟",
    "What's ruling on reading Al-Fatiha from mushaf in obligatory prayer?",
    "لا يجوز", "يجوز", "يجوز للناسي", "مكروه",
    "Not permissible", "Permissible", "Permissible for forgetful", "Disliked",
    1,
    "يجوز قراءة الفاتحة والسورة من المصحف في الصلاة الواجبة.",
    "Reading Al-Fatiha and surah from mushaf in obligatory prayer is permissible.",
    "sistani.org/5233", "sistani.org/5233",
    "beginner", "salat,fatiha,mushaf"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب الجهر بالبسملة في الصلاة الجهرية؟",
    "Is audible basmala required in loud prayer?",
    "نعم، يجب", "لا، يجوز الإخفات", "مستحب", "حسب المذهب",
    "Yes, required", "No, quiet permissible", "Recommended", "According to school",
    0,
    "يجب الجهر بالبسملة في الصلاة الجهرية.",
    "Audible basmala is required in loud prayer.",
    "sistani.org/5146", "sistani.org/5146",
    "beginner", "salat,basmala,loud"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من شك في عدد السجدات في الركعة الواحدة؟",
    "What's ruling for who doubts number of sajdahs in one rakat?",
    "يعيد الصلاة", "يبني على الأقل", "يبني على الأكثر", "يستمر ولا يلتفت",
    "Repeat prayer", "Assume less", "Assume more", "Continue and ignore",
    0,
    "من شك في عدد السجدات في الركعة الواحدة بطلت صلاته.",
    "Whoever doubts number of sajdahs in one rakat, his prayer is invalid.",
    "sistani.org/5217", "sistani.org/5217",
    "intermediate", "salat,doubt,sajdah"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الجمع بين الظهر والعصر في غير السفر؟",
    "Is combining Dhuhr and Asr permissible in non-travel?",
    "لا يجوز", "يجوز", "يجوز للضرورة", "يجوز في عرفة ومزدلفة",
    "Not permissible", "Permissible", "Permissible in necessity", "Permissible in Arafat and Muzdalifah",
    1,
    "يجوز الجمع بين الظهرين وبين العشاءين حتى في الحضر، والأفضل الفصل بينهما.",
    "Combining Dhuhr-Asr and Maghrib-Isha is permissible even in residence, though separating is better.",
    "sistani.org/5050", "sistani.org/5050",
    "intermediate", "salat,combining,prayers"));

// MORE TAHARAH QUESTIONS (15)
questionRepository.save(createQuestion(taharah, sistani,
    "هل يطهر الثوب بوضعه في الغسالة الأوتوماتيكية؟",
    "Does clothing purify by placing in automatic washing machine?",
    "نعم، بشرط التعدد", "نعم، بغسلة واحدة", "لا، لا يطهر", "يحتاج عصراً",
    "Yes, with multiple washes", "Yes, with one wash", "No, doesn't purify", "Needs wringing",
    0,
    "يطهر الثوب في الغسالة إذا غُسل بالماء الكثير مرة واحدة، أو بالماء القليل مرتين مع العصر.",
    "Clothing purifies in washer if washed with abundant water once, or with little water twice with wringing.",
    "sistani.org/113", "sistani.org/113",
    "intermediate", "taharah,purification,washer"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم استعمال الصابون النجس؟",
    "What's ruling on using impure soap?",
    "لا يجوز", "يجوز وتطهر اليد بالماء", "يطهر بالاستحالة", "مكروه",
    "Not permissible", "Permissible, hand purified with water", "Purifies by transformation", "Disliked",
    1,
    "يجوز استعمال الصابون المتنجس، وتطهر اليد بغسلها بالماء بعده.",
    "Using impurified soap is permissible, and hand purifies by washing with water after.",
    "sistani.org/127", "sistani.org/127",
    "intermediate", "taharah,soap,najasah"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل ينجس الماء الجاري بملاقاة النجاسة؟",
    "Does running water become impure by contact with najasah?",
    "نعم، ينجس", "لا، إلا بالتغير", "ينجس إن كان قليلاً", "ينجس السطح فقط",
    "Yes, becomes impure", "No, except by change", "Becomes impure if little", "Only surface impure",
    1,
    "الماء الجاري لا ينجس بملاقاة النجاسة إلا إذا تغير أحد أوصافه بها.",
    "Running water doesn't become impure by contact with najasah unless one of its qualities changes.",
    "sistani.org/70", "sistani.org/70",
    "beginner", "taharah,water,running"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجوز الوضوء بماء الورد المخلوط بالماء؟",
    "Is wudu with rose water mixed with regular water permissible?",
    "نعم، مطلقاً", "يجوز إن كان الماء غالباً", "لا يجوز", "يجوز للضرورة",
    "Yes, absolutely", "Permissible if water predominant", "Not permissible", "Permissible in necessity",
    1,
    "إذا كان الماء هو الغالب بحيث يصدق عليه الماء المطلق عرفاً صح الوضوء به.",
    "If water is predominant such that it's considered absolute water customarily, wudu with it is valid.",
    "sistani.org/62", "sistani.org/62",
    "intermediate", "taharah,wudu,mixed"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم ملاقاة المني للثوب الرطب؟",
    "What's ruling on semen contacting wet clothing?",
    "ينجسه", "لا ينجسه", "ينجسه إن كثر", "يستحب غسله",
    "Impurifies it", "Doesn't impurify", "Impurifies if excessive", "Washing recommended",
    0,
    "المني نجس، فإذا أصاب الثوب وكان رطباً تنجس الثوب.",
    "Semen is impure; if it touches clothing while wet, clothing becomes impure.",
    "sistani.org/88", "sistani.org/88",
    "beginner", "taharah,semen,clothing"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يطهر الإناء المتنجس بالماء القليل؟",
    "Does impure container purify with little water?",
    "نعم، بثلاث غسلات", "لا، يحتاج ماء كثيراً", "يطهر بمرة واحدة", "يطهر بالشمس",
    "Yes, with three washes", "No, needs abundant water", "Purifies with once", "Purifies by sun",
    0,
    "يطهر الإناء المتنجس بالماء القليل بثلاث غسلات.",
    "Impure container purifies with little water by three washes.",
    "sistani.org/119", "sistani.org/119",
    "beginner", "taharah,container,purification"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم دم البراغيث والبعوض؟",
    "What's ruling on blood of fleas and mosquitoes?",
    "طاهر", "نجس", "طاهر إن كان قليلاً", "مشكوك",
    "Pure", "Impure", "Pure if little", "Doubtful",
    0,
    "دم البراغيث والبعوض طاهر.",
    "Blood of fleas and mosquitoes is pure.",
    "sistani.org/88", "sistani.org/88",
    "beginner", "taharah,blood,insects"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب تطهير باطن الفم بعد أكل النجس؟",
    "Must inside of mouth be purified after eating najis?",
    "نعم، قبل الصلاة", "نعم، فوراً", "لا يجب", "يستحب",
    "Yes, before prayer", "Yes, immediately", "Not required", "Recommended",
    2,
    "لا يجب تطهير باطن الفم، ولكن إن أراد الصلاة فالأحوط تطهيره.",
    "Purifying inside of mouth is not required, but if wanting to pray, precautionary to purify it.",
    "sistani.org/96", "sistani.org/96",
    "intermediate", "taharah,mouth,purification"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل ينقض الوضوء بخروج الريح من القبل؟",
    "Does wudu break by wind from front passage?",
    "نعم", "لا", "على الأحوط", "للمرأة فقط",
    "Yes", "No", "As precaution", "For women only",
    1,
    "لا ينقض الوضوء بخروج الريح من القبل.",
    "Wudu doesn't break by wind from front passage.",
    "sistani.org/305", "sistani.org/305",
    "beginner", "taharah,wudu,nullifier"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الوضوء بالماء المتغير بطين طاهر؟",
    "What's ruling on wudu with water changed by pure mud?",
    "صحيح", "باطل", "صحيح إن لم يسلب اسم الماء", "مكروه",
    "Valid", "Invalid", "Valid if water name not removed", "Disliked",
    2,
    "إذا تغير الماء بشيء طاهر ولم يسلب عنه اسم الماء صح الوضوء به.",
    "If water changes by pure thing and water name not removed, wudu with it is valid.",
    "sistani.org/62", "sistani.org/62",
    "intermediate", "taharah,water,changed"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب الترتيب في غسل الجنابة الترتيبي؟",
    "Is sequence required in sequential ghusl janaba?",
    "نعم، يجب", "لا، لا يجب", "يستحب", "يجب بين الرأس والجسد",
    "Yes, required", "No, not required", "Recommended", "Required between head and body",
    0,
    "يجب في الغسل الترتيبي غسل الرأس والرقبة أولاً، ثم الجانب الأيمن، ثم الأيسر.",
    "In sequential ghusl, washing head and neck first is required, then right side, then left.",
    "sistani.org/345", "sistani.org/345",
    "beginner", "taharah,ghusl,sequence"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم المسح على الجورب في الوضوء؟",
    "What's ruling on wiping over socks in wudu?",
    "لا يجوز", "يجوز", "يجوز للضرورة", "يجوز في السفر",
    "Not permissible", "Permissible", "Permissible in necessity", "Permissible in travel",
    0,
    "لا يجوز المسح على الجورب في الوضوء، بل يجب مسح البشرة.",
    "Wiping over socks in wudu is not permissible; wiping skin is required.",
    "sistani.org/311", "sistani.org/311",
    "beginner", "taharah,wudu,socks"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب غسل الشعر في غسل الجنابة؟",
    "Is washing hair required in ghusl janaba?",
    "نعم، يجب غسل الشعر", "يجب إيصال الماء للبشرة", "يكفي المسح", "لا يجب",
    "Yes, washing hair required", "Reaching water to skin required", "Wiping suffices", "Not required",
    1,
    "يجب في الغسل إيصال الماء إلى البشرة، فإن كان الشعر كثيفاً وجب إيصال الماء تحته.",
    "In ghusl, reaching water to skin is required; if hair is thick, reaching water under it is required.",
    "sistani.org/347", "sistani.org/347",
    "intermediate", "taharah,ghusl,hair"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم بلع الماء أثناء الوضوء؟",
    "What's ruling on swallowing water during wudu?",
    "يبطل الوضوء", "لا يبطل", "يكره", "يبطل للصائم",
    "Invalidates wudu", "Doesn't invalidate", "Disliked", "Invalidates for fasting",
    1,
    "بلع الماء أثناء الوضوء لا يبطله.",
    "Swallowing water during wudu doesn't invalidate it.",
    "sistani.org/294", "sistani.org/294",
    "beginner", "taharah,wudu,swallowing"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب إزالة المناكير قبل الوضوء؟",
    "Must nail polish be removed before wudu?",
    "نعم، يجب", "لا، إن كان خفيفاً", "لا يجب", "يستحب",
    "Yes, required", "No, if light", "Not required", "Recommended",
    0,
    "يجب إزالة المناكير قبل الوضوء لأنه يمنع وصول الماء إلى البشرة.",
    "Removing nail polish before wudu is required as it prevents water reaching skin.",
    "sistani.org/294", "sistani.org/294",
    "beginner", "taharah,wudu,nailpolish"));

// MORE SAWM QUESTIONS (15)
questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم استعمال قطرة العين في نهار رمضان؟",
    "What's ruling on using eye drops during Ramadan?",
    "يبطل الصوم", "لا يبطل إن لم يصل للحلق", "لا يبطل مطلقاً", "مكروه",
    "Invalidates fast", "Doesn't invalidate if not reaching throat", "Doesn't invalidate absolutely", "Disliked",
    1,
    "قطرة العين لا تبطل الصوم إذا لم يصل طعمها إلى الحلق.",
    "Eye drops don't invalidate fast if taste doesn't reach throat.",
    "sistani.org/1574", "sistani.org/1574",
    "intermediate", "sawm,eyedrops,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز للصائم استعمال معجون الأسنان ذي النكهة القوية؟",
    "May fasting person use strongly flavored toothpaste?",
    "لا يجوز", "يجوز بشرط عدم الابتلاع", "يبطل الصوم", "مكروه",
    "Not permissible", "Permissible if not swallowing", "Invalidates fast", "Disliked",
    1,
    "يجوز استعمال معجون الأسنان للصائم بشرط عدم ابتلاع شيء منه.",
    "Using toothpaste for fasting person is permissible provided nothing is swallowed.",
    "sistani.org/1576", "sistani.org/1576",
    "beginner", "sawm,toothpaste,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الحقنة الوريدية للصائم؟",
    "What's ruling on intravenous injection for fasting person?",
    "تبطل الصوم", "لا تبطل", "تبطل إن كانت مغذية", "على الأحوط تبطل",
    "Invalidates fast", "Doesn't invalidate", "Invalidates if nutritious", "As precaution invalidates",
    1,
    "الحقنة الوريدية والعضلية لا تبطل الصوم حتى وإن كانت مغذية.",
    "Intravenous and intramuscular injections don't invalidate fast even if nutritious.",
    "sistani.org/1573", "sistani.org/1573",
    "intermediate", "sawm,injection,iv"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب الإمساك على من نوى الصيام ثم أصبح مسافراً؟",
    "Must one who intended fast then became traveler abstain?",
    "نعم، يجب", "لا، يفطر", "يفطر بعد الحد", "يكمل صومه",
    "Yes, must", "No, breaks fast", "Breaks after limit", "Completes fast",
    2,
    "من نوى الصيام ثم سافر بعد الزوال يتم صومه، وإن سافر قبله أفطر بعد تجاوز حد الترخص.",
    "Whoever intended fast then traveled after noon completes fast; if before noon, breaks after passing distance limit.",
    "sistani.org/1655", "sistani.org/1655",
    "advanced", "sawm,travel,timing"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم من احتلم في نهار رمضان؟",
    "What's ruling on fast of who had wet dream during Ramadan?",
    "باطل", "صحيح", "يجب الغسل فوراً", "يقضيه",
    "Invalid", "Valid", "Ghusl immediately required", "Makes it up",
    1,
    "من احتلم في نهار رمضان فصومه صحيح، ويستحب له الغسل.",
    "Whoever had wet dream during Ramadan, his fast is valid, and ghusl is recommended.",
    "sistani.org/1564", "sistani.org/1564",
    "beginner", "sawm,wetdream,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز للصائم التبرع بالدم؟",
    "May fasting person donate blood?",
    "لا يجوز", "يجوز", "يجوز إن لم يضعفه", "يبطل الصوم",
    "Not permissible", "Permissible", "Permissible if doesn't weaken", "Invalidates fast",
    2,
    "يجوز للصائم التبرع بالدم إذا لم يؤد إلى الضعف المفرط.",
    "Fasting person may donate blood if it doesn't lead to excessive weakness.",
    "sistani.org/1581", "sistani.org/1581",
    "intermediate", "sawm,blood,donation"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم الشك (يوم الثلاثين من شعبان)؟",
    "What's ruling on fasting day of doubt (30th of Sha'ban)?",
    "واجب", "حرام", "مستحب بنية الندب", "يجوز بنية رمضان",
    "Obligatory", "Forbidden", "Recommended with voluntary intention", "Permissible with Ramadan intention",
    2,
    "صوم يوم الشك مستحب بنية شعبان أو الندب، ولا يجوز بنية رمضان.",
    "Fasting day of doubt is recommended with Sha'ban or voluntary intention, not permissible with Ramadan intention.",
    "sistani.org/1685", "sistani.org/1685",
    "intermediate", "sawm,doubt,day"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب الصوم على المجنون إذا أفاق في نهار رمضان؟",
    "Must insane person fast if regains sanity during Ramadan?",
    "نعم، فوراً", "يمسك بقية النهار", "لا يجب", "يقضي هذا اليوم",
    "Yes, immediately", "Abstains rest of day", "Not required", "Makes up this day",
    2,
    "إذا أفاق المجنون في نهار رمضان لم يجب عليه الإمساك ولا القضاء.",
    "If insane person regains sanity during Ramadan, abstaining and making up are not required.",
    "sistani.org/1627", "sistani.org/1627",
    "advanced", "sawm,insanity,recovery"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الكذب على النبي في نهار رمضان؟",
    "What's ruling on lying about Prophet during Ramadan?",
    "يبطل الصوم", "حرام ولا يبطل", "يبطل على الأحوط", "يوجب الكفارة",
    "Invalidates fast", "Forbidden but doesn't invalidate", "Invalidates as precaution", "Requires kaffarah",
    0,
    "الكذب على الله والرسول والأئمة من مبطلات الصوم.",
    "Lying about Allah, Messenger and Imams is among fast invalidators.",
    "sistani.org/1553", "sistani.org/1553",
    "advanced", "sawm,lying,prophet"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يبطل الصوم بإخراج الريح من الدبر؟",
    "Does fast invalidate by passing wind from back passage?",
    "نعم", "لا", "على الأحوط", "يوجب سجود السهو",
    "Yes", "No", "As precaution", "Requires sajda sahw",
    1,
    "إخراج الريح لا يبطل الصوم.",
    "Passing wind doesn't invalidate fast.",
    "sistani.org/1547", "sistani.org/1547",
    "beginner", "sawm,wind,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم من أكل السحور ظاناً بقاء الليل فتبين طلوع الفجر؟",
    "What's ruling on fast of who ate suhoor thinking night remained then dawn appeared?",
    "باطل ويقضي", "صحيح", "صحيح إن كان معذوراً", "يستحب القضاء",
    "Invalid and makes up", "Valid", "Valid if excused", "Making up recommended",
    1,
    "إذا أكل في السحور معتقداً بقاء الليل ثم تبين طلوع الفجر فصومه صحيح.",
    "If ate at suhoor believing night remained then dawn appeared, his fast is valid.",
    "sistani.org/1600", "sistani.org/1600",
    "intermediate", "sawm,suhoor,dawn"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز للصائم شم العطور القوية؟",
    "May fasting person smell strong perfumes?",
    "لا يجوز", "يجوز", "يكره", "يبطل الصوم",
    "Not permissible", "Permissible", "Disliked", "Invalidates fast",
    1,
    "يجوز للصائم شم العطور، لكن يكره استنشاق البخور.",
    "Fasting person may smell perfumes, but inhaling incense is disliked.",
    "sistani.org/1579", "sistani.org/1579",
    "beginner", "sawm,perfume,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم النافلة لمن عليه قضاء من رمضان؟",
    "What's ruling on voluntary fasting for who has Ramadan qadha?",
    "لا يجوز", "يجوز", "مكروه", "يجوز بعد رمضان القادم",
    "Not permissible", "Permissible", "Disliked", "Permissible after next Ramadan",
    1,
    "يجوز صوم النافلة لمن عليه قضاء من رمضان.",
    "Voluntary fasting for who has Ramadan qadha is permissible.",
    "sistani.org/1721", "sistani.org/1721",
    "beginner", "sawm,voluntary,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب قضاء صوم النذر إذا أفطر؟",
    "Must vowed fast be made up if broken?",
    "نعم، والكفارة", "القضاء فقط", "الكفارة فقط", "لا شيء",
    "Yes, and kaffarah", "Only qadha", "Only kaffarah", "Nothing",
    0,
    "من أفطر يوماً منذوراً معيناً وجب عليه القضاء والكفارة.",
    "Whoever breaks specific vowed day, qadha and kaffarah are required.",
    "sistani.org/1752", "sistani.org/1752",
    "advanced", "sawm,vow,kaffarah"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الصوم المستحب في السفر؟",
    "What's ruling on recommended fasting while traveling?",
    "جائز", "غير جائز", "جائز لثلاثة أيام في الحج", "مكروه",
    "Permissible", "Not permissible", "Permissible for three days in Hajj", "Disliked",
    2,
    "لا يجوز الصوم المستحب في السفر، إلا ثلاثة أيام بدل هدي التمتع.",
    "Recommended fasting while traveling is not permissible, except three days replacing Hajj sacrifice.",
    "sistani.org/1719", "sistani.org/1719",
    "intermediate", "sawm,travel,recommended"));

// MORE KHUMS QUESTIONS (15)
questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في المال الموروث من الأب؟",
    "Is Khums due on money inherited from father?",
    "نعم، فوراً", "لا، إلا إذا زاد عن المؤونة", "نعم، بعد سنة", "لا يجب مطلقاً",
    "Yes, immediately", "No, unless exceeds expenses", "Yes, after a year", "Not required at all",
    1,
    "المال الموروث من الأب لا خمس فيه، إلا ما زاد عن مؤونة السنة.",
    "Money inherited from father has no Khums, except what exceeds year's expenses.",
    "sistani.org/1766", "sistani.org/1766",
    "intermediate", "khums,inheritance,father"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الهبة من الأب؟",
    "What's ruling on Khums in gift from father?",
    "يجب", "لا يجب", "يجب إن زادت عن المؤونة", "يجب بعد سنة",
    "Required", "Not required", "Required if exceeds expenses", "Required after year",
    1,
    "الهبة من الأب لا يجب فيها الخمس.",
    "Gift from father doesn't have Khums.",
    "sistani.org/1767", "sistani.org/1767",
    "beginner", "khums,gift,father"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في المال المقترض لشراء البيت؟",
    "Is Khums due on money borrowed to buy house?",
    "نعم", "لا", "يجب عند السداد", "يجب في البيت",
    "Yes", "No", "Due upon repayment", "Due on house",
    1,
    "المال المقترض لا خمس فيه.",
    "Borrowed money has no Khums.",
    "sistani.org/1773", "sistani.org/1773",
    "beginner", "khums,loan,house"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الأدوات المدرسية للطلاب؟",
    "What's ruling on Khums in school supplies for students?",
    "يجب", "لا يجب إن كانت من المؤونة", "يجب بعد التخرج", "يجب إن كانت غالية",
    "Required", "Not required if from expenses", "Required after graduation", "Required if expensive",
    1,
    "الأدوات المدرسية من مؤونة السنة فلا خمس فيها.",
    "School supplies are from year's expenses so no Khums on them.",
    "sistani.org/1770", "sistani.org/1770",
    "beginner", "khums,school,supplies"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الأضحية المشتراة؟",
    "Is Khums due on purchased sacrifice animal?",
    "نعم", "لا، إن اشتريت من مال السنة", "يجب بعد العيد", "لا يجب مطلقاً",
    "Yes", "No, if bought from year's money", "Due after Eid", "Not required at all",
    1,
    "الأضحية المشتراة من أرباح السنة لا خمس فيها.",
    "Sacrifice animal bought from year's profit has no Khums.",
    "sistani.org/1770", "sistani.org/1770",
    "beginner", "khums,sacrifice,animal"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في التعويض عن حادث؟",
    "What's ruling on Khums in accident compensation?",
    "يجب", "لا يجب", "يجب ما زاد عن الضرر", "يجب بعد سنة",
    "Required", "Not required", "Required for what exceeds damage", "Required after year",
    2,
    "التعويض عن الحادث لا خمس فيه، إلا ما زاد عن مقدار الضرر.",
    "Accident compensation has no Khums, except what exceeds damage amount.",
    "sistani.org/1768", "sistani.org/1768",
    "intermediate", "khums,compensation,accident"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في مكافأة نهاية الخدمة؟",
    "Is Khums due on end-of-service gratuity?",
    "نعم، كاملة", "لا", "يجب ما زاد عن المؤونة", "يجب نصفها",
    "Yes, complete", "No", "What exceeds expenses required", "Half required",
    2,
    "مكافأة نهاية الخدمة يجب فيها الخمس بالنسبة لما يزيد عن المؤونة.",
    "End-of-service gratuity has Khums regarding what exceeds expenses.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,gratuity,service"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الجائزة المكتسبة؟",
    "What's ruling on Khums in earned prize?",
    "يجب فوراً", "لا يجب", "يجب ما زاد عن المؤونة", "يجب نصفها",
    "Required immediately", "Not required", "What exceeds expenses required", "Half required",
    2,
    "الجائزة يجب فيها الخمس بالنسبة لما يبقى منها بعد المؤونة.",
    "Prize has Khums regarding what remains after expenses.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,prize,ruling"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الراتب التقاعدي؟",
    "Is Khums due on retirement pension?",
    "نعم، كاملاً", "يجب ما زاد عن المؤونة", "لا يجب", "يجب نصفه",
    "Yes, completely", "What exceeds expenses required", "Not required", "Half required",
    1,
    "الراتب التقاعدي يجب فيه الخمس بالنسبة لما يزيد عن مؤونة السنة.",
    "Retirement pension has Khums regarding what exceeds year's expenses.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,pension,retirement"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في النقود الموفرة من المصروف الشهري؟",
    "What's ruling on Khums in money saved from monthly allowance?",
    "يجب", "لا يجب إن كانت من المؤونة", "يجب بعد سنة", "لا يجب مطلقاً",
    "Required", "Not required if from expenses", "Required after year", "Not required at all",
    0,
    "ما يوفره من مصروفه الشهري يجب فيه الخمس.",
    "What is saved from monthly allowance has Khums.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,savings,monthly"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الحلي الذهبية المخزونة؟",
    "Is Khums due on stored gold jewelry?",
    "نعم، يجب", "لا يجب إن كانت للزينة", "يجب إن مضى عليها سنة", "يجب إن كانت كثيرة",
    "Yes, required", "Not required if for adornment", "Required if year passed", "Required if abundant",
    1,
    "الحلي الذهبية المحفوظة للاستعمال لا خمس فيها.",
    "Gold jewelry kept for use has no Khums.",
    "sistani.org/1775", "sistani.org/1775",
    "beginner", "khums,gold,jewelry"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في المبلغ المخصص للحج؟",
    "What's ruling on Khums in amount allocated for Hajj?",
    "يجب", "لا يجب", "يجب إن لم يحج", "يجب بعد الحج",
    "Required", "Not required", "Required if didn't perform Hajj", "Required after Hajj",
    2,
    "المال المدخر للحج الواجب لا خمس فيه، وإن كان للمستحب وجب فيه الخمس.",
    "Money saved for obligatory Hajj has no Khums; if for recommended, Khums is required.",
    "sistani.org/1770", "sistani.org/1770",
    "intermediate", "khums,hajj,allocation"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في المبلغ المدخر للزواج؟",
    "Is Khums due on amount saved for marriage?",
    "نعم", "لا، إن كان من المؤونة", "يجب بعد الزواج", "يجب نصفه",
    "Yes", "No, if from expenses", "Required after marriage", "Half required",
    1,
    "المال المدخر للزواج يعتبر من المؤونة فلا خمس فيه.",
    "Money saved for marriage is considered from expenses so no Khums on it.",
    "sistani.org/1770", "sistani.org/1770",
    "beginner", "khums,marriage,savings"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الأدوية المخزنة؟",
    "What's ruling on Khums in stored medicines?",
    "يجب", "لا يجب إن كانت للاستعمال", "يجب بعد سنة", "يجب إن انتهت صلاحيتها",
    "Required", "Not required if for use", "Required after year", "Required if expired",
    1,
    "الأدوية المحفوظة للاستعمال لا خمس فيها.",
    "Medicines kept for use have no Khums.",
    "sistani.org/1770", "sistani.org/1770",
    "beginner", "khums,medicine,stored"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الأرباح التجارية قبل نهاية السنة؟",
    "Is Khums due on commercial profits before year-end?",
    "نعم، فوراً", "لا، بعد السنة", "يجب إن صرفها", "حسب الاتفاق",
    "Yes, immediately", "No, after year", "Required if spent", "According to agreement",
    1,
    "الأرباح التجارية لا يجب فيها الخمس إلا بعد مرور السنة.",
    "Commercial profits don't have Khums except after year passes.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,profit,commercial"));

// MORE HAJJ QUESTIONS (10)
questionRepository.save(createQuestion(hajj, sistani,
    "هل يجب الحج فوراً على المستطيع؟",
    "Is Hajj immediately obligatory on capable person?",
    "نعم، في أول عام", "لا، في أي وقت", "يستحب فوراً", "خلال ثلاث سنوات",
    "Yes, in first year", "No, any time", "Recommended immediately", "Within three years",
    0,
    "يجب الحج فوراً على المستطيع في أول عام استطاعته.",
    "Hajj is immediately obligatory on capable person in first year of capability.",
    "sistani.org/2050", "sistani.org/2050",
    "beginner", "hajj,obligation,timing"));

questionRepository.save(createQuestion(hajj, sistani,
    "ما حكم من استطاع ثم زالت استطاعته؟",
    "What's ruling for who became capable then capability ceased?",
    "يسقط الوجوب", "يبقى واجباً", "يستحب", "يجب إن عادت",
    "Obligation drops", "Remains obligatory", "Recommended", "Required if returns",
    1,
    "من استطاع للحج ثم زالت استطاعته يبقى الحج واجباً عليه ولو بالاستدانة.",
    "Whoever became capable for Hajj then capability ceased, Hajj remains obligatory even by borrowing.",
    "sistani.org/2053", "sistani.org/2053",
    "advanced", "hajj,capability,loss"));

questionRepository.save(createQuestion(hajj, sistani,
    "هل يجوز الإنابة في حج الإسلام؟",
    "Is deputization permissible in obligatory Hajj?",
    "نعم، مطلقاً", "لا، إلا للميت", "لا، إلا للعاجز", "نعم، بإذن المرجع",
    "Yes, absolutely", "No, except for dead", "No, except for unable", "Yes, with marja permission",
    1,
    "لا تجوز النيابة في حج الإسلام إلا عن الميت أو العاجز المأيوس من زوال عجزه.",
    "Deputization in obligatory Hajj is not permissible except for dead or unable with no hope of recovery.",
    "sistani.org/2055", "sistani.org/2055",
    "intermediate", "hajj,deputization,ruling"));

questionRepository.save(createQuestion(hajj, sistani,
    "ما حكم الحج بمال فيه شبهة؟",
    "What's ruling on Hajj with doubtful money?",
    "صحيح", "باطل", "صحيح مع الإثم", "يجب إعادته",
    "Valid", "Invalid", "Valid with sin", "Must repeat",
    2,
    "الحج بمال فيه شبهة يجزئ عن حجة الإسلام وإن كان آثماً.",
    "Hajj with doubtful money suffices for obligatory Hajj though sinful.",
    "sistani.org/2058", "sistani.org/2058",
    "advanced", "hajj,money,doubtful"));

questionRepository.save(createQuestion(hajj, sistani,
    "هل يجوز العمرة المفردة في أشهر الحج؟",
    "Is individual Umrah permissible in Hajj months?",
    "نعم", "لا", "نعم، لغير المتمتع", "مكروهة",
    "Yes", "No", "Yes, for non-Tamattu'", "Disliked",
    0,
    "يجوز الإتيان بالعمرة المفردة في أشهر الحج.",
    "Performing individual Umrah in Hajj months is permissible.",
    "sistani.org/2103", "sistani.org/2103",
    "beginner", "hajj,umrah,timing"));

questionRepository.save(createQuestion(hajj, sistani,
    "ما حكم حلق اللحية في الإحرام؟",
    "What's ruling on shaving beard in ihram?",
    "جائز", "حرام", "مكروه", "يجب فدية",
    "Permissible", "Forbidden", "Disliked", "Fidyah required",
    1,
    "يحرم على المحرم حلق اللحية، ويجب عليه الفدية إن فعله عمداً.",
    "Shaving beard is forbidden for muhrim, and fidyah is required if done deliberately.",
    "sistani.org/2175", "sistani.org/2175",
    "intermediate", "hajj,ihram,beard"));

questionRepository.save(createQuestion(hajj, sistani,
    "هل يجوز قص الأظافر في الإحرام؟",
    "Is cutting nails permissible in ihram?",
    "نعم", "لا، مطلقاً", "نعم، للضرورة", "نعم، بفدية",
    "Yes", "No, absolutely", "Yes, for necessity", "Yes, with fidyah",
    2,
    "يحرم قص الأظافر في الإحرام إلا للضرورة.",
    "Cutting nails in ihram is forbidden except for necessity.",
    "sistani.org/2180", "sistani.org/2180",
    "beginner", "hajj,ihram,nails"));

questionRepository.save(createQuestion(hajj, sistani,
    "ما حكم استعمال الصابون المعطر في الإحرام؟",
    "What's ruling on using scented soap in ihram?",
    "جائز", "حرام", "جائز بلا قصد الطيب", "مكروه",
    "Permissible", "Forbidden", "Permissible without perfume intent", "Disliked",
    2,
    "يجوز استعمال الصابون المعطر في الإحرام إذا لم يقصد التطيب.",
    "Using scented soap in ihram is permissible if perfume is not intended.",
    "sistani.org/2170", "sistani.org/2170",
    "intermediate", "hajj,ihram,soap"));

questionRepository.save(createQuestion(hajj, sistani,
    "هل يجوز للمحرم النظر في المرآة؟",
    "May muhrim look in mirror?",
    "نعم", "لا", "نعم، للضرورة", "مكروه",
    "Yes", "No", "Yes, for necessity", "Disliked",
    0,
    "يجوز للمحرم النظر في المرآة.",
    "Muhrim may look in mirror.",
    "sistani.org/2189", "sistani.org/2189",
    "beginner", "hajj,ihram,mirror"));

questionRepository.save(createQuestion(hajj, sistani,
    "ما حكم الاغتسال في الإحرام؟",
    "What's ruling on bathing in ihram?",
    "جائز", "حرام", "مكروه", "جائز للضرورة",
    "Permissible", "Forbidden", "Disliked", "Permissible for necessity",
    0,
    "يجوز الاغتسال للمحرم، بل يستحب.",
    "Bathing for muhrim is permissible, even recommended.",
    "sistani.org/2164", "sistani.org/2164",
    "beginner", "hajj,ihram,bathing"));

// MORE MUAMALAT QUESTIONS (10)
questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع ما لا يملك؟",
    "Is selling what one doesn't own permissible?",
    "نعم", "لا", "نعم، بإذن المالك", "نعم، في السلم",
    "Yes", "No", "Yes, with owner permission", "Yes, in salam",
    2,
    "لا يجوز بيع ما لا يملك إلا بإذن المالك، أو في عقد السلم.",
    "Selling what one doesn't own is not permissible except with owner permission, or in salam contract.",
    "sistani.org/2469", "sistani.org/2469",
    "intermediate", "muamalat,sale,ownership"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم البيع بالتقسيط بزيادة في الثمن؟",
    "What's ruling on installment sale with price increase?",
    "جائز", "حرام", "جائز بشروط", "مكروه",
    "Permissible", "Forbidden", "Permissible with conditions", "Disliked",
    0,
    "يجوز البيع بالتقسيط مع زيادة في الثمن.",
    "Installment sale with price increase is permissible.",
    "sistani.org/2541", "sistani.org/2541",
    "beginner", "muamalat,installment,sale"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع الذهب بالذهب مع التفاضل؟",
    "Is selling gold for gold with difference permissible?",
    "نعم", "لا، إلا مثلاً بمثل", "نعم، بشروط", "نعم، نقداً",
    "Yes", "No, except like for like", "Yes, with conditions", "Yes, in cash",
    1,
    "لا يجوز بيع الذهب بالذهب أو الفضة بالفضة إلا مثلاً بمثل.",
    "Selling gold for gold or silver for silver is not permissible except like for like.",
    "sistani.org/2556", "sistani.org/2556",
    "intermediate", "muamalat,gold,exchange"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم بيع العنب لمن يصنعه خمراً؟",
    "What's ruling on selling grapes to who makes wine?",
    "جائز", "حرام", "جائز إن لم يقصد", "مكروه",
    "Permissible", "Forbidden", "Permissible if not intended", "Disliked",
    1,
    "يحرم بيع العنب لمن يعلم أنه يصنعه خمراً.",
    "Selling grapes to who is known to make wine is forbidden.",
    "sistani.org/2483", "sistani.org/2483",
    "intermediate", "muamalat,sale,haram"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز الشراء من البنك بالتقسيط (المرابحة)؟",
    "Is buying from bank by installment (murabaha) permissible?",
    "نعم", "لا", "نعم، بشروط", "على الأحوط لا",
    "Yes", "No", "Yes, with conditions", "As precaution no",
    2,
    "يجوز الشراء بالمرابحة إذا تم العقد بالصيغة الشرعية.",
    "Buying by murabaha is permissible if contract done with Islamic formula.",
    "sistani.org/2541", "sistani.org/2541",
    "advanced", "muamalat,bank,murabaha"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم بيع الكلب؟",
    "What's ruling on selling dog?",
    "جائز", "حرام", "جائز لكلب الصيد", "مكروه",
    "Permissible", "Forbidden", "Permissible for hunting dog", "Disliked",
    2,
    "يجوز بيع كلب الصيد دون غيره.",
    "Selling hunting dog is permissible, not others.",
    "sistani.org/2485", "sistani.org/2485",
    "intermediate", "muamalat,sale,dog"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز استئجار المحل لبيع الخمر؟",
    "Is renting shop to sell wine permissible?",
    "نعم", "لا", "نعم، بلا علم", "جائز بزيادة الأجرة",
    "Yes", "No", "Yes, without knowledge", "Permissible with increased rent",
    1,
    "لا يجوز تأجير المحل لبيع الخمر.",
    "Renting shop to sell wine is not permissible.",
    "sistani.org/2609", "sistani.org/2609",
    "beginner", "muamalat,rent,haram"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم الوعد بالبيع؟",
    "What's ruling on promise to sell?",
    "ملزم", "غير ملزم", "ملزم إن كان معلقاً", "ملزم بشروط",
    "Binding", "Not binding", "Binding if conditional", "Binding with conditions",
    1,
    "الوعد بالبيع غير ملزم شرعاً، لكن يستحب الوفاء به.",
    "Promise to sell is not binding religiously, but fulfilling it is recommended.",
    "sistani.org/2519", "sistani.org/2519",
    "intermediate", "muamalat,promise,sale"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع الدم؟",
    "Is selling blood permissible?",
    "نعم", "لا", "نعم، للعلاج", "مكروه",
    "Yes", "No", "Yes, for treatment", "Disliked",
    2,
    "يجوز بيع الدم إذا كان له نفع محلل.",
    "Selling blood is permissible if it has lawful benefit.",
    "sistani.org/2488", "sistani.org/2488",
    "intermediate", "muamalat,sale,blood"));


    questionRepository.save(createQuestion(amr, sistani,
    "هل يجوز النهي عن المنكر بالكذب؟",
    "May forbidding evil be done with lying?",
    "نعم", "لا", "للضرورة", "إن كان المنكر كبيراً",
    "Yes", "No", "For necessity", "If evil is major",
    1,
    "لا يجوز النهي عن المنكر بالكذب أو الغيبة.",
    "Forbidding evil with lying or backbiting is not permissible.",
    "sistani.org/2031", "sistani.org/2031",
    "advanced", "amr,lying,ruling"));

questionRepository.save(createQuestion(amr, sistani,
    "هل يسقط الأمر بالمعروف مع اليأس من التأثير؟",
    "Does enjoining good drop with despair from effect?",
    "نعم", "لا، واجب دائماً", "يبقى الإنكار القلبي", "على الأحوط لا",
    "Yes", "No, always obligatory", "Heart denial remains", "As precaution no",
    2,
    "إذا يئس من التأثير سقط الأمر بالمعروف إلا الإنكار القلبي.",
    "If despairing from effect, enjoining good drops except heart denial.",
    "sistani.org/2028", "sistani.org/2028",
    "intermediate", "amr,despair,ruling"));

questionRepository.save(createQuestion(amr, sistani,
    "ما حكم الأمر بالمعروف للوالدين؟",
    "What's ruling on enjoining good to parents?",
    "واجب", "غير واجب", "واجب برفق", "مستحب",
    "Obligatory", "Not obligatory", "Obligatory gently", "Recommended",
    2,
    "يجب الأمر بالمعروف للوالدين لكن بلطف ورفق.",
    "Enjoining good to parents is obligatory but gently and kindly.",
    "sistani.org/2032", "sistani.org/2032",
    "intermediate", "amr,parents,ruling"));

questionRepository.save(createQuestion(amr, sistani,
    "هل يجب الأمر بالمعروف في الأمور الشخصية؟",
    "Is enjoining good obligatory in personal matters?",
    "نعم، دائماً", "لا", "في ما يضر الآخرين", "في الواجبات فقط",
    "Yes, always", "No", "In what harms others", "Only in obligatories",
    3,
    "الأمر بالمعروف واجب في ترك الواجبات وفعل المحرمات.",
    "Enjoining good is obligatory in leaving obligatories and doing forbiddens.",
    "sistani.org/2024", "sistani.org/2024",
    "advanced", "amr,personal,ruling"));

questionRepository.save(createQuestion(amr, sistani,
    "هل يجوز استخدام القوة في النهي عن المنكر؟",
    "May force be used in forbidding evil?",
    "نعم، مطلقاً", "لا، مطلقاً", "بإذن الحاكم الشرعي", "في الضرورة القصوى",
    "Yes, absolutely", "No, absolutely", "With religious authority permission", "In extreme necessity",
    2,
    "لا يجوز استخدام القوة في النهي عن المنكر إلا بإذن الحاكم الشرعي.",
    "Using force in forbidding evil is not permissible except with religious authority permission.",
    "sistani.org/2033", "sistani.org/2033",
    "advanced", "amr,force,ruling"));

// MORE OATHS AND VOWS QUESTIONS (10)
questionRepository.save(createQuestion(oaths, sistani,
    "هل تنعقد اليمين بغير الله؟",
    "Does oath occur by other than Allah?",
    "نعم", "لا", "بالنبي فقط", "بالقرآن",
    "Yes", "No", "Only by Prophet", "By Quran",
    1,
    "لا تنعقد اليمين إلا بالله تعالى أو بأسمائه الخاصة.",
    "Oath doesn't occur except by Allah or His specific names.",
    "sistani.org/2711", "sistani.org/2711",
    "beginner", "oaths,allah,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "ما كفارة اليمين المنعقدة؟",
    "What's expiation of valid oath?",
    "إطعام عشرة مساكين", "صيام ثلاثة أيام", "عتق رقبة أو إطعام عشرة أو كسوتهم", "الاستغفار",
    "Feed ten poor", "Fast three days", "Free slave or feed ten or clothe them", "Seek forgiveness",
    2,
    "كفارة اليمين: عتق رقبة، أو إطعام عشرة مساكين، أو كسوتهم، فإن عجز صام ثلاثة أيام.",
    "Oath expiation: freeing slave, or feeding ten poor, or clothing them; if unable, fast three days.",
    "sistani.org/2732", "sistani.org/2732",
    "intermediate", "oaths,expiation,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "هل يجب الوفاء بالنذر المطلق؟",
    "Is fulfilling absolute vow obligatory?",
    "نعم", "لا", "إن كان في طاعة", "مستحب",
    "Yes", "No", "If in obedience", "Recommended",
    0,
    "يجب الوفاء بالنذر إذا كان صحيحاً.",
    "Fulfilling vow is obligatory if it's valid.",
    "sistani.org/2744", "sistani.org/2744",
    "beginner", "vows,fulfillment,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "ما شروط انعقاد النذر؟",
    "What are conditions for vow validity?",
    "البلوغ والعقل فقط", "القصد والصيغة", "أن يكون في طاعة", "جميع ما ذكر",
    "Only maturity and sanity", "Intent and formula", "Being in obedience", "All mentioned",
    3,
    "من شروط النذر: البلوغ، والعقل، والقصد، والاختيار، وأن يكون في طاعة.",
    "Vow conditions include: maturity, sanity, intent, choice, and being in obedience.",
    "sistani.org/2736", "sistani.org/2736",
    "intermediate", "vows,conditions,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "هل ينعقد نذر المعصية؟",
    "Does vow of sin occur?",
    "نعم", "لا", "ينعقد ويجب تركه", "ينعقد بالكفارة",
    "Yes", "No", "Occurs and must abandon", "Occurs with expiation",
    1,
    "نذر المعصية لا ينعقد.",
    "Vow of sin doesn't occur.",
    "sistani.org/2739", "sistani.org/2739",
    "beginner", "vows,sin,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "ما حكم العهد مع الله؟",
    "What's ruling on covenant with Allah?",
    "واجب الوفاء", "مستحب", "غير منعقد", "يحتاج شاهدين",
    "Fulfillment obligatory", "Recommended", "Not valid", "Needs two witnesses",
    0,
    "العهد مع الله واجب الوفاء به.",
    "Covenant with Allah is obligatory to fulfill.",
    "sistani.org/2756", "sistani.org/2756",
    "beginner", "oaths,covenant,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "هل تنحل اليمين بالإكراه؟",
    "Does oath dissolve by coercion?",
    "نعم", "لا", "تنحل بالكفارة", "على الأحوط لا",
    "Yes", "No", "Dissolves with expiation", "As precaution no",
    0,
    "اليمين بالإكراه لا تنعقد.",
    "Oath by coercion doesn't occur.",
    "sistani.org/2713", "sistani.org/2713",
    "intermediate", "oaths,coercion,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "ما كفارة النذر المخالف؟",
    "What's expiation of violated vow?",
    "كفارة يمين", "صيام شهرين", "إطعام ستين", "لا كفارة",
    "Oath expiation", "Fast two months", "Feed sixty", "No expiation",
    0,
    "كفارة مخالفة النذر ككفارة اليمين.",
    "Expiation of violating vow is like oath expiation.",
    "sistani.org/2753", "sistani.org/2753",
    "intermediate", "vows,expiation,ruling"));

questionRepository.save(createQuestion(oaths, sistani,
    "هل يصح نذر الصوم في السفر؟",
    "Is vowing fast while traveling valid?",
    "نعم", "لا", "يصح ويصوم", "يصح ويفطر",
    "Yes", "No", "Valid and fasts", "Valid and breaks",
    2,
    "إذا نذر الصوم في السفر صح نذره ووجب عليه الصوم.",
    "If one vows fast while traveling, his vow is valid and fasting is obligatory.",
    "sistani.org/2749", "sistani.org/2749",
    "advanced", "vows,fast,travel"));

questionRepository.save(createQuestion(oaths, sistani,
    "هل يجوز الحلف على فعل الغير؟",
    "May one swear on another's action?",
    "نعم", "لا", "إن كان مأذوناً", "على الأحوط لا",
    "Yes", "No", "If authorized", "As precaution no",
    1,
    "لا تنعقد اليمين على فعل الغير.",
    "Oath on another's action doesn't occur.",
    "sistani.org/2715", "sistani.org/2715",
    "advanced", "oaths,others,ruling"));

// MORE FOODS QUESTIONS (10)
questionRepository.save(createQuestion(foods, sistani,
    "هل يجوز أكل لحم الأرنب؟",
    "Is eating rabbit meat permissible?",
    "نعم", "لا، حرام", "مكروه", "يجوز المستأنس",
    "Yes", "No, forbidden", "Disliked", "Domestic permissible",
    1,
    "يحرم أكل لحم الأرنب على الأحوط وجوباً.",
    "Eating rabbit meat is forbidden as obligatory precaution.",
    "sistani.org/2256", "sistani.org/2256",
    "beginner", "foods,rabbit,ruling"));

questionRepository.save(createQuestion(foods, sistani,
    "ما حكم أكل الضفدع؟",
    "What's ruling on eating frog?",
    "حلال", "حرام", "مكروه", "حلال المائي",
    "Halal", "Haram", "Disliked", "Aquatic halal",
    1,
    "يحرم أكل الضفدع.",
    "Eating frog is forbidden.",
    "sistani.org/2258", "sistani.org/2258",
    "beginner", "foods,frog,ruling"));

questionRepository.save(createQuestion(foods, sistani,
    "هل يجوز أكل الجراد؟",
    "Is eating locust permissible?",
    "نعم، مطلقاً", "نعم، المذبوح", "نعم، ما أخذ حياً", "حرام",
    "Yes, absolutely", "Yes, slaughtered", "Yes, what taken alive", "Forbidden",
    2,
    "يحل أكل الجراد الذي يؤخذ حياً باليد أو بآلة.",
    "Eating locust taken alive by hand or tool is permissible.",
    "sistani.org/2259", "sistani.org/2259",
    "intermediate", "foods,locust,ruling"));

questionRepository.save(createQuestion(foods, sistani,
    "ما حكم الذبيحة بآلة كهربائية؟",
    "What's ruling on slaughter by electric device?",
    "حلال", "حرام", "حلال بشروط", "مكروه",
    "Halal", "Haram", "Halal with conditions", "Disliked",
    2,
    "تحل الذبيحة بالآلة الكهربائية إذا تحققت الشروط الشرعية.",
    "Slaughter by electric device is permissible if Islamic conditions are met.",
    "sistani.org/2233", "sistani.org/2233",
    "advanced", "foods,slaughter,electric"));

questionRepository.save(createQuestion(foods, sistani,
    "هل يجوز أكل جراد البحر (الروبيان)؟",
    "Is eating sea locust (shrimp) permissible?",
    "نعم", "لا", "الكبير فقط", "المقشر فقط",
    "Yes", "No", "Only large", "Only peeled",
    0,
    "يحل أكل الروبيان (القريدس).",
    "Eating shrimp is permissible.",
    "sistani.org/2252", "sistani.org/2252",
    "beginner", "foods,shrimp,ruling"));

questionRepository.save(createQuestion(foods, sistani,
    "ما حكم أكل السلحفاة؟",
    "What's ruling on eating turtle?",
    "حلال", "حرام", "البرية حلال", "البحرية حلال",
    "Halal", "Haram", "Land halal", "Sea halal",
    1,
    "يحرم أكل السلحفاة البحرية والبرية.",
    "Eating sea and land turtle is forbidden.",
    "sistani.org/2258", "sistani.org/2258",
    "beginner", "foods,turtle,ruling"));

questionRepository.save(createQuestion(foods, sistani,
    "هل تحل ذبيحة المرتد؟",
    "Is apostate's slaughter permissible?",
    "نعم", "لا", "الفطري لا", "بالتوبة",
    "Yes", "No", "Natural no", "With repentance",
    1,
    "لا تحل ذبيحة المرتد.",
    "Apostate's slaughter is not permissible.",
    "sistani.org/2231", "sistani.org/2231",
    "advanced", "foods,apostate,slaughter"));

questionRepository.save(createQuestion(foods, sistani,
    "ما حكم أكل لحم الحمار الوحشي؟",
    "What's ruling on eating wild donkey meat?",
    "حلال", "حرام", "مكروه", "حلال المذبوح",
    "Halal", "Haram", "Disliked", "Slaughtered halal",
    0,
    "يحل أكل لحم الحمار الوحشي.",
    "Eating wild donkey meat is permissible.",
    "sistani.org/2255", "sistani.org/2255",
    "intermediate", "foods,donkey,wild"));

questionRepository.save(createQuestion(foods, sistani,
    "هل يجوز أكل الطيور الجارحة؟",
    "Is eating predatory birds permissible?",
    "نعم", "لا", "ما له صفيف", "المستأنسة",
    "Yes", "No", "What has feathers", "Domesticated",
    1,
    "يحرم أكل الطير ذي المخلب الصائد.",
    "Eating bird with hunting claw is forbidden.",
    "sistani.org/2257", "sistani.org/2257",
    "beginner", "foods,birds,predatory"));

questionRepository.save(createQuestion(foods, sistani,
    "ما حكم الذبح بدون قطع الأوداج الأربعة؟",
    "What's ruling on slaughter without cutting four vessels?",
    "حلال", "حرام", "حلال بثلاثة", "مكروه",
    "Halal", "Haram", "Halal with three", "Disliked",
    1,
    "يجب في الذبح قطع الأوداج الأربعة.",
    "Cutting four vessels in slaughter is required.",
    "sistani.org/2234", "sistani.org/2234",
    "intermediate", "foods,slaughter,vessels"));

// MORE MUAMALAT QUESTIONS (10)
questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع العصير الذي يصير خمراً؟",
    "Is selling juice that becomes wine permissible?",
    "نعم", "لا", "إن لم يقصد", "بعد ثلاثة أيام",
    "Yes", "No", "If not intended", "After three days",
    1,
    "لا يجوز بيع العصير الذي يُعلم أنه يصير خمراً.",
    "Selling juice known to become wine is not permissible.",
    "sistani.org/2483", "sistani.org/2483",
    "advanced", "muamalat,juice,wine"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم البيع بالعربون؟",
    "What's ruling on sale with deposit?",
    "صحيح", "باطل", "صحيح بشرط استردادها", "مكروه",
    "Valid", "Invalid", "Valid with condition of return", "Disliked",
    0,
    "يجوز البيع بالعربون، فإن تم البيع احتسب من الثمن وإلا رده.",
    "Sale with deposit is permissible; if sale completes, counted from price, otherwise returned.",
    "sistani.org/2534", "sistani.org/2534",
    "intermediate", "muamalat,deposit,sale"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع الخمر للتخليل؟",
    "Is selling wine for vinegar making permissible?",
    "نعم", "لا", "نعم، إن تحققت الاستحالة", "للمسلم فقط",
    "Yes", "No", "Yes, if transformation verified", "To Muslim only",
    1,
    "لا يجوز بيع الخمر حتى للتخليل.",
    "Selling wine even for vinegar making is not permissible.",
    "sistani.org/2481", "sistani.org/2481",
    "advanced", "muamalat,wine,vinegar"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم السمسرة (الدلالة)؟",
    "What's ruling on brokerage?",
    "جائزة", "محرمة", "مكروهة", "جائزة بإذن الطرفين",
    "Permissible", "Forbidden", "Disliked", "Permissible with both parties permission",
    0,
    "السمسرة (الدلالة) جائزة ويستحق الأجر المتفق عليه.",
    "Brokerage is permissible and deserves agreed wage.",
    "sistani.org/2628", "sistani.org/2628",
    "beginner", "muamalat,brokerage,ruling"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع الميتة لأكل الكلاب؟",
    "Is selling carrion for dog food permissible?",
    "نعم", "لا", "يجوز لغير المأكول", "يجوز بلا نفع",
    "Yes", "No", "Permissible for non-edible", "Permissible without benefit",
    2,
    "يجوز بيع الميتة إذا كانت لها منفعة محللة كالانتفاع بجلدها.",
    "Selling carrion is permissible if it has lawful benefit like using its skin.",
    "sistani.org/2487", "sistani.org/2487",
    "intermediate", "muamalat,carrion,sale"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم بيع الدين بالدين؟",
    "What's ruling on selling debt for debt?",
    "جائز", "غير جائز", "جائز بشروط", "على الأحوط لا",
    "Permissible", "Not permissible", "Permissible with conditions", "As precaution no",
    1,
    "لا يجوز بيع الدين بالدين.",
    "Selling debt for debt is not permissible.",
    "sistani.org/2558", "sistani.org/2558",
    "advanced", "muamalat,debt,sale"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز الشراء من محل يبيع الحرام؟",
    "May one buy from shop selling haram?",
    "نعم، الحلال", "لا مطلقاً", "يكره", "بإذن المرجع",
    "Yes, the halal", "No absolutely", "Disliked", "With marja permission",
    0,
    "يجوز شراء الحلال من محل يبيع فيه حراماً.",
    "Buying halal from shop selling haram is permissible.",
    "sistani.org/2494", "sistani.org/2494",
    "beginner", "muamalat,shop,haram"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم بيع السلاح في حال الفتنة؟",
    "What's ruling on selling weapons during turmoil?",
    "جائز", "حرام", "جائز لأهل الحق", "مكروه",
    "Permissible", "Forbidden", "Permissible to people of truth", "Disliked",
    2,
    "يحرم بيع السلاح لأهل البغي والفتنة.",
    "Selling weapons to people of transgression and turmoil is forbidden.",
    "sistani.org/2492", "sistani.org/2492",
    "advanced", "muamalat,weapons,turmoil"));

questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز استئجار الأرض للزراعة المحرمة؟",
    "May land be rented for forbidden cultivation?",
    "نعم", "لا", "للتبغ فقط", "للقطن",
    "Yes", "No", "For tobacco only", "For cotton",
    1,
    "لا يجوز تأجير الأرض لزرع محرم كالتبغ.",
    "Renting land for forbidden cultivation like tobacco is not permissible.",
    "sistani.org/2609", "sistani.org/2609",
    "intermediate", "muamalat,rent,forbidden"));

questionRepository.save(createQuestion(muamalat, sistani,
    "ما حكم المضاربة الشرعية؟",
    "What's ruling on Islamic partnership?",
    "صحيحة", "باطلة", "صحيحة بشروط", "مكروهة",
    "Valid", "Invalid", "Valid with conditions", "Disliked",
    2,
    "المضاربة صحيحة إذا تحققت شروطها الشرعية.",
    "Mudaraba is valid if its Islamic conditions are met.",
    "sistani.org/2632", "sistani.org/2632",
    "advanced", "muamalat,mudaraba,ruling"));

// MORE TALAQ QUESTIONS (15)
questionRepository.save(createQuestion(talaq, sistani,
    "هل يقع الطلاق في حال الغضب الشديد؟",
    "Does divorce occur during extreme anger?",
    "نعم، يقع", "لا يقع", "يقع إن كان واعياً", "على الأحوط يقع",
    "Yes, occurs", "Doesn't occur", "Occurs if conscious", "As precaution occurs",
    1,
    "إذا كان الغضب شديداً بحيث فقد معه السيطرة على نفسه لا يقع الطلاق.",
    "If anger is extreme such that he lost control over himself, divorce doesn't occur.",
    "sistani.org/5479", "sistani.org/5479",
    "advanced", "talaq,anger,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "كم مرة يجوز رجوع المطلقة الرجعية؟",
    "How many times may revocably divorced woman be returned?",
    "مرة واحدة", "مرتين", "ثلاث مرات", "بلا حد",
    "Once", "Twice", "Three times", "No limit",
    2,
    "يجوز للزوج الرجوع إلى مطلقته الرجعية في العدة، وتحل له بعد الطلقة الثالثة بالمحلل.",
    "Husband may return to his revocably divorced wife during waiting period, and she becomes lawful after third divorce with muhallil.",
    "منهاج الصالحين، الطلاق", "Minhaj al-Salihin, Divorce",
    "intermediate", "talaq,return,times"));

questionRepository.save(createQuestion(talaq, sistani,
    "ما حكم طلاق السكران؟",
    "What's ruling on divorce by intoxicated person?",
    "يقع", "لا يقع", "يقع على الأحوط", "يقع إن كان متعمداً",
    "Occurs", "Doesn't occur", "Occurs as precaution", "Occurs if deliberate",
    1,
    "طلاق السكران غير واقع.",
    "Divorce by intoxicated person doesn't occur.",
    "منهاج الصالحين، مسألة 408", "Minhaj al-Salihin, Issue 408",
    "intermediate", "talaq,intoxication,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل يجوز الطلاق بالكتابة؟",
    "Is divorce by writing permissible?",
    "نعم، مطلقاً", "لا، يجب التلفظ", "نعم، للعاجز", "نعم، مع النية",
    "Yes, absolutely", "No, verbal required", "Yes, for unable", "Yes, with intention",
    2,
    "لا يقع الطلاق بالكتابة إلا للأخرس أو العاجز عن النطق.",
    "Divorce doesn't occur by writing except for mute or unable to speak.",
    "منهاج الصالحين، مسألة 411", "Minhaj al-Salihin, Issue 411",
    "advanced", "talaq,writing,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل يجوز للمرأة طلاق نفسها؟",
    "May woman divorce herself?",
    "نعم، مطلقاً", "لا، إلا بتوكيل", "نعم، في الخلع", "لا مطلقاً",
    "Yes, absolutely", "No, except by proxy", "Yes, in khula", "No absolutely",
    2,
    "المرأة لا تملك طلاق نفسها إلا في الخلع إذا وكلها الزوج.",
    "Woman doesn't have power to divorce herself except in khula if husband authorizes her.",
    "منهاج الصالحين، الخلع", "Minhaj al-Salihin, Khula",
    "intermediate", "talaq,woman,self"));

questionRepository.save(createQuestion(talaq, sistani,
    "ما عدة المطلقة اليائسة؟",
    "What's waiting period of menopausal divorced woman?",
    "ثلاثة أشهر", "شهران", "شهر ونصف", "لا عدة عليها",
    "Three months", "Two months", "Month and half", "No waiting period",
    2,
    "عدة المطلقة اليائسة شهران وخمسة أيام على الأحوط.",
    "Waiting period of menopausal divorced woman is two months and five days as precaution.",
    "منهاج الصالحين، مسألة 445", "Minhaj al-Salihin, Issue 445",
    "advanced", "talaq,iddah,menopause"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل يقع الطلاق المعلق؟",
    "Does conditional divorce occur?",
    "نعم", "لا", "يقع إن تحقق الشرط", "على الأحوط لا",
    "Yes", "No", "Occurs if condition met", "As precaution no",
    1,
    "الطلاق المعلق على شرط غير واقع.",
    "Divorce conditional on condition doesn't occur.",
    "منهاج الصالحين، مسألة 410", "Minhaj al-Salihin, Issue 410",
    "advanced", "talaq,conditional,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "ما حكم الطلاق في حال الحيض؟",
    "What's ruling on divorce during menstruation?",
    "يقع", "لا يقع", "يقع بشروط", "على الأحوط لا يقع",
    "Occurs", "Doesn't occur", "Occurs with conditions", "As precaution doesn't occur",
    1,
    "لا يقع الطلاق في حال الحيض إلا في موارد خاصة.",
    "Divorce during menstruation doesn't occur except in special cases.",
    "منهاج الصالحين، مسألة 417", "Minhaj al-Salihin, Issue 417",
    "intermediate", "talaq,menstruation,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل يجوز للمرأة اشتراط عدم التزويج عليها؟",
    "May woman stipulate no marriage upon her?",
    "نعم، ويلزم", "لا يجوز", "يجوز ولا يلزم", "يجوز بإذن الحاكم",
    "Yes, and binding", "Not permissible", "Permissible not binding", "Permissible with authority permission",
    0,
    "يجوز للمرأة أن تشترط في العقد عدم تزويج زوجها عليها، ويلزم الزوج بذلك.",
    "Woman may stipulate in contract that husband not marry upon her, and husband is bound by it.",
    "منهاج الصالحين، الشروط", "Minhaj al-Salihin, Conditions",
    "intermediate", "nikah,stipulation,marriage"));

questionRepository.save(createQuestion(talaq, sistani,
    "ما حكم الخلع للمرأة الكارهة لزوجها؟",
    "What's ruling on khula for woman hating husband?",
    "جائز وتدفع الفدية", "غير جائز", "جائز بلا فدية", "يحتاج إذن الحاكم",
    "Permissible and pays ransom", "Not permissible", "Permissible without ransom", "Needs authority permission",
    0,
    "إذا كرهت المرأة زوجها جاز لها الخلع بأن تفديه بشيء ليطلقها.",
    "If woman hates husband, khula is permissible for her to ransom him with something to divorce her.",
    "منهاج الصالحين، مسألة 452", "Minhaj al-Salihin, Issue 452",
    "intermediate", "talaq,khula,hatred"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل تحتاج المطلقة البائن إلى إذن للزواج؟",
    "Does irrevocably divorced woman need permission to marry?",
    "نعم، من الزوج", "نعم، من الولي", "لا، بعد العدة", "نعم، من القاضي",
    "Yes, from husband", "Yes, from guardian", "No, after waiting period", "Yes, from judge",
    2,
    "المطلقة البائن لا تحتاج إلى إذن أحد للزواج بعد انقضاء عدتها.",
    "Irrevocably divorced woman doesn't need anyone's permission to marry after her waiting period ends.",
    "منهاج الصالحين، الطلاق", "Minhaj al-Salihin, Divorce",
    "beginner", "talaq,remarriage,permission"));

questionRepository.save(createQuestion(talaq, sistani,
    "ما حكم الطلاق بثلاث بصيغة واحدة؟",
    "What's ruling on triple divorce in one formula?",
    "يقع ثلاثاً", "تقع واحدة", "لا يقع", "يقع اثنتان",
    "Three occur", "One occurs", "Doesn't occur", "Two occur",
    1,
    "الطلاق بثلاث في صيغة واحدة لا يقع إلا مرة واحدة.",
    "Triple divorce in one formula only occurs once.",
    "sistani.org/5489", "sistani.org/5489",
    "advanced", "talaq,triple,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل يجوز طلاق الحامل؟",
    "Is divorcing pregnant woman permissible?",
    "نعم، مطلقاً", "لا، حتى تضع", "يجوز بشروط", "يكره",
    "Yes, absolutely", "No, until she gives birth", "Permissible with conditions", "Disliked",
    0,
    "يجوز طلاق الحامل، وعدتها تنقضي بوضع الحمل.",
    "Divorcing pregnant woman is permissible, and her waiting period ends by giving birth.",
    "منهاج الصالحين، مسألة 420", "Minhaj al-Salihin, Issue 420",
    "intermediate", "talaq,pregnancy,ruling"));

questionRepository.save(createQuestion(talaq, sistani,
    "ما حكم رجوع المطلقة الرجعية بعد انقضاء العدة؟",
    "What's ruling on returning revocably divorced after waiting period?",
    "جائز بعقد جديد", "غير جائز", "جائز بدون عقد", "يحتاج محلل",
    "Permissible with new contract", "Not permissible", "Permissible without contract", "Needs muhallil",
    0,
    "بعد انقضاء العدة يحتاج إلى عقد جديد لإرجاعها.",
    "After waiting period ends, new contract is needed to return her.",
    "منهاج الصالحين، الرجعة", "Minhaj al-Salihin, Return",
    "intermediate", "talaq,return,iddah"));

questionRepository.save(createQuestion(talaq, sistani,
    "هل يجوز للمطلق الرجوع قبل انقضاء العدة؟",
    "May divorcer return before waiting period ends?",
    "نعم، بدون عقد", "نعم، بعقد جديد", "لا يجوز", "يجوز بإذن القاضي",
    "Yes, without contract", "Yes, with new contract", "Not permissible", "Permissible with judge permission",
    0,
    "يجوز للزوج الرجوع إلى مطلقته الرجعية في العدة بدون عقد جديد.",
    "Husband may return to his revocably divorced wife during waiting period without new contract.",
    "منهاج الصالحين، مسألة 432", "Minhaj al-Salihin, Issue 432",
    "beginner", "talaq,return,contract"));

// MORE ZAKAT QUESTIONS (15)
questionRepository.save(createQuestion(zakat, sistani,
    "هل تجب زكاة الفطرة على الفقير؟",
    "Is Zakat al-Fitr obligatory on poor person?",
    "نعم", "لا", "إن ملك قوت سنته", "إن ملك قوت يومه",
    "Yes", "No", "If owns year's food", "If owns day's food",
    2,
    "تجب زكاة الفطرة على من ملك قوت سنته له ولعياله.",
    "Zakat al-Fitr is obligatory on who owns year's food for himself and dependents.",
    "sistani.org/1900", "sistani.org/1900",
    "intermediate", "zakat,fitr,poor"));

questionRepository.save(createQuestion(zakat, sistani,
    "متى يجب إخراج زكاة الفطرة؟",
    "When must Zakat al-Fitr be paid?",
    "قبل العيد بيوم", "يوم العيد قبل الصلاة", "بعد صلاة العيد", "في أي وقت من رمضان",
    "Day before Eid", "Eid day before prayer", "After Eid prayer", "Anytime in Ramadan",
    1,
    "يجب إخراج زكاة الفطرة قبل صلاة العيد، ويجوز تقديمها في شهر رمضان.",
    "Zakat al-Fitr must be paid before Eid prayer, and may be advanced during Ramadan.",
    "sistani.org/1904", "sistani.org/1904",
    "beginner", "zakat,fitr,timing"));

questionRepository.save(createQuestion(zakat, sistani,
    "كم مقدار زكاة الفطرة؟",
    "How much is Zakat al-Fitr?",
    "صاع واحد", "نصف صاع", "صاعان", "ثلاثة أصواع",
    "One sa'", "Half sa'", "Two sa'", "Three sa'",
    0,
    "مقدار زكاة الفطرة صاع واحد (ثلاثة كيلوغرامات تقريباً) من الطعام.",
    "Amount of Zakat al-Fitr is one sa' (approximately three kilograms) of food.",
    "sistani.org/1902", "sistani.org/1902",
    "beginner", "zakat,fitr,amount"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل يجوز إعطاء زكاة الفطرة للكافر؟",
    "May Zakat al-Fitr be given to disbeliever?",
    "نعم", "لا", "نعم، للضرورة", "يجوز لأهل الكتاب",
    "Yes", "No", "Yes, for necessity", "Permissible to People of Book",
    1,
    "لا يجوز إعطاء زكاة الفطرة للكافر.",
    "Giving Zakat al-Fitr to disbeliever is not permissible.",
    "sistani.org/1910", "sistani.org/1910",
    "beginner", "zakat,fitr,recipient"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل تجب الزكاة في الذهب المستعمل للزينة؟",
    "Is Zakat due on gold used for adornment?",
    "نعم", "لا", "نعم، إن بلغ النصاب", "على الأحوط",
    "Yes", "No", "Yes, if reaches nisab", "As precaution",
    1,
    "لا تجب الزكاة في الذهب والفضة المستعملين للزينة.",
    "Zakat is not obligatory on gold and silver used for adornment.",
    "sistani.org/1858", "sistani.org/1858",
    "beginner", "zakat,gold,adornment"));

questionRepository.save(createQuestion(zakat, sistani,
    "ما نصاب زكاة الذهب؟",
    "What's nisab of gold Zakat?",
    "15 مثقالاً", "20 مثقالاً", "40 مثقالاً", "85 غراماً",
    "15 mithqals", "20 mithqals", "40 mithqals", "85 grams",
    1,
    "نصاب زكاة الذهب عشرون مثقالاً شرعياً (15 مثقالاً صيرفياً).",
    "Nisab of gold Zakat is twenty legal mithqals (15 exchange mithqals).",
    "sistani.org/1855", "sistani.org/1855",
    "intermediate", "zakat,gold,nisab"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل تجب الزكاة في المال المدخر؟",
    "Is Zakat due on saved money?",
    "نعم، دائماً", "لا", "نعم، إن بلغ النصاب", "لا، الخمس فقط",
    "Yes, always", "No", "Yes, if reaches nisab", "No, only Khums",
    3,
    "المال النقدي المدخر لا تجب فيه الزكاة، وإنما يجب فيه الخمس.",
    "Saved cash doesn't have Zakat, but Khums is obligatory on it.",
    "sistani.org/1853", "sistani.org/1853",
    "intermediate", "zakat,savings,ruling"));

questionRepository.save(createQuestion(zakat, sistani,
    "كم نصاب زكاة الغنم؟",
    "What's nisab of sheep Zakat?",
    "أربعون", "ثلاثون", "عشرون", "خمسون",
    "Forty", "Thirty", "Twenty", "Fifty",
    0,
    "نصاب الغنم أربعون شاة، وفيها شاة واحدة.",
    "Nisab of sheep is forty sheep, and in it is one sheep.",
    "sistani.org/1876", "sistani.org/1876",
    "intermediate", "zakat,sheep,nisab"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل يجوز نقل الزكاة من بلد إلى آخر؟",
    "May Zakat be transferred from one country to another?",
    "نعم، مطلقاً", "لا، إلا للضرورة", "يجوز لذوي القربى", "يكره",
    "Yes, absolutely", "No, except for necessity", "Permissible to relatives", "Disliked",
    1,
    "لا يجوز نقل الزكاة من بلدها إلى بلد آخر إلا مع عدم وجود المستحق فيه.",
    "Transferring Zakat from its country to another is not permissible except if no deserving person in it.",
    "sistani.org/1915", "sistani.org/1915",
    "advanced", "zakat,transfer,country"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل يجوز إعطاء الزكاة للزوجة؟",
    "May Zakat be given to wife?",
    "نعم", "لا", "نعم، للنفقة", "نعم، زكاة الفطرة فقط",
    "Yes", "No", "Yes, for maintenance", "Yes, only Zakat al-Fitr",
    1,
    "لا يجوز للزوج إعطاء زكاته الواجبة لزوجته.",
    "Husband may not give his obligatory Zakat to his wife.",
    "sistani.org/1912", "sistani.org/1912",
    "intermediate", "zakat,wife,ruling"));

questionRepository.save(createQuestion(zakat, sistani,
    "ما مقدار زكاة البقر؟",
    "What's amount of cattle Zakat?",
    "ثلاثون بقرة فيها تبيع", "أربعون فيها مسنة", "كلاهما", "عشرون فيها عجل",
    "Thirty cattle, in it calf", "Forty, in it mature", "Both", "Twenty, in it calf",
    2,
    "نصاب البقر ثلاثون وفيها تبيع أو تبيعة، وأربعون وفيها مسنة.",
    "Nisab of cattle is thirty with calf, and forty with mature cow.",
    "sistani.org/1883", "sistani.org/1883",
    "advanced", "zakat,cattle,nisab"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل تجب زكاة الفطرة عن الجنين؟",
    "Is Zakat al-Fitr obligatory for fetus?",
    "نعم", "لا", "إن كان قد أكمل أربعة أشهر", "مستحبة",
    "Yes", "No", "If completed four months", "Recommended",
    3,
    "يستحب إخراج زكاة الفطرة عن الجنين.",
    "Paying Zakat al-Fitr for fetus is recommended.",
    "sistani.org/1899", "sistani.org/1899",
    "beginner", "zakat,fitr,fetus"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل يجوز إعطاء الزكاة للأخ الفقير؟",
    "May Zakat be given to poor brother?",
    "نعم", "لا", "إن لم تجب نفقته", "يكره",
    "Yes", "No", "If his maintenance not obligatory", "Disliked",
    2,
    "يجوز إعطاء الزكاة للأخ الفقير إذا لم تجب نفقته على المزكي.",
    "Giving Zakat to poor brother is permissible if his maintenance is not obligatory on giver.",
    "sistani.org/1913", "sistani.org/1913",
    "intermediate", "zakat,brother,ruling"));

questionRepository.save(createQuestion(zakat, sistani,
    "ما حكم تأخير إخراج الزكاة؟",
    "What's ruling on delaying Zakat payment?",
    "لا يجوز", "يجوز", "يجوز لمدة شهر", "يجوز مع الضمان",
    "Not permissible", "Permissible", "Permissible for one month", "Permissible with guarantee",
    3,
    "لا يجوز تأخير إخراج الزكاة عن وقت وجوبها إلا مع الضمان.",
    "Delaying Zakat payment from its obligation time is not permissible except with guarantee.",
    "sistani.org/1918", "sistani.org/1918",
    "advanced", "zakat,delay,ruling"));

questionRepository.save(createQuestion(zakat, sistani,
    "هل يجوز دفع قيمة الزكاة نقداً بدل العين؟",
    "May Zakat value be paid in cash instead of in-kind?",
    "نعم", "لا", "يجوز بإذن الحاكم", "على الأحوط لا",
    "Yes", "No", "Permissible with authority permission", "As precaution no",
    0,
    "يجوز دفع قيمة الزكاة نقداً.",
    "Paying Zakat value in cash is permissible.",
    "sistani.org/1917", "sistani.org/1917",
    "beginner", "zakat,cash,ruling"));

// MORE AMR BIL MAROOF QUESTIONS (10)
questionRepository.save(createQuestion(amr, sistani,
    "هل يجب الأمر بالمعروف على الصبي؟",
    "Is enjoining good obligatory on child?",
    "نعم", "لا، حتى يبلغ", "يستحب", "واجب بعد التمييز",
    "Yes", "No, until maturity", "Recommended", "Obligatory after discernment",
    1,
    "لا يجب الأمر بالمعروف والنهي عن المنكر على الصبي.",
    "Enjoining good and forbidding evil are not obligatory on child.",
    "sistani.org/2029", "sistani.org/2029",
    "beginner", "amr,child,obligation"));

questionRepository.save(createQuestion(amr, sistani,
    "ما شروط وجوب الأمر بالمعروف؟",
    "What are conditions for enjoining good being obligatory?",
    "العلم بالمنكر", "احتمال التأثير", "عدم الضرر", "جميع ما ذكر",
    "Knowledge of wrong", "Probability of effect", "No harm", "All mentioned",
    3,
    "من شروط وجوب الأمر بالمعروف: العلم بالمنكر، واحتمال التأثير، وعدم الضرر.",
    "Conditions for enjoining good being obligatory include: knowledge of wrong, probability of effect, and no harm.",
    "sistani.org/2027", "sistani.org/2027",
    "intermediate", "amr,conditions,ruling"));

questionRepository.save(createQuestion(amr, sistani,
    "هل يجب الأمر بالمعروف إذا خاف الضرر على نفسه؟",
    "Is enjoining good obligatory if fearing harm to oneself?",
    "نعم", "لا", "يجب إن كان الضرر قليلاً", "على الأحوط نعم",
    "Yes", "No", "Obligatory if harm is little", "As precaution yes",
    1,
    "إذا خاف ضرراً على نفسه أو ماله سقط وجوب الأمر بالمعروف.",
    "If fearing harm to oneself or property, obligation of enjoining good drops.",
    "sistani.org/2028", "sistani.org/2028",
    "intermediate", "amr,harm,exemption"));

questionRepository.save(createQuestion(amr, sistani,
    "ما مراتب الأمر بالمعروف؟",
    "What are levels of enjoining good?",
    "الإنكار القلبي فقط", "القول، ثم الفعل", "القول فقط", "لا مراتب له",
    "Only heart denial", "Speech, then action", "Only speech", "No levels",
    1,
    "مراتب الأمر بالمعروف: الإنكار القلبي، ثم باللسان، ثم باليد إن أمكن.",
    "Levels of enjoining good: heart denial, then by tongue, then by hand if possible.",
    "sistani.org/2030", "sistani.org/2030",
    "intermediate", "amr,levels,ruling"));

questionRepository.save(createQuestion(amr, sistani,
    "هل يجب الأمر بالمعروف في المستحبات؟",
    "Is enjoining good obligatory in recommendeds?",
    "نعم، دائماً", "لا، في الواجبات فقط", "يستحب", "في بعض المستحبات",
    "Yes, always", "No, only in obligatories", "Recommended", "In some recommendeds",
    1,
    "الأمر بالمعروف واجب في الواجبات، ويستحب في المستحبات المؤكدة.",
    "Enjoining good is obligatory in obligatories, and recommended in emphasized recommendeds.",
    "sistani.org/2024", "sistani.org/2024",
    "advanced", "amr,recommendeds,ruling"));


        questionRepository.save(createQuestion(taharah, sistani,
            "هل يجب الغسل بعد الجماع الذي لم يحصل فيه إنزال؟",
            "Is ghusl required after intercourse without ejaculation?",
            "لا يجب", "نعم، يجب مطلقاً", "يجب على الرجل فقط", "يجب على المرأة فقط",
            "Not required", "Yes, absolutely required", "Required for man only", "Required for woman only",
            1,
            "يجب الغسل من الجنابة بمجرد دخول الحشفة في القُبُل أو الدُبُر، ولو لم يحصل إنزال.",
            "Ghusl from janaba is required upon penetration in the front or back passage, even without ejaculation.",
            "أحكام الجنابة", "Janaba Rulings",
            "intermediate", "taharah,ghusl,intercourse"));

        questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الصلاة بالنعال؟",
    "Is praying with sandals permissible?",
    "نعم، إن كانت طاهرة", "لا، مطلقاً", "يكره", "في النافلة فقط",
    "Yes, if pure", "No, absolutely", "Disliked", "Only in voluntary prayer",
    0,
    "يجوز الصلاة بالنعال والحذاء إذا كان طاهراً، بل يستحب أحياناً.",
    "Praying with sandals and shoes is permissible if pure, and even recommended sometimes.",
    "sistani.org/5298", "sistani.org/5298",
    "beginner", "salat,shoes,permissibility"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من نسي الركوع حتى سجد؟",
    "What's ruling for who forgot rukoo until prostrating?",
    "يعيد الصلاة", "يرجع للركوع", "يستمر ويقضي", "يسجد سجدتي السهو",
    "Repeat prayer", "Return to rukoo", "Continue and make up", "Do sajda al-sahw",
    0,
    "من نسي الركوع حتى سجد بطلت صلاته ويجب إعادتها.",
    "Whoever forgot rukoo until prostrating, his prayer is invalid and must be repeated.",
    "sistani.org/5200", "sistani.org/5200",
    "intermediate", "salat,rukoo,forgetfulness"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب الجلوس بين السجدتين؟",
    "Is sitting between two sajdahs required?",
    "نعم، يجب", "لا، مستحب", "يجب في الفريضة", "على الأحوط",
    "Yes, required", "No, recommended", "Required in obligatory", "As precaution",
    0,
    "يجب الجلوس بين السجدتين بمقدار الذكر الواجب.",
    "Sitting between two sajdahs is required for the time of obligatory dhikr.",
    "sistani.org/5185", "sistani.org/5185",
    "beginner", "salat,sitting,sajdah"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الصلاة في الطائرة؟",
    "What's ruling on praying in airplane?",
    "صحيحة قاعداً باتجاه القبلة", "باطلة", "قضاء فقط", "مع الإيماء",
    "Valid sitting toward qibla", "Invalid", "Make up only", "With gestures",
    0,
    "تجوز الصلاة في الطائرة جالساً باتجاه القبلة ما أمكن، ومع الإيماء للركوع والسجود.",
    "Praying in airplane sitting toward qibla as possible is permissible, with gestures for rukoo and sajdah.",
    "sistani.org/5301", "sistani.org/5301",
    "intermediate", "salat,airplane,travel"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز الصلاة في المسجد الذي فيه موسيقى؟",
    "Is praying in mosque with music permissible?",
    "نعم، إن لم يشغله", "لا، مطلقاً", "مكروه", "يجب الإنكار",
    "Yes, if not distracted", "No, absolutely", "Disliked", "Must object",
    2,
    "الصلاة في مكان فيه موسيقى مكروهة، وإن أمكن الإنكار وجب.",
    "Praying in place with music is disliked, and if objection possible, it's required.",
    "sistani.org/5254", "sistani.org/5254",
    "advanced", "salat,music,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم قراءة دعاء القنوت؟",
    "What's ruling on reciting qunut supplication?",
    "واجب", "مستحب مؤكد", "مستحب", "مباح",
    "Obligatory", "Highly recommended", "Recommended", "Permissible",
    1,
    "قراءة القنوت في الصلاة مستحبة استحباباً مؤكداً.",
    "Reciting qunut in prayer is highly recommended.",
    "sistani.org/5173", "sistani.org/5173",
    "beginner", "salat,qunut,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل تبطل الصلاة بالأكل والشرب سهواً؟",
    "Does prayer invalidate by eating/drinking forgetfully?",
    "نعم، تبطل", "لا، تصح", "على الأحوط تبطل", "يسجد سجدتي السهو",
    "Yes, invalidates", "No, valid", "As precaution invalidates", "Do sajda al-sahw",
    0,
    "الأكل والشرب في الصلاة يبطلها ولو كان سهواً.",
    "Eating and drinking in prayer invalidates it even if forgetfully.",
    "sistani.org/5242", "sistani.org/5242",
    "intermediate", "salat,eating,invalidator"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم صلاة الاستسقاء؟",
    "What's ruling on rain prayer?",
    "واجبة", "مستحبة", "بدعة", "جائزة",
    "Obligatory", "Recommended", "Innovation", "Permissible",
    1,
    "صلاة الاستسقاء مستحبة عند الحاجة للمطر.",
    "Rain prayer is recommended when needing rain.",
    "sistani.org/2134", "sistani.org/2134",
    "beginner", "salat,istisqa,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجب رفع اليدين عند الركوع؟",
    "Is raising hands at rukoo required?",
    "نعم، واجب", "لا، مستحب", "واجب على الأحوط", "مكروه",
    "Yes, obligatory", "No, recommended", "Obligatory as precaution", "Disliked",
    1,
    "رفع اليدين عند الركوع مستحب وليس واجباً.",
    "Raising hands at rukoo is recommended not obligatory.",
    "sistani.org/5177", "sistani.org/5177",
    "beginner", "salat,hands,rukoo"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الصلاة بثوب عليه صورة؟",
    "What's ruling on praying in clothing with images?",
    "باطلة", "صحيحة", "مكروهة", "باطلة إن كانت ذات روح",
    "Invalid", "Valid", "Disliked", "Invalid if of living being",
    2,
    "الصلاة بثوب عليه صورة مكروهة، خصوصاً إن كانت صورة إنسان أو حيوان.",
    "Praying in clothing with images is disliked, especially if images of humans or animals.",
    "sistani.org/5311", "sistani.org/5311",
    "intermediate", "salat,images,clothing"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز صلاة النافلة ماشياً؟",
    "Is voluntary prayer while walking permissible?",
    "نعم، مطلقاً", "نعم، للضرورة", "لا، مطلقاً", "في السفر فقط",
    "Yes, absolutely", "Yes, for necessity", "No, absolutely", "Only in travel",
    0,
    "يجوز صلاة النافلة ماشياً، ويومئ للركوع والسجود.",
    "Voluntary prayer while walking is permissible, with gestures for rukoo and sajdah.",
    "sistani.org/5231", "sistani.org/5231",
    "intermediate", "salat,walking,voluntary"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من شك بين الواحدة والاثنتين في صلاة الظهر؟",
    "What's ruling for doubt between one and two in Dhuhr prayer?",
    "يبني على الأكثر", "يبني على الأقل", "يعيد الصلاة", "يستمر",
    "Assume more", "Assume less", "Repeat prayer", "Continue",
    2,
    "الشك بين الواحدة والاثنتين مبطل للصلاة.",
    "Doubt between one and two invalidates prayer.",
    "sistani.org/5217", "sistani.org/5217",
    "intermediate", "salat,doubt,invalidating"));

questionRepository.save(createQuestion(salat, sistani,
    "هل تجب صلاة العيدين في زمن الغيبة؟",
    "Are Eid prayers obligatory during occultation?",
    "واجبة عيناً", "واجبة تخييراً", "مستحبة", "غير مشروعة",
    "Individually obligatory", "Optionally obligatory", "Recommended", "Not legislated",
    2,
    "صلاة العيدين في زمن الغيبة مستحبة استحباباً مؤكداً.",
    "Eid prayers during occultation are highly recommended.",
    "sistani.org/2117", "sistani.org/2117",
    "intermediate", "salat,eid,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الصلاة في الثوب الحرير للرجل؟",
    "What's ruling on man praying in silk clothing?",
    "باطلة", "صحيحة مع الإثم", "مكروهة", "صحيحة إن اضطر",
    "Invalid", "Valid with sin", "Disliked", "Valid if forced",
    3,
    "صلاة الرجل في ثوب الحرير باطلة إلا عند الاضطرار.",
    "Man's prayer in silk clothing is invalid except when forced.",
    "sistani.org/5313", "sistani.org/5313",
    "intermediate", "salat,silk,men"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز قطع النافلة؟",
    "Is interrupting voluntary prayer permissible?",
    "نعم، مطلقاً", "لا، مطلقاً", "يكره", "للضرورة",
    "Yes, absolutely", "No, absolutely", "Disliked", "For necessity",
    2,
    "يجوز قطع النافلة وإن كان الأولى إتمامها.",
    "Interrupting voluntary prayer is permissible though completing is better.",
    "sistani.org/5244", "sistani.org/5244",
    "beginner", "salat,interruption,voluntary"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم صلاة الغفيلة؟",
    "What's ruling on Ghafilah prayer?",
    "واجبة", "مستحبة", "بدعة", "مكروهة",
    "Obligatory", "Recommended", "Innovation", "Disliked",
    1,
    "صلاة الغفيلة من النوافل المستحبة، تُصلى بين المغرب والعشاء.",
    "Ghafilah prayer is among recommended voluntary prayers, prayed between Maghrib and Isha.",
    "sistani.org/2106", "sistani.org/2106",
    "beginner", "salat,ghafilah,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل تصح الصلاة خلف حائط الكعبة؟",
    "Is prayer valid behind wall of Kaaba?",
    "نعم", "لا", "على الأحوط لا", "في الطواف فقط",
    "Yes", "No", "As precaution no", "Only in tawaf",
    0,
    "تصح الصلاة خلف حائط الكعبة.",
    "Prayer behind wall of Kaaba is valid.",
    "sistani.org/5257", "sistani.org/5257",
    "advanced", "salat,kaaba,direction"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم الصلاة بالساعة؟",
    "What's ruling on praying with watch?",
    "صحيحة", "باطلة", "مكروهة", "باطلة إن كانت ذهباً",
    "Valid", "Invalid", "Disliked", "Invalid if gold",
    3,
    "الصلاة بالساعة جائزة إلا إن كانت من ذهب للرجل.",
    "Praying with watch is permissible except if gold for man.",
    "sistani.org/5314", "sistani.org/5314",
    "beginner", "salat,watch,ruling"));

questionRepository.save(createQuestion(salat, sistani,
    "هل يجوز التسبيح بالأصابع في الصلاة؟",
    "Is counting tasbih with fingers permissible in prayer?",
    "نعم", "لا", "يكره", "في النافلة فقط",
    "Yes", "No", "Disliked", "Only in voluntary",
    0,
    "يجوز التسبيح بالأصابع في الصلاة، بل هو مستحب.",
    "Counting tasbih with fingers in prayer is permissible, even recommended.",
    "sistani.org/5186", "sistani.org/5186",
    "beginner", "salat,tasbih,fingers"));

questionRepository.save(createQuestion(salat, sistani,
    "ما حكم من صلى بدون سورة ناسياً؟",
    "What's ruling for who prayed without surah forgetfully?",
    "صلاته باطلة", "صلاته صحيحة", "يسجد سجدتي السهو", "يقضي السورة",
    "Prayer invalid", "Prayer valid", "Do sajda al-sahw", "Make up surah",
    1,
    "من نسي السورة في الصلاة فصلاته صحيحة.",
    "Who forgot surah in prayer, his prayer is valid.",
    "sistani.org/5158", "sistani.org/5158",
    "beginner", "salat,surah,forgetfulness"));

// MORE TAHARAH QUESTIONS (15 questions: 201-215)
questionRepository.save(createQuestion(taharah, sistani,
    "هل يطهر الثوب بغسله في المطر؟",
    "Does clothing purify by washing in rain?",
    "نعم", "لا", "يحتاج عصراً", "بثلاث غسلات",
    "Yes", "No", "Needs wringing", "With three washes",
    0,
    "يطهر الثوب المتنجس بالمطر إذا أصابه ماء المطر.",
    "Impure clothing purifies with rain if rainwater reaches it.",
    "sistani.org/115", "sistani.org/115",
    "beginner", "taharah,rain,purification"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم غسل الجنابة بالماء البارد شتاءً؟",
    "What's ruling on ghusl janaba with cold water in winter?",
    "واجب", "يتيمم إن خاف الضرر", "يسخن الماء", "يؤخر الغسل",
    "Obligatory", "Do tayammum if fearing harm", "Heat water", "Delay ghusl",
    1,
    "إذا خاف من الضرر باستعمال الماء البارد يتيمم.",
    "If fearing harm from using cold water, do tayammum.",
    "sistani.org/359", "sistani.org/359",
    "intermediate", "taharah,ghusl,cold"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب الوضوء لمس المصحف؟",
    "Is wudu required for touching Quran?",
    "نعم، للمس الكتابة", "لا", "مستحب", "لمس الجلد فقط",
    "Yes, for touching writing", "No", "Recommended", "Only for touching cover",
    0,
    "يحرم على المحدث مس كتابة القرآن، فيجب الوضوء لمسه.",
    "Touching Quran writing is forbidden for one without wudu, so wudu required.",
    "sistani.org/387", "sistani.org/387",
    "beginner", "taharah,wudu,quran"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الماء المستعمل في رفع الحدث؟",
    "What's ruling on water used in removing hadath?",
    "نجس", "مكروه", "طاهر", "طاهر غير مطهر",
    "Impure", "Disliked", "Pure", "Pure but not purifying",
    2,
    "الماء المستعمل في الوضوء والغسل طاهر.",
    "Water used in wudu and ghusl is pure.",
    "sistani.org/85", "sistani.org/85",
    "intermediate", "taharah,water,used"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجوز الوضوء من ماء زمزم؟",
    "Is wudu with zamzam water permissible?",
    "نعم", "لا، يكره", "لا، حرام", "نعم، مع الكراهة",
    "Yes", "No, disliked", "No, forbidden", "Yes, with dislike",
    0,
    "يجوز الوضوء من ماء زمزم ولا كراهة فيه.",
    "Wudu with zamzam water is permissible without dislike.",
    "sistani.org/67", "sistani.org/67",
    "beginner", "taharah,wudu,zamzam"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الوضوء بالماء المشمس؟",
    "What's ruling on wudu with sun-heated water?",
    "جائز", "مكروه", "حرام", "باطل",
    "Permissible", "Disliked", "Forbidden", "Invalid",
    1,
    "يكره الوضوء بالماء المشمس في الأواني المعدنية.",
    "Wudu with sun-heated water in metal containers is disliked.",
    "sistani.org/68", "sistani.org/68",
    "intermediate", "taharah,wudu,sunheated"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل تجب المضمضة والاستنشاق في الوضوء؟",
    "Are rinsing mouth and nose required in wudu?",
    "نعم، واجبة", "لا، مستحبة", "واجبة في الغسل", "على الأحوط",
    "Yes, required", "No, recommended", "Required in ghusl", "As precaution",
    1,
    "المضمضة والاستنشاق مستحبة في الوضوء وليست واجبة.",
    "Rinsing mouth and nose are recommended in wudu not required.",
    "sistani.org/296", "sistani.org/296",
    "beginner", "taharah,wudu,rinsing"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الوضوء تحت الدش؟",
    "What's ruling on wudu under shower?",
    "صحيح", "باطل", "صحيح بالترتيب", "مكروه",
    "Valid", "Invalid", "Valid with sequence", "Disliked",
    2,
    "يصح الوضوء تحت الدش إذا راعى الترتيب والموالاة.",
    "Wudu under shower is valid if sequence and continuity observed.",
    "sistani.org/293", "sistani.org/293",
    "beginner", "taharah,wudu,shower"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب غسل الشعر في غسل الحيض؟",
    "Is washing hair required in menstruation ghusl?",
    "نعم، يجب", "لا، إيصال الماء للبشرة", "يكفي المسح", "على الأحوط",
    "Yes, required", "No, reaching water to skin", "Wiping suffices", "As precaution",
    1,
    "لا يجب غسل الشعر، بل يجب إيصال الماء إلى البشرة تحته.",
    "Washing hair not required, but reaching water to skin under it required.",
    "sistani.org/467", "sistani.org/467",
    "intermediate", "taharah,ghusl,hair"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم دم الأنف؟",
    "What's ruling on nose blood?",
    "نجس", "طاهر", "نجس إن كثر", "طاهر إن قل",
    "Impure", "Pure", "Impure if excessive", "Pure if little",
    0,
    "دم الأنف نجس كسائر دماء الإنسان.",
    "Nose blood is impure like other human bloods.",
    "sistani.org/88", "sistani.org/88",
    "beginner", "taharah,blood,nose"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب تطهير الفم بعد التقيؤ؟",
    "Is purifying mouth required after vomiting?",
    "نعم", "لا", "للصلاة فقط", "مستحب",
    "Yes", "No", "Only for prayer", "Recommended",
    1,
    "لا يجب تطهير الفم بعد التقيؤ.",
    "Purifying mouth after vomiting is not required.",
    "sistani.org/96", "sistani.org/96",
    "beginner", "taharah,vomit,mouth"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم العرق من الحيوان النجس؟",
    "What's ruling on sweat from impure animal?",
    "نجس", "طاهر", "مكروه", "طاهر إن جف",
    "Impure", "Pure", "Disliked", "Pure if dried",
    1,
    "عرق الحيوان النجس طاهر.",
    "Sweat of impure animal is pure.",
    "sistani.org/94", "sistani.org/94",
    "advanced", "taharah,sweat,animal"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل يجب التيمم للصلاة في القطار؟",
    "Is tayammum required for prayer on train?",
    "نعم", "لا، يصلي بدون وضوء", "لا، إن أمكن الوضوء", "في السفر فقط",
    "Yes", "No, pray without wudu", "No, if wudu possible", "Only in travel",
    2,
    "لا يجب التيمم إن أمكن الوضوء ولو في محطة القطار.",
    "Tayammum not required if wudu possible even at train station.",
    "sistani.org/375", "sistani.org/375",
    "intermediate", "taharah,tayammum,train"));

questionRepository.save(createQuestion(taharah, sistani,
    "ما حكم الغسل الارتماسي في البحر؟",
    "What's ruling on immersion ghusl in sea?",
    "صحيح", "باطل", "صحيح بالنية", "مكروه",
    "Valid", "Invalid", "Valid with intention", "Disliked",
    2,
    "يصح الغسل الارتماسي في البحر مع النية.",
    "Immersion ghusl in sea is valid with intention.",
    "sistani.org/345", "sistani.org/345",
    "beginner", "taharah,ghusl,sea"));

questionRepository.save(createQuestion(taharah, sistani,
    "هل ينقض الوضوء بالقهقهة في الصلاة؟",
    "Does wudu break by loud laughter in prayer?",
    "نعم", "لا، لكن تبطل الصلاة", "نعم، على الأحوط", "لا شيء",
    "Yes", "No, but prayer invalid", "Yes, as precaution", "Nothing",
    1,
    "القهقهة في الصلاة تبطل الصلاة ولا تنقض الوضوء.",
    "Loud laughter in prayer invalidates prayer but doesn't break wudu.",
    "sistani.org/308", "sistani.org/308",
    "intermediate", "taharah,wudu,laughter"));

// MORE SAWM QUESTIONS (15 questions: 216-230)
questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز صوم الوصال؟",
    "Is continuous fasting (wisal) permissible?",
    "نعم", "لا، حرام", "مكروه", "جائز ليومين",
    "Yes", "No, forbidden", "Disliked", "Permissible for two days",
    1,
    "صوم الوصال حرام، وهو أن يصوم يومين متواليين بدون إفطار بينهما.",
    "Continuous fasting is forbidden, which is fasting two consecutive days without breaking fast between.",
    "sistani.org/1745", "sistani.org/1745",
    "intermediate", "sawm,wisal,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم الصمت؟",
    "What's ruling on silence fasting?",
    "واجب", "مستحب", "بدعة", "مكروه",
    "Obligatory", "Recommended", "Innovation", "Disliked",
    2,
    "صوم الصمت بدعة، والإسلام لا يعرف إلا صوم الإمساك عن المفطرات.",
    "Silence fasting is innovation; Islam only recognizes fasting from invalidators.",
    "sistani.org/1547", "sistani.org/1547",
    "intermediate", "sawm,silence,innovation"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز صوم يوم الشك بنية رمضان؟",
    "Is fasting day of doubt with Ramadan intention permissible?",
    "نعم", "لا", "نعم، على الأحوط", "بنية الندب",
    "Yes", "No", "Yes, as precaution", "With voluntary intention",
    1,
    "لا يجوز صوم يوم الشك بنية رمضان، بل بنية شعبان أو الندب.",
    "Fasting day of doubt with Ramadan intention not permissible, but with Sha'ban or voluntary intention.",
    "sistani.org/1685", "sistani.org/1685",
    "intermediate", "sawm,doubt,intention"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم من أفطر ظاناً غروب الشمس؟",
    "What's ruling for who broke fast thinking sun had set?",
    "قضاء وكفارة", "قضاء فقط", "لا شيء", "كفارة فقط",
    "Qadha and kaffarah", "Only qadha", "Nothing", "Only kaffarah",
    1,
    "من أفطر ظاناً غروب الشمس ثم تبين عدمه وجب عليه القضاء دون الكفارة.",
    "Who broke fast thinking sun set then appeared it hadn't, qadha required without kaffarah.",
    "sistani.org/1598", "sistani.org/1598",
    "intermediate", "sawm,sunset,mistake"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب الإمساك على من أصبح جنباً في رمضان؟",
    "Must one who woke junub in Ramadan abstain?",
    "نعم، ويغتسل", "لا، يفطر", "يغتسل فوراً", "يتيمم",
    "Yes, and do ghusl", "No, breaks fast", "Do ghusl immediately", "Do tayammum",
    0,
    "من أصبح جنباً في رمضان يجب عليه الإمساك والاغتسال، وصومه صحيح.",
    "Who woke junub in Ramadan must abstain and do ghusl, and his fast is valid.",
    "sistani.org/1564", "sistani.org/1564",
    "beginner", "sawm,janaba,morning"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم التطوع لمن عليه قضاء واجب؟",
    "What's ruling on voluntary fasting for who has obligatory qadha?",
    "لا يجوز", "يجوز", "مكروه", "يجوز بعد رمضان",
    "Not permissible", "Permissible", "Disliked", "Permissible after Ramadan",
    2,
    "يجوز صوم التطوع لمن عليه قضاء، لكن الأولى البدء بالقضاء.",
    "Voluntary fasting for who has qadha is permissible, but starting with qadha is better.",
    "sistani.org/1721", "sistani.org/1721",
    "intermediate", "sawm,voluntary,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يبطل الصوم بالرعاف؟",
    "Does fast invalidate by nosebleed?",
    "نعم", "لا", "إن ابتلعه", "على الأحوط",
    "Yes", "No", "If swallowed", "As precaution",
    2,
    "الرعاف لا يبطل الصوم إلا إذا ابتلع الدم عمداً.",
    "Nosebleed doesn't invalidate fast unless blood deliberately swallowed.",
    "sistani.org/1559", "sistani.org/1559",
    "intermediate", "sawm,nosebleed,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم الحقنة المغذية للصائم؟",
    "What's ruling on nutritional injection for fasting person?",
    "تبطل الصوم", "لا تبطل", "على الأحوط تبطل", "للضرورة",
    "Invalidates fast", "Doesn't invalidate", "As precaution invalidates", "For necessity",
    1,
    "الحقنة المغذية لا تبطل الصوم.",
    "Nutritional injection doesn't invalidate fast.",
    "sistani.org/1573", "sistani.org/1573",
    "advanced", "sawm,injection,nutritional"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب على المسافر الإمساك بقية النهار إذا نوى الإقامة؟",
    "Must traveler abstain rest of day if intends residence?",
    "نعم", "لا", "يستحب", "إن كان قبل الزوال",
    "Yes", "No", "Recommended", "If before noon",
    1,
    "لا يجب الإمساك بقية النهار، لكن يستحب.",
    "Abstaining rest of day not required, but recommended.",
    "sistani.org/1660", "sistani.org/1660",
    "intermediate", "sawm,travel,residence"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم قطرة الأذن في نهار رمضان؟",
    "What's ruling on ear drops during Ramadan?",
    "تبطل", "لا تبطل", "على الأحوط تبطل", "إن وصلت للحلق",
    "Invalidates", "Doesn't invalidate", "As precaution invalidates", "If reaches throat",
    1,
    "قطرة الأذن لا تبطل الصوم.",
    "Ear drops don't invalidate fast.",
    "sistani.org/1574", "sistani.org/1574",
    "beginner", "sawm,eardrops,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجوز الافتتاح بصيام النذر قبل القضاء؟",
    "Is beginning vowed fasting before qadha permissible?",
    "نعم", "لا، القضاء أولى", "نعم، إن كان معيناً", "لا مطلقاً",
    "Yes", "No, qadha has priority", "Yes, if specific", "No absolutely",
    2,
    "يجوز صوم النذر المعين قبل القضاء.",
    "Specific vowed fasting before qadha is permissible.",
    "sistani.org/1747", "sistani.org/1747",
    "advanced", "sawm,vow,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم من نوى الإفطار ثم عدل؟",
    "What's ruling for who intended breaking then changed?",
    "بطل صومه", "صومه صحيح", "على الأحوط بطل", "يجدد النية",
    "Fast invalid", "Fast valid", "As precaution invalid", "Renew intention",
    1,
    "مجرد نية الإفطار لا يبطل الصوم ما لم يفعل مفطراً.",
    "Mere intention to break doesn't invalidate fast unless doing invalidator.",
    "sistani.org/1546", "sistani.org/1546",
    "intermediate", "sawm,intention,changing"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يجب قضاء صوم الحائض فوراً؟",
    "Must menstruating woman make up fast immediately?",
    "نعم، فوراً", "لا، قبل رمضان القادم", "خلال شهر", "لا يجب",
    "Yes, immediately", "No, before next Ramadan", "Within month", "Not required",
    1,
    "لا يجب القضاء فوراً، بل قبل رمضان القادم.",
    "Making up immediately not required, but before next Ramadan.",
    "sistani.org/1703", "sistani.org/1703",
    "beginner", "sawm,menstruation,qadha"));

questionRepository.save(createQuestion(sawm, sistani,
    "ما حكم صوم يوم عرفة لغير الحاج؟",
    "What's ruling on fasting Arafat day for non-pilgrim?",
    "واجب", "مستحب", "مكروه", "حرام",
    "Obligatory", "Recommended", "Disliked", "Forbidden",
    1,
    "صوم يوم عرفة مستحب لغير الحاج.",
    "Fasting Arafat day is recommended for non-pilgrim.",
    "sistani.org/1727", "sistani.org/1727",
    "beginner", "sawm,arafat,ruling"));

questionRepository.save(createQuestion(sawm, sistani,
    "هل يبطل الصوم بالغيبة؟",
    "Does fast invalidate by backbiting?",
    "نعم، يبطل", "لا، لكنها حرام", "على الأحوط", "ينقص الأجر",
    "Yes, invalidates", "No, but it's forbidden", "As precaution", "Reduces reward",
    1,
    "الغيبة لا تبطل الصوم، لكنها حرام وتنقص أجر الصوم.",
    "Backbiting doesn't invalidate fast, but it's forbidden and reduces fast's reward.",
    "sistani.org/1547", "sistani.org/1547",
    "intermediate", "sawm,backbiting,ruling"));

// Continue with MORE questions from other categories...
// I'll add a few more to reach 100+

// MORE KHUMS QUESTIONS (10 questions: 231-240)
questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الأرض التي اشتريتها للسكن؟",
    "Is Khums due on land bought for residence?",
    "نعم، فوراً", "لا، إن اشتريت من أرباح السنة", "يجب بعد البناء", "لا يجب مطلقاً",
    "Yes, immediately", "No, if bought from year's profit", "Due after building", "Not due at all",
    1,
    "الأرض المشتراة للسكن من أرباح السنة لا خمس فيها.",
    "Land bought for residence from year's profit has no Khums.",
    "sistani.org/1771", "sistani.org/1771",
    "intermediate", "khums,land,residence"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في المال المجهول المالك؟",
    "What's ruling on Khums in money of unknown owner?",
    "يجب الخمس", "لا يجب", "يتصدق به", "يجب ردّه",
    "Khums required", "Not required", "Give as charity", "Must return",
    2,
    "المال المجهول المالك يتصدق به، ولا يجب فيه الخمس.",
    "Money of unknown owner is given as charity, no Khums on it.",
    "sistani.org/1778", "sistani.org/1778",
    "advanced", "khums,unknown,owner"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في البضاعة المشتراة للتجارة؟",
    "Is Khums due on merchandise bought for trade?",
    "نعم، في قيمتها", "لا، في الربح فقط", "بعد البيع", "لا يجب",
    "Yes, on its value", "No, only on profit", "After sale", "Not due",
    1,
    "لا يجب الخمس في البضاعة، بل في الربح منها.",
    "Khums not due on merchandise, but on profit from it.",
    "sistani.org/1772", "sistani.org/1772",
    "intermediate", "khums,merchandise,trade"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في المبالغ المقترضة؟",
    "What's ruling on Khums in borrowed amounts?",
    "يجب", "لا يجب", "يجب عند السداد", "على الدائن",
    "Required", "Not required", "Due upon repayment", "On creditor",
    1,
    "المبالغ المقترضة لا خمس فيها.",
    "Borrowed amounts have no Khums.",
    "sistani.org/1773", "sistani.org/1773",
    "beginner", "khums,loan,borrowed"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الهدية من الوالد؟",
    "Is Khums due on gift from father?",
    "نعم", "لا", "إن زادت عن المؤونة", "بعد سنة",
    "Yes", "No", "If exceeds expenses", "After year",
    1,
    "الهدية من الوالد لا خمس فيها.",
    "Gift from father has no Khums.",
    "sistani.org/1767", "sistani.org/1767",
    "beginner", "khums,gift,father"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الراتب الشهري الباقي؟",
    "What's ruling on Khums in remaining monthly salary?",
    "يجب فوراً", "يجب بعد سنة", "لا يجب", "يجب ما زاد",
    "Due immediately", "Due after year", "Not due", "Due for excess",
    1,
    "ما بقي من الراتب بعد مرور السنة يجب فيه الخمس.",
    "What remains of salary after year passes, Khums is due on it.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,salary,remaining"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في ثمن بيع البيت؟",
    "Is Khums due on house sale price?",
    "نعم، كاملاً", "لا، إن كان للسكن", "في الزائد عن الشراء", "لا يجب",
    "Yes, completely", "No, if for residence", "On excess over purchase", "Not due",
    2,
    "إذا بيع البيت السكني، يجب الخمس في الزائد عن ثمن شرائه.",
    "If residential house sold, Khums due on excess over purchase price.",
    "sistani.org/1775", "sistani.org/1775",
    "advanced", "khums,house,sale"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في المكافأة الحكومية؟",
    "What's ruling on Khums in government bonus?",
    "يجب فوراً", "يجب ما زاد عن المؤونة", "لا يجب", "يجب نصفها",
    "Due immediately", "Due for what exceeds expenses", "Not due", "Half due",
    1,
    "المكافأة الحكومية يجب فيها الخمس بالنسبة لما يزيد عن المؤونة.",
    "Government bonus has Khums regarding what exceeds expenses.",
    "sistani.org/1769", "sistani.org/1769",
    "intermediate", "khums,bonus,government"));

questionRepository.save(createQuestion(khums, sistani,
    "هل يجب الخمس في الأدوات الدراسية للجامعة؟",
    "Is Khums due on university study tools?",
    "نعم", "لا، من المؤونة", "بعد التخرج", "في الزائد",
    "Yes", "No, from expenses", "After graduation", "On excess",
    1,
    "الأدوات الدراسية للجامعة من مؤونة السنة فلا خمس فيها.",
    "University study tools are from year's expenses so no Khums on them.",
    "sistani.org/1770", "sistani.org/1770",
    "beginner", "khums,university,tools"));

questionRepository.save(createQuestion(khums, sistani,
    "ما حكم الخمس في الأموال المدخرة للحج المستحب؟",
    "What's ruling on Khums in money saved for recommended Hajj?",
    "لا يجب", "يجب", "يجب بعد ثلاث سنين", "يجب ما زاد",
    "Not due", "Due", "Due after three years", "Excess due",
    1,
    "المال المدخر للحج المستحب يجب فيه الخمس.",
    "Money saved for recommended Hajj has Khums on it.",
    "sistani.org/1770", "sistani.org/1770",
    "intermediate", "khums,hajj,recommended"));

// MORE MUAMALAT QUESTIONS (10 questions: 241-250)
questionRepository.save(createQuestion(muamalat, sistani,
    "هل يجوز بيع الدين لغير من عليه الدين؟",
    "Is selling debt to other than debtor permissible?",
    "نعم", "لا", "بأقل من قيمته", "على الأحوط لا",
    "Yes", "No", "For less than value", "As precaution no",
    2,
    "يجوز بيع الدين بأقل من قيمته لغير من عليه الدين.",
    "Selling debt for less than value to other than debtor is permissible.",
    "sistani.org/2558", "sistani.org/2558",
    "advanced", "muamalat,debt,selling"));

        log.info("✓ Questions seeded successfully! Total: " + questionRepository.count());
    }

    private void seedTestUser() {
        log.info("👤 Seeding Test User...");
        
        if (userRepository.existsByEmail("test@gmail.com")) {
            log.info("✓ Test user already exists");
            return;
        }
        
        Marja sistani = marjaRepository.findById(1L).orElse(null);
        
        User testUser = new User();
        testUser.setEmail("test@gmail.com");
        testUser.setPassword(passwordEncoder.encode("000000"));
        testUser.setFullName("عبدالرحمن مجدي");
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
        
        testUser.getBadges().add("🎯");
        testUser.getBadges().add("🔥");
        testUser.getBadges().add("⭐");
        
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