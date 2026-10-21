
package com.facultyq.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Blue100,
    onPrimary = Navy900,
    secondary = Navy700,
    onSecondary = White,
    tertiary = StatusServing,
    background = Navy900,
    onBackground = White,
    surface = Navy800,
    onSurface = White,
    surfaceVariant = Navy700,
    onSurfaceVariant = Blue100,
    outline = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = Navy900,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Navy900,
    secondary = Navy700,
    onSecondary = White,
    tertiary = StatusServing,
    background = OffWhite,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = Blue50,
    onSurfaceVariant = TextSecondary,
    outline = BorderLight,
    error = StatusError,
    onError = White
)

@Composable
fun FacultyQTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
