package dev.theolm.freestyle_libre_alarm.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AlarmRed,
    onPrimary = OnPrimary,
    primaryContainer = AlarmRed.copy(alpha = 0.12f),
    onPrimaryContainer = AlarmRedActive,

    secondary = Surface,
    onSecondary = Ink,
    secondaryContainer = SurfaceElevated,
    onSecondaryContainer = Body,

    tertiary = InfoBlue,
    onTertiary = OnPrimary,
    tertiaryContainer = InfoBlue.copy(alpha = 0.12f),
    onTertiaryContainer = InfoBlueActive,

    background = Background,
    onBackground = Ink,

    surface = Surface,
    onSurface = Ink,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = Body,
    surfaceTint = AlarmRed,

    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.12f),
    onErrorContainer = Error,

    outline = Hairline,
    outlineVariant = Hairline.copy(alpha = 0.5f),

    inverseSurface = DarkSurface,
    inverseOnSurface = DarkInk,
    inversePrimary = DarkAlarmRed,

    scrim = Ink.copy(alpha = 0.4f)
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAlarmRed,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkAlarmRed.copy(alpha = 0.18f),
    onPrimaryContainer = DarkAlarmRedActive,

    secondary = DarkSurface,
    onSecondary = DarkInk,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkBody,

    tertiary = InfoBlue,
    onTertiary = OnPrimary,
    tertiaryContainer = InfoBlue.copy(alpha = 0.18f),
    onTertiaryContainer = InfoBlueActive,

    background = DarkBackground,
    onBackground = DarkInk,

    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkBody,
    surfaceTint = DarkAlarmRed,

    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.18f),
    onErrorContainer = Error.copy(alpha = 0.9f),

    outline = DarkHairline,
    outlineVariant = DarkHairline.copy(alpha = 0.5f),

    inverseSurface = Surface,
    inverseOnSurface = Ink,
    inversePrimary = AlarmRed,

    scrim = DarkInk.copy(alpha = 0.6f)
)

@Composable
fun FreeStyleLibreAlarmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
