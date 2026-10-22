package com.ouz.saatpro

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ouz.saatpro.alarm.AlarmService
import com.ouz.saatpro.data.AlarmPreferences
import com.ouz.saatpro.ui.theme.BraunBezelDark
import com.ouz.saatpro.ui.theme.BraunCharcoal
import com.ouz.saatpro.ui.theme.BraunOffWhite
import com.ouz.saatpro.ui.theme.BraunOrange
import com.ouz.saatpro.ui.theme.BraunSubText

class AlarmActivity : ComponentActivity() {

    private var localMediaPlayer: MediaPlayer? = null
    private var localVibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        showOnScreenAndDismissKeyguard()
        super.onCreate(savedInstanceState)

        // Ses ve titreşimi doğrudan aktivite üstünden başlat
        startRingtoneAndVibration()

        setContent {
            AlarmRingingScreen(
                onStopClicked = {
                    stopAlarmAndDismiss()
                }
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        showOnScreenAndDismissKeyguard()
    }

    @Suppress("DEPRECATION")
    private fun showOnScreenAndDismissKeyguard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }

    private fun startRingtoneAndVibration() {
        try {
            val prefs = AlarmPreferences(this)
            val ringtoneUriString = prefs.ringtoneUri
            var alertUri: Uri? = if (ringtoneUriString.isNotEmpty()) {
                Uri.parse(ringtoneUriString)
            } else {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            localMediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alertUri!!)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            localVibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val pattern = longArrayOf(0, 1000, 500, 1000, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localVibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                localVibrator?.vibrate(pattern, 0)
            }
        } catch (_: Exception) {}
    }

    private fun stopAlarmAndDismiss() {
        // Yerel ses ve titreşimi kapat
        localMediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
            localMediaPlayer = null
        }
        localVibrator?.cancel()

        // Servise de durdurma emri gönder
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP_ALARM
        }
        startService(stopIntent)

        finishAffinity()
    }

    override fun onDestroy() {
        localMediaPlayer?.release()
        localMediaPlayer = null
        localVibrator?.cancel()
        super.onDestroy()
    }
}

@Composable
fun AlarmRingingScreen(onStopClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BraunOffWhite)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ALARM",
                color = BraunCharcoal,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Döngüyü durdurmak için dokunun",
                color = BraunSubText,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0x40000000))
                    .clip(CircleShape)
                    .background(BraunOrange)
                    .border(6.dp, BraunBezelDark, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onStopClicked
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DURDUR",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
