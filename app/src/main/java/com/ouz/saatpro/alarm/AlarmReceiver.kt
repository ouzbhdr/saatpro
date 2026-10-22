package com.ouz.saatpro.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Alarm servisini ön plana fırlat (ses, titreşim, fullScreenIntent)
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START_ALARM
        }
        ContextCompat.startForegroundService(context, serviceIntent)

        // 2. Döngü güvencesi: Bir sonraki periyodu (48 saat sonrasını) derhal kuyruğa al
        val scheduler = AlarmScheduler(context)
        scheduler.scheduleNextOccurrence()
    }
}
