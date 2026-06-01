package dev.theolm.freestyle_libre_alarm.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.theolm.freestyle_libre_alarm.presentation.di.AppModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmDismissReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmManager.ACTION_DISMISS_ALARM) {
            AlarmManager.stopAlarm()

            // Activate 10-minute snooze
            val settingsRepository = AppModule.provideSettingsRepository(context)
            val endTime = System.currentTimeMillis() + (10 * 60 * 1000)

            GlobalScope.launch {
                settingsRepository.updateSnoozeEndTime(endTime)
            }
        }
    }
}
