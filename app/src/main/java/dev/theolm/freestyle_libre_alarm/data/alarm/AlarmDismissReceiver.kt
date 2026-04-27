package dev.theolm.freestyle_libre_alarm.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmManager.ACTION_DISMISS_ALARM) {
            AlarmManager.stopAlarm()
        }
    }
}
