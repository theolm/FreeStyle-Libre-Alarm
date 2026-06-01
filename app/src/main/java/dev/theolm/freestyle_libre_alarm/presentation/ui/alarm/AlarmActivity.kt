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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dev.theolm.freestyle_libre_alarm.data.alarm.AlarmManager
import dev.theolm.freestyle_libre_alarm.domain.model.AppSettings
import dev.theolm.freestyle_libre_alarm.presentation.di.AppModule
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.Canvas
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.Coral
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.FreeStyleLibreAlarmTheme
import dev.theolm.freestyle_libre_alarm.presentation.ui.theme.SurfaceDarkElevated
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

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
                    onDismiss = { dismissAlarm() },
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                dismissReceiver,
                IntentFilter(AlarmManager.ACTION_DISMISS_ALARM),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                dismissReceiver,
                IntentFilter(AlarmManager.ACTION_DISMISS_ALARM)
            )
        }
    }

    private fun dismissAlarm() {
        AlarmManager.stopAlarm()
        finish()
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
    onDismiss: () -> Unit,
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
                contentDescription = "Alerta",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = "Alerta de Glicose",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { onSnooze(10) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme) SurfaceDarkElevated else Canvas,
                    contentColor = Coral
                )
            ) {
                Text(
                    text = "Desligar Alarme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onSnooze(30) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("30 min")
                }
                OutlinedButton(
                    onClick = { onSnooze(60) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("1 h")
                }
                OutlinedButton(
                    onClick = { onSnooze(180) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("3 h")
                }
            }
        }
    }
}
