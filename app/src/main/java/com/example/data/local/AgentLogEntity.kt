package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_logs")
data class AgentLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER", "SASA_AI", "SYSTEM"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "CHAT" // "CHAT", "GIT_ACTION", "SERVICE"
)
