package dev.theolm.freestyle_libre_alarm.domain.usecase

import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShouldShowUpdateTest {

    private val fakeSettingsRepository = FakeSettingsRepository()
    private val shouldShowUpdate = ShouldShowUpdate(fakeSettingsRepository)

    @Test
    fun `returns true when no version was dismissed`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings()

        assertTrue(shouldShowUpdate("v0.0.2"))
    }

    @Test
    fun `returns true when dismissed version is older`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings(lastDismissedVersion = "v0.0.1")

        assertTrue(shouldShowUpdate("v0.0.2"))
    }

    @Test
    fun `returns false when dismissed version is the same`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings(lastDismissedVersion = "v0.0.2")

        assertFalse(shouldShowUpdate("v0.0.2"))
    }

    @Test
    fun `returns false when dismissed version is newer`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings(lastDismissedVersion = "v0.0.3")

        assertFalse(shouldShowUpdate("v0.0.2"))
    }

    @Test
    fun `returns true when dismissed version is malformed`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings(lastDismissedVersion = "not-a-version")

        assertTrue(shouldShowUpdate("v0.0.2"))
    }

    @Test
    fun `returns true when available version is malformed`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings(lastDismissedVersion = "v0.0.1")

        assertTrue(shouldShowUpdate("not-a-version"))
    }

    private class FakeSettingsRepository : SettingsRepository {
        val settingsFlow = MutableStateFlow(AppSettings())

        override val settings = settingsFlow

        override suspend fun updateAlarmEnabled(enabled: Boolean) = Unit
        override suspend fun updateLibrePackageName(packageName: String) = Unit
        override suspend fun updateLowGlucoseEnabled(enabled: Boolean) = Unit
        override suspend fun updateHighGlucoseEnabled(enabled: Boolean) = Unit
        override suspend fun updateDarkModeEnabled(enabled: Boolean) = Unit
        override suspend fun updateSnoozeEndTime(endTime: Long) = Unit
        override suspend fun updateLastDismissedVersion(version: String?) {
            settingsFlow.value = settingsFlow.value.copy(lastDismissedVersion = version)
        }
        override suspend fun updateUseCustomThresholds(enabled: Boolean) = Unit
        override suspend fun updateLowThresholdMgDl(threshold: Int) = Unit
        override suspend fun updateHighThresholdMgDl(threshold: Int) = Unit
    }
}
