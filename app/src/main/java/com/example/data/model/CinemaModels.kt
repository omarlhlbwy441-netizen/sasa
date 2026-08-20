package com.example.data.model

enum class CinemaProductionType(val titleAr: String) {
    MOVIE("فيلم سينمائي طويل"),
    SERIES("مسلسل درامي متعدد الحلقات"),
    SHORT_FILM("فيلم روائي قصير"),
    DOCUMENTARY("فيلم وثائقي سينمائي")
}

enum class CinemaStage {
    GENRE_DISCOVERY,      // مرحلة استكشاف نوع العمل ووصفه
    CONCEPT_PITCH,        // مرحلة عرض التصور والموافقة أو طلب تصور بديل
    CHARACTER_CASTING,    // مرحلة تحديد وتثبيت ملامح الشخصيات والأبطال من الصور
    SCENE_PRODUCTION,     // مرحلة التصوير والإنتاج السينمائي للمشاهد
    THEATER_PLAYBACK      // مشغل العرض السينمائي الجاهز للمشاهدة
}

data class CinemaCharacter(
    val id: String,
    val name: String,
    val roleTitle: String,         // e.g. "البطل الرئيسي", "الشخصية المساعدة", "الخصم"
    val avatarDescription: String,
    val facialFeatures: List<String>, // ملامح الوجه المستخرجة بدقة: العينين، الفك، البشرة، الشعر
    val costumeStyle: String,         // الزي والنمط السينمائي الموحد
    val faceLockConfidence: Float = 0.998f, // نسبة مطابقة وتثبيت الملامح 99.8%
    val sampleImageUrl: String = ""
)

data class CinemaScene(
    val sceneNumber: Int,
    val title: String,
    val durationSec: Int,
    val cameraAngle: String,          // e.g., "IMAX 70mm Anamorphic Wide Tracking"
    val lighting: String,             // e.g., "Volumetric Natural Rim Light & Haze"
    val dialogue: String,             // الحوار المنطوق
    val actionDescription: String,    // تفاصيل الحدث السينمائي
    val characterIds: List<String>,   // الشخصيات المتواجدة في المشهد
    val ambientSound: String,         // المؤثرات الصوتية والموسيقى التصويرية
    val visualPrompt: String          // التوجيه التصويري الواقعي Photorealistic Prompt
)

data class CinemaPitchConcept(
    val conceptId: Int,
    val title: String,
    val logline: String,
    val fullPlot: String,
    val visualMood: String,           // الطابع البصري واللوني
    val cinematographyStyle: String,  // نمط التصوير الواقعي
    val sampleCharacters: List<String>,
    val majorScenesOutline: List<String>
)

data class CinemaProject(
    val id: String,
    val title: String,
    val type: CinemaProductionType,
    val genre: String,
    val userDescription: String,
    val currentPitch: CinemaPitchConcept,
    val alternatePitches: List<CinemaPitchConcept> = emptyList(),
    val isConceptApproved: Boolean = false,
    val characters: List<CinemaCharacter> = emptyList(),
    val scenes: List<CinemaScene> = emptyList(),
    val stage: CinemaStage = CinemaStage.GENRE_DISCOVERY,
    val videoHtmlPlayer: String = "",
    val totalDurationSeconds: Int = 0,
    val isPhotorealisticLocked: Boolean = true // وضع التصوير الواقعي الحقيقي
)
