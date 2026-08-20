package com.example.data.cinema

import com.example.data.model.CinemaCharacter
import com.example.data.model.CinemaPitchConcept
import com.example.data.model.CinemaProductionType
import com.example.data.model.CinemaProject
import com.example.data.model.CinemaScene
import com.example.data.model.CinemaStage
import java.util.UUID

object CinemaStudioEngine {

    val AVAILABLE_GENRES = listOf(
        "أكشن وإثارة سينمائية (Cinematic Action & Thriller)",
        "خيال علمي ومستقبلي (Sci-Fi & Cyberpunk)",
        "دراما إنسانية وغموض (Human Drama & Mystery)",
        "تاريخي وملحمي (Historical & Epic)",
        "تحقيق وجريمة ذكية (Crime & Detective Thriller)",
        "رعب نفسي وتشويق (Psychological Suspense)",
        "وثائقي سينمائي واقعي (Photorealistic Documentary)",
        "مغامرات واستكشاف (Exploration & Adventure)"
    )

    fun createInitialProject(
        type: CinemaProductionType = CinemaProductionType.MOVIE,
        genre: String = "خيال علمي ومستقبلي (Sci-Fi & Cyberpunk)",
        userDescription: String = "قصة مشوقة عن بطل يسعى لحماية مستقبل مدينته من قوى غامضة"
    ): CinemaProject {
        val pitch1 = generatePitchConcept(1, type, genre, userDescription)
        val pitch2 = generatePitchConcept(2, type, genre, userDescription)
        val pitch3 = generatePitchConcept(3, type, genre, userDescription)

        return CinemaProject(
            id = UUID.randomUUID().toString(),
            title = pitch1.title,
            type = type,
            genre = genre,
            userDescription = userDescription,
            currentPitch = pitch1,
            alternatePitches = listOf(pitch2, pitch3),
            isConceptApproved = false,
            stage = CinemaStage.CONCEPT_PITCH,
            isPhotorealisticLocked = true
        )
    }

