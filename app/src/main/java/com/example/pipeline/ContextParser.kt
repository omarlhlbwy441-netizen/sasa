package com.example.pipeline

import com.example.data.local.AgentLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContextParser {

    fun parse(
        userMessage: String,
        logs: List<AgentLogEntity>,
        userMemory: Map<String, String>
    ): PipelineContext {
        // 1. Fetch recent conversation history (last 10 messages)
        val historyPairs = logs.takeLast(10).map { log ->
            log.sender to log.message
        }

        // 2. Apply Context Compression if conversation history is too large
        val compressedHistory = compressContextIfNeeded(historyPairs)

        // 3. Format current timestamp
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentTime = sdf.format(Date())

        return PipelineContext(
            userMessage = userMessage,
            conversationHistory = compressedHistory,
            longTermMemory = userMemory,
            timestamp = currentTime
        )
    }

    private fun compressContextIfNeeded(
        history: List<Pair<String, String>>,
        maxCharLimit: Int = 3000
    ): List<Pair<String, String>> {
        val totalChars = history.sumOf { it.second.length }
        if (totalChars <= maxCharLimit) return history

        // Compress older messages into a summary
        val recent = history.takeLast(4)
        val older = history.dropLast(4)
        
        val summaryText = "ملخص المحادثة السابقة: تضمنت ${older.size} تبادلات حول إعداد الأكواد والمستودعات."
        return listOf("SYSTEM" to summaryText) + recent
    }
}
