package com.ouz.saatpro.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ouz.saatpro.ui.theme.BraunBezelDark
import com.ouz.saatpro.ui.theme.BraunBorder
import com.ouz.saatpro.ui.theme.BraunCard
import com.ouz.saatpro.ui.theme.BraunCharcoal
import com.ouz.saatpro.ui.theme.BraunGreen
import com.ouz.saatpro.ui.theme.BraunOffWhite
import com.ouz.saatpro.ui.theme.BraunOrange
import com.ouz.saatpro.ui.theme.BraunSubText

@Composable
fun RamsStatusPill(
    title: String,
    subtitle: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BraunCard)
            .border(1.dp, BraunBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isActive) BraunGreen else BraunSubText.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title.uppercase(),
                        color = BraunSubText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = subtitle,
                        color = BraunCharcoal,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = BraunSubText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RamsControlTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(82.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BraunCard)
            .border(1.dp, BraunBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BraunSubText,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label.uppercase(),
                    color = BraunSubText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            Text(
                text = value,
                color = BraunCharcoal,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

/**
 * Dieter Rams Hi-Fi / Audio Switch esintili karizmatik buton
 */
@Composable
fun RamsIndustrialPowerButton(
    isActive: Boolean,
    size: Dp = 230.dp,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "powerPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(pulseScale)
            .shadow(
                elevation = if (isActive) 18.dp else 10.dp,
                shape = CircleShape,
                ambientColor = if (isActive) BraunGreen else Color(0x33000000),
                spotColor = if (isActive) BraunGreen else Color(0x44000000)
            )
            .clip(CircleShape)
            // Dış fırçalanmış çember
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE2E0D8),
                        BraunBezelDark
                    )
                )
            )
            .border(
                width = 3.dp,
                color = if (isActive) BraunGreen else BraunBorder,
                shape = CircleShape
            )
            .padding(14.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // İçe gömülü mat disk (Rotary Knob)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    if (isActive) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF2E3832), Color(0xFF1E2420))
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF383C42), BraunCharcoal)
                        )
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isActive) BraunGreen.copy(alpha = 0.6f) else Color(0xFF4A4F57),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Dieter Rams Turuncu Durum Göstergesi (LED Çizgi)
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isActive) BraunGreen else BraunOrange)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = null,
                    tint = if (isActive) BraunGreen else Color.White,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isActive) "AKTİF" else "BAŞLAT",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isActive) "Durdurmak İçin Dokun" else "Döngüyü Çalıştır",
                    color = BraunSubText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
