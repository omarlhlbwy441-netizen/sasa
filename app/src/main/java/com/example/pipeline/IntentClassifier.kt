package com.example.pipeline

data class ClassificationResult(
    val taskType: TaskType,
    val confidence: Float,
    val requiredTools: List<String>,
    val reasoning: String
)

class IntentClassifier {

    private val intentCache = mutableMapOf<String, ClassificationResult>()

    fun classify(userMessage: str, context: PipelineContext): ClassificationResult {
        val normalizedMsg = userMessage.trim().lowercase()

        // Check cache first (Intent Cache extension)
        intentCache[normalizedMsg]?.let { cached ->
            return cached
        }

        val text = userMessage.lowercase()

        val result = when {
            text.contains("موقع") || text.contains("تطبيق") || text.contains("ويب") || 
            text.contains("تواصل") || text.contains("صفحة") || text.contains("مشروع") ||
            text.contains("html") || text.contains("social") -> {
                ClassificationResult(
                    taskType = TaskType.APP_BUILD,
                    confidence = 0.98f,
                    requiredTools = listOf("web_builder", "github_push", "preview_generator"),
                    reasoning = "تم اكتشاف طلب بناء تطبيق/موقع أو منصة تفاعلية."
                )
            }

            text.contains("فيديو") || text.contains("video") || text.contains("صورة") || text.contains("شعار") -> {
                ClassificationResult(
                    taskType = TaskType.IMAGE_GENERATION,
                    confidence = 0.95f,
                    requiredTools = listOf("video_generator", "media_synthesizer"),
                    reasoning = "طلب توليد وسائط مرئية أو مشاهد فيديو."
                )
            }

            text.contains("أنشئ مستودع") || text.contains("احذف") || text.contains("ارفع") || 
            text.contains("push") || text.contains("git") || text.contains("github") -> {
                ClassificationResult(
                    taskType = TaskType.MULTI_STEP,
                    confidence = 0.92f,
                    requiredTools = listOf("github_api", "repo_manager", "git_task_executor"),
                    reasoning = "طلب إجراء عمليات متقدمة على مستودع GitHub."
                )
            }

            text.contains("احسب") || text.contains("حساب") || text.contains("معادلة") || text.contains("احصائيات") -> {
                ClassificationResult(
                    taskType = TaskType.CALCULATION,
                    confidence = 0.90f,
                    requiredTools = listOf("calculator", "data_engine"),
                    reasoning = "طلب إجراء حسابات أو تحليلات شفرة."
                )
            }

            text.contains("ابحث") || text.contains("اخبار") || text.contains("ما هو") || text.contains("معلومات") -> {
                ClassificationResult(
                    taskType = TaskType.WEB_SEARCH,
                    confidence = 0.88f,
                    requiredTools = listOf("web_search", "information_retriever"),
                    reasoning = "طلب الاستعلام عن معلومات من الشبكة."
                )
            }

            else -> {
                ClassificationResult(
                    taskType = TaskType.GENERAL_CHAT,
                    confidence = 0.85f,
                    requiredTools = listOf("general_llm"),
                    reasoning = "استفسار عادي أو محادثة عامة مع نعمه AI."
                )
            }
        }

        // Cache the result
        intentCache[normalizedMsg] = result
        return result
    }
}
typealias str = String
