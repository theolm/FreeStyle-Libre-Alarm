package dev.theolm.freestyle_libre_alarm.data.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.domain.model.NotificationLog
import dev.theolm.freestyle_libre_alarm.domain.repository.NotificationLogRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

class LibreNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var notificationLogRepository: NotificationLogRepository
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        notificationLogRepository = dev.theolm.freestyle_libre_alarm.presentation.di.AppModule.provideNotificationLogRepository(this)
        settingsRepository = dev.theolm.freestyle_libre_alarm.presentation.di.AppModule.provideSettingsRepository(this)
        AlarmManager.init(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Only process notifications from FreeStyle Libre app
        if (sbn.packageName != TARGET_PACKAGE) {
            return
        }

        val key = sbn.key
        val type = when {
            key.contains("HighGlucose", ignoreCase = true) -> GlucoseType.HIGH
            key.contains("LowGlucose", ignoreCase = true) -> GlucoseType.LOW
            else -> return // Ignora notificacoes que nao sao alarme de glicose
        }

        serviceScope.launch {
            try {
                val log = createNotificationLog(sbn)
                notificationLogRepository.insertNotificationLog(log)

                val settings = settingsRepository.settings.first()
                if (settings.isAlarmEnabled) {
                    // Check if snooze is active
                    val currentTime = System.currentTimeMillis()
                    if (settings.snoozeEndTime > currentTime) {
                        // Snooze is active, ignore this notification
                        return@launch
                    }

                    when (type) {
                        GlucoseType.HIGH -> if (settings.isHighGlucoseEnabled) {
                            AlarmManager.triggerAlarm()
                        }
                        GlucoseType.LOW -> if (settings.isLowGlucoseEnabled) {
                            AlarmManager.triggerAlarm()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }

    private fun createNotificationLog(sbn: StatusBarNotification): NotificationLog {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras

        // Get app name
        val appName = try {
            val pm = packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }

        // Extract all text fields
        val title = extras.getString(Notification.EXTRA_TITLE)
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()
        val tickerText = notification.tickerText?.toString()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val summaryText = extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT)?.toString()

        // Convert extras to JSON
        val extrasJson = extractExtrasToJson(extras)

        return NotificationLog(
            packageName = packageName,
            appName = appName,
            title = title,
            text = text,
            subText = subText,
            tickerText = tickerText,
            bigText = bigText,
            summaryText = summaryText,
            timestamp = System.currentTimeMillis(),
            postTime = sbn.postTime,
            key = sbn.key,
            groupKey = sbn.groupKey,
            sortKey = sbn.notification.sortKey,
            extrasJson = extrasJson,
            isOngoing = sbn.isOngoing,
            isClearable = sbn.isClearable
        )
    }

    @Suppress("DEPRECATION")
    private fun extractExtrasToJson(extras: Bundle): String {
        val json = JSONObject()
        try {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                when (value) {
                    is String -> json.put(key, value)
                    is Number -> json.put(key, value)
                    is Boolean -> json.put(key, value)
                    else -> json.put(key, value?.toString() ?: "null")
                }
            }
        } catch (e: Exception) {
            json.put("error", "Failed to parse extras: ${e.message}")
        }
        return json.toString(2)
    }

    private enum class GlucoseType {
        HIGH,
        LOW
    }

    companion object {
        const val TARGET_PACKAGE = "com.freestylelibre.app.br"

        fun isEnabled(context: Context): Boolean {
            val componentName = ComponentName(context, LibreNotificationListenerService::class.java)
            val enabledListeners = android.provider.Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            return enabledListeners?.contains(componentName.flattenToString()) == true
        }
    }
}