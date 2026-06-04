package dev.theolm.freestyle_libre_alarm.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.presentation.ui.alarm.AlarmActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AlarmManager {

    private lateinit var appContext: Context
    private var mediaPlayer: MediaPlayer? = null
    private var isInitialized = false

    private val _isAlarmPlaying = MutableStateFlow(false)
    val isAlarmPlaying: StateFlow<Boolean> = _isAlarmPlaying.asStateFlow()

    @Suppress("DEPRECATION")
    private val wakeLock: PowerManager.WakeLock? by lazy {
        val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "FreeStyleLibreAlarm::AlarmWakeLock"
        )
    }

    fun init(context: Context) {
        if (!isInitialized) {
            appContext = context.applicationContext
            createNotificationChannel()
            isInitialized = true
        }
    }

    fun triggerAlarm() {
        if (!isInitialized) {
            error("AlarmManager not initialized. Call init() first.")
        }

        if (_isAlarmPlaying.value) return

        stopVibration()

        // Acquire wake lock
        wakeLock?.acquire(60 * 1000L)

        // Play alarm sound
        playAlarmSound()

        // Vibrate
        vibrate()

        // Show persistent notification
        showAlarmNotification()

        // Open AlarmActivity automatically
        openAlarmActivity()

        _isAlarmPlaying.value = true
    }

    fun stopAlarm() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        try {
            wakeLock?.release()
        } catch (_: RuntimeException) {
            // Wake lock may have already timed out or been released
        }
        stopVibration()
        cancelAlarmNotification()
        _isAlarmPlaying.value = false
    }

    private fun playAlarmSound() {
        val alarmUri: Uri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
            ?: android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)

        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(appContext, alarmUri)
            isLooping = true
            prepare()
            start()
        }
    }

    private var vibrator: Vibrator? = null

    @Suppress("DEPRECATION")
    private fun vibrate() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibrator = vibratorManager.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0))
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 500), 0))
            }
            else -> {
                vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator?.vibrate(longArrayOf(0, 500, 500), 0)
            }
        }
    }

    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
    }

    private fun openAlarmActivity() {
        val intent = Intent(appContext, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        }
        appContext.startActivity(intent)
    }

    private fun showAlarmNotification() {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to open AlarmActivity when tapping the notification
        val alarmIntent = Intent(appContext, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val alarmPendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent to dismiss alarm from notification button
        val dismissIntent = Intent(appContext, AlarmDismissReceiver::class.java).apply {
            action = ACTION_DISMISS_ALARM
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            appContext,
            1,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarme de Glicose")
            .setContentText("Toque para desligar o alarme")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(alarmPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Desligar", dismissPendingIntent)
            .build()

        notificationManager.notify(ALARM_NOTIFICATION_ID, notification)
    }

    private fun cancelAlarmNotification() {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "Alarmes do FreeStyle Libre",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alarmes de glicose do FreeStyle Libre"
                setBypassDnd(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    const val ALARM_CHANNEL_ID = "libre_alarm_channel"
    const val ALARM_NOTIFICATION_ID = 1001
    const val ACTION_DISMISS_ALARM = "dev.theolm.freestyle_libre_alarm.DISMISS_ALARM"
}