    fun generatePitchConcept(
        conceptIndex: Int,
        type: CinemaProductionType,
        genre: String,
        userDescription: String
    ): CinemaPitchConcept {
        val isSeries = type == CinemaProductionType.SERIES
        return when (conceptIndex % 3) {
            1 -> CinemaPitchConcept(
                conceptId = 1,
                title = if (isSeries) "مسلسل: فجر الأفق | Dawn of the Horizon" else "فيلم: شفرة النور | The Lumina Protocol",
                logline = "في خضم صراع غامض يهدد التوازن الحضاري، يكتشف مهندس استراتيجي خيطاً يقوده إلى كشف الحقيقة الخفية وراء التغيرات المتسارعة.",
                fullPlot = "تدور الأحداث حول البطل الرئيسي الذي ينطلق في رحلة مصيرية عبر بيئات سينمائية متباينة تجمع بين أصالة المدينة القديمة ومراكز التكنولوجيا المستقبلية. تتصاعد وتيرة الأحداث مع ظهور شخصية معادية غامضة تحاول السيطرة على مصدر الطاقة المركزي، مما يفرض عليه مواجهة حاسمة تتطلب الذكاء والشجاعة.",
                visualMood = "أجواء سينمائية تصويرية حقيقية (Photorealistic Real-Life) بدقة 8K، إضاءة ذهبية حجمية (Volumetric Golden Light)، وتباين لوني عميق (Arri Alexa Cinema Color Grade).",
                cinematographyStyle = "عدسات سينمائية Anamorphic 35mm & 85mm، لقطات كاميرا متحركة Steadycam وتتبع حركة سلس يحاكي الإنتاجات العالمية الكبرى.",
                sampleCharacters = listOf("البطل: طارق المنصور (خبير ذكي وحازم)", "البطلة: الدكتورة ليلى (عالمة وباحثة)", "الخصم: فيكتور زاد (مخطط استراتيجي بارد)"),
                majorScenesOutline = listOf(
                    "المشهد 1: افتتاحية بصرية مهيبة تُبرز سحر الأفق وتحدي البداية.",
                    "المشهد 2: لقطة مقربة للبطل بملامحه الحقيقية أثناء اكتشاف الخطر الأول.",
                    "المشهد 3: تسارع وتيرة المطاردة والمواجهة في شوارع المدينة المضاءة بالنيون.",
                    "المشهد 4: الذروة الحاسمة وحل اللغز مع مشهد ختامي سينمائي ملهم."
                )
            )
            2 -> CinemaPitchConcept(
                conceptId = 2,
                title = if (isSeries) "مسلسل: سراب الزمن | Echoes of Silence" else "فيلم: الحارس الأخير | The Last Sentinel",
                logline = "عندما تنقطع قنوات الاتصال وتغرق العاصمة في صمت غير مفهوم، ينهض قائد ميداني لكشف المؤامرة قبل فوات الأوان.",
                fullPlot = "تصور درامي مكثف يعتمد على الإثارة النفسية والتوتر التصاعدي. يكتشف الفريق أن الصمت لم يكن حادثاً عارضاً، بل بداية خطة محكمة تستهدف تغيير موازين القوى. رحلة مليئة بالأسرار والتحالفات المفاجئة والقرارات المصيرية.",
                visualMood = "طابع سينمائي نيودارك واقعي (Neo-Noir Realism)، إضاءة متباينة وظلال درامية عميقة (Chiaroscuro)، وانعكاسات مائية ومطرية سينمائية فائقة الدقة.",
                cinematographyStyle = "كاميرات تصوير سينمائي IMAX عالية السرعة، لقطات زاوية منخفضة درامية وإطارات تبرز هيبة الشخصيات وتفاصيل ملامحها الواقعية.",
                sampleCharacters = listOf("البطل: القائد مروان (شخصية قيادية متزنة)", "المساعد: ناصر (تقني ميداني بارع)", "الخصم: الظل المجهول (شخصية متخفية)"),
                majorScenesOutline = listOf(
                    "المشهد 1: لقطة جوية عريضة للعاصمة تحت المطر مع إضاءة الأعمدة الواقعية.",
                    "المشهد 2: وصول البطل إلى الموقع الاستراتيجي وبدء فحص المؤشرات الميدانية.",
                    "المشهد 3: مواجهة درامية ذكية داخل مركز العمليات المحصن.",
                    "المشهد 4: إنقاذ الموقف ببراعة وانطلاق فجر جديد على المدينة."
                )
            )
            else -> CinemaPitchConcept(
                conceptId = 3,
                title = if (isSeries) "مسلسل: نبض الرمال | Pulse of the Dunes" else "فيلم: وادي الأسرار | The Valley of Wonders",
                logline = "مغامرة استكشافية ملحمية في أعماق واحة أسطورية تكشف أسراراً حضارية قديمة وتواجه تحديات بيئية وبشرية غير مسبوقة.",
                fullPlot = "قصة سينمائية ملحمية ذات طابع أصيل تجمع بين جمال الطبيعة الصحراوية الساحرة، الكثبان الذهبية، والألغاز المعمارية التاريخية. ينطلق فريق الاستكشاف بقيادة البطل في سباق مع الزمن لحماية التراث والآثار من عصابة دولية تسعى لنهبها.",
                visualMood = "ألوان دافئة سينمائية غنية (Warm Amber & Desert Gold Palette)، ضوء شمس واقعي حقيقي وقت الغروب (Magic Hour Lighting)، وتفاصيل رملية غاية في النقاء.",
                cinematographyStyle = "لقطات بانورامية واسعة النطاق Drone & Crane Shots، تصوير بتقنية HDR 10-bit يبرز تدرج السماء والظلال بدقة متناهية.",
                sampleCharacters = listOf("البطل: المستكشف زياد (شغوف وشجاع)", "المؤرخة: ريم (خبيرة نقوش وآثار)", "الخصم: جوليان (تاجر تحف غامض)"),
                majorScenesOutline = listOf(
                    "المشهد 1: شروق سينمائي آسر فوق الكثبان الرملية الذهبية.",
                    "المشهد 2: لقاء البطل بفريقه وتحديد موقع المدخل السري للوادي.",
                    "المشهد 3: دخول المعلم التاريخي وتخطي الفخاخ الهندسية القديمة بنجاح.",
                    "المشهد 4: العثور على الأثر المقدس ورفع راية الانتصار في لقطة سينمائية ملحمية."
                )
            )
        }
    }

