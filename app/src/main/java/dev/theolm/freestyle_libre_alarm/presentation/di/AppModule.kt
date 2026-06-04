package dev.theolm.freestyle_libre_alarm.presentation.di

import android.content.Context
import dev.theolm.freestyle_libre_alarm.data.local.datastore.SettingsDataStore
import dev.theolm.freestyle_libre_alarm.data.local.database.LibreDatabaseProvider
import dev.theolm.freestyle_libre_alarm.data.repository.AlarmRepositoryImpl
import dev.theolm.freestyle_libre_alarm.data.repository.GlucoseAlertRepositoryImpl
import dev.theolm.freestyle_libre_alarm.data.repository.SettingsRepositoryImpl
import dev.theolm.freestyle_libre_alarm.data.repository.UpdateRepositoryImpl
import dev.theolm.freestyle_libre_alarm.domain.repository.AlarmRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.GlucoseAlertRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository

object AppModule {

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(SettingsDataStore(context))
    }

    fun provideAlarmRepository(context: Context): AlarmRepository {
        return AlarmRepositoryImpl(LibreDatabaseProvider.getDatabase(context))
    }

    fun provideGlucoseAlertRepository(context: Context): GlucoseAlertRepository {
        return GlucoseAlertRepositoryImpl(LibreDatabaseProvider.getDatabase(context))
    }

    fun provideUpdateRepository(context: Context): UpdateRepository {
        return UpdateRepositoryImpl(context)
    }
}
