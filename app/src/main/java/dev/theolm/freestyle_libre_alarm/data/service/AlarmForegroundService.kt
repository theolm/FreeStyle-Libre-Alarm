package dev.theolm.freestyle_libre_alarm.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.presentation.MainActivity

class AlarmForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createServiceNotification()
        startForeground(FOREGROUND_SERVICE_ID, notification)

        // Service will remain running until explicitly stopped
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Serviço de Monitoramento",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantém o monitoramento do FreeStyle Libre ativo"
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
            .setContentTitle("Monitoramento Ativo")
            .setContentText("Monitorando notificações do FreeStyle Libre")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val FOREGROUND_SERVICE_ID = 1002
        const val SERVICE_CHANNEL_ID = "libre_service_channel"

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
