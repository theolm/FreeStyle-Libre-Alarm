package dev.theolm.freestyle_libre_alarm.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.theolm.freestyle_libre_alarm.data.service.AlarmForegroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            AlarmForegroundService.start(context)
        }
    }
}
