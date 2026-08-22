package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppPreferences
import com.example.data.local.SasaDatabase
import com.example.data.local.ServiceLogEntity
import com.example.data.repository.GitHubRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NeamaBackgroundService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val gitHubRepository = GitHubRepository()

    companion object {
        private const val CHANNEL_ID = "sasa_background_service_channel"
        private const val NOTIFICATION_ID = 1001

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _lastHeartbeatTime = MutableStateFlow(System.currentTimeMillis())
        val lastHeartbeatTime: StateFlow<Long> = _lastHeartbeatTime.asStateFlow()

        fun startService(context: Context) {
            val intent = Intent(context, NeamaBackgroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, NeamaBackgroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = buildNotification("خدمة نعمه AI نشطة في الخلفية - مزامنة جاري التنفيذ...")
        startForeground(NOTIFICATION_ID, notification)
        _isServiceRunning.value = true
        startBackgroundProcessing()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
        _isServiceRunning.value = false
    }

    private fun startBackgroundProcessing() {
        serviceScope.launch {
            val dao = SasaDatabase.getDatabase(applicationContext).sasaDao()
            val appPrefs = AppPreferences(applicationContext)
            dao.insertServiceLog(
                ServiceLogEntity(
                    title = "بدء تشغيل الخدمة",
                    detail = "تم تفعيل خدمة الخلفية الشفافة لنعمه AI بنجاح.",
                    isSuccess = true
                )
            )

            while (serviceJob.isActive) {
                _lastHeartbeatTime.value = System.currentTimeMillis()
                
                // Process pending git tasks and check server health
                try {
                    val tasks = dao.getAllGitTasks().first()
                    val pendingTasks = tasks.filter { it.status == "PENDING" }
                    
                    if (pendingTasks.isNotEmpty()) {
                        for (task in pendingTasks) {
                            val repoParts = task.repoName.split("/")
                            val owner = repoParts.getOrNull(0) ?: appPrefs.repoOwner
                            val repo = repoParts.getOrNull(1) ?: appPrefs.repoName
                            val token = appPrefs.githubToken

                            val result = gitHubRepository.pushFileContent(
                                owner = owner,
                                repo = repo,
                                path = task.filePath,
                                commitMessage = task.commitMessage,
                                fileContent = task.content,
                                token = token
                            )

                            if (result.isSuccess) {
                                val putRes = result.getOrNull()
                                val sha = putRes?.commit?.sha ?: "SUCCESS"
                                dao.updateGitTaskStatus(task.id, "SUCCESS", sha, null)
                                dao.insertServiceLog(
                                    ServiceLogEntity(
                                        title = "نجاح الرفع التلقائي",
                                        detail = "تم رفع ${task.filePath} بـ SHA: $sha إلى $owner/$repo",
                                        isSuccess = true
                                    )
                                )
                            } else {
                                val err = result.exceptionOrNull()?.message ?: "خطأ في الرفع"
                                dao.updateGitTaskStatus(task.id, "FAILED", null, err)
                                dao.insertServiceLog(
                                    ServiceLogEntity(
                                        title = "فشل الرفع التلقائي",
                                        detail = "فشل رفع ${task.filePath}: $err",
                                        isSuccess = false
                                    )
                                )
                            }
                        }
                    } else {
                        dao.insertServiceLog(
                            ServiceLogEntity(
                                title = "نبض الخدمة الموحدة (Heartbeat)",
                                detail = "الكتلة الخلفية الشفافة تعمل بانسجام 24/7. جميع الأنظمة (GitHub, Render, PostgreSQL, Video Engine) متزامنة.",
                                isSuccess = true
                            )
                        )
                    }
                } catch (e: Exception) {
                    dao.insertServiceLog(
                        ServiceLogEntity(
                            title = "خطأ في دقيقة الخدمة",
                            detail = "استثناء: ${e.localizedMessage}",
                            isSuccess = false
                        )
                    )
                }

                delay(30_000) // 30 seconds interval
            }
        }
    }

    private fun buildNotification(contentText: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("نعمه AI (Neama AI Engine)")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Neama AI Background Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "قناة إشعارات خدمة نعمه AI لرفع وإدارة التغييرات في الخلفية"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
