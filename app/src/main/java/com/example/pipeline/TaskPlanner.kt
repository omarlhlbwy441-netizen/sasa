package com.example.pipeline

class TaskPlanner {

    fun createPlan(
        userMessage: String,
        classification: ClassificationResult,
        context: PipelineContext
    ): List<PipelinePlanStep> {
        val steps = mutableListOf<PipelinePlanStep>()

        when (classification.taskType) {
            TaskType.APP_BUILD -> {
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_1",
                        toolName = "requirements_analyzer",
                        description = "تحليل وتفكيك متطلبات التطبيق والواجهات",
                        inputs = mapOf("prompt" to userMessage),
                        expectedOutput = "قائمة بالمكونات والصفحات المطلوبة"
                    )
                )
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_2",
                        toolName = "code_generator",
                        description = "توليد كود الخادم وواجهة المستخدم (Tailwind/JS/HTML)",
                        inputs = mapOf("prompt" to userMessage),
                        expectedOutput = "ملفات الكود البرمجي الكاملة",
                        dependencies = listOf("step_1")
                    )
                )
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_3",
                        toolName = "github_sync",
                        description = "المزامنة والرفع الشفاف إلى مستودع GitHub",
                        inputs = mapOf("repo" to (context.longTermMemory["repo"] ?: "sasa")),
                        expectedOutput = "SHA الالتزام ورابط GitHub",
                        dependencies = listOf("step_2")
                    )
                )
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_4",
                        toolName = "preview_renderer",
                        description = "تجهيز وتفعيل المعاينة الحية التفاعلية",
                        inputs = mapOf("html" to "ready"),
                        expectedOutput = "مكون WebView مع العرض التفاعلي",
                        dependencies = listOf("step_3")
                    )
                )
            }

            TaskType.IMAGE_GENERATION -> {
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_1",
                        toolName = "video_synthesizer",
                        description = "توليد وإيقاد مشهد الفيديو أو الصورة التفاعلية",
                        inputs = mapOf("prompt" to userMessage),
                        expectedOutput = "عنصر تشغيل مرئي تفاعلي"
                    )
                )
            }

            TaskType.MULTI_STEP -> {
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_1",
                        toolName = "github_action_executor",
                        description = "التفاعل المباشر مع REST API الخاص بـ GitHub",
                        inputs = mapOf("prompt" to userMessage),
                        expectedOutput = "تأكيد تنفيذ العملية"
                    )
                )
            }

            else -> {
                steps.add(
                    PipelinePlanStep(
                        stepId = "step_1",
                        toolName = "ai_response_generator",
                        description = "صياغة إجابة ذكية رصينة مع الاستناد إلى سياق المحادثة",
                        inputs = mapOf("prompt" to userMessage),
                        expectedOutput = "النص الصريح والاستجابة"
                    )
                )
            }
        }

        return steps
    }

    fun replanOnFailure(
        failedStep: PipelinePlanStep,
        errorMessage: String,
        currentPlan: List<PipelinePlanStep>
    ): List<PipelinePlanStep> {
        // Self-Correction extension: creates a recovery step
        val recoveryStep = PipelinePlanStep(
            stepId = "${failedStep.stepId}_fallback",
            toolName = "fallback_engine",
            description = "إعادة المحاولة بأسلوب بديل لتجاوز الخطأ: $errorMessage",
            inputs = mapOf("retry_target" to failedStep.toolName),
            expectedOutput = "النتيجة البديلة المستقرة"
        )
        return listOf(recoveryStep) + currentPlan.filter { it.stepId != failedStep.stepId }
    }
}
