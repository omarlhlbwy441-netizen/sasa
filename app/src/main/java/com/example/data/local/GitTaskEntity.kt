package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "git_tasks")
data class GitTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repoName: String,
    val filePath: String,
    val commitMessage: String,
    val content: String,
    val status: String, // "PENDING", "SUCCESS", "FAILED"
    val sha: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)
