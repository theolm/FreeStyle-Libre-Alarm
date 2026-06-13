package dev.theolm.freestyle_libre_alarm.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.core.view.WindowCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.theolm.freestyle_libre_alarm.LibreConstants
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.data.service.AlarmForegroundService
import dev.theolm.freestyle_libre_alarm.presentation.ui.history.HistoryScreen
import dev.theolm.freestyle_libre_alarm.presentation.ui.monitoring.MonitoringScreen
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.DownloadCompleteDialog
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.DownloadProgressDialog
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.NeedsPermissionDialog
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.SettingsScreen
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.UpdateAvailableDialog
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.FreeStyleLibreAlarmTheme
import androidx.annotation.StringRes
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.SettingsViewModel
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateUiState
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem("monitoring", R.string.nav_home, Icons.Default.Home)
    object History : BottomNavItem("history", R.string.nav_history, Icons.Default.History)
    object Settings : BottomNavItem("settings", R.string.nav_settings, Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModel()
    private val updateViewModel: UpdateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        AlarmForegroundService.start(this)
        AlarmManager.init(this)

        handleUpdateIntent(intent)

        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            val updateState by updateViewModel.uiState.collectAsState()
            val darkTheme = settings.isDarkModeEnabled

            LaunchedEffect(Unit) {
                updateViewModel.checkForUpdate(isAutomatic = true)
            }

            FreeStyleLibreAlarmTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        updateState = updateState,
                        onDismissUpdate = { updateViewModel.dismissUpdate() },
                        onDownloadUpdate = { updateViewModel.downloadUpdate() },
                        onCancelDownload = { updateViewModel.cancelDownload() },
                        onDismissError = { updateViewModel.resetState() },
                        onRequestPermission = {
                            updateViewModel.requestInstallPermission(this@MainActivity)
                        },
                        onRetryInstall = {
                            updateViewModel.retryInstallAfterPermission(this@MainActivity)
                        },
                        onInstall = {
                            updateViewModel.installUpdate(this@MainActivity)
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleUpdateIntent(intent)
    }

    private fun handleUpdateIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(LibreConstants.EXTRA_SHOW_UPDATE, false) == true) {
            updateViewModel.checkForUpdate(isAutomatic = true)
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun MainScreen(
    updateState: UpdateUiState = UpdateUiState.Idle,
    onDismissUpdate: () -> Unit = {},
    onDownloadUpdate: () -> Unit = {},
    onCancelDownload: () -> Unit = {},
    onDismissError: () -> Unit = {},
    onRequestPermission: () -> Unit = {},
    onRetryInstall: () -> Unit = {},
    onInstall: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.History,
        BottomNavItem.Settings
    )

    when (updateState) {
        is UpdateUiState.UpdateAvailable -> {
            UpdateAvailableDialog(
                updateInfo = updateState.updateInfo,
                onDismiss = onDismissUpdate,
                onConfirm = onDownloadUpdate
            )
        }
        is UpdateUiState.Downloading -> {
            DownloadProgressDialog(
                progress = updateState.progress,
                onCancel = onCancelDownload
            )
        }
        is UpdateUiState.NeedsPermission -> {
            NeedsPermissionDialog(
                onConfirm = onRequestPermission,
                onDismiss = onRetryInstall
            )
        }
        is UpdateUiState.Downloaded -> {
            DownloadCompleteDialog(
                onInstall = onInstall,
                onDismiss = onDismissError
            )
        }
        else -> Unit
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 1.dp,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = stringResource(item.titleRes)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.titleRes),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                MonitoringScreen()
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
