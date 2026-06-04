package dev.theolm.freestyle_libre_alarm.domain.repository

import dev.theolm.freestyle_libre_alarm.domain.model.GlucoseAlert
import kotlinx.coroutines.flow.Flow

interface GlucoseAlertRepository {
    suspend fun insertAlert(alert: GlucoseAlert): Long
    fun getAllAlerts(): Flow<List<GlucoseAlert>>
    suspend fun clearAllAlerts()
}
