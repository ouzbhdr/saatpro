package com.ouz.saatpro.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.ouz.saatpro.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Ekranı donanımsal olarak anında yak (WakeLock)
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "saatpro:AlarmWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L /*10 dakika*/)

        // 2. AlarmActivity'yi doğrudan en öne fırlat
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        context.startActivity(alarmIntent)

        // 3. Arka plan ses ve titreşim servisini ateşle
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START_ALARM
        }
        ContextCompat.startForegroundService(context, serviceIntent)

        // 4. Bir sonraki döngüyü kur
        val scheduler = AlarmScheduler(context)
        scheduler.scheduleNextOccurrence()
    }
}
