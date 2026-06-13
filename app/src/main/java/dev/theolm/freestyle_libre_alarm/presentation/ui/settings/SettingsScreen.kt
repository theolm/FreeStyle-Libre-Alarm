package dev.theolm.freestyle_libre_alarm.presentation.ui.settings

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.presentation.di.AppModule
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.SettingsViewModel
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateUiState
import dev.theolm.freestyle_libre_alarm.presentation.viewmodel.UpdateViewModel

private const val MinThreshold = 40f
private const val MaxThreshold = 400f
private const val ThresholdGap = 10

private val CardShape = RoundedCornerShape(12.dp)
private val ButtonShape = RoundedCornerShape(8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(
            settingsRepository = AppModule.provideSettingsRepository(context)
        )
    )
    val updateViewModel: UpdateViewModel = viewModel(
        factory = UpdateViewModel.Factory(
            updateRepository = AppModule.provideUpdateRepository(context),
            settingsRepository = AppModule.provideSettingsRepository(context)
        )
    )
    val settings by viewModel.settings.collectAsState()
    val updateState by updateViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                SettingsSection(
                    icon = Icons.Default.MonitorHeart,
                    title = stringResource(R.string.monitoring_types)
                ) {
                    SettingToggle(
                        label = stringResource(R.string.monitor_low_glucose),
                        checked = settings.isLowGlucoseEnabled,
                        onCheckedChange = { viewModel.updateLowGlucoseEnabled(it) }
                    )
                    SettingToggle(
                        label = stringResource(R.string.monitor_high_glucose),
                        checked = settings.isHighGlucoseEnabled,
                        onCheckedChange = { viewModel.updateHighGlucoseEnabled(it) }
                    )
                    SettingToggle(
                        label = stringResource(R.string.use_custom_thresholds),
                        checked = settings.useCustomThresholds,
                        onCheckedChange = { viewModel.updateUseCustomThresholds(it) }
                    )
                    AnimatedVisibility(visible = settings.useCustomThresholds) {
                        ThresholdRangeSlider(
                            lowValue = settings.lowThresholdMgDl,
                            highValue = settings.highThresholdMgDl,
                            onLowValueChangeFinished = { viewModel.updateLowThresholdMgDl(it) },
                            onHighValueChangeFinished = { viewModel.updateHighThresholdMgDl(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                }

                SettingsSection(
                    icon = Icons.Default.Brightness6,
                    title = stringResource(R.string.appearance)
                ) {
                    SettingToggle(
                        label = stringResource(R.string.dark_mode),
                        checked = settings.isDarkModeEnabled,
                        onCheckedChange = { viewModel.updateDarkModeEnabled(it) }
                    )
                }

                SettingsSection(
                    icon = Icons.Default.Update,
                    title = stringResource(R.string.update_section)
                ) {
                    UpdateSection(
                        updateState = updateState,
                        updateViewModel = updateViewModel,
                        context = context
                    )
                }

                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Help,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.notification_access_required),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = stringResource(R.string.notification_access_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
}

@Composable
private fun SettingsSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = CardShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}


private const val RangeSliderSteps = ((MaxThreshold - MinThreshold) / 1f).toInt() - 1

@Composable
private fun ThresholdRangeSlider(
    lowValue: Int,
    highValue: Int,
    onLowValueChangeFinished: (Int) -> Unit,
    onHighValueChangeFinished: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var localRange by remember(lowValue, highValue) {
        mutableStateOf(lowValue.toFloat()..highValue.toFloat())
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(
                R.string.threshold_range_label,
                localRange.start.toInt(),
                localRange.endInclusive.toInt()
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        RangeSlider(
            value = localRange,
            onValueChange = { newRange ->
                val lowMoved = newRange.start != localRange.start
                if (lowMoved) {
                    val constrainedLow = newRange.start.coerceIn(
                        MinThreshold,
                        (localRange.endInclusive - ThresholdGap).coerceAtMost(MaxThreshold)
                    )
                    localRange = constrainedLow..localRange.endInclusive
                } else {
                    val constrainedHigh = newRange.endInclusive.coerceIn(
                        (localRange.start + ThresholdGap).coerceAtLeast(MinThreshold),
                        MaxThreshold
                    )
                    localRange = localRange.start..constrainedHigh
                }
            },
            onValueChangeFinished = {
                onLowValueChangeFinished(localRange.start.toInt())
                onHighValueChangeFinished(localRange.endInclusive.toInt())
            },
            valueRange = MinThreshold..MaxThreshold,
            steps = RangeSliderSteps
        )
    }
}

@Composable
private fun UpdateSection(
    updateState: UpdateUiState,
    updateViewModel: UpdateViewModel,
    context: Context
) {
    when (updateState) {
        is UpdateUiState.Checking -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.checking),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                CircularProgressIndicator(
                    modifier = Modifier.height(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
        is UpdateUiState.UpdateAvailable -> {
            val version = updateState.updateInfo.version
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_version_available, version),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = { updateViewModel.downloadUpdate() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.update_now))
                }
            }
        }
        is UpdateUiState.Downloading -> {
            val progress = updateState.progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.downloading_progress, progress),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        is UpdateUiState.UpToDate -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_up_to_date),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { updateViewModel.checkForUpdate(isAutomatic = false) }) {
                    Text(stringResource(R.string.check))
                }
            }
        }
        is UpdateUiState.Error -> {
            val errorMessage = if (updateState.formatArgs.isEmpty()) {
                stringResource(updateState.messageResId)
            } else {
                stringResource(updateState.messageResId, updateState.formatArgs.first())
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(
                    onClick = { updateViewModel.checkForUpdate(isAutomatic = false) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.try_again))
                }
            }
        }
        is UpdateUiState.Downloaded -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.download_complete_exclamation),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick = { updateViewModel.installUpdate(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.install_now))
                }
            }
        }
        is UpdateUiState.NeedsPermission -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.permission_required_for_updates),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = { updateViewModel.requestInstallPermission(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = ButtonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text(stringResource(R.string.open_settings_short))
                }
            }
        }
        else -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.current_version, updateViewModel.currentVersion),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { updateViewModel.checkForUpdate(isAutomatic = false) }) {
                    Text(stringResource(R.string.check))
                }
            }
        }
    }
}
