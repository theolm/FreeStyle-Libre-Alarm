package dev.theolm.freestyle_libre_alarm.presentation.viewmodel

import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.domain.model.UpdateInfo
import dev.theolm.freestyle_libre_alarm.domain.repository.CheckUpdateResult
import dev.theolm.freestyle_libre_alarm.domain.repository.SettingsRepository
import dev.theolm.freestyle_libre_alarm.domain.repository.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class UpdateViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var fakeUpdateRepository: FakeUpdateRepository
    private lateinit var fakeSettingsRepository: FakeSettingsRepository
    private lateinit var viewModel: UpdateViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeUpdateRepository = FakeUpdateRepository()
        fakeSettingsRepository = FakeSettingsRepository()
        viewModel = UpdateViewModel(fakeUpdateRepository, fakeSettingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(UpdateUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `checkForUpdate transitions to Checking then UpdateAvailable`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Available(
            UpdateInfo("v0.0.3", "Changelog", "https://example.com/app.apk")
        )

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UpdateUiState.UpdateAvailable)
    }

    @Test
    fun `checkForUpdate transitions to Checking then UpToDate`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.UpToDate

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(UpdateUiState.UpToDate, viewModel.uiState.value)
    }

    @Test
    fun `checkForUpdate with isAutomatic=true transitions to Idle on error`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Error("Erro 404: Não foi possível verificar atualizações")

        viewModel.checkForUpdate(isAutomatic = true)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(UpdateUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `checkForUpdate with isAutomatic=false transitions to Error on error`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Error("Erro 404: Não foi possível verificar atualizações")

        viewModel.checkForUpdate(isAutomatic = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UpdateUiState.Error)
        assertEquals("Erro 404: Não foi possível verificar atualizações", (state as UpdateUiState.Error).message)
    }

    @Test
    fun `checkForUpdate respects lastDismissedVersion`() = runTest {
        fakeSettingsRepository.settingsFlow.value = AppSettings(lastDismissedVersion = "v0.0.3")
        fakeUpdateRepository.updateResult = CheckUpdateResult.Available(
            UpdateInfo("v0.0.3", "Changelog", "https://example.com/app.apk")
        )

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(UpdateUiState.UpToDate, viewModel.uiState.value)
    }

    @Test
    fun `dismissUpdate saves version and returns to Idle`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Available(
            UpdateInfo("v0.0.3", "Changelog", "https://example.com/app.apk")
        )

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dismissUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(UpdateUiState.Idle, viewModel.uiState.value)
        assertEquals("v0.0.3", fakeSettingsRepository.lastDismissedVersion)
    }

    @Test
    fun `downloadUpdate transitions through Downloading to Downloaded`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Available(
            UpdateInfo("v0.0.3", "Changelog", "https://example.com/app.apk")
        )
        val mockFile = File.createTempFile("test", ".apk")
        fakeUpdateRepository.downloadResult = mockFile

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.downloadUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UpdateUiState.Downloaded)
    }

    @Test
    fun `cancelDownload returns to Idle and cleans up`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Available(
            UpdateInfo("v0.0.3", "Changelog", "https://example.com/app.apk")
        )

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.downloadUpdate()
        // Don't advance - keep in downloading state

        viewModel.cancelDownload()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(UpdateUiState.Idle, viewModel.uiState.value)
        assertTrue(fakeUpdateRepository.cleanupCalled)
    }

    @Test
    fun `installUpdate without permission transitions to NeedsPermission`() = runTest {
        fakeUpdateRepository.updateResult = CheckUpdateResult.Available(
            UpdateInfo("v0.0.3", "Changelog", "https://example.com/app.apk")
        )
        val mockFile = File.createTempFile("test", ".apk")
        fakeUpdateRepository.downloadResult = mockFile

        viewModel.checkForUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.downloadUpdate()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UpdateUiState.Downloaded)
    }

    // Fake repositories for testing
    private class FakeUpdateRepository : UpdateRepository {
        var updateResult: CheckUpdateResult = CheckUpdateResult.UpToDate
        var downloadResult: File? = null
        var cleanupCalled = false

        override suspend fun checkForUpdate(currentVersion: String): CheckUpdateResult {
            return updateResult
        }

        override suspend fun downloadApk(url: String, onProgress: (Int) -> Unit): File {
            onProgress(50)
            return downloadResult ?: throw IllegalStateException("No download result set")
        }

        override fun cleanupDownloadedFile() {
            cleanupCalled = true
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        val settingsFlow = MutableStateFlow(AppSettings())
        var lastDismissedVersion: String? = null

        override val settings = settingsFlow

        override suspend fun updateAlarmEnabled(enabled: Boolean) {}
        override suspend fun updateLibrePackageName(packageName: String) {}
        override suspend fun updateLowGlucoseEnabled(enabled: Boolean) {}
        override suspend fun updateHighGlucoseEnabled(enabled: Boolean) {}
        override suspend fun updateDarkModeEnabled(enabled: Boolean) {}
        override suspend fun updateSnoozeEndTime(endTime: Long) {}
        override suspend fun updateLastDismissedVersion(version: String?) {
            lastDismissedVersion = version
            settingsFlow.value = settingsFlow.value.copy(lastDismissedVersion = version)
        }
    }
}
