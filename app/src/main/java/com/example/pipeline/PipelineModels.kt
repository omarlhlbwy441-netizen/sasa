package com.example.pipeline

enum class TaskType(val titleAr: String) {
    GENERAL_CHAT("محادثة عادية"),
    WEB_SEARCH("بحث وحلب معلومات من الشبكة"),
    CALCULATION("حسابات رياضية وتحليل بيانات"),
    IMAGE_GENERATION("طلب توليد وإعداد صور/مشاهد"),
    CODE_EXECUTION("تشغيل وتطوير شفرة برمجية"),
    DATA_ANALYSIS("تحليل ومعالجة البيانات"),
    MULTI_STEP("عملية معقدة متعددة الأدوات"),
    APP_BUILD("بناء موقع/تطبيق متكامل")
}

data class PipelineContext(
    val userMessage: String,
    val conversationHistory: List<Pair<String, String>>, // sender to text
    val longTermMemory: Map<String, String>,
    val timestamp: String,
    val userId: String = "user_default",
    val sessionId: String = "session_default"
)

data class PipelinePlanStep(
    val stepId: String,
    val toolName: String,
    val description: String,
    val inputs: Map<String, Any>,
    val expectedOutput: String,
    val dependencies: List<String> = emptyList()
)

data class StepExecutionResult(
    val stepId: String,
    val toolName: String,
    val status: String, // "success", "failed", "running"
    val result: String,
    val error: String? = null,
    val source: String? = null
)

data class PipelineProgressUpdate(
    val stageIndex: Int, // 0..5 (Stages 1..6)
    val stageTitle: String,
    val stageDetail: String,
    val isCompleted: Boolean = false,
    val activeTool: String? = null
)

data class FormattedOutput(
    val type: String, // "text", "widget", "file"
    val content: String,
    val htmlWidget: String? = null,
    val codeBlocks: String? = null,
    val pipelineMarker: String = "",
    val nextStepsMarker: String = "",
    val sources: List<String> = emptyList(),
    val executionTimeMs: Long = 0
)
