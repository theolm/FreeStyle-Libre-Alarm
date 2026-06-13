package dev.theolm.freestyle_libre_alarm

import android.app.Application
import dev.theolm.freestyle_libre_alarm.di.appModule
import dev.theolm.freestyle_libre_alarm.di.viewModelModule
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

class FreeStyleLibreAlarmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FreeStyleLibreAlarmApp)
            modules(appModule, viewModelModule)
        }
    }
}
