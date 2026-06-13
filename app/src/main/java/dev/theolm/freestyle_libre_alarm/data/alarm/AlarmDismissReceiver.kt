package dev.theolm.freestyle_libre_alarm.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext

class AlarmDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmManager.ACTION_DISMISS_ALARM) {
            AlarmManager.stopAlarm()

            val koin = GlobalContext.get()
            val settingsRepository: SettingsRepository = koin.get()
            val endTime = System.currentTimeMillis() + (10 * 60 * 1000)

            runBlocking {
                settingsRepository.updateSnoozeEndTime(endTime)
            }
        }
    }
}
