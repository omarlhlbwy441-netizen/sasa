package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.github.GitHubCommitResponse
import com.example.data.remote.github.GitHubRepoResponse
import com.example.data.remote.github.GitHubUserDetail
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.IndigoSecondary
import com.example.ui.theme.RoseError
import com.example.ui.theme.TechDarkBorder
import com.example.ui.theme.TechDarkSurface
import com.example.ui.theme.TechDarkSurfaceVariant
import com.example.ui.viewmodel.NeamaViewModel

@Composable
fun GitHubScreen(
    viewModel: NeamaViewModel,
    repoOwner: String,
    repoName: String,
    repoInfo: GitHubRepoResponse?,
    userInfo: GitHubUserDetail?,
    commitList: List<GitHubCommitResponse>,
    isLoading: Boolean,
    repoError: String?,
    isPushing: Boolean,
    pushResult: String?
) {
    var filePath by remember { mutableStateOf("server.py") }
    var commitMessage by remember { mutableStateOf("feat: Add background sync handler for Neama AI agent") }
    var targetRepoInput by remember { mutableStateOf("$repoOwner/$repoName") }
    var targetTokenInput by remember { mutableStateOf("") }
    var codeContent by remember {
        mutableStateOf(
            """# Server Background Handler for Neama AI
import os
import sys

def main():
    print("Neama AI Background Agent Bridge initialized successfully.")
    print("Repository: $repoOwner/$repoName")

if __name__ == "__main__":
    main()
"""
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Repo Overview Card
        item {
            RepoHeaderCard(
                owner = repoOwner,
                name = repoName,
                repoInfo = repoInfo,
                userInfo = userInfo,
                isLoading = isLoading,
                repoError = repoError,
                onRefresh = { viewModel.fetchRepositoryData() }
            )
        }

        // Direct Commit & Push Editor Card
        item {
            DirectCommitPushCard(
                targetRepo = targetRepoInput,
                onTargetRepoChange = { targetRepoInput = it },
                targetToken = targetTokenInput,
                onTargetTokenChange = { targetTokenInput = it },
                filePath = filePath,
                onFilePathChange = { filePath = it },
                commitMessage = commitMessage,
                onCommitMessageChange = { commitMessage = it },
                codeContent = codeContent,
                onCodeContentChange = { codeContent = it },
                isPushing = isPushing,
                pushResult = pushResult,
                onGenerateAiMessage = {
                    viewModel.generateCommitMessageWithAI(filePath, codeContent) { msg ->
                        commitMessage = msg
                    }
                },
                onPushCode = {
                    val parsed = viewModel.parseRepoOwnerAndName(targetRepoInput)
                    viewModel.pushCodeToGitHub(
                        filePath = filePath,
                        commitMessage = commitMessage,
                        fileContent = codeContent,
                        customRepoOwner = parsed.first,
                        customRepoName = parsed.second,
                        customToken = targetTokenInput
                    )
                },
                onDeleteFile = {
                    val parsed = viewModel.parseRepoOwnerAndName(targetRepoInput)
                    viewModel.deleteFileFromRepository(
                        owner = parsed.first,
                        repo = parsed.second,
                        path = filePath,
                        commitMessage = commitMessage.ifBlank { "auto: Delete $filePath via Neama AI Agent" },
                        token = targetTokenInput.ifBlank { null }
                    )
                }
            )
        }

        // Commit Timeline History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "سجل الالتزامات (Commits Timeline)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "${commitList.size} الالتزام",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Commit List Items
        if (commitList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TechDarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isLoading) "جاري تحميل سجل التغييرات..." else "لا يوجد سجل commits متاح حالياً، اضغط تحديث للفحص.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(commitList, key = { it.sha }) { commitItem ->
                CommitTimelineItem(commit = commitItem)
            }
        }
    }
}

