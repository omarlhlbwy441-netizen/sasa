package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SasaDao {
    @Query("SELECT * FROM agent_logs ORDER BY timestamp ASC")
    fun getAllAgentLogs(): Flow<List<AgentLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentLog(log: AgentLogEntity)

    @Query("DELETE FROM agent_logs")
    suspend fun clearAgentLogs()

    @Query("SELECT * FROM git_tasks ORDER BY timestamp DESC")
    fun getAllGitTasks(): Flow<List<GitTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGitTask(task: GitTaskEntity): Long

    @Query("UPDATE git_tasks SET status = :status, sha = :sha, errorMessage = :errorMessage WHERE id = :id")
    suspend fun updateGitTaskStatus(id: Long, status: String, sha: String?, errorMessage: String?)

    @Query("SELECT * FROM service_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentServiceLogs(): Flow<List<ServiceLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceLog(log: ServiceLogEntity)
}
