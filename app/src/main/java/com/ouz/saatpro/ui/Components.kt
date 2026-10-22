package com.ouz.saatpro.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ouz.saatpro.ui.theme.BraunBezelDark
import com.ouz.saatpro.ui.theme.BraunBorder
import com.ouz.saatpro.ui.theme.BraunCard
import com.ouz.saatpro.ui.theme.BraunCharcoal
import com.ouz.saatpro.ui.theme.BraunDialBg
import com.ouz.saatpro.ui.theme.BraunGreen
import com.ouz.saatpro.ui.theme.BraunOrange
import com.ouz.saatpro.ui.theme.BraunSubText
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

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
                // Braun tarzı küçük durum lambası
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

@Composable
fun RamsClockDialButton(
    hour: Int,
    minute: Int,
    isActive: Boolean,
    size: Dp = 250.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .shadow(6.dp, CircleShape, spotColor = Color(0x33000000))
            .clip(CircleShape)
            .background(BraunDialBg)
            .border(8.dp, BraunBezelDark, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Logodaki analog saat kadranı ve minimalist çizgiler
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Kadran çizgileri (12 ana ve ara çizgiler)
            for (i in 0 until 60) {
                val angle = (i * 6) * (PI / 180f)
                val isHour = i % 5 == 0
                val lineLength = if (isHour) 14.dp.toPx() else 6.dp.toPx()
                val strokeWidth = if (isHour) 2.5.dp.toPx() else 1.dp.toPx()
                val lineColor = if (isHour) BraunCharcoal else BraunSubText.copy(alpha = 0.6f)

                val start = Offset(
                    x = (center.x + (radius - lineLength) * cos(angle)).toFloat(),
                    y = (center.y + (radius - lineLength) * sin(angle)).toFloat()
                )
                val end = Offset(
                    x = (center.x + radius * cos(angle)).toFloat(),
                    y = (center.y + radius * sin(angle)).toFloat()
                )
                drawLine(
                    color = lineColor,
                    start = start,
                    end = end,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            // Saat İbresi
            val hourAngle = ((hour % 12 + minute / 60f) * 30f - 90f) * (PI / 180f)
            val hourLength = radius * 0.48f
            drawLine(
                color = BraunCharcoal,
                start = center,
                end = Offset(
                    (center.x + hourLength * cos(hourAngle)).toFloat(),
                    (center.y + hourLength * sin(hourAngle)).toFloat()
                ),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Yelkovan İbresi
            val minuteAngle = (minute * 6f - 90f) * (PI / 180f)
            val minuteLength = radius * 0.72f
            drawLine(
                color = BraunCharcoal,
                start = center,
                end = Offset(
                    (center.x + minuteLength * cos(minuteAngle)).toFloat(),
                    (center.y + minuteLength * sin(minuteAngle)).toFloat()
                ),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Dieter Rams Turuncu Saniye İbresi / Alarm Vurgusu
            val secondAngle = -45f * (PI / 180f)
            val secondLength = radius * 0.76f
            drawLine(
                color = BraunOrange,
                start = center,
                end = Offset(
                    (center.x + secondLength * cos(secondAngle)).toFloat(),
                    (center.y + secondLength * sin(secondAngle)).toFloat()
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Merkez pim
            drawCircle(color = BraunOrange, radius = 5.dp.toPx(), center = center)
            drawCircle(color = BraunCharcoal, radius = 2.dp.toPx(), center = center)
        }

        // Kadranın ortasında veya altında durum ibaresi
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 90.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isActive) BraunGreen else BraunCharcoal)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (isActive) "AKTİF" else "BAŞLAT",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
