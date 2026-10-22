package com.ouz.saatpro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    background = BgDark,
    surface = StatusPillBg,
    onPrimary = TextDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = ButtonBlack,
    background = BgLightGray,
    surface = ButtonBlack,
    onPrimary = TextPrimary,
    onBackground = TextDark,
    onSurface = TextPrimary
)

@Composable
fun SaatProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
