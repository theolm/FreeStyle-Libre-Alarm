package dev.theolm.freestyle_libre_alarm.presentation.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.theolm.freestyle_libre_alarm.R
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.presentation.di.AppModule
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.Background
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.DarkSurfaceElevated
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.FreeStyleLibreAlarmTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val DismissButtonShape = RoundedCornerShape(8.dp)
private val SnoozeButtonShape = RoundedCornerShape(8.dp)

class AlarmActivity : ComponentActivity() {

    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AlarmManager.ACTION_DISMISS_ALARM) {
                finish()
            }
        }
    }

    private var settings by mutableStateOf(AppSettings())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showOnLockScreen()
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        registerDismissReceiver()

        val settingsRepository = AppModule.provideSettingsRepository(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsRepository.settings.collect { newSettings ->
                    settings = newSettings
                }
            }
        }

        setContent {
            val darkTheme = settings.isDarkModeEnabled

            WindowInsetsControllerCompat(window, window.decorView).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }

            FreeStyleLibreAlarmTheme(darkTheme = darkTheme) {
                AlarmScreen(
                    isDarkTheme = darkTheme,
                    onSnooze = { minutes -> snoozeAlarm(minutes) }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dismissReceiver)
    }

    private fun registerDismissReceiver() {
        val intentFilter = IntentFilter(AlarmManager.ACTION_DISMISS_ALARM)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                dismissReceiver,
                intentFilter,
                RECEIVER_NOT_EXPORTED
            )
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(dismissReceiver, intentFilter)
        }
    }

    private fun snoozeAlarm(minutes: Int) {
        AlarmManager.stopAlarm()

        val settingsRepository = AppModule.provideSettingsRepository(this)
        val endTime = System.currentTimeMillis() + (minutes * 60 * 1000)

        lifecycleScope.launch(Dispatchers.IO) {
            settingsRepository.updateSnoozeEndTime(endTime)
        }

        finish()
    }

    @Suppress("DEPRECATION")
    private fun showOnLockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}

@Composable
fun AlarmScreen(
    isDarkTheme: Boolean,
    onSnooze: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(R.string.alert_icon_description),
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = stringResource(R.string.glucose_alert_title),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { onSnooze(10) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = DismissButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) DarkSurfaceElevated else Background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.dismiss_alarm),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = stringResource(R.string.snooze_label),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SnoozeButton(minutes = 30, onClick = { onSnooze(30) }, modifier = Modifier.weight(1f))
                SnoozeButton(minutes = 60, onClick = { onSnooze(60) }, modifier = Modifier.weight(1f))
                SnoozeButton(minutes = 180, onClick = { onSnooze(180) }, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SnoozeButton(
    minutes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (minutes) {
        60 -> stringResource(R.string.snooze_one_hour)
        180 -> stringResource(R.string.snooze_three_hours)
        else -> stringResource(R.string.snooze_minutes, minutes)
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = SnoozeButtonShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(label)
    }
}
