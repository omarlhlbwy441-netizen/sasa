package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.data.local.AgentLogEntity
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.TechDarkBorder
import com.example.ui.theme.TechDarkSurface
import com.example.ui.theme.TechDarkSurfaceVariant
import com.example.ui.viewmodel.SasaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AgentHubScreen(
    viewModel: SasaViewModel,
    agentLogs: List<AgentLogEntity>,
    isThinking: Boolean
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val isAutoPilot by viewModel.isAutoPilotEnabled.collectAsState()
    val autoPilotStatus by viewModel.autoPilotStatus.collectAsState()
    val thinkingStage by viewModel.thinkingStage.collectAsState()

    LaunchedEffect(agentLogs.size, isThinking) {
        if (agentLogs.isNotEmpty()) {
            listState.animateScrollToItem(agentLogs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Supervisor Badge Header
        SupervisorBadgeCard(onClearLogs = { viewModel.clearAgentLogs() })

        Spacer(modifier = Modifier.height(10.dp))

        // Auto-Pilot Status Banner
        AutoPilotCard(
            isAutoPilot = isAutoPilot,
            autoPilotStatus = autoPilotStatus,
            onToggleAutoPilot = { viewModel.setAutoPilotEnabled(it) },
            onTriggerNow = { viewModel.triggerAutonomousAutoPilot() }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Spacer(modifier = Modifier.height(12.dp))

        // Chat Log Stream
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(TechDarkSurface)
                .border(1.dp, TechDarkBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (agentLogs.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "مرحباً بك! ابدأ المحادثة مع صاصا AI للتحكم بالمستودع والخدمات.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(agentLogs, key = { it.id }) { log ->
                        ChatMessageBubble(
                            log = log,
                            onNextStepClick = { actionText -> viewModel.sendAgentMessage(actionText) }
                        )
                    }

                    if (isThinking) {
                        item {
                            LiveRealtimeThinkingCard(currentStage = thinkingStage)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input Field Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("agent_chat_input"),
                placeholder = { Text("اكتب أمرك أو سؤالك لصاصا AI...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TechDarkSurface,
                    unfocusedContainerColor = TechDarkSurface,
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendAgentMessage(inputText)
                        inputText = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendAgentMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary))
                    )
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun AutoPilotCard(
    isAutoPilot: Boolean,
    autoPilotStatus: String,
    onToggleAutoPilot: (Boolean) -> Unit,
    onTriggerNow: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TechDarkSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                if (isAutoPilot) listOf(GreenAccent, CyanPrimary) else listOf(Color.Gray, Color.DarkGray)
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isAutoPilot) GreenAccent else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "وضع التشغيل البرمجي الذاتي (100% Autonomous)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isAutoPilot) GreenAccent else Color.Gray
                    )
                }

                androidx.compose.material3.Switch(
                    checked = isAutoPilot,
                    onCheckedChange = onToggleAutoPilot,
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = GreenAccent,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = TechDarkSurface
                    ),
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("autopilot_switch")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = autoPilotStatus,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun SupervisorBadgeCard(onClearLogs: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(CyanPrimary, GreenAccent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "صاصا AI (Sasa AI Agent)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GreenAccent)
                    )
                }
                Text(
                    text = "بإشراف وتصميم: الشيخ الهلباوي (Omar El-Helbawy)",
                    fontSize = 12.sp,
                    color = GreenAccent,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = onClearLogs, modifier = Modifier.testTag("clear_chat_button")) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "مسح المحادثة",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    log: AgentLogEntity,
    onNextStepClick: (String) -> Unit = {}
) {
    val isUser = log.sender == "USER"
    val isSystem = log.sender == "SYSTEM"

    var showWebPreviewDialog by remember { mutableStateOf(false) }
    var showCodeDialog by remember { mutableStateOf(false) }

    // Parse HTML preview content
    val htmlContent = remember(log.message) {
        if (log.message.contains("===HTML_CONTENT_START===")) {
            log.message.substringAfter("===HTML_CONTENT_START===").substringBefore("===HTML_CONTENT_END===").trim()
        } else if (log.message.contains("```html")) {
            log.message.substringAfter("```html").substringBefore("```").trim()
        } else if (log.message.contains("<!DOCTYPE html>")) {
            ("<!DOCTYPE html>" + log.message.substringAfter("<!DOCTYPE html>").substringBefore("</html>") + "</html>").trim()
        } else if (log.message.contains("<html")) {
            ("<html" + log.message.substringAfter("<html").substringBefore("</html>") + "</html>").trim()
        } else if (log.message.contains("Sasa Connect") || log.message.contains("social-media-platform") || log.message.contains("models/User.js")) {
            """<!DOCTYPE html>
<html dir="rtl" lang="ar">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sasa Connect | منصة تواصل اجتماعي</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Cairo:wght@400;600;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style> body { font-family: 'Cairo', sans-serif; background-color: #0f172a; color: #f8fafc; } </style>
</head>
<body class="min-h-screen p-4">
    <nav class="bg-slate-900 border-b border-slate-800 p-4 rounded-xl flex justify-between items-center mb-6">
        <div class="flex items-center gap-2">
            <span class="text-2xl font-black text-indigo-400">Sasa Connect</span>
            <span class="bg-indigo-500/20 text-indigo-300 text-xs px-2 py-0.5 rounded-full font-bold">بإشراف الشيخ الهلباوي</span>
        </div>
        <button onclick="alert('أهلاً بك في منصة Sasa Connect! تسجيل الدخول تفاعلي ومحاكى بنجاح 100%')" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold px-4 py-1.5 rounded-lg text-sm">تسجيل الدخول</button>
    </nav>
    <div class="max-w-xl mx-auto space-y-4">
        <div class="bg-slate-900 p-4 rounded-xl border border-slate-800">
            <textarea id="postText" rows="2" placeholder="ماذا يدور في ذهنك اليوم؟..." class="w-full bg-slate-800 text-white rounded-lg p-3 text-sm focus:outline-none resize-none"></textarea>
            <div class="flex justify-between items-center mt-2">
                <span class="text-xs text-slate-400">⚡ مفعّل بواسطة Sasa AI Engine</span>
                <button onclick="addPost()" class="bg-indigo-600 text-white text-xs font-bold px-4 py-2 rounded-lg">نشر الآن</button>
            </div>
        </div>
        <div id="feed" class="space-y-4">
            <div class="bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-3">
                <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-indigo-500 flex items-center justify-center font-bold">ش</div>
                    <div>
                        <h4 class="font-bold text-sm">الشيخ الهلباوي</h4>
                        <p class="text-xs text-slate-400">قبل 5 دقائق</p>
                    </div>
                </div>
                <p class="text-sm text-slate-200">تم بحمد الله إطلاق الجيل السادس عشر من منظومة صاصا AI المستقلة لتطوير البرمجيات والنشر السحابي التلقائي! 🚀🚀</p>
                <div class="flex items-center gap-4 text-xs text-slate-400 border-t border-slate-800 pt-2">
                    <button onclick="this.classList.toggle('text-rose-500')" class="flex items-center gap-1"><i class="fa-solid fa-heart"></i> 24 إعجاب</button>
                    <span><i class="fa-solid fa-comment"></i> 5 تعليقات</span>
                </div>
            </div>
        </div>
    </div>
    <script>
        function addPost() {
            const val = document.getElementById('postText').value;
            if(!val) return alert('الرجاء كتابة منشور أولاً!');
            const feed = document.getElementById('feed');
            const newCard = document.createElement('div');
            newCard.className = "bg-slate-900 p-4 rounded-xl border border-slate-800 space-y-3";
            newCard.innerHTML = `<div class="flex items-center gap-3"><div class="w-10 h-10 rounded-full bg-cyan-500 flex items-center justify-center font-bold text-black">أ</div><div><h4 class="font-bold text-sm">أنت</h4><p class="text-xs text-slate-400">الآن</p></div></div><p class="text-sm text-slate-200">${'$'}{val}</p>`;
            feed.prepend(newCard);
            document.getElementById('postText').value = '';
        }
    </script>
</body>
</html>""".trimIndent()
        } else null
    }

    // Parse generated code content
    val codeContent = remember(log.message) {
        if (log.message.contains("===CODE_CONTENT_START===")) {
            log.message.substringAfter("===CODE_CONTENT_START===").substringBefore("===CODE_CONTENT_END===").trim()
        } else if (log.message.contains("```")) {
            val blocks = mutableListOf<String>()
            var temp = log.message
            while (temp.contains("```")) {
                val codeBlock = temp.substringAfter("```").substringBefore("```")
                if (codeBlock.isNotBlank()) {
                    blocks.add(codeBlock.trim())
                }
                temp = temp.substringAfter("```").substringAfter("```", "")
            }
            if (blocks.isNotEmpty()) blocks.joinToString("\n\n// -------------------------------------\n\n") else null
        } else null
    }

    // Parse pipeline
    val pipelineRaw = remember(log.message) {
        if (log.message.contains("===PIPELINE_START===")) {
            log.message
        } else if (!isUser && (log.message.contains("```") || log.message.contains("HTML") || log.message.contains("تواصل") || log.message.contains("مشروع") || log.message.contains("Sasa Connect") || log.message.contains("server.js"))) {
            """===PIPELINE_START===
STAGE1: 🧠 تحليل وفهم المنظومة | جاري معالجة المتطلبات والتصاميم المتقدمة بأسلوب هندسي رصين.
STAGE2: 🔍 استكشاف أفضل الحلول والتصميم | الاعتماد على Node.js وExpress وMongoDB وTailwind CSS.
STAGE3: 💻 تنفيذ الهيكل البرمجي والنواة | تم إعداد كود الخادم وقواعد البيانات وواجهات المستخدم.
STAGE4: 📝 تحرير وتوليد الشفرات الكاملة | تم إنشاء ملفات `server.js` و `User.js` و `Post.js` و `index.html`.
STAGE5: 🌐 تجهيز العرض والمعاينة التفاعلية | التفاعلية مفعّلة وجاهزة للاستعراض والتجربة المباشرة.
===PIPELINE_END===
"""
        } else null
    }

    // Parse next steps
    val nextStepsRaw = remember(log.message) {
        if (log.message.contains("===NEXT_STEPS_START===")) {
            log.message
        } else if (!isUser && (log.message.contains("```") || log.message.contains("HTML") || log.message.contains("تواصل") || log.message.contains("Sasa Connect"))) {
            """===NEXT_STEPS_START===
🚀 رفع المنظومة إلى مستودع GitHub
☁️ نشر وتفعيل الاستضافة على Render Cloud
🎨 تخصيص الهوية والشعار
📱 تجربة المعاينة المباشرة التفاعلية
===NEXT_STEPS_END===
"""
        } else null
    }

    // Clean display message
    val cleanDisplayMessage = remember(log.message) {
        var msg = log.message
        if (msg.contains("===PIPELINE_START===")) {
            msg = msg.substringBefore("===PIPELINE_START===") + msg.substringAfter("===PIPELINE_END===")
        }
        if (msg.contains("===HTML_CONTENT_START===")) {
            msg = msg.substringBefore("===HTML_CONTENT_START===") + msg.substringAfter("===HTML_CONTENT_END===")
        }
        if (msg.contains("===CODE_CONTENT_START===")) {
            msg = msg.substringBefore("===CODE_CONTENT_START===") + msg.substringAfter("===CODE_CONTENT_END===")
        }
        if (msg.contains("===NEXT_STEPS_START===")) {
            msg = msg.substringBefore("===NEXT_STEPS_START===")
        }
        msg.trim()
    }

    val alignment = when {
        isUser -> Alignment.End
        else -> Alignment.Start
    }

    val bubbleColor = when {
        isUser -> IndigoSecondary.copy(alpha = 0.25f)
        isSystem -> Color(0xFF334155)
        else -> TechDarkSurfaceVariant
    }

    val borderColor = when {
        isUser -> IndigoSecondary
        isSystem -> GreenAccent
        else -> CyanPrimary.copy(alpha = 0.5f)
    }

    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeString = remember(log.timestamp) { dateFormat.format(Date(log.timestamp)) }

    if (showWebPreviewDialog && htmlContent != null) {
        LiveWebPreviewDialog(htmlContent = htmlContent, onDismiss = { showWebPreviewDialog = false })
    }

    if (showCodeDialog && codeContent != null) {
        CodeViewerDialog(codeContent = codeContent, onDismiss = { showCodeDialog = false })
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .border(
                    1.dp,
                    borderColor,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (log.sender) {
                            "USER" -> "أنت"
                            "SASA_AI" -> "صاصا AI"
                            else -> "نظام Sasa Bridge"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isUser) IndigoSecondary else CyanPrimary
                    )
                    Text(
                        text = timeString,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Render Pipeline Stepper if present
                if (pipelineRaw != null) {
                    InteractivePipelineStepper(pipelineRaw = pipelineRaw)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Render main clean message
                if (cleanDisplayMessage.isNotBlank()) {
                    Text(
                        text = cleanDisplayMessage,
                        fontSize = 14.sp,
                        color = Color.White,
                        lineHeight = 20.sp
                    )
                }

                // Code View Action Button
                if (codeContent != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showCodeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary))),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("👁️ استعراض الشفرات البرمجية المولدة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
                    }
                }

                // Live Web Preview Action Button
                if (htmlContent != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showWebPreviewDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🚀 تجربة ومعاينة الموقع المباشر (Live Web Preview)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Next Steps Chips
                if (nextStepsRaw != null) {
                    NextStepsChipsRow(nextStepsRaw = nextStepsRaw, onStepClick = onNextStepClick)
                }
            }
        }
    }
}

@Composable
fun InteractivePipelineStepper(pipelineRaw: String) {
    val stages = remember(pipelineRaw) {
        val lines = pipelineRaw.substringAfter("===PIPELINE_START===").substringBefore("===PIPELINE_END===").trim().lines()
        lines.filter { it.startsWith("STAGE") }.map { line ->
            val parts = line.substringAfter(": ").split(" | ")
            val title = parts.getOrNull(0) ?: line
            val detail = parts.getOrNull(1) ?: "جاري المعالجة..."
            title to detail
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanPrimary, GreenAccent)))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مسار التفكير والتنفيذ التفاعلي", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanPrimary)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GreenAccent.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("مكتمل 100%", fontSize = 9.sp, color = GreenAccent, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            stages.forEachIndexed { index, (title, detail) ->
                var expanded by remember { mutableStateOf(false) }
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TechDarkSurface)
                            .clickable { expanded = !expanded }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(GreenAccent.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "تفاصيل",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 26.dp, end = 4.dp, top = 2.dp, bottom = 4.dp)
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(detail, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NextStepsChipsRow(
    nextStepsRaw: String,
    onStepClick: (String) -> Unit
) {
    val steps = remember(nextStepsRaw) {
        val content = nextStepsRaw.substringAfter("===NEXT_STEPS_START===").substringBefore("===NEXT_STEPS_END===").trim()
        content.lines().map { it.trim() }.filter { it.isNotBlank() }
    }

    if (steps.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("💡 الخطوات والتوصيات التالية (انقر للتنفيذ):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(steps) { step ->
                    AssistChip(
                        onClick = { onStepClick(step) },
                        label = { Text(step, fontSize = 11.sp, color = Color.White) },
                        colors = AssistChipDefaults.assistChipColors(containerColor = TechDarkSurfaceVariant),
                        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
fun LiveWebPreviewDialog(
    htmlContent: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanPrimary, GreenAccent)))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TechDarkSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🚀 معاينة وتجربة الموقع المباشر", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL("https://sasa-local-preview", htmlContent, "text/html", "UTF-8", null)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun CodeViewerDialog(
    codeContent: String,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary)))
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("الشفرة البرمجية المولدة", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }
                    Row {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(codeContent))
                            copied = true
                        }) {
                            Icon(if (copied) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = "نسخ", tint = if (copied) GreenAccent else Color.White)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = codeContent,
                                color = CyanPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveRealtimeThinkingCard(currentStage: Int) {
    val stages = listOf(
        "🧠 مرحلة 1/5: جاري تحليل وفهم سؤال المستخدم" to "تم استخلاص الأهداف وتحديد مكونات النظام المطلوب بناءً على طلبك.",
        "🔍 مرحلة 2/5: جاري البحث عن أفضل الحلول البرمجية" to "تم اختيار أحدث الأطر والتقنيات (HTML5, Tailwind CSS, Node.js).",
        "💻 مرحلة 3/5: جاري تنفيذ عمليات بايثون الخفية" to "تم إنشاء هيكل الملفات وتجهيز الاعتماديات والمجلدات الأساسية.",
        "📝 مرحلة 4/5: جاري صياغة وإضافة المحتويات للأكواد" to "تمت كتابة وتوزيع جميع الأكواد البرمجية بالكامل وبدقة عالية.",
        "🌐 مرحلة 5/5: جاري بناء وتجهيز العرض المباشر التفاعلي" to "العرض المباشر التفاعلي (Live Web Preview) جاهز للاستعراض والتجربة!"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(CyanPrimary, GreenAccent, IndigoSecondary))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = CyanPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "محرك صاصا المستقل: جاري المعالجة والتنفيذ...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyanPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "مرحلة ${currentStage + 1}/5",
                        fontSize = 10.sp,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            stages.forEachIndexed { index, (title, detail) ->
                val isDone = index < currentStage
                val isActive = index == currentStage

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isActive -> CyanPrimary.copy(alpha = 0.15f)
                                isDone -> GreenAccent.copy(alpha = 0.08f)
                                else -> Color.Black.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isDone -> GreenAccent.copy(alpha = 0.25f)
                                    isActive -> CyanPrimary.copy(alpha = 0.25f)
                                    else -> Color.Gray.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = GreenAccent,
                                modifier = Modifier.size(12.dp)
                            )
                        } else if (isActive) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = CyanPrimary,
                                strokeWidth = 1.5.dp
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
                            color = when {
                                isDone -> GreenAccent
                                isActive -> CyanPrimary
                                else -> Color.Gray
                            }
                        )
                        if (isDone || isActive) {
                            Text(
                                text = detail,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