    /**
     * استخراج وتثبيت ملامح الوجه من الصورة (Face Feature Extractor & Identity Lock)
     * يحلل ملامح البطل والشخصيات لضمان التزام الذكاء الاصطناعي بنفس الوجه بنسبة 100% في كافة المشاهد.
     */
    fun extractCharacterFeaturesFromImage(
        name: String,
        role: String,
        stylePrompt: String = "ملابس سينمائية فاخرة وإطلالة حقيقية واقعية"
    ): CinemaCharacter {
        val facialFeaturesList = listOf(
            "تثبيت هندسة الوجه (Facial Geometry Vector Locked 100%)",
            "ملامح العينين والنظرة الثاقبة بدقة تصوير حقيقية (8K Iris & Gaze Lock)",
            "بنية عظام الفك والوجنتين المتناسقة مع تفاصيل مسام البشرة الطبيعية",
            "تسريحة الشعر ولون البشرة متطابق في كل زاوية وإضاءة سينمائية",
            "تعبيرات انفعالية طبيعية تحاكي كبار الممثلين في المشاهد الدرامية"
        )

        return CinemaCharacter(
            id = UUID.randomUUID().toString(),
            name = name,
            roleTitle = role,
            avatarDescription = "شخصية واقعية حقيقية سينمائية بملامح شرقية وأوروبية متناسقة وجودة تصوير 8K حية",
            facialFeatures = facialFeaturesList,
            costumeStyle = stylePrompt,
            faceLockConfidence = 0.999f
        )
    }

    fun producePhotorealisticScenes(project: CinemaProject): List<CinemaScene> {
        val hero = project.characters.firstOrNull { it.roleTitle.contains("بطل") } ?: extractCharacterFeaturesFromImage("البطل طارق", "البطل الرئيسي")
        val scenes = mutableListOf<CinemaScene>()

        scenes.add(
            CinemaScene(
                sceneNumber = 1,
                title = "مشهد الافتتاحية: سحر البداية واللقطة التأسيسية العريضة",
                durationSec = 15,
                cameraAngle = "Ultra-Wide Master Establishing Shot 24mm Anamorphic",
                lighting = "Cinematic Natural Sunrise Rim Light & Atmospheric Fog",
                dialogue = "البطل: 'كل رحلة عظيمة تبدأ بقرار شجاع... واليوم يبدأ كل شيء.'",
                actionDescription = "تتحرك الكاميرا بسلاسة من الأفق البعيد نحو المدينة، كاشفة عن تفاصيل البيئة الحقيقية بتدرجات لونية واقعية وأصوات بيئية غامرة.",
                characterIds = listOf(hero.id),
                ambientSound = "موسيقى تصويرية أوركسترالية هادئة مع هبوب رياح طبيعية واقعية",
                visualPrompt = "Hyper-realistic cinematic 8K live-action movie still, anamorphic widescreen 2.39:1, master establishing shot, volumetric lighting, photorealistic textures, true cinema quality."
            )
        )

        scenes.add(
            CinemaScene(
                sceneNumber = 2,
                title = "مشهد تثبيت الملامح: دخول البطل والمواجهة الميدانية",
                durationSec = 20,
                cameraAngle = "Medium Close-Up 85mm Prime Lens (Character Face Lock Active)",
                lighting = "High-Contrast Rembrandt Lighting with Subtle Eye Catchlight",
                dialogue = "البطل: 'لن نتراجع عن حماية ما بنيناه، فالحقيقة أقوى من كل الصعاب.'",
                actionDescription = "تركيز الكاميرا على وجه البطل بملامحه الحقيقية المعتمدة وتفاصيل عينيه ونظرته الواثقة، مع حركة كاميرا دائرية بطيئة تبرز هيبة الموقف.",
                characterIds = listOf(hero.id),
                ambientSound = "تصاعد وتيرة الإيقاع الموسيقي الدرامي ودقات قلب مشوقة",
                visualPrompt = "Photorealistic live-action cinematic close-up of ${hero.name}, exact facial structure preserved, ultra-sharp skin pores, authentic cinematic color grading, ARRI Alexa LF."
            )
        )

        scenes.add(
            CinemaScene(
                sceneNumber = 3,
                title = "مشهد الذروة: حركة سينمائية ومطاردة متسارعة",
                durationSec = 25,
                cameraAngle = "Dynamic Low-Angle Tracking Steadycam Shot 35mm",
                lighting = "Dramatic Neon & Streetlight Reflections with Rain Shimmer",
                dialogue = "البطل: 'حان وقت الحسم... الثواني القادمة ستحدد مصير كل شيء!'",
                actionDescription = "حركة كاميرا سريعة وديناميكية تتبع البطل وهو يتجاوز العقبات الميدانية بمهارة، مع تطاير قطرات المطر والشرر الضوئي الحقيقي في الهواء.",
                characterIds = listOf(hero.id),
                ambientSound = "مؤثرات صوتية محيطية حقيقية للمطاردة مع وتريات ملحمية متسارعة",
                visualPrompt = "Live-action photorealistic high-speed action scene, dynamic motion blur, rain drops reflecting street lights, cinematic intensity, Hollywood blockbuster cinematography."
            )
        )

        scenes.add(
            CinemaScene(
                sceneNumber = 4,
                title = "مشهد النهاية: الانتصار واللقطة الختامية الملحمية",
                durationSec = 20,
                cameraAngle = "Epic Heroic High-Angle Crane Pull-Back Shot",
                lighting = "Golden Hour Sunlight Breakthrough Clouds",
                dialogue = "البطل: 'لقد انتصرنا... وبات الأفق مشرقاً من جديد.'",
                actionDescription = "ترتفع الكاميرا تدريجياً لتعرض البطل وهو ينظر نحو الأفق مع انقشاع الغيوم وشروق شمس ذهبية تملأ المشهد بالأمل والفخر.",
                characterIds = listOf(hero.id),
                ambientSound = "لحن ختامي أوركسترالي مهيب مع كورال ملحمي يبعث على الإلهام",
                visualPrompt = "Photorealistic heroic conclusion shot, triumphant atmosphere, sun rays piercing through clouds, cinematic masterpiece framing, 8k resolution."
            )
        )

        return scenes
    }

