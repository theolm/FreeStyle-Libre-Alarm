package dev.theolm.freestyle_libre_alarm.data.repository

import dev.theolm.freestyle_libre_alarm.data.local.database.LibreDatabase
import dev.theolm.freestyle_libre_alarm.data.local.database.entity.GlucoseAlertEntity
import dev.theolm.freestyle_libre_alarm.domain.model.GlucoseAlert
import dev.theolm.freestyle_libre_alarm.domain.repository.GlucoseAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GlucoseAlertRepositoryImpl(
    private val database: LibreDatabase
) : GlucoseAlertRepository {

    override suspend fun insertAlert(alert: GlucoseAlert): Long {
        return database.glucoseAlertDao().insert(
            GlucoseAlertEntity(
                type = alert.type,
                glucoseValueMgDl = alert.glucoseValueMgDl,
                trend = alert.trend,
                timestamp = alert.timestamp,
                rawTitle = alert.rawTitle,
                rawText = alert.rawText
            )
        )
    }

    override fun getAllAlerts(): Flow<List<GlucoseAlert>> {
        return database.glucoseAlertDao().getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun clearAllAlerts() {
        database.glucoseAlertDao().clearAll()
    }

    private fun GlucoseAlertEntity.toDomainModel(): GlucoseAlert {
        return GlucoseAlert(
            id = id,
            type = type,
            glucoseValueMgDl = glucoseValueMgDl,
            trend = trend,
            timestamp = timestamp,
            rawTitle = rawTitle,
            rawText = rawText
        )
    }
}