@Composable
fun RepoHeaderCard(
    owner: String,
    name: String,
    repoInfo: GitHubRepoResponse?,
    userInfo: GitHubUserDetail?,
    isLoading: Boolean,
    repoError: String?,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyanPrimary, IndigoSecondary)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TechDarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Source,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "$owner / $name",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = userInfo?.login?.let { "متصل بحساب: @$it" } ?: "GitHub Repository connected",
                            fontSize = 12.sp,
                            color = GreenAccent
                        )
                    }
                }

                IconButton(onClick = onRefresh, modifier = Modifier.testTag("refresh_repo_button")) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = CyanPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث",
                            tint = CyanPrimary
                        )
                    }
                }
            }

            if (repoError != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = RoseError.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RoseError, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = repoError, color = RoseError, fontSize = 12.sp)
                    }
                }
            }

            if (repoInfo != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatChip(
                        icon = Icons.Default.Star,
                        label = "النجوم",
                        value = "${repoInfo.starsCount}",
                        tint = AmberWarning
                    )
                    StatChip(
                        icon = Icons.Default.ForkRight,
                        label = "الفوركس",
                        value = "${repoInfo.forksCount}",
                        tint = IndigoSecondary
                    )
                    StatChip(
                        icon = Icons.Default.Code,
                        label = "الفرع الرئيسية",
                        value = repoInfo.defaultBranch,
                        tint = CyanPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun DirectCommitPushCard(
    targetRepo: String,
    onTargetRepoChange: (String) -> Unit,
    targetToken: String,
    onTargetTokenChange: (String) -> Unit,
    filePath: String,
    onFilePathChange: (String) -> Unit,
    commitMessage: String,
    onCommitMessageChange: (String) -> Unit,
    codeContent: String,
    onCodeContentChange: (String) -> Unit,
    isPushing: Boolean,
    pushResult: String?,
    onGenerateAiMessage: () -> Unit,
    onPushCode: () -> Unit,
    onDeleteFile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TechDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(GreenAccent, CyanPrimary)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = GreenAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الرفع المباشر المتعدد (Dynamic Multi-Repo Push)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Target Repo Field
            OutlinedTextField(
                value = targetRepo,
                onValueChange = onTargetRepoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_repo_url_input"),
                label = { Text("رابط أو اسم المستودع المستهدف (owner/repo أو URL)") },
                placeholder = { Text("https://github.com/owner/repository") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenAccent,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Dynamic Target Token Field
            OutlinedTextField(
                value = targetToken,
                onValueChange = onTargetTokenChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("target_token_input"),
                label = { Text("توكن GitHub للرفع المباشر (Personal Access Token)") },
                placeholder = { Text("ghp_xxxxxxxxxxxxxxxxxxxx") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenAccent,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Template Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        onFilePathChange("app/server.py")
                        onCommitMessageChange("feat: Add Flask server backend for Neama AI")
                        onCodeContentChange(
                            """import os
from flask import Flask, jsonify, request
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

@app.route("/", methods=["GET"])
def health_check():
    return jsonify({
        "status": "online",
        "agent": "Neama AI v15.5",
        "supervisor": "El-Helbawy",
        "message": "Sasa Backend Server is running smoothly."
    })

@app.route("/api/execute", methods=["POST"])
def execute_command():
    try:
        data = request.get_json() or {}
        command = data.get("command", "")
        return jsonify({
            "status": "success",
            "executed_command": command,
            "result": "Command processed successfully by Neama AI backend."
        })
    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port, debug=True)
"""
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanPrimary)
                ) {
                    Text("تحميل app/server.py", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = {
                        onFilePathChange(".gitignore")
                        onCommitMessageChange("chore: Update .gitignore for Python & Android")
                        onCodeContentChange(
                            "# Python\n" +
                            "__pycache__/\n" +
                            "*.py[cod]\n" +
                            "*${'$'}py.class\n" +
                            ".env\n\n" +
                            "# Android / Gradle\n" +
                            ".gradle/\n" +
                            "/build/\n" +
                            "/app/build/\n" +
                            "captures/\n" +
                            ".idea/\n" +
                            "*.iml\n" +
                            ".DS_Store\n"
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenAccent)
                ) {
                    Text("تحميل .gitignore", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // File Path
            OutlinedTextField(
                value = filePath,
                onValueChange = onFilePathChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("git_file_path_input"),
                label = { Text("مسار الملف (File Path)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Commit Message & AI Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commitMessage,
                    onValueChange = onCommitMessageChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("git_commit_msg_input"),
                    label = { Text("رسالة الالتزام (Commit Message)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = TechDarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onGenerateAiMessage,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TechDarkSurfaceVariant)
                        .testTag("generate_commit_msg_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "اقترح بالذكاء الإصطناعي",
                        tint = CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Code Content Box
            OutlinedTextField(
                value = codeContent,
                onValueChange = onCodeContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("git_code_content_input"),
                label = { Text("محتوى الكود (Code Content)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = TechDarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Commit & Push and Delete Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPushCode,
                    enabled = !isPushing,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("commit_and_push_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenAccent,
                        contentColor = Color.Black
                    )
                ) {
                    if (isPushing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "جاري الحفظ...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "رفع وتحديث (Push)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = onDeleteFile,
                    enabled = !isPushing && filePath.isNotBlank(),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("delete_git_file_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoseError.copy(alpha = 0.85f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف الملف")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "حذف الملف", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Push Result Banner
            if (pushResult != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val isSuccess = pushResult.contains("بنجاح")
                Surface(
                    color = if (isSuccess) GreenAccent.copy(alpha = 0.15f) else RoseError.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = if (isSuccess) GreenAccent else RoseError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pushResult,
                            color = if (isSuccess) GreenAccent else RoseError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommitTimelineItem(commit: GitHubCommitResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TechDarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TechDarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = commit.author?.login?.take(1)?.uppercase() ?: "G",
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = commit.commit.message,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = commit.commit.author.name,
                        fontSize = 11.sp,
                        color = GreenAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = commit.commit.author.date.take(10),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(TechDarkSurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = commit.sha.take(7),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = CyanPrimary
                )
            }
        }
    }
}

@Composable
fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TechDarkSurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$label: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
