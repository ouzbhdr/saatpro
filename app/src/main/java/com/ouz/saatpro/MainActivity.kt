package com.ouz.saatpro

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ouz.saatpro.alarm.AlarmScheduler
import com.ouz.saatpro.data.AlarmPreferences
import com.ouz.saatpro.ui.RamsControlTile
import com.ouz.saatpro.ui.RamsIndustrialPowerButton
import com.ouz.saatpro.ui.RamsStatusPill
import com.ouz.saatpro.ui.theme.BraunCard
import com.ouz.saatpro.ui.theme.BraunCharcoal
import com.ouz.saatpro.ui.theme.BraunOffWhite
import com.ouz.saatpro.ui.theme.BraunOrange
import com.ouz.saatpro.ui.theme.BraunSubText
import com.ouz.saatpro.ui.theme.SaatProTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestSystemPermissions()

        setContent {
            SaatProTheme {
                MainScreen(this)
            }
        }
    }

    private fun requestSystemPermissions() {
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

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                startActivity(intent)
            }
        }

        if (!Settings.canDrawOverlays(this)) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } catch (_: Exception) {}
        }
    }
}

data class RingtoneItem(val title: String, val uri: String)

@Composable
fun MainScreen(context: Context) {
    val preferences = remember { AlarmPreferences(context) }
    val scheduler = remember { AlarmScheduler(context) }

    val calendar = remember { Calendar.getInstance() }
    var targetYear by remember {
        mutableIntStateOf(if (preferences.targetYear != -1) preferences.targetYear else calendar.get(Calendar.YEAR))
    }
    var targetMonth by remember {
        mutableIntStateOf(if (preferences.targetMonth != -1) preferences.targetMonth else calendar.get(Calendar.MONTH))
    }
    var targetDay by remember {
        mutableIntStateOf(if (preferences.targetDay != -1) preferences.targetDay else calendar.get(Calendar.DAY_OF_MONTH))
    }
    var targetHour by remember { mutableIntStateOf(preferences.targetHour) }
    var targetMinute by remember { mutableIntStateOf(preferences.targetMinute) }
    var intervalHours by remember { mutableIntStateOf(preferences.intervalHours) }
    var intervalMinutes by remember { mutableIntStateOf(preferences.intervalMinutes) }
    var isAlarmActive by remember { mutableStateOf(preferences.isAlarmActive) }
    var nextTriggerMillis by remember { mutableLongStateOf(preferences.nextTriggerMillis) }
    var ringtoneTitle by remember { mutableStateOf(preferences.ringtoneTitle) }

    var showIntervalDialog by remember { mutableStateOf(false) }
    var showRingtoneDialog by remember { mutableStateOf(false) }
    var showScheduleListDialog by remember { mutableStateOf(false) }

    val fullDateFormat = remember { SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale("tr")) }
    val shortDateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("tr")) }

    fun refreshState() {
        isAlarmActive = preferences.isAlarmActive
        nextTriggerMillis = preferences.nextTriggerMillis
        targetYear = preferences.targetYear
        targetMonth = preferences.targetMonth
        targetDay = preferences.targetDay
        targetHour = preferences.targetHour
        targetMinute = preferences.targetMinute
        intervalHours = preferences.intervalHours
        intervalMinutes = preferences.intervalMinutes
        ringtoneTitle = preferences.ringtoneTitle
    }

    Scaffold(
        containerColor = BraunOffWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 1. ÜST: STATUS CARD
            val statusTitle = if (isAlarmActive) "Döngü Devrede" else "Alarm Kapalı"
            val statusSub = if (isAlarmActive && nextTriggerMillis > 0L) {
                fullDateFormat.format(Date(nextTriggerMillis))
            } else {
                "Başlatmak için dokunun"
            }
            RamsStatusPill(
                title = statusTitle,
                subtitle = statusSub,
                isActive = isAlarmActive,
                onClick = {
                    if (isAlarmActive) {
                        showScheduleListDialog = true
                    } else {
                        Toast.makeText(context, "Önce alarm döngüsünü başlatın", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // 2. ÜST PARAMETRELER: Tarih & Saat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val cal = Calendar.getInstance().apply {
                    set(targetYear, targetMonth, targetDay)
                }
                RamsControlTile(
                    icon = Icons.Default.CalendarMonth,
                    label = "Başlangıç",
                    value = shortDateFormat.format(cal.time),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                targetYear = y
                                targetMonth = m
                                targetDay = d
                                preferences.targetYear = y
                                preferences.targetMonth = m
                                preferences.targetDay = d
                                if (isAlarmActive) {
                                    scheduler.startRoutine(y, m, d, targetHour, targetMinute, intervalHours, intervalMinutes)
                                    refreshState()
                                }
                            },
                            targetYear,
                            targetMonth,
                            targetDay
                        ).show()
                    }
                )

                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", targetHour, targetMinute)
                RamsControlTile(
                    icon = Icons.Default.AccessTime,
                    label = "Saat",
                    value = timeStr,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, m ->
                                targetHour = h
                                targetMinute = m
                                preferences.targetHour = h
                                preferences.targetMinute = m
                                if (isAlarmActive) {
                                    scheduler.startRoutine(targetYear, targetMonth, targetDay, h, m, intervalHours, intervalMinutes)
                                    refreshState()
                                }
                            },
                            targetHour,
                            targetMinute,
                            true
                        ).show()
                    }
                )
            }

            // 3. MERKEZ: HAVALI VE KARİZMATİK DIETER RAMS INDUSTRIAL POWER BUTONU
            RamsIndustrialPowerButton(
                isActive = isAlarmActive,
                size = 230.dp,
                onClick = {
                    if (isAlarmActive) {
                        scheduler.cancelRoutine()
                        Toast.makeText(context, "Döngü durduruldu", Toast.LENGTH_SHORT).show()
                    } else {
                        scheduler.startRoutine(targetYear, targetMonth, targetDay, targetHour, targetMinute, intervalHours, intervalMinutes)
                        Toast.makeText(context, "Döngü başlatıldı", Toast.LENGTH_SHORT).show()
                    }
                    refreshState()
                }
            )

            // 4. ALT PARAMETRELER: Aralık (Saat + Dakika) & Zil Sesi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val intervalDisplay = if (intervalMinutes > 0) {
                    "${intervalHours}s ${intervalMinutes}dk"
                } else {
                    "${intervalHours} Saat"
                }

                RamsControlTile(
                    icon = Icons.Default.Loop,
                    label = "Aralık",
                    value = intervalDisplay,
                    modifier = Modifier.weight(1f),
                    onClick = { showIntervalDialog = true }
                )

                RamsControlTile(
                    icon = Icons.Default.GraphicEq,
                    label = "Zil Sesi",
                    value = ringtoneTitle,
                    modifier = Modifier.weight(1f),
                    onClick = { showRingtoneDialog = true }
                )
            }
        }
    }

    // ARALIK (SAAT + DAKİKA) SEÇİCİ DİYALOG
    if (showIntervalDialog) {
        var inputHours by remember { mutableStateOf(intervalHours.toString()) }
        var inputMinutes by remember { mutableStateOf(intervalMinutes.toString()) }

        AlertDialog(
            onDismissRequest = { showIntervalDialog = false },
            containerColor = BraunCard,
            title = { Text("Tekrar Aralığı Belirle", color = BraunCharcoal, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Hızlı Hazır Butonlar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(24, 48, 72).forEach { h ->
                            val isSelected = intervalHours == h && intervalMinutes == 0
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BraunCharcoal else BraunOffWhite)
                                    .clickable {
                                        inputHours = h.toString()
                                        inputMinutes = "0"
                                        intervalHours = h
                                        intervalMinutes = 0
                                        preferences.intervalHours = h
                                        preferences.intervalMinutes = 0
                                        if (isAlarmActive) {
                                            scheduler.startRoutine(targetYear, targetMonth, targetDay, targetHour, targetMinute, h, 0)
                                            refreshState()
                                        }
                                        showIntervalDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${h}s",
                                    color = if (isSelected) Color.White else BraunCharcoal,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text("Özel Saat ve Dakika Girişi:", color = BraunSubText, fontSize = 12.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputHours,
                            onValueChange = { inputHours = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Saat", color = BraunSubText) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = inputMinutes,
                            onValueChange = { inputMinutes = it.filter { ch -> ch.isDigit() } },
                            label = { Text("Dakika", color = BraunSubText) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val h = inputHours.toIntOrNull() ?: 0
                        val m = inputMinutes.toIntOrNull() ?: 0
                        if (h > 0 || m > 0) {
                            intervalHours = h
                            intervalMinutes = m
                            preferences.intervalHours = h
                            preferences.intervalMinutes = m
                            if (isAlarmActive) {
                                scheduler.startRoutine(targetYear, targetMonth, targetDay, targetHour, targetMinute, h, m)
                                refreshState()
                            }
                        }
                        showIntervalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BraunCharcoal)
                ) {
                    Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIntervalDialog = false }) {
                    Text("İptal", color = BraunSubText)
                }
            }
        )
    }

    // ZİL SESİ DİYALOG
    if (showRingtoneDialog) {
        val ringtones = remember {
            val list = mutableListOf<RingtoneItem>()
            val manager = RingtoneManager(context).apply {
                setType(RingtoneManager.TYPE_ALARM)
            }
            val cursor: Cursor? = manager.cursor
            cursor?.use {
                while (it.moveToNext()) {
                    val title = it.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                    val uri = manager.getRingtoneUri(it.position).toString()
                    list.add(RingtoneItem(title, uri))
                }
            }
            if (list.isEmpty()) {
                list.add(RingtoneItem("Varsayılan Sistem Alarmı", ""))
            }
            list
        }

        AlertDialog(
            onDismissRequest = { showRingtoneDialog = false },
            containerColor = BraunCard,
            title = { Text("Zil Sesi", color = BraunCharcoal, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(ringtones) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    ringtoneTitle = item.title
                                    preferences.ringtoneTitle = item.title
                                    preferences.ringtoneUri = item.uri
                                    showRingtoneDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.title,
                                color = if (ringtoneTitle == item.title) BraunOrange else BraunCharcoal,
                                fontSize = 15.sp,
                                fontWeight = if (ringtoneTitle == item.title) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (ringtoneTitle == item.title) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = BraunOrange)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRingtoneDialog = false }) {
                    Text("Kapat", color = BraunSubText)
                }
            }
        )
    }

    // TAKVİM LİSTESİ DİYALOG
    if (showScheduleListDialog) {
        val upcomingAlarms = remember(nextTriggerMillis, intervalHours, intervalMinutes) {
            val list = mutableListOf<String>()
            var current = nextTriggerMillis
            val intervalMs = ((intervalHours * 60L) + intervalMinutes) * 60L * 1000L
            val df = SimpleDateFormat("dd MMMM yyyy, EEEE - HH:mm", Locale("tr"))
            for (i in 1..8) {
                list.add("$i. Seans: " + df.format(Date(current)))
                current += intervalMs
            }
            list
        }

        AlertDialog(
            onDismissRequest = { showScheduleListDialog = false },
            containerColor = BraunCard,
            title = { Text("Planlanan Alarm Takvimi", color = BraunCharcoal, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(upcomingAlarms) { alarmText ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BraunOffWhite)
                                .padding(12.dp)
                        ) {
                            Text(text = alarmText, color = BraunCharcoal, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showScheduleListDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BraunCharcoal)
                ) {
                    Text("Tamam", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
