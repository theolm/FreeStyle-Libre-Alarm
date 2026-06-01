package dev.theolm.freestyle_libre_alarm.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.data.service.AlarmForegroundService
import dev.theolm.freestyle_libre_alarm.presentation.di.AppModule
import dev.theolm.freestyle_libre_alarm.presentation.ui.history.HistoryScreen
import dev.theolm.freestyle_libre_alarm.presentation.ui.monitoring.MonitoringScreen
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.DownloadProgressDialog
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.ErrorDialog
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.SettingsScreen
import dev.theolm.freestyle_libre_alarm.presentation.ui.settings.UpdateAvailableDialog
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.FreeStyleLibreAlarmTheme
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.SettingsViewModel
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateUiState
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateViewModel

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("monitoring", "Home", Icons.Default.Home)
    object History : BottomNavItem("history", "Hist\u00f3rico", Icons.Default.History)
    object Settings : BottomNavItem("settings", "Config", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        AlarmForegroundService.start(this)
        AlarmManager.init(this)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(
                    settingsRepository = AppModule.provideSettingsRepository(this)
                )
            )
            val updateViewModel: UpdateViewModel = viewModel(
                factory = UpdateViewModel.Factory(
                    updateRepository = AppModule.provideUpdateRepository(this),
                    settingsRepository = AppModule.provideSettingsRepository(this)
                )
            )
            val settings by settingsViewModel.settings.collectAsState()
            val updateState by updateViewModel.uiState.collectAsState()
            val darkTheme = settings.isDarkModeEnabled

            LaunchedEffect(Unit) {
                updateViewModel.checkForUpdate(isAutomatic = true)
            }

            LaunchedEffect(updateState) {
                if (updateState is UpdateUiState.Downloaded) {
                    updateViewModel.installUpdate(this@MainActivity)
                    updateViewModel.resetState()
                }
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
                        onDismissError = { updateViewModel.resetState() }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    updateState: UpdateUiState = UpdateUiState.Idle,
    onDismissUpdate: () -> Unit = {},
    onDownloadUpdate: () -> Unit = {},
    onCancelDownload: () -> Unit = {},
    onDismissError: () -> Unit = {}
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
        else -> { }
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
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
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
