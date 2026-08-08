package com.example.data

enum class GeminiModel(
    val id: String,
    val displayName: String,
    val description: String
) {
    FLASH_2_5("gemini-2.5-flash", "Gemini 2.5 Flash", "الموديل السريع والموصى به للمهام العامة"),
    PRO_3_1("gemini-3.1-pro-preview", "Gemini 3.1 Pro", "موديل المنطق المتقدم والمهام البرمجية المعقدة"),
    LITE_3_1("gemini-3.1-flash-lite-preview", "Gemini 3.1 Flash Lite", "موديل منخفض الاستهلاك وخفيف جداً"),
    FLASH_2_0("gemini-2.0-flash", "Gemini 2.0 Flash", "موديل الجيل الثاني السريع"),
    FLASH_1_5("gemini-1.5-flash", "Gemini 1.5 Flash", "موديل الاحتياطي السريع")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String? = null,
    val isError: Boolean = false,
    val isSystemNotice: Boolean = false
)

enum class MessageSender {
    USER,
    SASA_AI,
    SYSTEM
}

data class ApiKeyStatus(
    val isCustom: Boolean,
    val keyPreview: String
)
