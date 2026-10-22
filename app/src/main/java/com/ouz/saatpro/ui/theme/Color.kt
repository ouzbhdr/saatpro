package com.ouz.saatpro.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Modern Dark Theme Paleti (OLED Black + Glassmorphism / Cyber Minimalist)
val SurfaceDark = Color(0xFF0F1015)
val CardDark = Color(0xFF181A20)
val CardBorder = Color(0xFF262A34)
val SurfaceOverlay = Color(0xFF232732)

val AccentPrimary = Color(0xFF10B981) // Emerald Green
val AccentPrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF34D399), Color(0xFF059669))
)

val AccentDestructive = Color(0xFFEF4444) // Coral Red
val AccentDestructiveGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFF87171), Color(0xFFDC2626))
)

val IdleCircleGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF1F2430), Color(0xFF13161F))
)

val TextPrimary = Color(0xFFF9FAFB)
val TextSecondary = Color(0xFF9CA3AF)
val TextMuted = Color(0xFF6B7280)
