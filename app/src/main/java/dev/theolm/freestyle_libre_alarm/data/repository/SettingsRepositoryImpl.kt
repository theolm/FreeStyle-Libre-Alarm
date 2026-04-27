package dev.theolm.freestyle_libre_alarm.data.repository

import dev.theolm.freestyle_libre_alarm.data.local.datastore.SettingsDataStore
import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val dataStore: SettingsDataStore
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.settings

    override suspend fun updateAlarmEnabled(enabled: Boolean) {
        dataStore.updateAlarmEnabled(enabled)
    }

    override suspend fun updateLibrePackageName(packageName: String) {
        dataStore.updateLibrePackageName(packageName)
    }

    override suspend fun updateLowGlucoseEnabled(enabled: Boolean) {
        dataStore.updateLowGlucoseEnabled(enabled)
    }

    override suspend fun updateHighGlucoseEnabled(enabled: Boolean) {
        dataStore.updateHighGlucoseEnabled(enabled)
    }

    override suspend fun updateDarkModeEnabled(enabled: Boolean) {
        dataStore.updateDarkModeEnabled(enabled)
    }
}
