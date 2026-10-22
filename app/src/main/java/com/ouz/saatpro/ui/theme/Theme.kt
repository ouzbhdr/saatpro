package com.ouz.saatpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RamsColorScheme = lightColorScheme(
    primary = BraunCharcoal,
    background = BraunOffWhite,
    surface = BraunCard,
    onPrimary = BraunOffWhite,
    onBackground = BraunCharcoal,
    onSurface = BraunCharcoal
)

@Composable
fun SaatProTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RamsColorScheme,
        content = content
    )
}