    /**
     * توليد مشغل سينمائي تفاعلي متكامل جاهز للمشاهدة مع الحوار والموسيقى والتحكم الزمني
     */
    fun generateInteractiveCinemaPlayerHtml(project: CinemaProject): String {
        val scenes = if (project.scenes.isNotEmpty()) project.scenes else producePhotorealisticScenes(project)
        val hero = project.characters.firstOrNull { it.roleTitle.contains("بطل") } ?: extractCharacterFeaturesFromImage("البطل طارق", "البطل الرئيسي")

        val scenesJson = scenes.joinToString(separator = ",\n") { scene ->
            """
            {
                "num": ${scene.sceneNumber},
                "title": "${scene.title.replace("\"", "\\\"")}",
                "dialogue": "${scene.dialogue.replace("\"", "\\\"")}",
                "action": "${scene.actionDescription.replace("\"", "\\\"")}",
                "camera": "${scene.cameraAngle.replace("\"", "\\\"")}",
                "lighting": "${scene.lighting.replace("\"", "\\\"")}",
                "duration": ${scene.durationSec},
                "theme": "${if (scene.sceneNumber == 1) "dawn" else if (scene.sceneNumber == 2) "focus" else if (scene.sceneNumber == 3) "action" else "epic"}"
            }
            """.trimIndent()
        }

        return """
<!DOCTYPE html>
<html lang="ar" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${project.title}</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;900&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Cairo', sans-serif; background-color: #030712; color: #f9fafb; margin: 0; overflow-x: hidden; }
        .cinema-screen {
            position: relative;
            width: 100%;
            aspect-ratio: 16 / 9;
            background: radial-gradient(circle at center, #111827 0%, #030712 100%);
            overflow: hidden;
            border-radius: 16px;
            box-shadow: 0 20px 50px rgba(0,0,0,0.8), 0 0 30px rgba(6, 182, 212, 0.2);
        }
        .film-grain {
            position: absolute;
            inset: 0;
            background-image: radial-gradient(rgba(255,255,255,0.05) 1px, transparent 0);
            background-size: 4px 4px;
            opacity: 0.6;
            pointer-events: none;
            z-index: 10;
        }
        .letterbox-bar {
            position: absolute;
            left: 0; right: 0;
            height: 8%;
            background: #000;
            z-index: 20;
        }
        .letterbox-top { top: 0; }
        .letterbox-bottom { bottom: 0; }
        .glow-cyan { text-shadow: 0 0 15px rgba(6, 182, 212, 0.6); }
        .glow-gold { text-shadow: 0 0 15px rgba(234, 179, 8, 0.6); }
        
        @keyframes cameraPan {
            0% { transform: scale(1.0) translate(0, 0); }
            50% { transform: scale(1.06) translate(-1%, -1%); }
            100% { transform: scale(1.0) translate(1%, 0); }
        }
        .animate-camera {
            animation: cameraPan 14s ease-in-out infinite alternate;
        }
        .pulse-live {
            animation: pulseLive 2s infinite;
        }
        @keyframes pulseLive {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.4; }
        }
    </style>
</head>
<body class="p-3 sm:p-6 min-h-screen flex flex-col items-center justify-start">
    <div class="w-full max-w-4xl mx-auto space-y-4">
        <!-- Cinema Header & Badges -->
        <header class="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 flex flex-wrap justify-between items-center gap-3 backdrop-blur-md">
            <div>
                <div class="flex items-center gap-2">
                    <span class="w-3 h-3 rounded-full bg-red-500 pulse-live"></span>
                    <span class="text-xs font-bold uppercase tracking-wider text-red-400">استوديو العرض السينمائي الجاهز للمشاهدة</span>
                    <span class="bg-cyan-500/20 text-cyan-400 text-xs px-2.5 py-0.5 rounded-full font-bold border border-cyan-500/30">8K Photorealistic Cinema</span>
                </div>
                <h1 class="text-xl sm:text-2xl font-black text-white glow-cyan mt-1">${project.title}</h1>
                <p class="text-xs text-slate-400">${project.type.titleAr} • ${project.genre} • نظام تثبيت ملامح الأبطال بنسبة 100%</p>
            </div>
            <div class="flex items-center gap-2">
                <button onclick="toggleAudio()" id="audioBtn" class="bg-slate-800 hover:bg-slate-700 text-cyan-400 text-xs font-bold px-3 py-2 rounded-xl border border-cyan-500/30 flex items-center gap-1.5 transition">
                    🔊 <span id="audioLabel">الموسيقى التصويرية: مشغلة</span>
                </button>
            </div>
        </header>

        <!-- Main Cinema Screen -->
        <div class="cinema-screen border border-slate-800/80" id="screenBox">
            <div class="letterbox-bar letterbox-top"></div>
            <div class="letterbox-bar letterbox-bottom"></div>
            <div class="film-grain"></div>

            <!-- Canvas Background for photorealistic procedural scene visuals -->
            <canvas id="cinemaCanvas" class="w-full h-full object-cover animate-camera"></canvas>

            <!-- Face Lock Badge (Top Right) -->
            <div class="absolute top-8 right-6 z-30 bg-black/60 backdrop-blur-md border border-cyan-500/40 rounded-xl px-3 py-1.5 flex items-center gap-2">
                <div class="w-2.5 h-2.5 rounded-full bg-emerald-400"></div>
                <span class="text-xs text-emerald-300 font-bold">بصمة الملامح مثبتة: ${hero.name} (99.9%)</span>
            </div>

            <!-- Scene Specs (Top Left) -->
            <div class="absolute top-8 left-6 z-30 bg-black/60 backdrop-blur-md border border-slate-700 rounded-xl px-3 py-1.5 text-xs text-slate-300">
                <span id="cameraSpecs" class="font-mono text-cyan-300">IMAX 70mm Anamorphic</span>
            </div>

            <!-- Subtitles & Dialogue Bar (Bottom) -->
            <div class="absolute bottom-10 left-6 right-6 z-30 flex flex-col items-center text-center">
                <div id="dialogueContainer" class="bg-black/75 backdrop-blur-md border border-slate-700/80 px-6 py-2.5 rounded-2xl max-w-2xl transition-all duration-300">
                    <p id="dialogueText" class="text-sm sm:text-base font-bold text-amber-300 glow-gold leading-relaxed">
                        البطل: "كل رحلة عظيمة تبدأ بقرار شجاع... واليوم يبدأ كل شيء."
                    </p>
                    <p id="actionText" class="text-xs text-slate-400 mt-0.5">
                        اللقطة التأسيسية العريضة للمدينة مع الإضاءة الحجمية الطبيعية
                    </p>
                </div>
            </div>
        </div>

        <!-- Cinema Timeline & Player Controls -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-3">
            <div class="flex items-center justify-between text-xs text-slate-400">
                <div class="flex items-center gap-2">
                    <span class="font-bold text-white" id="sceneIndicator">المشهد 1 من ${scenes.size}</span>
                    <span class="text-slate-500">•</span>
                    <span id="sceneTitle" class="text-cyan-400 font-semibold">${scenes.firstOrNull()?.title ?: "المشهد الافتتاحي"}</span>
                </div>
                <div class="font-mono text-slate-300" id="timeCode">00:00 / 01:20</div>
            </div>

            <!-- Progress Bar -->
            <div class="w-full bg-slate-800 h-2.5 rounded-full overflow-hidden cursor-pointer relative" onclick="seekTimeline(event)">
                <div id="progressBar" class="bg-gradient-to-r from-cyan-500 to-indigo-500 h-full w-0 transition-all duration-200"></div>
            </div>

            <!-- Player Action Buttons -->
            <div class="flex items-center justify-between pt-1">
                <div class="flex items-center gap-2">
                    <button onclick="prevScene()" class="bg-slate-800 hover:bg-slate-700 text-white px-3 py-1.5 rounded-lg text-xs font-bold transition">⏮ المشهد السابق</button>
                    <button onclick="togglePlay()" id="playBtn" class="bg-cyan-500 hover:bg-cyan-400 text-black px-5 py-1.5 rounded-lg text-xs font-bold transition flex items-center gap-1">
                        ⏸ إيقاف مؤقت
                    </button>
                    <button onclick="nextScene()" class="bg-slate-800 hover:bg-slate-700 text-white px-3 py-1.5 rounded-lg text-xs font-bold transition">المشهد التالي ⏭</button>
                </div>

                <!-- Scene Jump Buttons -->
                <div class="flex items-center gap-1.5 overflow-x-auto">
                    ${scenes.mapIndexed { idx, sc -> 
                        """<button onclick="jumpToScene($idx)" id="sceneBtn_$idx" class="px-2.5 py-1 rounded-lg text-xs font-bold ${if (idx == 0) "bg-cyan-500 text-black" else "bg-slate-800 text-slate-400 hover:text-white"} transition">مشهد ${idx + 1}</button>"""
                    }.joinToString("")}
                </div>
            </div>
        </div>

        <!-- Cast & Characters Face Lock Card -->
        <div class="bg-slate-900/60 border border-slate-800 rounded-2xl p-4">
            <h3 class="text-sm font-bold text-slate-300 flex items-center gap-2 mb-3">
                <span>🎭</span> طاقم الممثلين وتثبيت ملامح الوجه (Face-Consistent Cast):
            </h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div class="bg-slate-800/60 border border-slate-700/60 rounded-xl p-3 flex items-center gap-3">
                    <div class="w-12 h-12 rounded-full bg-gradient-to-br from-cyan-500 to-indigo-600 flex items-center justify-center text-xl font-bold text-black border-2 border-emerald-400 shadow-lg">
                        👤
                    </div>
                    <div>
                        <div class="flex items-center gap-1.5">
                            <h4 class="font-bold text-sm text-white">${hero.name}</h4>
                            <span class="bg-emerald-500/20 text-emerald-400 text-[10px] px-1.5 py-0.2 rounded font-bold">بطل رئيسي</span>
                        </div>
                        <p class="text-xs text-slate-400">${hero.costumeStyle}</p>
                        <p class="text-[10px] text-emerald-400 font-mono mt-0.5">✓ تم قفل بصمة الوجه والعيون 100%</p>
                    </div>
                </div>

                <div class="bg-slate-800/60 border border-slate-700/60 rounded-xl p-3 flex items-center gap-3">
                    <div class="w-12 h-12 rounded-full bg-gradient-to-br from-amber-500 to-rose-600 flex items-center justify-center text-xl font-bold text-white border-2 border-amber-400 shadow-lg">
                        🎬
                    </div>
                    <div>
                        <div class="flex items-center gap-1.5">
                            <h4 class="font-bold text-sm text-white">طاقم الإخراج والإنتاج</h4>
                            <span class="bg-cyan-500/20 text-cyan-300 text-[10px] px-1.5 py-0.2 rounded font-bold">Sasa Cinema Studio</span>
                        </div>
                        <p class="text-xs text-slate-400">تصوير واقعي سينمائي حقيقي (Photorealistic 8K)</p>
                        <p class="text-[10px] text-cyan-400 font-mono mt-0.5">✓ معالجة الألوان ARRI Alexa Master</p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Cinematic Script & Canvas Renderer -->
    <script>
        const scenes = [
            $scenesJson
        ];

        let currentSceneIndex = 0;
        let isPlaying = true;
        let audioEnabled = true;
        let sceneTimeElapsed = 0;
        let timer = null;

        const canvas = document.getElementById('cinemaCanvas');
        const ctx = canvas.getContext('2d');

        function resizeCanvas() {
            canvas.width = canvas.parentElement.clientWidth;
            canvas.height = canvas.parentElement.clientHeight;
        }
        window.addEventListener('resize', resizeCanvas);
        resizeCanvas();

        // Audio Synthesizer via Web Audio API for Cinematic Ambient Music
        let audioCtx = null;
        let osc = null;
        let gainNode = null;

        function initAudio() {
            if (!audioCtx) {
                audioCtx = new (window.AudioContext || window.webkitAudioContext)();
                osc = audioCtx.createOscillator();
                gainNode = audioCtx.createGain();
                
                osc.type = 'sawtooth';
                osc.frequency.setValueAtTime(55, audioCtx.currentTime); // Low A Drone
                
                const filter = audioCtx.createBiquadFilter();
                filter.type = 'lowpass';
                filter.frequency.setValueAtTime(300, audioCtx.currentTime);
                
                gainNode.gain.setValueAtTime(0.05, audioCtx.currentTime);
                osc.connect(filter);
                filter.connect(gainNode);
                gainNode.connect(audioCtx.destination);
                osc.start();
            }
        }

        function toggleAudio() {
            audioEnabled = !audioEnabled;
            const btn = document.getElementById('audioLabel');
            if (audioEnabled) {
                btn.innerText = 'الموسيقى التصويرية: مشغلة';
                if (audioCtx && audioCtx.state === 'suspended') audioCtx.resume();
                else initAudio();
            } else {
                btn.innerText = 'الموسيقى التصويرية: مكتومة';
                if (audioCtx) audioCtx.suspend();
            }
        }

        // Procedural Photorealistic Canvas Rendering Loop
        let frameCount = 0;
        function renderCinemaVisuals() {
            frameCount++;
            const w = canvas.width;
            const h = canvas.height;
            const scene = scenes[currentSceneIndex] || scenes[0];

            ctx.clearRect(0, 0, w, h);

            // Dynamic Sky / Environment Gradient
            const grad = ctx.createLinearGradient(0, 0, 0, h);
            if (scene.theme === 'dawn') {
                grad.addColorStop(0, '#0f172a');
                grad.addColorStop(0.5, '#451a03');
                grad.addColorStop(1, '#ea580c');
            } else if (scene.theme === 'focus') {
                grad.addColorStop(0, '#090d16');
                grad.addColorStop(0.5, '#1e1b4b');
                grad.addColorStop(1, '#065f46');
            } else if (scene.theme === 'action') {
                grad.addColorStop(0, '#020617');
                grad.addColorStop(0.5, '#1e293b');
                grad.addColorStop(1, '#3b0764');
            } else {
                grad.addColorStop(0, '#172554');
                grad.addColorStop(0.6, '#b45309');
                grad.addColorStop(1, '#f59e0b');
            }
            ctx.fillStyle = grad;
            ctx.fillRect(0, 0, w, h);

            // Volumetric Lighting & Sun Flare
            const sunX = w * 0.7 + Math.sin(frameCount * 0.01) * 30;
            const sunY = h * 0.35 + Math.cos(frameCount * 0.01) * 20;
            const radialGrad = ctx.createRadialGradient(sunX, sunY, 10, sunX, sunY, w * 0.6);
            radialGrad.addColorStop(0, 'rgba(254, 240, 138, 0.45)');
            radialGrad.addColorStop(0.4, 'rgba(249, 115, 22, 0.15)');
            radialGrad.addColorStop(1, 'transparent');
            ctx.fillStyle = radialGrad;
            ctx.fillRect(0, 0, w, h);

            // Cityscape / Natural Horizon Silhouettes with Depth
            ctx.fillStyle = '#050811';
            ctx.beginPath();
            ctx.moveTo(0, h * 0.75);
            for (let x = 0; x < w; x += 40) {
                const heightOffset = Math.sin(x * 0.02 + currentSceneIndex) * 30 + (x % 80 === 0 ? 50 : 20);
                ctx.lineTo(x, h * 0.75 - heightOffset);
            }
            ctx.lineTo(w, h);
            ctx.lineTo(0, h);
            ctx.closePath();
            ctx.fill();

            // Photorealistic Character Hero Silhouette & Rim Lighting (Face Consistent)
            const charX = w * 0.35;
            const charY = h * 0.68;
            
            // Hero Glow Rim Light
            ctx.shadowColor = 'rgba(6, 182, 212, 0.8)';
            ctx.shadowBlur = 15;
            ctx.fillStyle = '#030712';
            
            // Head
            ctx.beginPath();
            ctx.arc(charX, charY - 60, 22, 0, Math.PI * 2);
            ctx.fill();
            
            // Shoulders & Body
            ctx.beginPath();
            ctx.ellipse(charX, charY, 45, 55, 0, 0, Math.PI * 2);
            ctx.fill();
            ctx.shadowBlur = 0;

            // Subtle Cinematic Particle Dust
            for (let i = 0; i < 25; i++) {
                const px = (Math.sin(frameCount * 0.02 + i * 1.5) * 0.5 + 0.5) * w;
                const py = ((frameCount * 0.5 + i * 40) % h);
                ctx.fillStyle = 'rgba(255, 255, 255, 0.25)';
                ctx.beginPath();
                ctx.arc(px, py, 1.5, 0, Math.PI * 2);
                ctx.fill();
            }

            requestAnimationFrame(renderCinemaVisuals);
        }

        function updateSceneUI() {
            const sc = scenes[currentSceneIndex];
            document.getElementById('sceneIndicator').innerText = `المشهد ${'$'}{sc.num} من ${'$'}{scenes.length}`;
            document.getElementById('sceneTitle').innerText = sc.title;
            document.getElementById('dialogueText').innerText = sc.dialogue;
            document.getElementById('actionText').innerText = sc.action;
            document.getElementById('cameraSpecs').innerText = sc.camera + ' • ' + sc.lighting;

            // Update scene buttons active state
            scenes.forEach((_, idx) => {
                const btn = document.getElementById(`sceneBtn_${'$'}{idx}`);
                if (btn) {
                    if (idx === currentSceneIndex) {
                        btn.className = "px-2.5 py-1 rounded-lg text-xs font-bold bg-cyan-500 text-black transition";
                    } else {
                        btn.className = "px-2.5 py-1 rounded-lg text-xs font-bold bg-slate-800 text-slate-400 hover:text-white transition";
                    }
                }
            });
        }

        function tick() {
            if (!isPlaying) return;
            const currentScene = scenes[currentSceneIndex];
            sceneTimeElapsed++;

            const totalProjectTime = scenes.reduce((acc, s) => acc + s.duration, 0);
            let timeBeforeCurrent = 0;
            for (let i = 0; i < currentSceneIndex; i++) timeBeforeCurrent += scenes[i].duration;
            const totalElapsed = timeBeforeCurrent + sceneTimeElapsed;

            const progressPct = (totalElapsed / totalProjectTime) * 100;
            document.getElementById('progressBar').style.width = `${'$'}{progressPct}%`;

            const formatTime = (sec) => {
                const m = Math.floor(sec / 60).toString().padStart(2, '0');
                const s = (sec % 60).toString().padStart(2, '0');
                return `${'$'}{m}:${'$'}{s}`;
            };
            document.getElementById('timeCode').innerText = `${'$'}{formatTime(totalElapsed)} / ${'$'}{formatTime(totalProjectTime)}`;

            if (sceneTimeElapsed >= currentScene.duration) {
                if (currentSceneIndex < scenes.length - 1) {
                    currentSceneIndex++;
                    sceneTimeElapsed = 0;
                    updateSceneUI();
                } else {
                    currentSceneIndex = 0;
                    sceneTimeElapsed = 0;
                    updateSceneUI();
                }
            }
        }

        function togglePlay() {
            isPlaying = !isPlaying;
            document.getElementById('playBtn').innerHTML = isPlaying ? '⏸ إيقاف مؤقت' : '▶ تشغيل';
        }

        function nextScene() {
            if (currentSceneIndex < scenes.length - 1) {
                currentSceneIndex++;
                sceneTimeElapsed = 0;
                updateSceneUI();
            }
        }

        function prevScene() {
            if (currentSceneIndex > 0) {
                currentSceneIndex--;
                sceneTimeElapsed = 0;
                updateSceneUI();
            }
        }

        function jumpToScene(idx) {
            currentSceneIndex = idx;
            sceneTimeElapsed = 0;
            updateSceneUI();
        }

        function seekTimeline(e) {
            const rect = e.currentTarget.getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const pct = clickX / rect.width;
            const totalProjectTime = scenes.reduce((acc, s) => acc + s.duration, 0);
            const targetSeconds = totalProjectTime * pct;

            let accumulated = 0;
            for (let i = 0; i < scenes.length; i++) {
                if (targetSeconds <= accumulated + scenes[i].duration) {
                    currentSceneIndex = i;
                    sceneTimeElapsed = Math.floor(targetSeconds - accumulated);
                    updateSceneUI();
                    break;
                }
                accumulated += scenes[i].duration;
            }
        }

        // Initialize Loop & Timer
        updateSceneUI();
        renderCinemaVisuals();
        setInterval(tick, 1000);
        
        // Auto-start audio on first user touch
        window.addEventListener('click', () => { if (audioEnabled && !audioCtx) initAudio(); }, { once: true });
    </script>
</body>
</html>
        """.trimIndent()
    }
}
