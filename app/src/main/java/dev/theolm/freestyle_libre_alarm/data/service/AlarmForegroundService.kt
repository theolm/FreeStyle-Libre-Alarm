package dev.theolm.freestyle_libre_alarm.data.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dev.theolm.freestyle_libre_alarm.AppLogger
import dev.theolm.freestyle_libre_alarm.BuildConfig
import dev.theolm.freestyle_libre_alarm.LibreConstants
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import dev.theolm.freestyle_libre_alarm.domain.usecase.ShouldShowUpdate
import dev.theolm.freestyle_libre_alarm.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.android.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

private fun Context.canPostNotifications(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

class AlarmForegroundService : Service() {

    private val logger = AppLogger.log
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val updateRepository: UpdateRepository by inject()
    private val shouldShowUpdate: ShouldShowUpdate by inject()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createUpdateNotificationChannel()

        startPeriodicUpdateCheck()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createServiceNotification()
        startForeground(FOREGROUND_SERVICE_ID, notification)

        // Service will remain running until explicitly stopped
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                getString(R.string.service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.service_channel_description)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createUpdateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPDATE_CHANNEL_ID,
                getString(R.string.update_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.update_channel_description)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createServiceNotification(): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_text))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun startPeriodicUpdateCheck() {
        serviceScope.launch {
            delay(INITIAL_UPDATE_CHECK_DELAY)
            while (isActive) {
                checkForUpdate()
                delay(UPDATE_CHECK_INTERVAL)
            }
        }
    }

    private suspend fun checkForUpdate() {
        logger.d { "Background update check started" }

        val result = try {
            updateRepository.checkForUpdate(BuildConfig.VERSION_NAME)
        } catch (e: Exception) {
            logger.e(e) { "Background update check failed" }
            return
        }

        when (result) {
            is CheckUpdateResult.Available -> {
                logger.d { "Background update available: ${result.info.version}" }
                val show = try {
                    shouldShowUpdate(result.info.version)
                } catch (e: Exception) {
                    logger.e(e) { "Failed to read dismissed version" }
                    true
                }

                if (show) {
                    showUpdateNotification(result.info)
                }
            }
            is CheckUpdateResult.UpToDate -> {
                logger.d { "Background update check: up to date" }
            }
            is CheckUpdateResult.Error -> {
                logger.e { "Background update check error: code=${result.code}" }
            }
        }
    }

    private fun showUpdateNotification(info: UpdateInfo) {
        if (!canPostNotifications()) {
            logger.d { "Skipping update notification: permission not granted" }
            return
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(LibreConstants.EXTRA_SHOW_UPDATE, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.update_notification_title))
            .setContentText(getString(R.string.update_notification_body, info.version))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(UPDATE_NOTIFICATION_ID, notification)
        logger.d { "Update notification posted for ${info.version}" }
    }

    companion object {
        const val FOREGROUND_SERVICE_ID = 1002
        const val SERVICE_CHANNEL_ID = "libre_service_channel"
        const val UPDATE_CHANNEL_ID = "libre_update_channel"
        const val UPDATE_NOTIFICATION_ID = 1003

        private val INITIAL_UPDATE_CHECK_DELAY = 5.minutes
        private val UPDATE_CHECK_INTERVAL = 8.hours

        fun start(context: Context) {
            val intent = Intent(context, AlarmForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AlarmForegroundService::class.java))
        }
    }
}
