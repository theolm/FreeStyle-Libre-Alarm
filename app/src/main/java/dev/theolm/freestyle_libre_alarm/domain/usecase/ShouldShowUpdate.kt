package dev.theolm.freestyle_libre_alarm.domain.usecase

import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.domain.util.SemVerParser
import kotlinx.coroutines.flow.first

class ShouldShowUpdate(private val settingsRepository: SettingsRepository) {

    suspend operator fun invoke(availableVersion: String): Boolean {
        val lastDismissed = settingsRepository.settings.first().lastDismissedVersion

        return if (lastDismissed != null) {
            try {
                SemVerParser.isNewer(lastDismissed, availableVersion)
            } catch (_: Exception) {
                true
            }
        } else {
            true
        }
    }
}
