package com.example.data.model

data class TerminalEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val command: String,
    val output: String,
    val isSuccess: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class WorkspaceFileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean = false,
    val size: Long? = null
)

data class ToolExecutionResult(
    val toolName: String,
    val isSuccess: Boolean,
    val summary: String,
    val details: String
)
