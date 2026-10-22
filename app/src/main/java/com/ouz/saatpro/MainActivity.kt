package com.ouz.saatpro

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ouz.saatpro.alarm.AlarmScheduler
import com.ouz.saatpro.alarm.AlarmService
import com.ouz.saatpro.data.AlarmPreferences
import com.ouz.saatpro.ui.ActionPillButton
import com.ouz.saatpro.ui.GiantCircleButton
import com.ouz.saatpro.ui.PillDisplay
import com.ouz.saatpro.ui.theme.AccentGreen
import com.ouz.saatpro.ui.theme.AccentRed
import com.ouz.saatpro.ui.theme.BgLightGray
import com.ouz.saatpro.ui.theme.ButtonBlack
import com.ouz.saatpro.ui.theme.SaatProTheme
import com.ouz.saatpro.ui.theme.TextDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        setContent {
            SaatProTheme {
                MainScreen(this)
            }
        }
    }

    private fun requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }

        // Samsung & Android Batarya Optimizasyonu Kontrolü
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback normal battery settings
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    val preferences = remember { AlarmPreferences(context) }
    val scheduler = remember { AlarmScheduler(context) }

    var isAlarmActive by remember { mutableStateOf(preferences.isAlarmActive) }
    var selectedHour by remember { mutableIntStateOf(preferences.targetHour) }
    var selectedMinute by remember { mutableIntStateOf(preferences.targetMinute) }
    var intervalHours by remember { mutableIntStateOf(preferences.intervalHours) }
    var nextTriggerMillis by remember { mutableLongStateOf(preferences.nextTriggerMillis) }

    var showIntervalDialog by remember { mutableStateOf(false) }

    // Tarih formatı
    val dateFormat = remember { SimpleDateFormat("dd MMMM EEEE - HH:mm", Locale("tr")) }

    fun refreshState() {
        isAlarmActive = preferences.isAlarmActive
        selectedHour = preferences.targetHour
        selectedMinute = preferences.targetMinute
        intervalHours = preferences.intervalHours
        nextTriggerMillis = preferences.nextTriggerMillis
    }

    Scaffold(
        containerColor = BgLightGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 1. ÜST: Calender / Durum Rozeti
            val statusText = if (isAlarmActive && nextTriggerMillis > 0L) {
                "SIRADAKİ: " + dateFormat.format(Date(nextTriggerMillis))
            } else {
                "ALARM KAPALI"
            }
            PillDisplay(
                text = statusText,
                backgroundColor = if (isAlarmActive) ButtonBlack else Color(0xFF333333),
                textColor = if (isAlarmActive) AccentGreen else Color.White
            )

            // 2. ÜST YARDIMCI BUTONLAR (Ses Testi / Saat Seçimi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Üst Sol: Ses Testi
                ActionPillButton(
                    title = "Sesi Dene",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val testIntent = Intent(context, AlarmService::class.java).apply {
                            action = AlarmService.ACTION_START_ALARM
                        }
                        ContextCompat.startForegroundService(context, testIntent)
                        Toast.makeText(context, "Alarm başlatıldı. Kapatmak için çalma ekranına dokunun.", Toast.LENGTH_LONG).show()
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Üst Sağ: Saat Belirle (New Alarm)
                val timeString = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                ActionPillButton(
                    title = "Saat: $timeString",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                selectedHour = hourOfDay
                                selectedMinute = minute
                                preferences.targetHour = hourOfDay
                                preferences.targetMinute = minute
                                if (isAlarmActive) {
                                    scheduler.startRoutine(hourOfDay, minute, intervalHours)
                                    refreshState()
                                }
                            },
                            selectedHour,
                            selectedMinute,
                            true
                        ).show()
                    }
                )
            }

            // 3. MERKEZ: DEV DAİRESEL START / DURDUR BUTONU
            GiantCircleButton(
                title = if (isAlarmActive) "AKTİF" else "Start",
                size = 240.dp,
                backgroundColor = if (isAlarmActive) AccentGreen else ButtonBlack,
                textColor = if (isAlarmActive) ButtonBlack else Color.White,
                onClick = {
                    if (isAlarmActive) {
                        scheduler.cancelRoutine()
                        Toast.makeText(context, "Döngü durduruldu", Toast.LENGTH_SHORT).show()
                    } else {
                        scheduler.startRoutine(selectedHour, selectedMinute, intervalHours)
                        Toast.makeText(context, "Döngü başlatıldı (${intervalHours} saat)", Toast.LENGTH_SHORT).show()
                    }
                    refreshState()
                }
            )

            // 4. ALT YARDIMCI BUTONLAR (Aralık Seçimi & Bilgi/Tema)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Alt Sol: Aralık Belirleme (24s / 48s / 72s)
                ActionPillButton(
                    title = "Döngü: ${intervalHours}s",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        showIntervalDialog = true
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Alt Sağ: İleriye dönük Tema / Rehber
                ActionPillButton(
                    title = "Rehber / Tema",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        Toast.makeText(context, "Saat Pro v1.0.0 - Anneanne Modu Devrede", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Aralık Seçim Diyaloğu
    if (showIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            title = { Text(text = "Alarm Tekrar Aralığı", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    listOf(24, 48, 72).forEach { hours ->
                        Button(
                            onClick = {
                                intervalHours = hours
                                preferences.intervalHours = hours
                                if (isAlarmActive) {
                                    scheduler.startRoutine(selectedHour, selectedMinute, hours)
                                    refreshState()
                                }
                                showIntervalDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (intervalHours == hours) AccentGreen else ButtonBlack,
                                contentColor = if (intervalHours == hours) ButtonBlack else Color.White
                            )
                        ) {
                            Text(text = "$hours Saatte Bir ${if (hours == 48) "(Gün Aşırı - Önerilen)" else ""}")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showIntervalDialog = false }) {
                    Text("Kapat")
                }
            }
        )
    }
}
