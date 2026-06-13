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
import dev.theolm.freestyle_libre_alarm.domain.usecase.GlucoseThresholdEvaluator
import dev.theolm.freestyle_libre_alarm.domain.util.GlucoseValueParser
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.android.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LibreNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val glucoseAlertRepository: GlucoseAlertRepository by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val thresholdEvaluator = GlucoseThresholdEvaluator()

    override fun onCreate() {
        super.onCreate()
        AlarmManager.init(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Only process notifications from FreeStyle Libre app
        if (!sbn.packageName.contains(FREESTYLE_LIBRE_PREFIX, ignoreCase = true)) {
            return
        }

        val key = sbn.key
        val type = when {
            key.contains("HighGlucose", ignoreCase = true) -> GlucoseThresholdEvaluator.GlucoseType.HIGH
            key.contains("LowGlucose", ignoreCase = true) -> GlucoseThresholdEvaluator.GlucoseType.LOW
            else -> return // Ignora notificacoes que nao sao alarme de glicose
        }

        serviceScope.launch {
            try {
                val glucoseAlert = createGlucoseAlert(sbn)
                glucoseAlertRepository.insertAlert(glucoseAlert)

                if (AlarmManager.isAlarmPlaying.value) return@launch

                val settings = settingsRepository.settings.first()
                if (settings.isAlarmEnabled) {
                    // Check if snooze is active
                    val currentTime = System.currentTimeMillis()
                    if (settings.snoozeEndTime > currentTime) {
                        // Snooze is active, ignore this notification
                        return@launch
                    }

                    val shouldTrigger = thresholdEvaluator(
                        value = glucoseAlert.glucoseValueMgDl,
                        type = type,
                        settings = settings
                    )
                    if (shouldTrigger) {
                        AlarmManager.triggerAlarm()
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

        val glucoseValue = GlucoseValueParser.parse(rawText)
        val trend = parseTrend(rawText)

        return GlucoseAlert(
            type = type,
            glucoseValueMgDl = glucoseValue,
            trend = trend,
            timestamp = System.currentTimeMillis(),
            rawTitle = rawTitle,
            rawText = rawText
        )
    }

    private fun parseTrend(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val match = Regex("""[↑↓→]""").find(text)
        return match?.value
    }

    companion object {
        private const val FREESTYLE_LIBRE_PREFIX = "freestylelibre"

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
