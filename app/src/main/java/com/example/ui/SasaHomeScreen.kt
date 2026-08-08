package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import com.example.data.ChatMessage
import com.example.data.GeminiModel
import com.example.data.MessageSender
import com.example.ui.components.ApiKeyDialog
import com.example.ui.components.MessageTextWithCodeBlocks
import com.example.ui.theme.SasaAccentGreen
import com.example.ui.theme.SasaAiBubble
import com.example.ui.theme.SasaCardBackground
import com.example.ui.theme.SasaDarkBackground
import com.example.ui.theme.SasaDarkSurface
import com.example.ui.theme.SasaPrimary
import com.example.ui.theme.SasaPrimaryContainer
import com.example.ui.theme.SasaSecondary
import com.example.ui.theme.SasaTextSecondary
import com.example.ui.theme.SasaUserBubble
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SasaHomeScreen(
    viewModel: SasaViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var showModelMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size, uiState.isGenerating) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Show system notice if any
    LaunchedEffect(uiState.systemNotice) {
        uiState.systemNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.dismissSystemNotice()
        }
    }

    // Force RTL for Arabic layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                HeaderBar(
                    selectedModel = uiState.selectedModel,
                    activeModelTag = uiState.activeModelTag,
                    onOpenModelMenu = { showModelMenu = true },
                    onOpenKeyDialog = { viewModel.setShowApiKeyDialog(true) },
                    onClearChat = { viewModel.onClearChat() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = SasaDarkBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    // Chat messages list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        items(uiState.messages, key = { it.id }) { msg ->
                            ChatMessageItem(message = msg)
                        }

                        if (uiState.isGenerating) {
                            item {
                                ThinkingIndicator(modelName = uiState.selectedModel.displayName)
                            }
                        }

                        // Quick prompt suggestion chips if conversation is brief
                        if (uiState.messages.size <= 2 && !uiState.isGenerating) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "💡 اقتراحات سريعة للبدء:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SasaTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PromptChip("💻 كتابة كود Python أو Kotlin") {
                                        inputText = "اكتب لي كوداً محترفاً ومكتتملاً لنظام إدارة مهام مع شرح العمليات."
                                    }
                                    PromptChip("⚡ تحليل ومعالجة خطأ برمجي") {
                                        inputText = "كيف أعالج خطأ Quota limits exceeded (429) في استخدام Gemini API برمجياً مع التغيير التلقائي للنماذج؟"
                                    }
                                    PromptChip("📊 إنشاء استعلام SQL ذكي") {
                                        inputText = "صمم جدول SQL لإدارة المستخدمين والمستندات مع الاستعلامات الأكثر استخداماً."
                                    }
                                    PromptChip("📝 تلخيص وتبسيط الفكرة") {
                                        inputText = "اشرح لي مفهوم التفكير المعماري للذكاء الاصطناعي وكيف يختار النماذج المناسبة."
                                    }
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }

                    // Input Bar
                    BottomInputBar(
                        inputText = inputText,
                        onInputChanged = { inputText = it },
                        isGenerating = uiState.isGenerating,
                        activeModelName = uiState.selectedModel.displayName,
                        onSend = {
                            if (inputText.isNotBlank()) {
                                val textToSend = inputText
                                inputText = ""
                                viewModel.onSendMessage(textToSend)
                            }
                        }
                    )
                }

                // Dropdown menu for model selection
                DropdownMenu(
                    expanded = showModelMenu,
                    onDismissRequest = { showModelMenu = false },
                    modifier = Modifier.background(SasaCardBackground)
                ) {
                    GeminiModel.entries.forEach { model ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = model.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (model == uiState.selectedModel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (model == uiState.selectedModel) SasaPrimary else Color.Unspecified
                                    )
                                    Text(
                                        text = model.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SasaTextSecondary
                                    )
                                }
                            },
                            onClick = {
                                viewModel.onSelectModel(model)
                                showModelMenu = false
                            }
                        )
                    }
                }

                // API Key Dialog
                if (uiState.showApiKeyDialog) {
                    ApiKeyDialog(
                        currentKey = uiState.customApiKey,
                        onDismiss = { viewModel.setShowApiKeyDialog(false) },
                        onSaveKey = { key ->
                            viewModel.onSaveCustomApiKey(key)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderBar(
    selectedModel: GeminiModel,
    activeModelTag: String,
    onOpenModelMenu: () -> Unit,
    onOpenKeyDialog: () -> Unit,
    onClearChat: () -> Unit
) {
    Surface(
        color = SasaCardBackground,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SasaPrimaryContainer)
                        .border(1.5.dp, SasaPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "صاصا AI",
                        tint = SasaSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "منظومة صاصا AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SasaAccentGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "v15.2",
                                fontSize = 10.sp,
                                color = SasaAccentGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "المحرك: $activeModelTag",
                        style = MaterialTheme.typography.labelSmall,
                        color = SasaTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenModelMenu,
                    modifier = Modifier.testTag("model_selector_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "تبديل النموذج",
                        tint = SasaSecondary
                    )
                }
                IconButton(
                    onClick = onOpenKeyDialog,
                    modifier = Modifier.testTag("api_key_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "إعدادات المفتاح",
                        tint = SasaPrimary
                    )
                }
                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.testTag("clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "مسح المحادثة",
                        tint = SasaTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER
    val isSystem = message.sender == MessageSender.SYSTEM

    if (isSystem) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color(0xFF3E2723),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6D00))
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFD180),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SasaPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "صاصا",
                    tint = SasaSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 4.dp else 16.dp,
                bottomEnd = if (isUser) 16.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) SasaUserBubble else if (message.isError) Color(0xFF3C1818) else SasaAiBubble
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .testTag(if (isUser) "user_message_bubble" else "ai_message_bubble")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "صاصا AI",
                            style = MaterialTheme.typography.labelMedium,
                            color = SasaSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        message.modelUsed?.let { model ->
                            Text(
                                text = model,
                                style = MaterialTheme.typography.labelSmall,
                                color = SasaTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                MessageTextWithCodeBlocks(text = message.text)
            }
        }
    }
}

@Composable
fun ThinkingIndicator(modelName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = SasaSecondary,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "جارٍ التفكير ومعالجة الرد عبر $modelName...",
            style = MaterialTheme.typography.bodySmall,
            color = SasaTextSecondary
        )
    }
}

@Composable
fun PromptChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = SasaCardBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, SasaPrimary.copy(alpha = 0.4f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun BottomInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    isGenerating: Boolean,
    activeModelName: String,
    onSend: () -> Unit
) {
    Surface(
        color = SasaDarkSurface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = {
                    Text(
                        text = "اكتب استفسارك أو طلبك البرمجي لـ صاصا AI...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SasaTextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(
                        onClick = onSend,
                        enabled = inputText.isNotBlank() && !isGenerating,
                        modifier = Modifier.testTag("send_message_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SasaPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "إرسال",
                                tint = if (inputText.isNotBlank()) SasaPrimary else SasaTextSecondary
                            )
                        }
                    }
                },
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SasaCardBackground,
                    unfocusedContainerColor = SasaCardBackground,
                    focusedBorderColor = SasaPrimary,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}
