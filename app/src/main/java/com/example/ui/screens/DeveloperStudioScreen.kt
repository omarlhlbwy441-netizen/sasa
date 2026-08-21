package com.example.ui.screens

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.TerminalEntry
import com.example.data.model.WorkspaceFileItem
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.TechDarkBorder
import com.example.ui.theme.TechDarkSurface
import com.example.ui.theme.TechDarkSurfaceVariant
import com.example.ui.viewmodel.SasaViewModel

@Composable
fun DeveloperStudioScreen(viewModel: SasaViewModel) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        "💻 الطرفية (Terminal)",
        "📝 محرر الكود (Code)",
        "⚙️ الأدوات (Tools)",
        "🎮 محاكي الألعاب (Sandbox)",
        "🌐 النظم السحابية (Cloud)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Dev Studio Header
        DevStudioHeader(onRefresh = {
            viewModel.loadWorkspaceFiles()
        })

        // Sub Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = TechDarkSurface,
            contentColor = CyanPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = CyanPrimary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSubTab == index) CyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Sub Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            when (selectedSubTab) {
                0 -> TerminalView(viewModel)
                1 -> CodeEditorView(viewModel)
                2 -> AgentToolsRunnerView(viewModel)
                3 -> GameSandboxView(viewModel)
                4 -> CloudTelemetryView(viewModel)
            }
        }
    }
}

@Composable
private fun DevStudioHeader(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TechDarkSurface)
            .border(BorderStroke(1.dp, TechDarkBorder))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(CyanPrimary, IndigoSecondary))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Terminal, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Sasa AI Developer Studio (بيئة التطوير المتكاملة)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Text(
                    text = "محرك الطرفية • محرر الأكواد الجراحي • أدوات الوكيل • محاكي 3D",
                    fontSize = 10.sp,
                    color = CyanPrimary
                )
            }
        }

        IconButton(
            onClick = onRefresh,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "تحديث", tint = CyanPrimary)
        }
    }
}

