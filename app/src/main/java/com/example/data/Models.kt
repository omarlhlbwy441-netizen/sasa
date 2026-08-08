package com.example.data

enum class GeminiModel(
    val id: String,
    val displayName: String,
    val description: String
) {
    FLASH_2_5("gemini-2.5-flash", "Gemini 2.5 Flash", "الموديل السريع والموصى به للمهام العامة"),
    PRO_2_5("gemini-2.5-pro", "Gemini 2.5 Pro", "موديل المنطق المتقدم والمهام البرمجية المعقدة"),
    FLASH_2_0("gemini-2.0-flash", "Gemini 2.0 Flash", "موديل الجيل الثاني السريع"),
    FLASH_1_5("gemini-1.5-flash", "Gemini 1.5 Flash", "موديل الاحتياطي السريع"),
    PRO_1_5("gemini-1.5-pro", "Gemini 1.5 Pro", "موديل المنطق المتقدم الاحتياطي")
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
