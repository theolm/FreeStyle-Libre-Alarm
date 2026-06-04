package dev.theolm.freestyle_libre_alarm.data.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.domain.model.GlucoseAlert
import dev.theolm.freestyle_libre_alarm.domain.repository.GlucoseAlertRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.presentation.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LibreNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var glucoseAlertRepository: GlucoseAlertRepository
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate() {
        super.onCreate()
        glucoseAlertRepository = AppModule.provideGlucoseAlertRepository(this)
        settingsRepository = AppModule.provideSettingsRepository(this)
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
                glucoseAlertRepository.insertAlert(createGlucoseAlert(sbn))

                if (AlarmManager.isAlarmPlaying.value) return@launch

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

    private fun createGlucoseAlert(sbn: StatusBarNotification): GlucoseAlert {
        val notification = sbn.notification
        val extras = notification.extras
        val rawTitle = extras.getString(Notification.EXTRA_TITLE)
        val rawText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()

        val key = sbn.key
        val type = when {
            key.contains("HighGlucose", ignoreCase = true) -> "HIGH"
            key.contains("LowGlucose", ignoreCase = true) -> "LOW"
            else -> "UNKNOWN"
        }

        val (glucoseValue, trend) = parseGlucoseData(rawText)

        return GlucoseAlert(
            type = type,
            glucoseValueMgDl = glucoseValue,
            trend = trend,
            timestamp = System.currentTimeMillis(),
            rawTitle = rawTitle,
            rawText = rawText
        )
    }

    @Suppress("ReturnCount")
    private fun parseGlucoseData(text: String?): Pair<Int?, String?> {
        if (text.isNullOrBlank()) return null to null

        val regex = Regex("""(\d+)(?:mg/dL)?\s*([↑↓→])?""", RegexOption.IGNORE_CASE)
        val match = regex.find(text.trim())
        if (match == null) return null to null

        val value = match.groupValues[1].toIntOrNull()
        val trend = match.groupValues[2].ifBlank { null }
        return value to trend
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
