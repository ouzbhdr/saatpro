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
import androidx.compose.ui.text.font.FontWeight
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
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            val cX = drawContext.size.width / 2f
            val cY = drawContext.size.height / 2f
            val r = drawContext.size.width / 2f

            val hourLineLen = 14.dp.toPx()
            val minLineLen = 6.dp.toPx()
            val hourStroke = 2.5f.dp.toPx()
            val minStroke = 1.dp.toPx()

            // 60 kadran çizgisi
            for (i in 0 until 60) {
                val angle = (i * 6f) * (PI.toFloat() / 180f)
                val isHour = i % 5 == 0
                val lineLen = if (isHour) hourLineLen else minLineLen
                val strokeW = if (isHour) hourStroke else minStroke
                val color = if (isHour) BraunCharcoal else BraunSubText.copy(alpha = 0.6f)

                val startX = cX + (r - lineLen) * cos(angle)
                val startY = cY + (r - lineLen) * sin(angle)
                val endX = cX + r * cos(angle)
                val endY = cY + r * sin(angle)

                drawLine(
                    color = color,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }

            // Saat İbresi
            val hAngle = ((hour % 12 + minute / 60f) * 30f - 90f) * (PI.toFloat() / 180f)
            val hLen = r * 0.48f
            drawLine(
                color = BraunCharcoal,
                start = Offset(cX, cY),
                end = Offset(cX + hLen * cos(hAngle), cY + hLen * sin(hAngle)),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Yelkovan İbresi
            val mAngle = (minute * 6f - 90f) * (PI.toFloat() / 180f)
            val mLen = r * 0.72f
            drawLine(
                color = BraunCharcoal,
                start = Offset(cX, cY),
                end = Offset(cX + mLen * cos(mAngle), cY + mLen * sin(mAngle)),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Dieter Rams Turuncu Saniye İbresi
            val sAngle = -45f * (PI.toFloat() / 180f)
            val sLen = r * 0.76f
            drawLine(
                color = BraunOrange,
                start = Offset(cX, cY),
                end = Offset(cX + sLen * cos(sAngle), cY + sLen * sin(sAngle)),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Merkez Pimler
            drawCircle(color = BraunOrange, radius = 5.dp.toPx(), center = Offset(cX, cY))
            drawCircle(color = BraunCharcoal, radius = 2.dp.toPx(), center = Offset(cX, cY))
        }

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
