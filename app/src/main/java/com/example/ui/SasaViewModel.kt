package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.GeminiModel
import com.example.data.GeminiRepository
import com.example.data.GeminiResult
import com.example.data.MessageSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SasaUiState(
    val messages: List<ChatMessage> = emptyList(),
    val selectedModel: GeminiModel = GeminiModel.FLASH_2_5,
    val isGenerating: Boolean = false,
    val customApiKey: String = "",
    val activeModelTag: String = GeminiModel.FLASH_2_5.displayName,
    val systemNotice: String? = null,
    val showApiKeyDialog: Boolean = false
)

class SasaViewModel(
    private val repository: GeminiRepository = GeminiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SasaUiState(
            messages = listOf(
                ChatMessage(
                    sender = MessageSender.SASA_AI,
                    text = "مرحباً بك! أنا منظومة صاصا AI (Sasa AI v15.2).\n" +
                            "المساعد الذكي للتحليل والبرمجة باللغة العربية.\n\n" +
                            "💡 تم تزويد التطبيق بنظام التنقل التلقائي الذكي بين نماذج Gemini (2.5 Flash, 3.1 Pro, 3.1 Lite) ومعالجة قيود الاستخدام (Quota Limits) تلقائياً.\n\n" +
                            "كيف يمكنني مساعدتك اليوم؟",
                    modelUsed = GeminiModel.FLASH_2_5.displayName
                )
            )
        )
    )
    val uiState: StateFlow<SasaUiState> = _uiState.asStateFlow()

    fun onSendMessage(inputPrompt: String) {
        val prompt = inputPrompt.trim()
        if (prompt.isBlank() || _uiState.value.isGenerating) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = prompt
        )

        val updatedMessages = _uiState.value.messages + userMessage
        _uiState.value = _uiState.value.copy(
            messages = updatedMessages,
            isGenerating = true,
            systemNotice = null
        )

        viewModelScope.launch {
            val result = repository.generateContentWithFailover(
                prompt = prompt,
                conversationHistory = updatedMessages,
                preferredModel = _uiState.value.selectedModel,
                customApiKey = _uiState.value.customApiKey
            )

            when (result) {
                is GeminiResult.Success -> {
                    val aiMessage = ChatMessage(
                        sender = MessageSender.SASA_AI,
                        text = result.text,
                        modelUsed = result.modelUsed.displayName
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + aiMessage,
                        isGenerating = false,
                        activeModelTag = result.modelUsed.displayName
                    )
                }
                is GeminiResult.QuotaExceeded -> {
                    val noticeMessage = ChatMessage(
                        sender = MessageSender.SYSTEM,
                        text = "⚠️ تنبيه قيود الاستخدام: ${result.message}\nجارٍ تحويل الطلب تلقائياً...",
                        isSystemNotice = true
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + noticeMessage,
                        isGenerating = false
                    )
                }
                is GeminiResult.Error -> {
                    val errorMessage = ChatMessage(
                        sender = MessageSender.SASA_AI,
                        text = "❌ حدثت مشكلة أثناء معالجة الطلب:\n${result.message}\n\n💡 نصيحة: يمكنك إضافة مفتاح Gemini API الخاص بك من زر الإعدادات بالتقاط مفتاح مجاني لضمان عدم توقف الاستجابة.",
                        isError = true
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isGenerating = false,
                        systemNotice = result.message
                    )
                }
            }
        }
    }

    fun onSelectModel(model: GeminiModel) {
        _uiState.value = _uiState.value.copy(
            selectedModel = model,
            activeModelTag = model.displayName
        )
    }

    fun onSaveCustomApiKey(key: String) {
        _uiState.value = _uiState.value.copy(
            customApiKey = key.trim(),
            showApiKeyDialog = false,
            systemNotice = "تم حفظ مفتاح API بنجاح!"
        )
    }

    fun setShowApiKeyDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showApiKeyDialog = show)
    }

    fun onClearChat() {
        _uiState.value = _uiState.value.copy(
            messages = listOf(
                ChatMessage(
                    sender = MessageSender.SASA_AI,
                    text = "تم البدء في محادثة جديدة. يسعدني مساعدتك!",
                    modelUsed = _uiState.value.selectedModel.displayName
                )
            ),
            systemNotice = null
        )
    }

    fun dismissSystemNotice() {
        _uiState.value = _uiState.value.copy(systemNotice = null)
    }
}