// ----------------------------------------------------
// 1. TERMINAL VIEW
// ----------------------------------------------------
@Composable
private fun TerminalView(viewModel: SasaViewModel) {
    var cmdInput by remember { mutableStateOf("") }
    val terminalHistory by viewModel.terminalHistory.collectAsState()
    val isExecuting by viewModel.isExecutingCommand.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(terminalHistory.size) {
        if (terminalHistory.isNotEmpty()) {
            listState.animateScrollToItem(terminalHistory.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Shell Command Chips
        val quickCmds = listOf("git status", "ls -la", "python3 --version", "git log -n 3", "cat requirements.txt", "ps aux")
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickCmds) { cmd ->
                AssistChip(
                    onClick = {
                        viewModel.executeTerminalCommand(cmd)
                    },
                    label = { Text(cmd, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = TechDarkSurfaceVariant,
                        labelColor = CyanPrimary
                    ),
                    border = BorderStroke(1.dp, TechDarkBorder)
                )
            }
        }

        // Terminal Console Screen
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF030712))
                .border(1.dp, TechDarkBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (terminalHistory.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = CyanPrimary.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("طرفية أوامر Sasa AI التفاعلية جاهزة لتنفيذ كافة الأوامر...", color = Color.Gray, fontSize = 12.sp)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(terminalHistory, key = { it.id }) { entry ->
                        TerminalHistoryItem(entry)
                    }
                    if (isExecuting) {
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = CyanPrimary, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري تنفيذ الأمر في الخلفية...", color = CyanPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Command Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = cmdInput,
                onValueChange = { cmdInput = it },
                modifier = Modifier.weight(1f).testTag("terminal_command_input"),
                placeholder = { Text("أدخل أمر الشل (مثال: git status أو ls -la)...", color = Color.Gray, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TechDarkSurface,
                    unfocusedContainerColor = TechDarkSurface,
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color(0xFF4ADE80),
                    unfocusedTextColor = Color(0xFF4ADE80)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (cmdInput.isNotBlank()) {
                        viewModel.executeTerminalCommand(cmdInput)
                        cmdInput = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (cmdInput.isNotBlank()) {
                        viewModel.executeTerminalCommand(cmdInput)
                        cmdInput = ""
                    }
                },
                enabled = !isExecuting && cmdInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(52.dp).testTag("run_command_btn")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "تنفيذ", tint = Color.Black)
            }
        }
    }
}

@Composable
private fun TerminalHistoryItem(entry: TerminalEntry) {
    val clipboard = LocalClipboardManager.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("sasa@engine:~$", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.width(6.dp))
                Text(entry.command, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
            }
            IconButton(
                onClick = { clipboard.setText(AnnotatedString(entry.output)) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = Color.Gray, modifier = Modifier.size(14.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = entry.output.ifBlank { "[Command executed with exit code 0]" },
            color = if (entry.isSuccess) Color(0xFFE2E8F0) else Color(0xFFF87171),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

// ----------------------------------------------------
// 2. CODE STUDIO & SURGICAL EDITOR VIEW
// ----------------------------------------------------
@Composable
private fun CodeEditorView(viewModel: SasaViewModel) {
    val workspaceFiles by viewModel.workspaceFiles.collectAsState()
    val activeFilePath by viewModel.activeFilePath.collectAsState()
    val fileContent by viewModel.activeFileContent.collectAsState()
    val isSaving by viewModel.isSavingFile.collectAsState()
    var editableText by remember(fileContent) { mutableStateOf(fileContent ?: "") }
    var searchQuery by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        viewModel.loadWorkspaceFiles()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // File Selector Ribbon
        Text("ملفات مساحة العمل (Workspace Files):", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(workspaceFiles) { file ->
                val isSelected = activeFilePath == file.path
                AssistChip(
                    onClick = { viewModel.openWorkspaceFile(file.path) },
                    label = { Text(file.name, fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            if (file.isDirectory) Icons.Default.Folder else Icons.Default.Code,
                            contentDescription = null,
                            tint = if (isSelected) CyanPrimary else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) TechDarkSurfaceVariant else TechDarkSurface,
                        labelColor = if (isSelected) CyanPrimary else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isSelected) CyanPrimary else TechDarkBorder)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active File Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TechDarkSurface)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الملف المفتوح: ${activeFilePath ?: "اختر ملفاً من الأعلى"}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary,
                fontFamily = FontFamily.Monospace
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(editableText))
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الكود", tint = Color.White, modifier = Modifier.size(16.dp))
                }

                Button(
                    onClick = {
                        activeFilePath?.let { path ->
                            viewModel.saveWorkspaceFile(path, editableText)
                        }
                    },
                    enabled = !isSaving && activeFilePath != null,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حفظ التعديلات", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Code Editor Text Area
        OutlinedTextField(
            value = editableText,
            onValueChange = { editableText = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("code_editor_field"),
            placeholder = { Text("// شفرة الكود المصدري ستظهر هنا...", color = Color.Gray, fontSize = 11.sp) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF020617),
                unfocusedContainerColor = Color(0xFF020617),
                focusedBorderColor = CyanPrimary,
                unfocusedBorderColor = TechDarkBorder,
                focusedTextColor = Color(0xFF93C5FD),
                unfocusedTextColor = Color(0xFF93C5FD)
            ),
            shape = RoundedCornerShape(10.dp),
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        )
    }
}

// ----------------------------------------------------
// 3. AGENT TOOLS RUNNER VIEW
// ----------------------------------------------------
@Composable
private fun AgentToolsRunnerView(viewModel: SasaViewModel) {
    val toolList = listOf(
        "view_file" to "قراءة ملف محدد مع تحديد الأسطر",
        "edit_file" to "تعديل جراحي واستبدال شفرة",
        "create_file" to "إنشاء ملف جديد في مساحة العمل",
        "delete_file" to "حذف ملف من المشروع",
        "list_dir" to "استعراض محتويات المجلدات",
        "run_command" to "تنفيذ أوامر النظام والطرفية",
        "build_autonomous_game" to "توليد لعبة 3D WebGL كاملة",
        "verify_build" to "فحص وتجميع سلامة الكود",
        "github_push_file" to "رفع ملف مباشرة إلى المستودع"
    )
    val toolExecutionLog by viewModel.toolExecutionLog.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text("الأدوات التنفيذية للوكيل الذكي (Autonomous Agent Tools):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(toolList) { (toolName, toolDesc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
                    border = BorderStroke(1.dp, TechDarkBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TechDarkSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(toolName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                                Text(toolDesc, fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.runAutonomousDevTool(toolName)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("تشغيل الأداة", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (toolExecutionLog != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("نتيجة تشغيل الأداة الأخيرة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF030712))
                            .border(1.dp, GreenAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(toolExecutionLog ?: "", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// 4. GAME & WEB SANDBOX VIEW
// ----------------------------------------------------
@Composable
private fun GameSandboxView(viewModel: SasaViewModel) {
    val activeSandboxUrl by viewModel.activeSandboxUrl.collectAsState()
    var customGamePrompt by remember { mutableStateOf("") }
    val isBuildingGame by viewModel.isBuildingGame.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Game Generator Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customGamePrompt,
                onValueChange = { customGamePrompt = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("اطلب لعبة لتوليدها وتشغيلها (مثال: سباق نيون، فضاء 3D)...", color = Color.Gray, fontSize = 11.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TechDarkSurface,
                    unfocusedContainerColor = TechDarkSurface,
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = {
                    if (customGamePrompt.isNotBlank()) {
                        viewModel.generateAndLaunchGame(customGamePrompt)
                    }
                },
                enabled = !isBuildingGame && customGamePrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(50.dp)
            ) {
                if (isBuildingGame) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Live WebView Sandbox
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()
                        loadUrl(activeSandboxUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != activeSandboxUrl) {
                        webView.loadUrl(activeSandboxUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// ----------------------------------------------------
// 5. CLOUD TELEMETRY VIEW
// ----------------------------------------------------
@Composable
private fun CloudTelemetryView(viewModel: SasaViewModel) {
    val repoOwner by viewModel.repoOwner.collectAsState()
    val repoName by viewModel.repoName.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("حالة الخدمات والبنية التحتية السحابية (Cloud Infrastructure):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyanPrimary)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
            border = BorderStroke(1.dp, TechDarkBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = GreenAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Render Cloud API & Auto Deploy", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("• الاتصال: 🟢 متصل ونشط", fontSize = 11.sp, color = Color(0xFF94A3B8))
                Text("• النشر التلقائي: مدعوم عبر /api/render/deploy", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
            border = BorderStroke(1.dp, TechDarkBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("قاعدة بيانات PostgreSQL & Room", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("• PostgreSQL السحابية: dpg-d9fiq7laeets73c57lq0-a (Render)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                Text("• Room DB المحلية: SQLite Android Persistent Engine", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
            border = BorderStroke(1.dp, TechDarkBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = IndigoSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مستودع GitHub المتزامن", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("• المستودع: $repoOwner/$repoName", fontSize = 11.sp, color = Color(0xFF94A3B8))
                Text("• المسار السحابي: $serverUrl", fontSize = 11.sp, color = Color(0xFF94A3B8))
            }
        }
    }
}
