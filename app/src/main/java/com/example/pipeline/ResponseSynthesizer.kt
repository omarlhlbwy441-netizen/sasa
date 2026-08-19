package com.example.pipeline

class ResponseSynthesizer {

    fun synthesize(
        userMessage: String,
        executionLog: List<StepExecutionResult>,
        context: PipelineContext,
        classification: ClassificationResult
    ): Map<String, Any> {
        val pipelineMarker = """===PIPELINE_START===
STAGE1: 🧩 1. فهم السياق (Context Parsing) | تحليل الرسالة + الذاكرة + التاريخ الزمني واستخراج الأهداف.
STAGE2: 🎯 2. تحديد نوع المهمة (Intent Classification) | نوع المهمة: ${classification.taskType.titleAr} [ثقة: ${(classification.confidence * 100).toInt()}%].
STAGE3: 🧠 3. التفكير المسبق (Pre-reasoning) | تم إعداد خطة تنفيذ تتكون من ${executionLog.size} خطوات.
STAGE4: ⚙️ 4. التنفيذ التفاعلي (Interactive Execution) | تم استدعاء الأدوات بنجاح وتحليل النتائج.
STAGE5: 🧪 5. التوليف النهائي (Synthesis) | دمج كافة النتائج والشفرات البرمجية والمكونات.
STAGE6: 🚀 6. تقديم النتيجة (Output) | جاهز للعرض الشامل والتجربة المباشرة.
===PIPELINE_END===
""".trimIndent()

        val nextStepsMarker = """===NEXT_STEPS_START===
🚀 رفع المكتسبات لمستودع GitHub
☁️ نشر وتفعيل الاستضافة على Render Cloud
🎨 تخصيص الهوية والشعار
📱 تجربة المعاينة المباشرة التفاعلية
===NEXT_STEPS_END===
""".trimIndent()

        val sources = executionLog.mapNotNull { it.source }.distinct()

        return mapOf(
            "pipelineMarker" to pipelineMarker,
            "nextStepsMarker" to nextStepsMarker,
            "executionLog" to executionLog,
            "sources" to sources,
            "originalQuery" to userMessage
        )
    }
}
