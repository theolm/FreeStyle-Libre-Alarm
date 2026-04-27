package dev.theolm.freestyle_libre_alarm.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = OnPrimary,
    primaryContainer = Coral.copy(alpha = 0.12f),
    onPrimaryContainer = CoralActive,

    secondary = SurfaceSoft,
    onSecondary = Ink,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = Body,

    tertiary = AccentTeal,
    onTertiary = OnPrimary,

    background = Canvas,
    onBackground = Ink,

    surface = Canvas,
    onSurface = Ink,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = Muted,
    surfaceTint = Coral,

    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.12f),
    onErrorContainer = Error,

    outline = Hairline,
    outlineVariant = HairlineSoft,

    inverseSurface = SurfaceDark,
    inverseOnSurface = OnDark,
    inversePrimary = Coral,

    scrim = Ink.copy(alpha = 0.4f)
)

private val DarkColorScheme = darkColorScheme(
    primary = Coral,
    onPrimary = OnPrimary,
    primaryContainer = Coral.copy(alpha = 0.18f),
    onPrimaryContainer = Coral.copy(alpha = 0.9f),

    secondary = SurfaceDarkElevated,
    onSecondary = OnDark,
    secondaryContainer = SurfaceDarkSoft,
    onSecondaryContainer = OnDarkSoft,

    tertiary = AccentTeal,
    onTertiary = OnPrimary,

    background = SurfaceDark,
    onBackground = OnDark,

    surface = SurfaceDark,
    onSurface = OnDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = OnDarkSoft,
    surfaceTint = Coral,

    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.18f),
    onErrorContainer = Error.copy(alpha = 0.9f),

    outline = OnDarkSoft.copy(alpha = 0.3f),
    outlineVariant = OnDarkSoft.copy(alpha = 0.15f),

    inverseSurface = Canvas,
    inverseOnSurface = Ink,
    inversePrimary = Coral,

    scrim = Ink.copy(alpha = 0.6f)
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
